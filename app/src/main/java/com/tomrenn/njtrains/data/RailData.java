package com.tomrenn.njtrains.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.WorkerThread;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 *
 */
public class RailData {
    public static final String TMP_URL = "https://drive.google.com/uc?export=download&id=0BzRjnq6vdRTvdDJ5ZWUzbmVBZlk";

    private OkHttpClient httpClient;
    private File rootDir;
    private SQLiteDatabase db;

    public RailData(OkHttpClient httpClient, File rootDir) {
        this.httpClient = httpClient;
        this.rootDir = rootDir;
    }

    public static Func1<File, Observable<File>> unzipRailData(final File unzipDir) {
        return new Func1<File, Observable<File>>() {
            @Override
            public Observable<File> call(final File file) {
                return Observable.create(new Observable.OnSubscribe<File>() {
                    @Override
                    public void call(Subscriber<? super File> subscriber) {
                        try {
                            if (!(unzipDir.exists() || unzipDir.mkdir())){
                                subscriber.onError(new RuntimeException("Directory unavailable"));
                                return;
                            }
                            unzip(file, unzipDir);
                            for (File file : unzipDir.listFiles()){
                                subscriber.onNext(file);
                            }
                            subscriber.onCompleted();
                        } catch (IOException e){
                            subscriber.onError(e);
                        }
                    }
                });
            }
        };
    }

    /**
     * Given a file with table_name.txt, insert rows into the associated table
     */
    public Action1<File> csvTableInserts = new Action1<File>() {
        @Override
        public void call(File file) {
            String tableName = file.getName();
            if (tableName.endsWith(".txt")){
                tableName = tableName.replace(".txt", "");
            }

        }
    };

    public Single<File> getRailDataZip(final String url, final File file){
        if (file.exists()){
            return Single.just(file);
        }
        return Single.create(new Single.OnSubscribe<File>() {
            @Override
            public void call(final SingleSubscriber<? super File> singleSubscriber) {
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                httpClient.newCall(request)
                        .enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                singleSubscriber.onError(e);
                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                                try {
                                    saveResponseToDisk(response, file);
                                    singleSubscriber.onSuccess(file);
                                } catch (Exception e){
                                    singleSubscriber.onError(e);
                                }
                            }
                        });
            }
        });
    }

    public static void unzip(File zipFile, File targetDirectory) throws IOException {
        ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            }
        } finally {
            zis.close();
        }
    }

    @WorkerThread
    void saveResponseToDisk(Response response, File file) throws IOException{
        BufferedSink fileSink = Okio.buffer(Okio.sink(file));
        try {
            fileSink.writeAll(response.body().source());
        } finally {
            fileSink.close();
        }
    }
}

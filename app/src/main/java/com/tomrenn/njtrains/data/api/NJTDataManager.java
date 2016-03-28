package com.tomrenn.njtrains.data.api;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tomrenn.njtrains.data.db.util.CSVTableImport;
import com.tomrenn.njtrains.data.db.util.TidyTable;
import com.tomrenn.njtrains.data.prefs.StringPreference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okio.BufferedSink;
import okio.Okio;
import rx.Completable;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * States we need to check.
 * - See if there is an update and then download it and apply
 * -- Zip downloaded but not extracted
 * --- Files extracted but not loaded
 *
 * -- if lastUpdated is null - launch welcome.
 *
 * -- once all files are loaded, set lastUpdated to updated timestamp.
 *
 * -- GcmNetworkTask that performs update checks.
 */
public class NJTDataManager implements TransitDataManager {
    public static final String DATA_DIR = "csv_data";
    public static final String DATA_ZIP = "csv_data.zip";

    SQLiteOpenHelper sqLiteOpenHelper;
    StringPreference lastUpdated;
    OkHttpClient httpClient;
    File directory;

    public NJTDataManager(SQLiteOpenHelper sqLiteOpenHelper, StringPreference lastUpdated,
                          OkHttpClient httpClient, File directory) {
        this.lastUpdated = lastUpdated;
        this.httpClient = httpClient;
        this.directory = directory;
        this.sqLiteOpenHelper = sqLiteOpenHelper;
    }


    Action1<? super Object> notifyListener(final StateListener stateListener, final String message){
        return new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (stateListener != null){
                    stateListener.update(message);
                }
            }
        };
    }

    /**
     *
     */
    @Override
    public Completable fetchLatestData(String zipUrl, StateListener stateListener) {
        final File zipFile = new File(directory, DATA_ZIP);
        final File csvDir = new File(directory, DATA_DIR);

        notifyListener(stateListener, "Downloading...").call(null);
        Action1<File> tableImport = new CSVTableImport(sqLiteOpenHelper.getWritableDatabase());

        Timber.d("Fetching latest data");

        return Observable.create(new FetchFile(httpClient, zipFile))
                .doOnNext(notifyListener(stateListener, "Unzipping..."))
                .flatMap(unzip(csvDir))
                .doOnNext(notifyListener(stateListener, "Delayed outside tunnel"))
                .doOnNext(tableImport)
                .ignoreElements().cast(Void.class)
                .concatWith(TidyTable.asObservable(sqLiteOpenHelper.getWritableDatabase()))
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        lastUpdated.set("20150823");
                    }
                })
                .toCompletable();
    }


    Func1<File, Observable<File>> unzip(final File targetDir){
        return new Func1<File, Observable<File>>() {
            @Override
            public Observable<File> call(File zipFile) {
                try {
                    unzip(zipFile, targetDir);
                    // no longer need zip file
                    zipFile.delete();
                    return Observable.from(targetDir.listFiles());
                } catch (IOException e){
                    return Observable.error(e);
                }
            }
        };
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

    // Get the latest zip file from backend
    static class FetchFile implements Observable.OnSubscribe<File> {
        public static final String TMP_URL = "https://drive.google.com/uc?export=download&id=0BzRjnq6vdRTvdDJ5ZWUzbmVBZlk";
        private OkHttpClient httpClient;
        private File zipFile;

        public FetchFile(OkHttpClient httpClient, File zipFile) {
            this.httpClient = httpClient;
            this.zipFile = zipFile;
        }

        @WorkerThread
        void saveResponseToDisk(Response response) throws IOException{
            BufferedSink fileSink = Okio.buffer(Okio.sink(zipFile));
            try {
                fileSink.writeAll(response.body().source());
            } finally {
                fileSink.close();
            }
        }

        @Override
        public void call(final Subscriber<? super File> subscriber) {
            Request request = new Request.Builder()
                    .url(TMP_URL)
                    .get()
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    subscriber.onError(e);
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    if (response.isSuccessful()){
                        saveResponseToDisk(response);
                        subscriber.onNext(zipFile);
                        subscriber.onCompleted();
                    } else {
                        subscriber.onError(new RuntimeException("Bad response code " + response.code()));
                    }
                }
            });
        }
    }

    static class Unzip implements Observable.OnSubscribe<File> {
        private File zipFile;

        public Unzip(@NonNull File zipFile){
            if (!zipFile.getName().endsWith(".zip")){
                throw new IllegalArgumentException("Unzip expects a file that ends in .zip");
            }
            this.zipFile = zipFile;
        }

        @Override
        public void call(Subscriber<? super File> subscriber) {

        }
    }
}

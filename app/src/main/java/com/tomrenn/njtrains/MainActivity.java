package com.tomrenn.njtrains;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.okhttp.OkHttpClient;

import java.io.File;

import com.tomrenn.njtrains.data.RailData;
import com.tomrenn.njtrains.data.db.DbOpenHelper;

import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Timber.plant(new Timber.DebugTree());

        OkHttpClient httpClient = new OkHttpClient();
        File rootDir = getExternalFilesDir(null);
        RailData railData = new RailData(httpClient, rootDir);

        File zipFile = new File(rootDir, "railData.zip");
        File dataDir = new File(rootDir, "railData");

        // todo: make splash screen with loading text
        // -- downloading trains
        // -- reading train data

        DbOpenHelper helper = new DbOpenHelper(this);

        railData.getRailDataZip(RailData.TMP_URL, zipFile)
                .flatMapObservable(railData.unzipRailData(dataDir))
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<File>() {
                    @Override
                    public void call(File file) {
                        Timber.d(file.getName());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.e(throwable, throwable.toString());
                        Timber.w("sadface");
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.tomrenn.njtrains.ui;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.tomrenn.njtrains.Injector;
import com.tomrenn.njtrains.R;
import com.tomrenn.njtrains.data.api.LastUpdated;
import com.tomrenn.njtrains.data.api.TripRequest;
import com.tomrenn.njtrains.data.prefs.StringPreference;
import com.tomrenn.njtrains.ui.MainActivityModule;
import com.tomrenn.njtrains.ui.MainFragment;
import com.tomrenn.njtrains.data.StopLookup;
import com.tomrenn.njtrains.ui.WelcomeFragment;

import javax.inject.Inject;

import dagger.ObjectGraph;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @Inject @LastUpdated StringPreference lastUpdated;

    ObjectGraph activityGraph;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Timber.plant(new Timber.DebugTree());

        ObjectGraph appGraph = Injector.obtain(getApplicationContext());
        appGraph.inject(this);
        activityGraph = appGraph.plus(new MainActivityModule(new TripRequest(null, null)));

        Fragment startFragment;
        if (lastUpdated.isSet()){
            startFragment = MainFragment.getInstance();
        } else {
            startFragment = WelcomeFragment.getInstance();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, startFragment)
                .commit();
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if (Injector.matchesService(name)){
            return activityGraph;
        }
        return super.getSystemService(name);
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

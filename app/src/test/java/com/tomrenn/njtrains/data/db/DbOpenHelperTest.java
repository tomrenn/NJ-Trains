package com.tomrenn.njtrains.data.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tomrenn.njtrains.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.assertj.android.api.Assertions.assertThat;
/**
 *
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk=21)
public class DbOpenHelperTest {

    DbOpenHelper helper;

    @Before
    public void setup(){
        helper = new DbOpenHelper(RuntimeEnvironment.application);
    }


    /** Verifies tables are created properly. */
    @Test
    public void testServiceDateInsert(){
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Stop.TABLE, null);

        assertThat(cursor).hasCount(0);
    }
}

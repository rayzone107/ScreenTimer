package com.rachitgoyal.screentimer;

import android.content.ContextWrapper;

import com.orm.SugarApp;
import com.pixplicity.easyprefs.library.Prefs;

/**
 * Created by Rachit Goyal on 17/04/18
 */

public class GazeAway extends SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }
}

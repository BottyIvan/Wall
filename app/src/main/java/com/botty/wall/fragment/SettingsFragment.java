package com.botty.wall.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.botty.wall.R;

/**
 * Created by BottyIvan on 06/08/16.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String NIGHTMODE = "nightmode";
    public static final String TWOROW = "tworow";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(NIGHTMODE)) {
            sharedPreferences.getBoolean(key, true);
        }
        if (key.equals(TWOROW)) {
            sharedPreferences.getBoolean(key, true);
        }

    }

}

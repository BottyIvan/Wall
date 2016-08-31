package com.botty.wall.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.botty.wall.R;

/**
 * Created by BottyIvan on 06/08/16.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String NIGHTMODE = "nightmode";
    public static final String TWOROW = "tworow";
    public static final String DIRECTORY_NAME = "directory";

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

        if (key.equals(DIRECTORY_NAME)){
            Preference preference = findPreference(key);
            if (preference instanceof EditTextPreference){
                EditTextPreference editTextPreference =  (EditTextPreference)preference;
                if (editTextPreference.getText().trim().length() > 0){
                    editTextPreference.setText(editTextPreference.getText());
                }else{
                    editTextPreference.setText("Not change");
                }
            }
        }

    }

}

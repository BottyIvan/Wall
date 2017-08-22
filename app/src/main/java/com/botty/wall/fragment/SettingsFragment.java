package com.botty.wall.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
    public static final String SWIPE_ACTIVITY_WALL = "swipe_ui_wall_act";
    public static final String DIRECTORY_NAME = "directory";

    public static final String VERSION = "version";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(NIGHTMODE)) {
            sharedPreferences.getBoolean(key, true);
        } else if (key.equals(TWOROW)) {
            sharedPreferences.getBoolean(key, true);
        } else if (key.equals(SWIPE_ACTIVITY_WALL)) {
            sharedPreferences.getBoolean(key, true);
        } else if (key.equals(DIRECTORY_NAME)){
            Preference preference = findPreference(key);
            if (preference instanceof EditTextPreference){
                EditTextPreference editTextPreference =  (EditTextPreference)preference;
                if (editTextPreference.getText().trim().length() > 0){
                    editTextPreference.setSummary(editTextPreference.getText());
                }else{
                    editTextPreference.setText("Not change");
                }
            }
        }

        if (key.equals(VERSION)){
            Preference customPref = findPreference(key);
            customPref.setSummary(getVersionApp(getActivity()));
        }

    }
    public static String getVersionApp(Context context){
        PackageInfo pInfo = null;
        String str = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        int verCode = pInfo.versionCode;
        str =  context.getString(R.string.app_name)+" "+" "+version+" ( "+verCode+" ) ";
        return str;
    }

}

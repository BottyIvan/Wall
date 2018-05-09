package com.botty.wall.fragment;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.botty.wall.R;
import com.botty.wall.app.PrefManager;

import java.io.File;

/**
 * Created by BottyIvan on 06/08/16.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String NIGHTMODE = "nightmode";
    public static final String TWO_ROW = "two_row";
    public static final String SWIPE_ACTIVITY_WALL = "swipe_ui_wall_act";
    public static final String DIRECTORY_NAME = "directory";

    public static final String VERSION = "version";

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private PrefManager prefManager;
    private Preference myAskSD,myVers;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        myAskSD = findPreference("grant_access");
        myAskSD.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                //open browser or intent here
                AskForWriteSDPermission();
                return true;
            }
        });

        myVers = findPreference("version");
        myVers.setTitle(getVersionApp(getActivity()));
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(NIGHTMODE)) {
            sharedPreferences.getBoolean(key, true);
        } else if (key.equals(TWO_ROW)) {
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

    /**
     *  Start method for asking the permission to write SD card
     */
    protected void AskForWriteSDPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasWriteSDPermission = getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWriteSDPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    prefManager = new PrefManager(getActivity());
                    prefManager.setCanWriteSd(true);;
                    CreateDirectory();
                    Toast.makeText(getActivity(), R.string.can_download_wall, Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission Denied
                    Toast.makeText(getActivity(), R.string.cant_download_wall, Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void CreateDirectory(){
        File wallpaperDirectory = new File("/sdcard/WallApp/");
        wallpaperDirectory.mkdirs();
    }

    // End asking permission for SD card
}

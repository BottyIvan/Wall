package com.botty.wall.app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by BottyIvan on 17/08/16.
 */
public class PrefManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "com.botty.wall-welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private static final  String CAN_WRITE_SD = "grant_access";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setCanWriteSd(boolean canWriteSd){
        editor.putBoolean(CAN_WRITE_SD, canWriteSd);
        editor.commit();
    }

    public boolean canWriteSD(){
        return pref.getBoolean(CAN_WRITE_SD,false);
    }

}

package com.botty.wall.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.botty.wall.R;

/**
 * Created by BottyIvan on 21/08/16.
 */
public class Fragment_SlidePrefUI extends Fragment{

    private SwitchCompat switchCompatNightMode,switchCompatTwoCol;
    private SharedPreferences settings;

    public Fragment_SlidePrefUI() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, true);
        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.welcome_slide3, container, false);

        switchCompatNightMode = (SwitchCompat) rootView.findViewById(R.id.switchNightMode);
        switchCompatTwoCol = (SwitchCompat) rootView.findViewById(R.id.switchTwoCol);

        switchCompatNightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    settings.getBoolean("nightmode", true);
                } else {
                    settings.getBoolean("nightmode", false);
                }
            }
        });

        switchCompatTwoCol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    settings.getBoolean("tworow", true);
                } else {
                    settings.getBoolean("tworow", false);
                }
            }
        });

        return rootView;
    }
}

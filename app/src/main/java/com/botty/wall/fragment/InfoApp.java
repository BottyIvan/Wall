package com.botty.wall.fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.botty.wall.R;

public class InfoApp extends BaseFragment {

    public InfoApp(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.info_app, container, false);

        TextView textView = rootView.findViewById(R.id.app_name);
        textView.setText(R.string.app_name);

        TextView textView1 = rootView.findViewById(R.id.version);
        try {
            String versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            final int versionCode = getActivity().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0).versionCode;
            textView1.setText(versionName+" ("+versionCode+")");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView textView2 = rootView.findViewById(R.id.about_text);
        textView2.setText(getString(R.string.app_name)+" is an application for finding cool wallpaper, with the focus on the friendly ui with a beautiful interpretation of Material Design.\n\n2018 © Ivan Botty \n\nMade with ❤️ in Italy.\n");
        return rootView;
    }

}

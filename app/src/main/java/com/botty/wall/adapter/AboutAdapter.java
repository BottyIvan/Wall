package com.botty.wall.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.botty.wall.fragment.InfoApp;
import com.botty.wall.fragment.ListLibs;

public class AboutAdapter extends FragmentStatePagerAdapter {

    private String[] titles={"Info","Libs"};
    private final int PAGES = titles.length ;

    public AboutAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new InfoApp();
            case 1:
                return new ListLibs();
            default:
                throw new IllegalArgumentException("The item position should be less or equal to:" + PAGES);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return PAGES;
    }
}
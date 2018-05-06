package com.botty.wall.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.botty.wall.R;
import com.botty.wall.adapter.AboutAdapter;
import com.botty.wall.widget.SwipeBackCoordinatorLayout;
import com.pixelcan.inkpageindicator.InkPageIndicator;

public class About extends BaseActivity{

    private AboutAdapter mAdapter;

    private ViewPager viewPager;
    private InkPageIndicator mIndicator;
    private CoordinatorLayout swipeBackView;


    private boolean tabletSize;

    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tabletSize = getResources().getBoolean(R.bool.isTablet);


        swipeBackView = findViewById(R.id.swipeBackView);

        mAdapter = new AboutAdapter(getSupportFragmentManager());

        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(mAdapter);

        mIndicator = findViewById(R.id.indicator);
        mIndicator.setViewPager(viewPager);

        getFitsSystmeUI();
    }

}

package com.botty.wall.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewPager;

import com.botty.wall.R;
import com.botty.wall.adapter.AboutAdapter;
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

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            centerToolbarTitle(toolbar,true);
        }

        swipeBackView = findViewById(R.id.swipeBackView);

        mAdapter = new AboutAdapter(getSupportFragmentManager());

        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(mAdapter);

        mIndicator = findViewById(R.id.indicator);
        mIndicator.setViewPager(viewPager);

    }

}

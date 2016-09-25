package com.botty.wall.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.botty.wall.R;
import com.botty.wall.adapter.RecyclerAdapter;
import com.botty.wall.model.linkSocial;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class About extends AppCompatActivity {

    private ImageView imageView;
    private List<linkSocial> linkSocialList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private FloatingActionButton floatingActionButton;
    private boolean tabletSize;
    View decorView;

    private String img_me_Twitter = "https://pbs.twimg.com/profile_images/775986514459262976/FLRH9AbA.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // inside your activity (if you did not enable transitions in your theme)
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Explode());
            // set an exit transition
            getWindow().setExitTransition(new Explode());
        }

        tabletSize = getResources().getBoolean(R.bool.isTablet);

        if (tabletSize){
            decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getString(R.string.title_activity_about));

        imageView = (ImageView) findViewById(R.id.about_me);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.mail_me);

        Picasso.with(getApplicationContext())
                .load(img_me_Twitter)
                .into(imageView,
                        PicassoPalette.with(img_me_Twitter, imageView)
                                .use(PicassoPalette.Profile.VIBRANT_LIGHT)
                                .intoBackground(collapsingToolbar)
                );

        mAdapter = new RecyclerAdapter(linkSocialList);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mStaggeredLayoutManager);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                linkSocial linkSocial = linkSocialList.get(position);
                if (position == 0){
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(About.this, Uri.parse("https://t.co/IZuIDOWuKm"));
                } else if (position == 1){
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(About.this, Uri.parse("https://twitter.com/bottyivan"));
                } else if (position == 2){
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(About.this, Uri.parse("https://github.com/BottyIvan"));
                } else if (position == 3){
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(About.this, Uri.parse("https://www.youtube.com/channel/UCfeaOwmTsHoj1VUYWNA_gtg"));
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(About.this, "¯\\_(ツ)_/¯",Toast.LENGTH_LONG).show();
            }
        }));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"droidbotty@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Wall By Botty App");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(About.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setData();

    }

    private void setData() {
        linkSocial social = new linkSocial("Google +", "+IvanBotty","https://lh3.googleusercontent.com/-ly967oaFSRI/V59oacX6cJI/AAAAAAAAsog/3e9wqGjuQqIc4Rq6vxYd0R6ptpcTL8cbQCL0B/s1000-fcrop64=1,00000bf0fffff40e/11f8b991-bf0e-4e38-9b82-499e96476920");
        linkSocialList.add(social);

        social = new linkSocial("Twitter", "@bottyivan", "https://pbs.twimg.com/profile_banners/237006808/1459728428/1500x500");
        linkSocialList.add(social);

        social = new linkSocial("GitHub", "BottyIvan", null);
        linkSocialList.add(social);

        social = new linkSocial("YouTube", "bottydroid", "https://yt3.ggpht.com/hAZoU8lot5uLaSVrGbUo31RLQtA1a1-WrFUbtX3hzgA1vt2PKOzs8Wl9AWtnmLZ0Oec_1g=w2120-fcrop64=1,00005a57ffffa5a8-nd-c0xffffffff-rj-k-no");
        linkSocialList.add(social);


        mAdapter.notifyDataSetChanged();

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private About.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final About.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}

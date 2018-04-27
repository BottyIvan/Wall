package com.botty.wall.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.botty.wall.R;
import com.botty.wall.activity.Settings;
import com.botty.wall.adapter.RecyclerAdapter;
import com.botty.wall.model.linkSocial;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class About extends Fragment {

    private ImageView imageView;
    private List<linkSocial> linkSocialList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter mAdapter;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private FloatingActionButton floatingActionButton;
    private boolean tabletSize;
    View decorView;

    private String img_me_Twitter = "https://pbs.twimg.com/profile_images/981326359611011072/TOtxL59z_400x400.jpg";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        tabletSize = getResources().getBoolean(R.bool.isTablet);

        if (tabletSize){
            decorView = getActivity().getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_about, container, false);

        Toolbar myToolbar = (Toolbar) rootView.findViewById(R.id.tool);
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) rootView.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getString(R.string.title_activity_about));

        imageView = (ImageView) rootView.findViewById(R.id.about_me);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler);
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.mail_me);

        Picasso.get().load(img_me_Twitter).into(imageView);

        mAdapter = new RecyclerAdapter(linkSocialList);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mStaggeredLayoutManager);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
              return;
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getActivity(), "¯\\_(ツ)_/¯",Toast.LENGTH_LONG).show();
            }
        }));

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"droidbotty@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Wall By BottyIvan");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setData();

        return rootView;
    }

    private void setData() {
        linkSocial social;

        social = new linkSocial("GitHub", "BottyIvan", null,"https://github.com/BottyIvan");
        linkSocialList.add(social);

        social = new linkSocial("Google +", "+IvanBotty","https://lh3.googleusercontent.com/-ly967oaFSRI/V59oacX6cJI/AAAAAAAAsog/3e9wqGjuQqIc4Rq6vxYd0R6ptpcTL8cbQCL0B/s1000-fcrop64=1,00000bf0fffff40e/11f8b991-bf0e-4e38-9b82-499e96476920","https://t.co/IZuIDOWuKm");
        linkSocialList.add(social);

        social = new linkSocial("Twitter", "@bottyivan", "https://pbs.twimg.com/profile_banners/237006808/1523493332/1500x500","https://twitter.com/bottyivan");
        linkSocialList.add(social);

        social = new linkSocial("YouTube", "bottydroid", "https://yt3.ggpht.com/hAZoU8lot5uLaSVrGbUo31RLQtA1a1-WrFUbtX3hzgA1vt2PKOzs8Wl9AWtnmLZ0Oec_1g=w2120-fcrop64=1,00005a57ffffa5a8-nd-c0xffffffff-rj-k-no","https://www.youtube.com/channel/UCfeaOwmTsHoj1VUYWNA_gtg");
        linkSocialList.add(social);

        social = new linkSocial("SoundCloud", "Ivan-Botty", "https://pbs.twimg.com/profile_banners/237006808/1523493332/1500x500","https://soundcloud.com/ivan-botty");
        linkSocialList.add(social);

        social = new linkSocial("Instagram", "ivanbotty", "https://scontent-mxp1-1.cdninstagram.com/vp/3c438ab7d5e97834eaf2f51f3410d0a4/5B6843BF/t51.2885-15/e35/26865485_145737819424647_7429988771459760128_n.jpg","https://www.instagram.com/IvanBotty/");
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.setting, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.navigation_item_setting:
                Intent iSetting = new Intent(getActivity(), Settings.class);
                startActivity(iSetting);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

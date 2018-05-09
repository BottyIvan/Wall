package com.botty.wall.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.botty.wall.R;
import com.botty.wall.model.Image;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class ImageFull extends BaseActivity {

    private String TAG = ImageFull.class.getSimpleName();
    private static ArrayList<Image> images;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private static int selectedPosition = 0;
    public ImageFull.SetWallpaperAsyncTask loader;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_full);

        loader = new  ImageFull.SetWallpaperAsyncTask();

        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.pager);

        images = (ArrayList<Image>) getIntent().getExtras().getSerializable("images");
        selectedPosition = getIntent().getExtras().getInt("position");
        Log.e(TAG, "position: " + selectedPosition);
        Log.e(TAG, "images size: " + images.size());


        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

        setMySnackbar(R.string.wallpaper_snack_to_set);

        bottomSheet = findViewById(R.id.framelayout_bottom_sheet);

        AskForWriteSDPermission();

    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
    }

    //  page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            selectedPosition = position;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    //  adapter
    public class MyViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.frame_image, container, false);

            ImageView imageViewPreview = (ImageView) view.findViewById(R.id.imageView);

            final Image image = images.get(position);

            // get the display for improve the memory

            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) ImageFull.this).getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displayMetrics);

            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;

            Picasso.get()
                    .load(image.getPath())
                    .noPlaceholder()
                    .noFade()
                    .resize(width,height)
                    .centerInside()
                    .into(imageViewPreview);

            imageViewPreview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final View bottomSheetLayout = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);
                    (bottomSheetLayout.findViewById(R.id.botton_sheet_home_screen)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            loader.setWallpaper(image.getPath());
                            loader.setType(WallpaperManager.FLAG_SYSTEM);
                            loader.execute();
                            mBottomSheetDialog.dismiss();
                        }
                    });
                    (bottomSheetLayout.findViewById(R.id.botton_sheet_lock_screen)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            loader.setWallpaper(image.getPath());
                            loader.setType(WallpaperManager.FLAG_LOCK);
                            loader.execute();
                            mBottomSheetDialog.dismiss();
                        }
                    });
                    (bottomSheetLayout.findViewById(R.id.bottom_sheet_both_screen)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            loader.setWallpaper(image.getPath());
                            loader.setType(WallpaperManager.FLAG_SYSTEM
                                    | WallpaperManager.FLAG_LOCK);
                            loader.execute();
                            mBottomSheetDialog.dismiss();
                        }
                    });
                    (bottomSheetLayout.findViewById(R.id.bottom_sheet_download_wall)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            IonDownloadMethod(image.getPath(),position);
                            mBottomSheetDialog.dismiss();
                        }
                    });
                    mBottomSheetDialog = new BottomSheetDialog(ImageFull.this);
                    mBottomSheetDialog.setContentView(bottomSheetLayout);
                    mBottomSheetDialog.show();
                    return true;
                }
            });
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private class SetWallpaperAsyncTask extends AsyncTask<String, Void, String> {

        int mWallpaperType;

        @Override
        protected String doInBackground(String... params) {
            Image image = images.get(selectedPosition);
            String URL = image.getPath();
            setWallpaper(URL);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            setMySnackbar(R.string.wallpaper_set);
        }


        public void setType(int type) {
            mWallpaperType = type;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

        @SuppressLint("NewApi")
        public void setWallpaper(String url) {
            try {
                WallpaperManager wpm = WallpaperManager.getInstance(ImageFull.this);
                InputStream ins = new URL(url).openStream();
                wpm.setStream(ins,null,true,mWallpaperType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

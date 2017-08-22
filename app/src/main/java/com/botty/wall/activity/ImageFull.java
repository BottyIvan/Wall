package com.botty.wall.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.botty.wall.R;
import com.botty.wall.model.Image;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;

public class ImageFull extends AppCompatActivity {

    private String TAG = ImageFull.class.getSimpleName();
    private ArrayList<Image> images;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private int selectedPosition = 0;
    private Toolbar myToolbar;
    private ImageFull.SetWallpaperAsyncTask loader;
    private Snackbar mySnackbar;

    //Download Image via Ion
    private ProgressDialog progressDialog;
    private Future<File> downloading;
    private boolean downloaded = false;
    private String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/WallApp";

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private boolean SDPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_full);

        loader = new  ImageFull.SetWallpaperAsyncTask();

        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        viewPager = (ViewPager) findViewById(R.id.pager);

        images = (ArrayList<Image>) getIntent().getExtras().getSerializable("images");
        selectedPosition = getIntent().getExtras().getInt("position");
        Log.e(TAG, "position: " + selectedPosition);
        Log.e(TAG, "images size: " + images.size());


        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

        mySnackbar= Snackbar.make(findViewById(R.id.CoordinatorLayout),
                "Long press to apply wallpaper", Snackbar.LENGTH_SHORT);
        mySnackbar.show();

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
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.frame_image, container, false);

            ImageView imageViewPreview = (ImageView) view.findViewById(R.id.imageView);

            final Image image = images.get(position);

            Glide.with(getApplicationContext()).load(image.getLarge())
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewPreview);

            imageViewPreview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(ImageFull.this);
                    builderSingle.setTitle(getString(R.string.wallpaper_instructions));
                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ImageFull.this,
                            android.R.layout.simple_list_item_1);
                    arrayAdapter.add(getString(R.string.wallpaper_option_home_screen));
                    arrayAdapter.add(getString(R.string.wallpaper_option_lock_screen));
                    arrayAdapter.add(getString(R.string.wallpaper_option_home_screen_and_lock_screen));
                    arrayAdapter.add(getString(R.string.dialog_list_download));
                    builderSingle.setAdapter(
                            arrayAdapter,
                            new DialogInterface.OnClickListener() {
                                @SuppressLint("InlinedApi")
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case 0:
                                            loader.setWallpaper(image.getMedium());
                                            loader.setType(WallpaperManager.FLAG_SYSTEM);
                                            loader.execute();
                                            break;
                                        case 1:
                                            loader.setWallpaper(image.getMedium());
                                            loader.setType(WallpaperManager.FLAG_LOCK);
                                            loader.execute();
                                            break;
                                        case 2:
                                            loader.setWallpaper(image.getMedium());
                                            loader.setType(WallpaperManager.FLAG_SYSTEM
                                                    | WallpaperManager.FLAG_LOCK);
                                            loader.execute();
                                            break;
                                        case 3:
                                            String URL = image.getLarge();
                                            if (downloading != null && !downloading.isCancelled()) {
                                                resetDownload();
                                                return;
                                            }
                                            downloading = Ion.with(getApplicationContext())
                                                    .load(URL)
                                                    // have a ProgressBar get updated automatically with the percent
                                                    // and a ProgressDialog
                                                    .progressDialog(Progress())
                                                    .progress(new ProgressCallback() {
                                                        @Override
                                                        public void onProgress(long downloaded, long total) {
                                                            System.out.println("" + downloaded + " / " + total);
                                                        }
                                                    })
                                                    .write(new File(fullPath, "wall_"+selectedPosition+".jpg"))
                                                    .setCallback(new FutureCallback<File>() {
                                                        @Override
                                                        public void onCompleted(Exception e, File file) {
                                                            // download done...
                                                            // do stuff with the File or error
                                                            progressDialog.dismiss();
                                                            mySnackbar= Snackbar.make(findViewById(R.id.CoordinatorLayout),
                                                                    R.string.toast_info_downloaded, Snackbar.LENGTH_SHORT);
                                                            mySnackbar.show();                                                        }
                                                    });
                                            break;
                                        default:
                                            mySnackbar= Snackbar.make(findViewById(R.id.CoordinatorLayout),
                                                    "No correct option", Snackbar.LENGTH_SHORT);
                                            mySnackbar.show();
                                            break;
                                    }
                                }
                            });
                    builderSingle.show();
                    return false;
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
            String URL = image.getLarge();
            setWallpaper(URL);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            mySnackbar= Snackbar.make(findViewById(R.id.CoordinatorLayout),
                    "Wallpaper set :D", Snackbar.LENGTH_SHORT);
            mySnackbar.show();
        }


        private void setType(int type) {
            mWallpaperType = type;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

        @SuppressLint("NewApi")
        private void setWallpaper(String url) {
            try {
                WallpaperManager wpm = WallpaperManager.getInstance(getApplicationContext());
                InputStream ins = new URL(url).openStream();
                wpm.setStream(ins,null,true,mWallpaperType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void AskForWriteSDPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hasWriteSDPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
                    SDPermission = true;
                    CreateDirectory();
                    mySnackbar= Snackbar.make(findViewById(R.id.CoordinatorLayout),
                            R.string.can_download_wall, Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                } else {
                    // Permission Denied
                    mySnackbar= Snackbar.make(findViewById(R.id.CoordinatorLayout),
                            R.string.cant_download_wall, Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void CreateDirectory(){
        File wallpaperDirectory = new File("/sdcard/WallApp/");
        wallpaperDirectory.mkdirs();
    }

    public ProgressDialog Progress(){
        progressDialog = new ProgressDialog(ImageFull.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("Downloading ...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    void resetDownload() {
        // cancel any pending download
        downloading.cancel();
        downloading = null;
    }

    /**
     * Sets up transparent navigation and status bars in LMP.
     * This method is a no-op for other platform versions.
     */
    @TargetApi(19)
    private void setupTransparentSystemBarsForLmp() {
        // TODO(sansid): use the APIs directly when compiling against L sdk.
        // Currently we use reflection to access the flags and the API to set the transparency
        // on the System bars.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                getWindow().getAttributes().systemUiVisibility |=
                        (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                Field drawsSysBackgroundsField = WindowManager.LayoutParams.class.getField(
                        "FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS");
                getWindow().addFlags(drawsSysBackgroundsField.getInt(null));

                Method setStatusBarColorMethod =
                        Window.class.getDeclaredMethod("setStatusBarColor", int.class);
                Method setNavigationBarColorMethod =
                        Window.class.getDeclaredMethod("setNavigationBarColor", int.class);
                setStatusBarColorMethod.invoke(getWindow(), Color.TRANSPARENT);
                setNavigationBarColorMethod.invoke(getWindow(), Color.TRANSPARENT);
            } catch (NoSuchFieldException e) {
                Log.w(TAG, "NoSuchFieldException while setting up transparent bars");
            } catch (NoSuchMethodException ex) {
                Log.w(TAG, "NoSuchMethodException while setting up transparent bars");
            } catch (IllegalAccessException e) {
                Log.w(TAG, "IllegalAccessException while setting up transparent bars");
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "IllegalArgumentException while setting up transparent bars");
            } catch (InvocationTargetException e) {
                Log.w(TAG, "InvocationTargetException while setting up transparent bars");
            } finally {}
        }
    }
}

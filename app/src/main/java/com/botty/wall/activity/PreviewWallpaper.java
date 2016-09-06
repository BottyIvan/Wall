package com.botty.wall.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.botty.wall.R;
import com.botty.wall.model.Image;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.florent37.picassopalette.PicassoPalette;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class PreviewWallpaper extends AppCompatActivity {

    private String TAG = ImageFull.class.getSimpleName();
    private ArrayList<Image> images;
    private TextView lblCount, lblTitle, lblDate;
    private int selectedPosition = 0;
    private Toolbar myToolbar;
    private FloatingActionButton fab;
    private ImageView myImage;
    private LinearLayout infoL;
    private FrameLayout frameLayout;
    boolean isFrameVis;
    View decorView;

    //Download Image via Ion
    private ProgressDialog progressDialog;
    private Future<File> downloading;
    private boolean downloaded = false;
    private String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/WallApp";

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private boolean SDPermission;

    final private int CROP_RESULT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_wallpaper);

        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        setupTransparentSystemBarsForLmp();

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        myImage = (ImageView) findViewById(R.id.wallpaper);
        lblTitle = (TextView) findViewById(R.id.titleWall);
        lblDate = (TextView) findViewById(R.id.upload_date);
        fab = (FloatingActionButton) findViewById(R.id.setWall);
        infoL = (LinearLayout) findViewById(R.id.infoL);
        frameLayout = (FrameLayout) findViewById(R.id.frame);

        images = (ArrayList<Image>) getIntent().getExtras().getSerializable("images");
        selectedPosition = getIntent().getExtras().getInt("position");
        Log.e(TAG, "position: " + selectedPosition);
        Log.e(TAG, "images size: " + images.size());

        final Image image = images.get(selectedPosition);

        Glide.with(getApplicationContext()).load(image.getLarge())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(myImage);

       /* Picasso.with(getApplicationContext())
                .load(image.getLarge())
                .noFade()
                .into(myImage,
                        PicassoPalette.with(image.getLarge(), myImage)
                                .use(PicassoPalette.Profile.VIBRANT_LIGHT)
                                .intoBackground(infoL)
                                .intoBackground(frameLayout)
                );*/

        setCurrentItem(selectedPosition);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Image image = images.get(selectedPosition);
                new SetWallpaperAsyncTask().execute(image.getLarge());
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(PreviewWallpaper.this);
                builderSingle.setTitle(PreviewWallpaper.this.getString(R.string.dialog_option));
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        PreviewWallpaper.this,
                        android.R.layout.simple_list_item_1);
                arrayAdapter.add(PreviewWallpaper.this.getString(R.string.dialog_list_download));
                builderSingle.setAdapter(
                        arrayAdapter,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Image image = images.get(selectedPosition);
                                switch (which) {
                                    case 0:
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
                                                        Toast.makeText(getApplicationContext(),"Downloaded !!",Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                }
                            }
                        });
                builderSingle.show();
                return false;
            }
        });

      /*  fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_OK);
            }
        });*/


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
                    Toast.makeText(PreviewWallpaper.this, "You can download the wallpaper !", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission Denied
                    Toast.makeText(PreviewWallpaper.this, "You can't download the wallpaper :(", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setCurrentItem(int position) {
        displayMetaInfo(selectedPosition);
    }

    private void displayMetaInfo(int position) {
        //lblCount.setText((position + 1) + " of " + images.size());

        Image image = images.get(position);
        lblTitle.setText(image.getName());
        lblDate.setText(image.getTimestamp());
    }

    private class SetWallpaperAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Image image = images.get(selectedPosition);
            String URL = image.getLarge();
            setWallpaper(URL);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            revealFrame();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

        private void setWallpaper(String url) {
            try {
                WallpaperManager wpm = WallpaperManager.getInstance(getApplicationContext());
                InputStream ins = new URL(url).openStream();
                wpm.setStream(ins);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void revealFrame(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int cx = frameLayout.getRight() - 30;
            int cy = frameLayout.getBottom() - 60;
            int finalRadius = Math.max(frameLayout.getWidth(), frameLayout.getHeight());
            Animator anim = ViewAnimationUtils.createCircularReveal(frameLayout, cx, cy, 0, finalRadius);
            frameLayout.setVisibility(View.VISIBLE);
            isFrameVis = true;
            anim.start();
        }
    }

    private void hideFrame(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int cx = frameLayout.getRight() - 30;
            int cy = frameLayout.getBottom() - 60;
            int initialRadius = frameLayout.getWidth();
            Animator anim = ViewAnimationUtils.createCircularReveal(frameLayout, cx, cy, initialRadius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    frameLayout.setVisibility(View.INVISIBLE);
                }
            });
            isFrameVis = false;
            anim.start();
        }
    }

    @Override
    public void onBackPressed() {
        if (isFrameVis){
            hideFrame();
        } else super.onBackPressed();
    }

    public void CreateDirectory(){
        File wallpaperDirectory = new File("/sdcard/WallApp/");
        wallpaperDirectory.mkdirs();
    }

    public ProgressDialog Progress(){
        progressDialog = new ProgressDialog(PreviewWallpaper.this);
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
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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

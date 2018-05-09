package com.botty.wall.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.botty.wall.R;
import com.botty.wall.app.PrefManager;
import com.botty.wall.util.Util;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import java.io.File;
import java.util.ArrayList;

abstract class BaseActivity extends AppCompatActivity {

    protected ProgressDialog mProgressDialog;
    protected Snackbar mySnackbar;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    //Download Image via Ion
    protected Future<File> downloading;
    protected boolean downloaded = false;
    protected String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/WallApp";

    protected Toolbar toolbar;
    protected RecyclerView recyclerView;
    protected BottomNavigationView bottomNavigationView;
    protected SwipeRefreshLayout refreshLayout;
    protected StaggeredGridLayoutManager mStaggeredLayoutManager;
    protected BottomSheetBehavior behavior;
    protected BottomSheetDialog mBottomSheetDialog;
    protected View bottomSheet;

    // preference
    protected PrefManager prefManager;
    protected SharedPreferences preferences;

    protected  Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, true);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (settings.getBoolean("nightmode", true)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        /**
         * Ask for Write SD
         */
        try {
            AskForWriteSDPermission();
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println(e);
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected static void centerToolbarTitle(final Toolbar toolbar,boolean icon) {
        final CharSequence title = toolbar.getTitle();
        final ArrayList<View> outViews = new ArrayList<>(1);
        toolbar.findViewsWithText(outViews, title, View.FIND_VIEWS_WITH_TEXT);
        if (!outViews.isEmpty()) {
            final TextView titleView = (TextView) outViews.get(0);
            titleView.setGravity(Gravity.CENTER_HORIZONTAL);
            final Toolbar.LayoutParams layoutParams = (Toolbar.LayoutParams) titleView.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            if (icon) {
                layoutParams.setMargins(0, 0, 60, 0);
            } else {
                layoutParams.setMargins(0, 0, 0, 0);
            }
            toolbar.requestLayout();
        }
    }

    /**
     *  Start method for asking the permission to write SD card
     */
    protected void AskForWriteSDPermission() {
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
                    prefManager = new PrefManager(this);
                    prefManager.setCanWriteSd(true);;
                    CreateDirectory();
                    setMySnackbar(R.string.can_download_wall);
                    Toast.makeText(this, R.string.can_download_wall, Toast.LENGTH_SHORT)
                            .show();
                } else {
                    // Permission Denied
                    setMySnackbar(R.string.cant_download_wall);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void CreateDirectory(){
        File wallpaperDirectory = new File("/sdcard/WallApp/");
        wallpaperDirectory.mkdirs();
    }

    // End asking permission for SD card

    /*
     * UI and Method for download wallpaper With Ion
     */
    public void IonDownloadMethod(String mURL,int position){

        String URL = mURL;
        if (downloading != null && !downloading.isCancelled()) {
            resetDownload();
            return;
        }

        downloading = Ion.with(this)
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
                .write(new File(fullPath, "wall_"+position+".jpg"))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        // download done...
                        // do stuff with the File or error
                        mProgressDialog.dismiss();
                        //setMySnackbar(R.string.toast_info_downloaded);
                    }
                });
        return;

    }

    public ProgressDialog Progress(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMessage("Downloading ...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        return mProgressDialog;
    }

    void resetDownload() {
        // cancel any pending download
        downloading.cancel();
        downloading = null;
    }

    // end  for UI Ion Method

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            finish();

        } else if (itemId == R.id.navigation_item_setting) {
            startActivity(new Intent(this,Settings.class));
        }  else if (itemId == R.id.navigation_item_about){
            startActivity(new Intent(this,About.class));
        }
        /*else if (itemId == R.id.menu_search) {
            onSearchRequested();

        }*/

        return super.onOptionsItemSelected(item);
    }

    protected void showProgress(String msg) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            dismissProgress();

        mProgressDialog = ProgressDialog.show(this, getResources().getString(R.string.app_name), msg);
    }

    protected void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void setMySnackbar(int message) {
        mySnackbar = Snackbar.make(findViewById(R.id.root_view),
                getString(message), Snackbar.LENGTH_SHORT);
        TextView mainTextView = (mySnackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
        TextView actionTextView = (mySnackbar.getView()).findViewById(android.support.design.R.id.snackbar_action);
        // To Apply Custom Fonts for Message and Action
        Typeface robotomono_regular = ResourcesCompat.getFont(getApplicationContext(), R.font.robotomono_regular);
        Typeface robotomono_bold = ResourcesCompat.getFont(getApplicationContext(), R.font.robotomono_bold);
        mainTextView.setTypeface(robotomono_regular);
        actionTextView.setTypeface(robotomono_bold);
        mySnackbar.show();
    }

    protected void showAlert(String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.app_name))
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    protected void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    protected void runLayoutAnimationRight(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_slide_right);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    protected void getFitsSystmeUI(){
        //setting window insets manually
        final ViewGroup rootView = findViewById(R.id.root_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            rootView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
                public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {
                    // clear this listener so insets aren't re-applied
                    rootView.setOnApplyWindowInsetsListener(null);
                    Log.d("MainActivity", "onApplyWindowInsets()"
                            + "[" + insets.getSystemWindowInsetLeft() + ", " +
                            insets.getSystemWindowInsetTop() + ", " +
                            insets.getSystemWindowInsetRight() + ", " +
                            insets.getSystemWindowInsetBottom() + "]");

                    try {
                        toolbar.setPadding(toolbar.getPaddingStart(),
                                toolbar.getPaddingTop() + insets.getSystemWindowInsetTop(),
                                toolbar.getPaddingEnd(),
                                toolbar.getPaddingBottom());

                        ViewGroup.MarginLayoutParams toolbarParams
                                = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                        toolbarParams.leftMargin = insets.getSystemWindowInsetLeft();
                        toolbarParams.rightMargin = insets.getSystemWindowInsetRight();
                        toolbar.setLayoutParams(toolbarParams);
                    } catch (Exception e) {
                        System.out.print("no toolbar and error: " + e);
                    }

                    try {
                        recyclerView.setPadding(recyclerView.getPaddingStart() + insets.getSystemWindowInsetLeft(),
                                recyclerView.getPaddingTop() + insets.getSystemWindowInsetTop(),
                                recyclerView.getPaddingEnd() + insets.getSystemWindowInsetRight(),
                                recyclerView.getPaddingBottom() + insets.getSystemWindowInsetBottom());
                    }catch (Exception e){
                        System.out.print("no recyclerView and error: "+e);
                    } finally {
                        try {
                            getAddOnScrollRecycler();
                        }catch (Exception e){
                            System.out.print("no recyclerView and error: "+e);
                        }
                    }

                    try {
                        bottomNavigationView.setPadding(bottomNavigationView.getPaddingStart(),
                                bottomNavigationView.getPaddingTop(),
                                bottomNavigationView.getPaddingEnd(),
                                bottomNavigationView.getPaddingBottom() + insets.getSystemWindowInsetBottom());

                        ViewGroup.MarginLayoutParams navParams
                                = (ViewGroup.MarginLayoutParams) bottomNavigationView.getLayoutParams();
                        navParams.leftMargin = insets.getSystemWindowInsetLeft();
                        navParams.rightMargin = insets.getSystemWindowInsetRight();
                        bottomNavigationView.setLayoutParams(navParams);
                    }catch (Exception e){
                        System.out.print("no bottomNavigationView and error: "+e);
                    }

                    return insets.consumeSystemWindowInsets();
                }
            });
        } else {
            rootView.getViewTreeObserver()
                    .addOnGlobalLayoutListener(
                            new ViewTreeObserver.OnGlobalLayoutListener() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                                @Override
                                public void onGlobalLayout() {
                                    rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                    // hacky way of getting window insets on pre-Lollipop
                                    // somewhat works...
                                    int[] screenSize = Util.getScreenSize(activity);

                                    int[] windowInsets = new int[]{
                                            Math.abs(screenSize[0] - rootView.getLeft()),
                                            Math.abs(screenSize[1] - rootView.getTop()),
                                            Math.abs(screenSize[2] - rootView.getRight()),
                                            Math.abs(screenSize[3] - rootView.getBottom())};
                                    try {
                                        toolbar.setPadding(toolbar.getPaddingStart(),
                                                toolbar.getPaddingTop() + windowInsets[1],
                                                toolbar.getPaddingEnd(),
                                                toolbar.getPaddingBottom());

                                        ViewGroup.MarginLayoutParams toolbarParams
                                                = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
                                        toolbarParams.leftMargin += windowInsets[0];
                                        toolbarParams.rightMargin += windowInsets[2];
                                        toolbar.setLayoutParams(toolbarParams);
                                    }catch (Exception e){
                                        System.out.print("no toobar and error: "+e);
                                    }

                                    try {
                                        recyclerView.setPadding(recyclerView.getPaddingStart() + windowInsets[0],
                                                recyclerView.getPaddingTop() + windowInsets[1],
                                                recyclerView.getPaddingEnd() + windowInsets[2],
                                                recyclerView.getPaddingBottom() + windowInsets[3]);
                                    }catch (Exception e){
                                        System.out.print("no recyclerView and error: "+e);
                                    } finally {
                                        try {
                                            getAddOnScrollRecycler();
                                        }catch (Exception e){
                                            System.out.print("no recyclerView and error: "+e);
                                        }
                                    }

                                    try {
                                        bottomNavigationView.setPadding(bottomNavigationView.getPaddingStart(),
                                                bottomNavigationView.getPaddingTop(),
                                                bottomNavigationView.getPaddingEnd(),
                                                bottomNavigationView.getPaddingBottom() + windowInsets[3]);

                                        ViewGroup.MarginLayoutParams navParams
                                                = (ViewGroup.MarginLayoutParams) bottomNavigationView.getLayoutParams();
                                        navParams.leftMargin += windowInsets[1];
                                        navParams.rightMargin += windowInsets[2];
                                        bottomNavigationView.setLayoutParams(navParams);
                                    }catch (Exception e){
                                        System.out.print("no bottomNavigationView and error: "+e);
                                    }

                                }
                            });
        }
    }

    protected void getAddOnScrollRecycler(){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @SuppressLint("ResourceAsColor")
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                //hiding toolbar on scroll
                float translationY = toolbar.getTranslationY() - dy;
                if (-translationY > toolbar.getHeight()) {
                    translationY = -toolbar.getHeight();
                    toolbar.setActivated(true);
                    toolbar.setElevation(5);
                } else if (translationY > 0) {
                    translationY = 0;
                    if (!recyclerView.canScrollVertically(-1)) {
                        toolbar.setActivated(false);
                        toolbar.setElevation(0);
                    }
                }
                toolbar.setTranslationY(translationY);
                try {
                    bottomNavigationView.setTranslationY(-(translationY * 2));
                }catch (Exception e){
                    System.out.print("no bottomNavigationView and error: "+e);
                }

            }
        });
    }

}

package com.botty.wall.activity;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.botty.wall.R;
import com.botty.wall.adapter.GalleryAdapter;
import com.botty.wall.app.AppController;
import com.botty.wall.model.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class Home extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String endpoint = "http://www.gnexushd.altervista.org/beta/wall/wall.json";

    private String TAG = Home.class.getSimpleName();

    private boolean tabletSize;

    private ArrayList<Image> images;

    private GalleryAdapter mAdapter;

    private File[] listFile;

    private SetWallpaperAsyncTask loader;

    private static int selectedPosition = 0;

    private boolean isListView;
    private int layout_row = 1;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tabletSize = getResources().getBoolean(R.bool.isTablet);

        if (tabletSize) {
            // do something
            System.out.print("Is tablet");
        } else {
            // do something else
            // Initializing Drawer Layout and ActionBarToggle
            BottomNavUI();
        }

        bottomNavigationView.setSelectedItemId(R.id.navigation_item_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        refreshLayout = findViewById(R.id.refresh_l);

        images = new ArrayList<>();

        mAdapter = new GalleryAdapter(images, this);

        recyclerView = findViewById(R.id.recycler_view);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(layout_row, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mStaggeredLayoutManager);
        recyclerView.setAdapter(mAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bottomSheet = findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(Home.this);

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(this, recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);
                Intent i = new Intent(Home.this,ImageFull.class);
                i.putExtras(bundle);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(Home.this, view.findViewById(R.id.thumbnail), getString(R.string.transition_thumb));
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, final int position) {
                final Image image = images.get(position);

                if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

                final View bottomSheetLayout = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);
                (bottomSheetLayout.findViewById(R.id.botton_sheet_home_screen)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loader.setWallpaper(image.getLarge());
                        loader.setType(WallpaperManager.FLAG_SYSTEM);
                        loader.execute();
                        mBottomSheetDialog.dismiss();
                    }
                });
                (bottomSheetLayout.findViewById(R.id.botton_sheet_lock_screen)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loader.setWallpaper(image.getLarge());
                        loader.setType(WallpaperManager.FLAG_LOCK);
                        loader.execute();
                        mBottomSheetDialog.dismiss();
                    }
                });
                (bottomSheetLayout.findViewById(R.id.bottom_sheet_both_screen)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loader.setWallpaper(image.getLarge());
                        loader.setType(WallpaperManager.FLAG_SYSTEM
                                | WallpaperManager.FLAG_LOCK);
                        loader.execute();
                        mBottomSheetDialog.dismiss();
                    }
                });
                (bottomSheetLayout.findViewById(R.id.bottom_sheet_download_wall)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        IonDownloadMethod(image.getLarge(),position);
                        mBottomSheetDialog.dismiss();
                    }
                });
                mBottomSheetDialog = new BottomSheetDialog(getApplicationContext());
                mBottomSheetDialog.setContentView(bottomSheetLayout);
                mBottomSheetDialog.show();
                return;

            }

        }));

        refreshLayout.setOnRefreshListener(this);

        setMySnackbar(R.string.wallpaper_snack_to_set);

        getFitsSystmeUI();

    }

    @Override
    public void onRefresh() {
        if (bottomNavigationView.getSelectedItemId() == R.id.navigation_item_home)
            fetchImages();
        else if (bottomNavigationView.getSelectedItemId() == R.id.navigation_item_local){
            getFromSdcard();
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
                WallpaperManager wpm = WallpaperManager.getInstance(getApplicationContext());
                InputStream ins = new URL(url).openStream();
                wpm.setStream(ins,null,true,mWallpaperType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void fetchImages() {

        try {
            refreshLayout.setRefreshing(true);
        }catch (Exception e){

        }

        JsonArrayRequest req = new JsonArrayRequest(endpoint,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        refreshLayout.setRefreshing(false);

                        images.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                Image image = new Image();
                                image.setName(object.getString("name"));

                                JSONObject url = object.getJSONObject("url");
                                image.setPath(url.getString("large"));
                                image.setTimestamp(object.getString("timestamp"));

                                images.add(image);

                            } catch (JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                refreshLayout.setRefreshing(false);
            }
        });


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
    }

    public void getFromSdcard() {
        images.clear();
        File file= new File(fullPath);
        if (file.isDirectory()) {
            listFile = file.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                Image image = new Image();
                image.setName(listFile[i].getName());
                image.setPath("file://"+listFile[i].getAbsolutePath());
                System.out.println("file://"+listFile[i].getAbsolutePath());
                images.add(image);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void BottomNavUI(){
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                //Check to see which item was being clicked and perform appropriate action
                switch (item.getItemId()) {

                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.navigation_item_home:
                        fetchImages();
                        return true;
                    case R.id.navigation_item_local:
                        if (SDPermission) {
                            getFromSdcard();
                            System.out.print("enabled write sd: "+SDPermission);
                        } else {
                            setMySnackbar(R.string.allow_the_app_to_read_and_write_memory);
                            mySnackbar.setAction("Alright", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AskForWriteSDPermission();
                                }
                            });
                            System.out.print("enabled write sd: "+SDPermission);
                        }
                        return true;
                    case R.id.navigation_item_about:
                        startActivity(new Intent(getApplicationContext(),About.class));
                        return true;
                    default:
                        break;
                }


                return false;
            }
        });
    }
}

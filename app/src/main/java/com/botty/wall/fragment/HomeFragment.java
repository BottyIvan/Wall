package com.botty.wall.fragment;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.botty.wall.R;
import com.botty.wall.activity.Home;
import com.botty.wall.activity.ImageFull;
import com.botty.wall.adapter.GalleryAdapter;
import com.botty.wall.app.AppController;
import com.botty.wall.model.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by BottyIvan on 07/08/16.
 */
public class HomeFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private String TAG = Home.class.getSimpleName();

    private static final String endpoint = "http://www.gnexushd.altervista.org/beta/wall/wall.json";

    // View
    private Toolbar toolbar;
    private SwipeRefreshLayout refreshLayout;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private RecyclerView recyclerView;
    private BottomNavigationView bottomNavigationView;

    private ArrayList<Image> images;

    private GalleryAdapter mAdapter;

    private SetWallpaperAsyncTask loader;

    private static int selectedPosition = 0;

    private boolean isListView;
    private int layout_row = 1;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }

        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, true);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (settings.getBoolean("tworow", true)) {
            layout_row = 2;
            isListView = true;
        } else {
            layout_row = 1;
            isListView = false;
        }

        loader = new SetWallpaperAsyncTask();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.content_main, container, false);

        toolbar = rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);


        refreshLayout = rootView.findViewById(R.id.refresh_l);

        images = new ArrayList<>();

        mAdapter = new GalleryAdapter(images, getActivity());

        recyclerView = rootView.findViewById(R.id.recycler_view);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(layout_row, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mStaggeredLayoutManager);
        recyclerView.setAdapter(mAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        bottomSheet = rootView.findViewById(R.id.framelayout_bottom_sheet);

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getActivity(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);
                Intent i = new Intent(getActivity(),ImageFull.class);
                i.putExtras(bundle);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(getActivity(), view.findViewById(R.id.thumbnail), getString(R.string.transition_thumb));
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, final int position) {
                final Image image = images.get(position);

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
                mBottomSheetDialog = new BottomSheetDialog(getActivity());
                mBottomSheetDialog.setContentView(bottomSheetLayout);
                mBottomSheetDialog.show();
                return;

            }

        }));

        refreshLayout.setOnRefreshListener(this);

        setMySnackbar(R.string.wallpaper_snack_to_set);

        fetchImages();

        return rootView;
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
                WallpaperManager wpm = WallpaperManager.getInstance(getActivity());
                InputStream ins = new URL(url).openStream();
                wpm.setStream(ins,null,true,mWallpaperType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void fetchImages() {

        refreshLayout.setRefreshing(true);

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
                                image.setSmall(url.getString("small"));
                                image.setMedium(url.getString("medium"));
                                image.setLarge(url.getString("large"));
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

    @Override
    public void onRefresh() {
        fetchImages();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.layout_manager, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.one_row:
                if (isListView) {
                    mStaggeredLayoutManager.setSpanCount(1);
                    //      item.setIcon(R.drawable.ic_action_list);
                    //      item.setTitle("Show as list");
                    isListView = false;
                }
                return true;
            case R.id.two_row:
                if (!isListView) {
                    mStaggeredLayoutManager.setSpanCount(2);
                    //    item.setIcon(R.drawable.ic_action_grid);
                    //   item.setTitle("Show as grid");
                    isListView = true;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

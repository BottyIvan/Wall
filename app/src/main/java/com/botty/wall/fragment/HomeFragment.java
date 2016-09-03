package com.botty.wall.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.botty.wall.R;
import com.botty.wall.activity.Home;
import com.botty.wall.activity.ImageFull;
import com.botty.wall.activity.PreviewWallpaper;
import com.botty.wall.adapter.GalleryAdapter;
import com.botty.wall.app.AppController;
import com.botty.wall.model.Image;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by BottyIvan on 07/08/16.
 */
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private String TAG = Home.class.getSimpleName();
    private static final String endpoint = "http://www.gnexushd.altervista.org/beta/wall/wall.json";
    private ArrayList<Image> images;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private SwipeRefreshLayout refreshLayout;
    private boolean isListView;
    private Toolbar toolbar;
    private int pos = 0;
    private int layout_row = 1;

    //Download Image via Ion
    private ProgressDialog progressDialog;
    private Future<File> downloading;
    private boolean downloaded = false;
    private String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/WallApp";
    private String directoryName = "WallApp";

    private static boolean ACTIVITY_WALL_SWIPE = false;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, true);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (settings.getBoolean("tworow", true)) {
            layout_row = 2;
            isListView = true;
        } else {
            layout_row = 1;
            isListView = false;
        }

        if (settings.getBoolean("swipe_ui_wall_act", true)) {
            ACTIVITY_WALL_SWIPE = true;
        } else {
            ACTIVITY_WALL_SWIPE = false;
        }

        settings.getString("directory",directoryName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_main, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_l);

        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getActivity(), images);

        mStaggeredLayoutManager = new StaggeredGridLayoutManager(layout_row, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mStaggeredLayoutManager);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getActivity(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);
                Intent i = null;
                if (ACTIVITY_WALL_SWIPE)
                    i = new Intent(getActivity(),ImageFull.class);
                else
                    i = new Intent(getActivity(),PreviewWallpaper.class);
                i.putExtras(bundle);
                String wall = getString(R.string.transition_wall);
                String title = getString(R.string.transition_title);
                String back = getString(R.string.transition_back);
                ImageView placeImage = (ImageView) view.findViewById(R.id.thumbnail);
                TextView textView = (TextView) view.findViewById(R.id.placeName);
                LinearLayout layout = (LinearLayout) view.findViewById(R.id.placeNameHolder);
                Pair<View, String> imagePair = Pair.create((View) placeImage, wall);
                Pair<View, String> titlePair = Pair.create((View) textView, title);
                Pair<View, String> backPair = Pair.create((View) layout, back);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), imagePair,titlePair,backPair);
                ActivityCompat.startActivity(getActivity(), i, options.toBundle());
            }

            @Override
            public void onLongClick(View view, final int position) {
                pos = position;
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                builderSingle.setTitle(getActivity().getString(R.string.dialog_option));
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                        getActivity(),
                        android.R.layout.simple_list_item_1);
                arrayAdapter.add(getActivity().getString(R.string.dialog_list_set_wall));
                arrayAdapter.add(getActivity().getString(R.string.dialog_list_download));
                builderSingle.setAdapter(
                        arrayAdapter,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Image image = images.get(position);
                                switch (which){
                                    case 0:
                                        new SetWallpaperAsyncTask().execute(image.getLarge());
                                        break;
                                    case 1:
                                        String URL = image.getLarge();
                                        if (downloading != null && !downloading.isCancelled()) {
                                            resetDownload();
                                            return;
                                        }
                                        downloading = Ion.with(getActivity())
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
                                                .write(new File(fullPath, "wall_"+pos+".jpg"))
                                              //  .write(new File("sdcard/WallApp/wall_"+pos+".jpg"))
                                                .setCallback(new FutureCallback<File>() {
                                                    @Override
                                                    public void onCompleted(Exception e, File file) {
                                                        // download done...
                                                        // do stuff with the File or error
                                                        progressDialog.dismiss();
                                                        Toast.makeText(getActivity(), R.string.toast_info_downloaded,Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                }
                            }
                        });
                builderSingle.show();
            }
        }));

        refreshLayout.setOnRefreshListener(this);


        fetchImages();

        // Inflate the layout for this fragment
        return rootView;
    }

    public ProgressDialog Progress(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage(getActivity().getString(R.string.dialog_downloading));
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    void resetDownload() {
        // cancel any pending download
        downloading.cancel();
        downloading = null;
    }

    public void CreateDirectory(){
        File wallpaperDirectory = new File(fullPath+File.pathSeparator+directoryName);
        wallpaperDirectory.mkdirs();
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

    private class SetWallpaperAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Image image = images.get(pos);
            String URL = image.getLarge();
            setWallpaper(URL);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity(), R.string.toast_info_set_wall,Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

        private void setWallpaper(String url) {
            try {
                WallpaperManager wpm = WallpaperManager.getInstance(getActivity());
                InputStream ins = new URL(url).openStream();
                wpm.setStream(ins);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

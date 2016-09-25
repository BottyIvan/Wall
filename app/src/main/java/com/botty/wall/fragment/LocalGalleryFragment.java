package com.botty.wall.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.botty.wall.R;
import com.botty.wall.adapter.GalleryLocalAdapter;
import com.botty.wall.adapter.GridViewAdapter;
import com.botty.wall.model.ImageLocal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BottyIvan on 07/08/16.
 */
public class LocalGalleryFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<ImageLocal> imageLocalList = new ArrayList<>();
    private GalleryLocalAdapter mAdapter;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private Cursor mediaCursor;
    private String[] FilePathStrings;
    private String[] FileNameStrings;
    private File[] listFile;
    GridView grid;
    GridViewAdapter adapter;
    File file;

    private LinearLayout noWallLayout;
    private TextView info_no_wall;
    private boolean isSDPermission;

    public LocalGalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gallery_local, container, false);

        // Locate the GridView in gridview_main.xml
        grid = (GridView) rootView.findViewById(R.id.gridview);

        noWallLayout = (LinearLayout) rootView.findViewById(R.id.no_walls);

        try {

            // Check for SD Card
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                Toast.makeText(getActivity(), "Error! No SDCARD Found!", Toast.LENGTH_LONG)
                        .show();
            } else {
                // Locate the image folder in your SD Card
                file = new File(Environment.getExternalStorageDirectory()
                        + File.separator + "WallApp");
                // Create a new folder if no folder named SDImageTutorial exist
                file.mkdirs();
            }

            if (file.isDirectory()) {
                listFile = file.listFiles();
                // Create a String array for FilePathStrings
                FilePathStrings = new String[listFile.length];
                // Create a String array for FileNameStrings
                FileNameStrings = new String[listFile.length];

                for (int i = 0; i < listFile.length; i++) {
                    // Get the path of the image file
                    FilePathStrings[i] = listFile[i].getAbsolutePath();
                    // Get the name image file
                    FileNameStrings[i] = listFile[i].getName();
                }
            }

            if (listFile.length == 0){
                grid.setVisibility(View.GONE);
                noWallLayout.setVisibility(View.VISIBLE);
            }

            // Pass String arrays to LazyAdapter Class
            adapter = new GridViewAdapter(getActivity(), FilePathStrings, FileNameStrings);
            // Set the LazyAdapter to the GridView
            grid.setAdapter(adapter);

        } catch (Exception e){
            grid.setVisibility(View.GONE);
            noWallLayout.setVisibility(View.VISIBLE);
            System.out.print(e);
        }

        /*
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mAdapter = new GalleryLocalAdapter(imageLocalList);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mStaggeredLayoutManager);
        recyclerView.setAdapter(mAdapter);*/

        //loadMore();

        return rootView;
    }

    public void loadMore() {
        if (mediaCursor == null) {
            mediaCursor = getActivity().managedQuery(
                    MediaStore.Files.getContentUri("external"),
                    null,
                    null,
                    null,
                    null);
        }

        int loaded = 0;
        while (mediaCursor.moveToNext() && loaded < 10) {
            int mediaType = mediaCursor.getInt(mediaCursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
            if (mediaType != MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                continue;
            }

            loaded++;

            String uri = mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            File file = new File(uri);
            // turn this into a file uri if necessary/possible
            if (file.exists()) {
                ImageLocal imageLocal = new ImageLocal(file.toURI().toString());
                imageLocalList.add(imageLocal);
            } else {
                ImageLocal imageLocal = new ImageLocal(uri);
                imageLocalList.add(imageLocal);
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
}
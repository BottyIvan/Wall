package com.botty.wall.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.botty.wall.R;
import com.botty.wall.adapter.GalleryLocalAdapter;
import com.botty.wall.model.ImageLocal;

import java.io.File;
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
        View rootView = inflater.inflate(R.layout.content_main, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mAdapter = new GalleryLocalAdapter(imageLocalList);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mStaggeredLayoutManager);
        recyclerView.setAdapter(mAdapter);

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
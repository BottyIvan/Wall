package com.botty.wall.fragment;

import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.botty.wall.R;
import com.botty.wall.adapter.LibsAdapter;
import com.botty.wall.model.libs_model;

import java.util.ArrayList;
import java.util.List;

public class LibsFragment extends BaseFragment {

    private LibsAdapter libsAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private List<libs_model> libs_models = new ArrayList<>();

    public LibsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.recycler_view, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view);

        libsAdapter = new LibsAdapter(libs_models);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(libsAdapter);

        loadLink();

        return rootView;
    }

    public void loadLink() {
        libs_model libsModel;

        libsModel = new libs_model("Android Support Library", "https://developer.android.com/topic/libraries/support-library/", "https://raw.githubusercontent.com/github/explore/6c6508f34230f0ac0d49e847a326429eefbfc030/topics/android/android.png", "The Android Open Source Project");
        libs_models.add(libsModel);

        libsModel = new libs_model("Ion", "https://github.com/koush/ion", "https://avatars3.githubusercontent.com/u/73924?s=400&v=4", "koush");
        libs_models.add(libsModel);

        libsModel = new libs_model("AndroidAsync", "https://github.com/koush/AndroidAsync", "https://avatars3.githubusercontent.com/u/73924?s=400&v=4", "koush");
        libs_models.add(libsModel);

        libsModel = new libs_model("Volley", "https://github.com/google/volley", "https://avatars2.githubusercontent.com/u/1342004?s=200&v=4", "google");
        libs_models.add(libsModel);

        libsModel = new libs_model("Picasso", "https://github.com/square/picasso", "https://avatars0.githubusercontent.com/u/82592?s=200&v=4", "square");
        libs_models.add(libsModel);

        libsModel = new libs_model("InkPageIndicator", "https://github.com/DavidPacioianu/InkPageIndicator", "https://avatars0.githubusercontent.com/u/10657845?s=400&v=4", "DavidPacioianu");
        libs_models.add(libsModel);

        libsAdapter.notifyDataSetChanged();
    }
}

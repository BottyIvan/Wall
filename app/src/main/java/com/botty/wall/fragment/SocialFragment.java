package com.botty.wall.fragment;

import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.botty.wall.R;
import com.botty.wall.adapter.SocialAdapter;
import com.botty.wall.model.linkSocial;

import java.util.ArrayList;
import java.util.List;

public class SocialFragment extends BaseFragment {

    private SocialAdapter listLibsAdapter;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private List<linkSocial> linkSocials = new ArrayList<>();

    public SocialFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.recycler_view, container, false);

        recyclerView = rootView.findViewById(R.id.recycler_view);

        listLibsAdapter = new SocialAdapter(linkSocials);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(listLibsAdapter);

        loadLink();

        return rootView;
    }

    public void loadLink() {
        linkSocial social;

        social = new linkSocial("GitHub", "BottyIvan", null, "https://github.com/BottyIvan");
        linkSocials.add(social);

        social = new linkSocial("Google +", "+IvanBotty", "https://lh3.googleusercontent.com/-ly967oaFSRI/V59oacX6cJI/AAAAAAAAsog/3e9wqGjuQqIc4Rq6vxYd0R6ptpcTL8cbQCL0B/s1000-fcrop64=1,00000bf0fffff40e/11f8b991-bf0e-4e38-9b82-499e96476920", "https://t.co/IZuIDOWuKm");
        linkSocials.add(social);

        social = new linkSocial("Twitter", "@bottyivan", "https://pbs.twimg.com/profile_banners/237006808/1523493332/1500x500", "https://twitter.com/bottyivan");
        linkSocials.add(social);

        social = new linkSocial("YouTube", "bottydroid", "https://yt3.ggpht.com/hAZoU8lot5uLaSVrGbUo31RLQtA1a1-WrFUbtX3hzgA1vt2PKOzs8Wl9AWtnmLZ0Oec_1g=w2120-fcrop64=1,00005a57ffffa5a8-nd-c0xffffffff-rj-k-no", "https://www.youtube.com/channel/UCfeaOwmTsHoj1VUYWNA_gtg");
        linkSocials.add(social);

        social = new linkSocial("SoundCloud", "Ivan-Botty", "https://pbs.twimg.com/profile_banners/237006808/1523493332/1500x500", "https://soundcloud.com/ivan-botty");
        linkSocials.add(social);

        social = new linkSocial("Instagram", "ivanbotty", "https://scontent-mxp1-1.cdninstagram.com/vp/3c438ab7d5e97834eaf2f51f3410d0a4/5B6843BF/t51.2885-15/e35/26865485_145737819424647_7429988771459760128_n.jpg", "https://www.instagram.com/IvanBotty/");
        linkSocials.add(social);

        listLibsAdapter.notifyDataSetChanged();
    }
}

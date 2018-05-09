package com.botty.wall.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.botty.wall.R;
import com.botty.wall.model.linkSocial;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by BottyIvan on 20/06/16.
 */
public class SocialAdapter extends RecyclerView.Adapter<SocialAdapter.MyViewHolder>{

    private List<linkSocial> linkSocialList;
    public Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView sName;
        public Button sLink;
        public ImageView sLogo;

        public MyViewHolder(View view) {
            super(view);
            sName = view.findViewById(R.id.social_title);
            sLink = view.findViewById(R.id.social_link);
            sLogo = view.findViewById(R.id.social_logo);
        }
    }

    public SocialAdapter(List<linkSocial> linkSocialList){
        this.linkSocialList = linkSocialList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (context != null)
            return;
        final linkSocial linkSocial = linkSocialList.get(position);
        holder.sName.setText(linkSocial.getNomeSocial());
        holder.sLink.setText(linkSocial.getLinkSocial());

        if (linkSocial.getUrl() != null){
            Picasso.get()
                    .load(linkSocial.getUrl())
                    .noFade()
                    .into(holder.sLogo);
        } else {
            holder.sLogo.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return linkSocialList.size();
    }
}
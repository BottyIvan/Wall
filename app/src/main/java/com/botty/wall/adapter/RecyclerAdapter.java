package com.botty.wall.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.botty.wall.R;
import com.botty.wall.model.linkSocial;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by BottyIvan on 20/06/16.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{

    private List<linkSocial> linkSocialList;
    private Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView sName, sLink;
        public ImageView sLogo;

        public MyViewHolder(View view) {
            super(view);
            sName = (TextView) view.findViewById(R.id.social_title);
            sLink = (TextView) view.findViewById(R.id.social_link);
            sLogo = (ImageView) view.findViewById(R.id.social_logo);
        }
    }

    public RecyclerAdapter(List<linkSocial> linkSocialList){
        this.linkSocialList = linkSocialList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        linkSocial linkSocial = linkSocialList.get(position);
        holder.sName.setText(linkSocial.getNomeSocial());
        holder.sLink.setText(linkSocial.getLinkSocial());
        Picasso.with(context)
                .load(linkSocial.getUrl())
                .into(holder.sLogo);
    }


    @Override
    public int getItemCount() {
        return linkSocialList.size();
    }
}
package com.botty.wall.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.botty.wall.R;
import com.botty.wall.model.linkSocial;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by BottyIvan on 20/06/16.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{

    private List<linkSocial> linkSocialList;
    public Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView sName;
        public Button sLink;
        public ImageView sLogo;

        public MyViewHolder(View view) {
            super(view);
            sName = (TextView) view.findViewById(R.id.social_title);
            sLink = (Button) view.findViewById(R.id.social_link);
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
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (context != null)
            return;
        final linkSocial linkSocial = linkSocialList.get(position);
        holder.sName.setText(linkSocial.getNomeSocial());
        holder.sLink.setText(linkSocial.getLinkSocial());

        Picasso.get()
                .load(linkSocial.getUrl())
                .placeholder(R.drawable.breakingbad3)
                .noFade()
                .into(holder.sLogo);

        holder.sLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context != null){
                    return;
                } else {
                    Toast.makeText(context, linkSocial.getUrlLink(), Toast.LENGTH_LONG).show();
                }
                /*
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(context, Uri.parse(linkSocial.getUrlLink()));
                */
            }
        });
    }


    @Override
    public int getItemCount() {
        return linkSocialList.size();
    }
}
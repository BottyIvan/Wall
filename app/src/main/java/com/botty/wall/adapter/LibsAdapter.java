package com.botty.wall.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.botty.wall.R;
import com.botty.wall.model.libs_model;
import com.botty.wall.util.ItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LibsAdapter extends RecyclerView.Adapter<LibsAdapter.MyViewHolder> {

    private List<libs_model> libs_models;
    public Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView libsName,creator;
        public ImageView libsLogo;
        public CardView linkLibs;
        private ItemClickListener clickListener;

        public MyViewHolder(View view) {
            super(view);
            libsName = view.findViewById(R.id.name_lib);
            libsLogo = view.findViewById(R.id.logo_libs);
            creator = view.findViewById(R.id.name_creator);
            linkLibs = view.findViewById(R.id.link_libs);

            view.setOnClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }


        @Override
        public void onClick(View v) {
            clickListener.onClick(v);
        }

    }

    public LibsAdapter(List<libs_model> libs_models){
        this.libs_models = libs_models;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_libs_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if (context != null)
            return;
        final libs_model libsModel = libs_models.get(position);
        holder.libsName.setText(libsModel.getNameLib());
        holder.creator.setText(libsModel.getCreator());

        Picasso.get()
                .load(libsModel.getIconLib())
                .noFade()
                .into(holder.libsLogo);
    }


    @Override
    public int getItemCount() {
        return libs_models.size();
    }
}
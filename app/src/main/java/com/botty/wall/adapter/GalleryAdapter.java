package com.botty.wall.adapter;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.botty.wall.R;
import com.botty.wall.model.Image;
import com.github.florent37.picassopalette.PicassoPalette;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Lincoln on 31/03/16.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder> {

    private List<Image> images;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout placeNameHolder;
        public TextView placeName;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            placeNameHolder = (LinearLayout) view.findViewById(R.id.placeNameHolder);
            placeName = (TextView) view.findViewById(R.id.placeName);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }


    public GalleryAdapter(Context context, List<Image> images) {
        mContext = context;
        this.images = images;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_thumbnail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Image image = images.get(position);

        holder.placeName.setText(image.getName());

      /*  Picasso.with(mContext)
                .load(image.getMedium())
                .resize(600,600)
                .onlyScaleDown() // the image will only be resized if it's bigger than 6000x2000 pixels.
                .centerCrop()
                .into(holder.thumbnail,
                PicassoPalette.with(image.getMedium(), holder.thumbnail)
                        .use(PicassoPalette.Profile.VIBRANT_LIGHT)
                        .intoBackground(holder.placeNameHolder)
        ); */

        Picasso.with(mContext)
                .load(image.getMedium())
                .resize(600,600)
                .onlyScaleDown() // the image will only be resized if it's bigger than 6000x2000 pixels.
                .centerCrop()
                .into(holder.thumbnail);

}


    @Override
    public int getItemCount() {
        return images.size();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private GalleryAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final GalleryAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
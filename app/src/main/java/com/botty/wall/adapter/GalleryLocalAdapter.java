package com.botty.wall.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.botty.wall.R;
import com.botty.wall.model.ImageLocal;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by BottyIvan on 08/08/16.
 */
public class GalleryLocalAdapter extends RecyclerView.Adapter<GalleryLocalAdapter.MyViewHolder>{

    private List<ImageLocal> imageLocals;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView myImg;

        public MyViewHolder(View view) {
            super(view);
            myImg = (ImageView) view.findViewById(R.id.my_l_img);
        }
    }

    public GalleryLocalAdapter(List<ImageLocal> imageLocals){
        this.imageLocals = imageLocals;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_local, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ImageLocal imageLocal = imageLocals.get(position);
        Picasso.with(context)
                .load(imageLocal.getPathImg())
                .into(holder.myImg);
    }

    @Override
    public int getItemCount() {
        return imageLocals.size();
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
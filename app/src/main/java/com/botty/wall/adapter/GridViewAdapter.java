package com.botty.wall.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.botty.wall.R;
import com.koushikdutta.ion.Ion;

/**
 * Created by BottyIvan on 08/08/16.
 */
public class GridViewAdapter extends BaseAdapter {

    // Declare variables
    private Context context;
    private String[] filepath;
    private String[] filename;
    int pos;

    private static LayoutInflater inflater = null;

    public GridViewAdapter(Context context, String[] fpath, String[] fname) {
        this.context = context;
        this.filepath = fpath;
        this.filename = fname;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return filepath.length;

    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.row_local, null);
        pos = position;
        // Locate the TextView in gridview_item.xml
        TextView text = (TextView) vi.findViewById(R.id.local_name);
        // Locate the ImageView in gridview_item.xml
        ImageView image = (ImageView) vi.findViewById(R.id.my_l_img);

        // Set file name to the TextView followed by the position
        text.setText(filename[position]);

        Ion.with(context)
                .load(filepath[position])
                .intoImageView(image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" +filepath[position]),"image/*");
                context.startActivity(intent);
            }
        });

        return vi;
    }

}
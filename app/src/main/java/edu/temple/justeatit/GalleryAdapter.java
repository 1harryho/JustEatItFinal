package edu.temple.justeatit;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by 1harr on 12/1/2017.
 */

public class GalleryAdapter<Item> extends BaseAdapter {

    LayoutInflater inflater;
    Context context;
    ArrayList<Item> items;

    public GalleryAdapter(Context context, ArrayList<Item> items) {
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView thumbnailView;
        if (convertView == null) {
            thumbnailView = new ImageView(context);
            thumbnailView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumbnailView.setPadding(8, 8, 8, 8);
        } else {
            thumbnailView = (ImageView) convertView;
        }
        Bitmap thumbnail = (Bitmap) items.get(position);
        thumbnailView.setImageBitmap(thumbnail);
        return thumbnailView;
    }
}

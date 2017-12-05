package edu.temple.justeatit;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class GalleryAdapter<Item> extends BaseAdapter {

    Context context; // context, ie. the activity so that we can create new views on
    ArrayList<Item> items; // list of items, ie. bitmaps to display on our gridview

    public GalleryAdapter(Context context, ArrayList<Item> items) {
        this.context = context; // getting context
        this.items = items; // getting list of bitmaps
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Determines how the grids in the gridview looks
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CustomView customView;
        if (convertView == null) { // no views can be reused, make a new one
            customView = new CustomView(context);
        } else { // there's a view that can be reused
            customView = (CustomView) convertView;
        }

        // get and set our bitmap in a customview class
        Bitmap bitmap = (Bitmap) items.get(position);
        customView.setImage(bitmap);
        return customView;
    }
}

package edu.temple.justeatit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class GalleryAdapter<Item> extends BaseAdapter {

    Context context; // context, ie. the activity so that we can create new views on
    ArrayList<Item> items; // list of items, ie. files to display on our gridview

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
            customView.setImage(null);
        }

        // get and set our bitmap in a customview class
        Bitmap bitmap = decodeFile((File) items.get(position));
        customView.setImage(bitmap);
        return customView;
    }

    private Bitmap decodeFile(File f) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            final int REQUIRED_SIZE=70;

            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
}

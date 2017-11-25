package edu.temple.justeatit;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by 1harr on 11/25/2017.
 */

public class CustomAdapter<Item> extends BaseAdapter {

    Context context;
    Item[] items;

    public CustomAdapter(Context context, Item[] items) {
        this.context = context;
        this.items = (Item[]) items;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView txtview = new TextView(context);
        String text = items[position].toString();
        txtview.setText(text);
        txtview.setGravity(Gravity.CENTER);
        return txtview;
    }
}

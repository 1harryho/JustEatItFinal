package edu.temple.justeatit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class CustomView extends FrameLayout implements Checkable{
    ImageView imageView; // imageview to place our bitmap onto
    boolean checked = false; // determines if this view is checked or not
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    public CustomView(Context context) {
        super(context);
        // inflate our custom view and set its background selector and imageview
        LayoutInflater.from(context).inflate(R.layout.custom_view, this);
        this.setBackgroundResource(R.drawable.selector);
        imageView = (ImageView) getRootView().findViewById(R.id.gridview_image);
    }

    // used to draw the checked state
    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked())
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        return drawableState;
    }

    // sets this view's imageview to the bitmap
    public void setImage(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
        refreshDrawableState();
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }
}

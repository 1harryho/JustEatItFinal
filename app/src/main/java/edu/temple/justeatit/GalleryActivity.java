package edu.temple.justeatit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class GalleryActivity extends AppCompatActivity {

    GridView thumbnailsList; // gridview to display images
    GalleryAdapter<File> galleryAdapter; // gridview's adapter, determines how each grid in the view looks and acts
    ArrayList<File> files; // arraylist to hold files

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        thumbnailsList = (GridView) findViewById(R.id.thumbnails_gridview);

        // getting the external storage where pictures are saved
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] file = storageDir.listFiles();

        // converting array into arraylist for easy removal of items
        files = new ArrayList<>(Arrays.asList(file));

        // setting the gridview's adapter using our files
        galleryAdapter = new GalleryAdapter<>(this, files);
        thumbnailsList.setAdapter(galleryAdapter);

        // allows for multiple selection on the gridview
        thumbnailsList.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        thumbnailsList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            /**
             * functionality to show/hide menu option depending on the number of items in the gridview checked
             * there should only be one item selected to be able to get nutritional information
             */
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                MenuItem item = mode.getMenu().findItem((R.id.gallery_get_nutrition));
                if (thumbnailsList.getCheckedItemCount() == 1) {
                    item.setVisible(true);
                } else {
                    item.setVisible(false);
                }
            }

            /**
             *
             * Creating the contexual action bar from our menu resource
             */
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.gallery_batch_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            /**
             * Functionality to determine what to do when each menu option is clicked
             */
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch(item.getItemId()) {
                    // delete the item
                    case R.id.gallery_delete:
                        if (!deleteItem()) {
                            Toast.makeText(GalleryActivity.this, "Failed to delete images!", Toast.LENGTH_LONG).show();
                        }
                        mode.finish();
                        return true;
                    // select all items in the gridview
                    case R.id.gallery_select_all:
                        setAllItemsChecked();
                        return true;
                    // get the nutritional information of the picture using the api
                    case R.id.gallery_get_nutrition:
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }

    /**
     * Sets all items in the gridview into the checked state
     */
    private void setAllItemsChecked() {
        int numItems = thumbnailsList.getCount();
        for (int i = 0; i < numItems; i++) {
            thumbnailsList.setItemChecked(i, true);
        }
    }

    /**
     * Deletes all items in the gridview that are checked
     * @return a boolean to determine if deleting worked or not
     */
    private boolean deleteItem() {
        SparseBooleanArray checked = thumbnailsList.getCheckedItemPositions();
        int len = thumbnailsList.getCount();
        for (int i = len; i >= 0; i--) {
            if (checked.get(i)) {
                boolean deleted = files.get(i).delete();
                if (deleted) {
                    files.remove(i);
                    galleryAdapter.notifyDataSetChanged();
                }
                else return deleted;
            }
        }
        return true;
    }
}

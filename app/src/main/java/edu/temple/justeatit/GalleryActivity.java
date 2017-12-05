package edu.temple.justeatit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class GalleryActivity extends AppCompatActivity {

    //TODO open the image's locations and display the images
    GridView thumbnailsList;
    GalleryAdapter<Bitmap> galleryAdapter;
    ArrayList<File> files;
    ArrayList<Bitmap> bitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        thumbnailsList = (GridView) findViewById(R.id.thumbnails_gridview);

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File[] file = storageDir.listFiles();
        files = new ArrayList<>(Arrays.asList(file));

        bitmaps = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            Log.i("Files", files.get(i).toString());
            bitmaps.add(BitmapFactory.decodeFile(files.get(i).getAbsolutePath()));
        }

        galleryAdapter = new GalleryAdapter<>(this, bitmaps);
        thumbnailsList.setAdapter(galleryAdapter);

        thumbnailsList.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        thumbnailsList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

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

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.gallery_delete:
                        deleteItem();
                        mode.finish();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }


    private boolean deleteItem() {
        SparseBooleanArray checked = thumbnailsList.getCheckedItemPositions();
        int len = thumbnailsList.getCount();
        for (int i = len; i >= 0; i--) {
            if (checked.get(i)) {
                boolean deleted = files.get(i).delete();
                files.remove(i);
                if (deleted) {
                    bitmaps.remove(i);
                    galleryAdapter.notifyDataSetChanged();
                }
                else return deleted;
            }
        }
        return true;
    }
}

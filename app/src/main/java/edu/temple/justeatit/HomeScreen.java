package edu.temple.justeatit;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeScreen extends AppCompatActivity implements  AsyncResponse{

    private Menu optionsMenu;
    boolean gallery_enabled;
    File fileImage;
    RelativeLayout main; // main activity layout
    ImageButton imgButton; // camera button
    Bitmap image; // the current picture's bitmap
    Uri photoURI; // our photo's uri
    static final int CAMERA_REQUEST_CODE = 1; // request code to start camera
    static final int PERMISSION_CAMERA_ACCESS = 2; // code to see if we have access to the camera
    static final int OPTIONS_REQUEST_CODE = 3; // request code to start option activity
    static final String GALLERY_KEY = "gallery_key_url"; // key to start gallery activity
    String picturePath; // path to picture taken
    String gallery_option;
    String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // gets references to our views
        main = (RelativeLayout) findViewById(R.id.activity_home_screen);
        imgButton = (ImageButton) findViewById(R.id.imageButton);

        gallery_enabled = true;
        gallery_option = getString(R.string.disable_gallery);
        // checks if the app have access to use the camera
        checkForCameraAccess();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CAMERA_ACCESS){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setCameraButton();
            } else {
                showMessage("Camera access needed!",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(HomeScreen.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_ACCESS);
                            }
                        });
            }
        }
    }

    /**
     * Sets-up the listener for the camera button
     */
    private void setCameraButton() {
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });
    }

    /**
     * Asks the user for permission to use the camera
     * If permission was already granted, just sets up the camera button listener
     */
    private void checkForCameraAccess() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                showMessage("Camera access needed!",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(HomeScreen.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_ACCESS);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_ACCESS);
            return;
        }
        setCameraButton();
    }

    /**
     * Displays an alert dialog explaining why a permission is needed
     *
     * @param message the message to display explaining why a permission is needed
     * @param listener the alertbox listener
     */
    private void showMessage(String message, DialogInterface.OnClickListener listener)  {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    /**
     * Creates the menu
     * @param menu the menu to create
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        optionsMenu = menu;
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Determines what to do depending on which menu item was clicked
     * @param item the item being selected
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_Options) {
            openOptionsActivity();
        } else if (itemId == R.id.action_Gallery) {
            openGalleryActivity();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            image = null;
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                ComputeVision computeVision = new ComputeVision();
                computeVision.result = this;
                computeVision.execute(fileImage);
                if (!gallery_enabled) {
                    if (fileImage.delete()) {
                        Log.i("Delete", "Delete successful");
                    } else {
                        Log.i("Delete", "Delete failed");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == OPTIONS_REQUEST_CODE && resultCode == RESULT_OK) {
            gallery_option = data.getStringExtra("optionsValue");
            if (gallery_option.equals(getString(R.string.disable_gallery))) {
                optionsMenu.findItem(R.id.action_Gallery).setVisible(true);
                gallery_enabled = true;
            } else {
                optionsMenu.findItem(R.id.action_Gallery).setVisible(false);
                gallery_enabled = false;
            }
        }
    }

    /**
     * Starts the options activity
     */
    private void openOptionsActivity() {
        Intent intent = new Intent(this, OptionsActivity.class);
        intent.putExtra("textviewValue", gallery_option);
        startActivityForResult(intent, OPTIONS_REQUEST_CODE);
    }

    /**
     * Starts the gallery activity
     */
    private void openGalleryActivity() {
        Intent intent = new Intent(this, GalleryActivity.class);
        intent.putExtra(GALLERY_KEY, "gallery_key_url");
        startActivity(intent);
    }

    private void openNutritionWebActivity(String food) {
        Intent intent = new Intent(this, NutritionWebActivity.class);
        intent.putExtra("food_name", food);
        startActivity(intent);
    }

    /**
     * Creates a uniquely-named file to store an image inside and
     * starts the camera
     */
    private void startCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File image = null;
            try {
                image = createImage();
                fileImage = image;
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (image != null) {
                //TODO error here when using a real phone
                photoURI = FileProvider.getUriForFile(this, "JustEatitFinal.edu.temple.provider", image);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    /**
     * Creates a uniquely named file
     * @return a uniquely-named file using a timestamp
     * @throws IOException
     */
    private File createImage() throws IOException {
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + time + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        picturePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void sendResult(String string) {
        tag = string;
        if (tag != null) {
            System.out.println(tag);
            openNutritionWebActivity(tag);
        } else {
            Toast.makeText(this, "No food could be found in the picture!", Toast.LENGTH_SHORT).show();
        }
    }
}

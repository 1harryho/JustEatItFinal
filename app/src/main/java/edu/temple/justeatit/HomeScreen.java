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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeScreen extends AppCompatActivity implements  AsyncResponse{

    RelativeLayout main;
    ImageButton imgButton;
    Bitmap image;
    Uri photoURI;
    static final int CAMERA_REQUEST_CODE = 1;
    static final int PERMISSION_CAMERA_ACCESS = 2;
    String picturePath;
    JSONObject obj = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        main = (RelativeLayout) findViewById(R.id.activity_home_screen);
        imgButton = (ImageButton) findViewById(R.id.imageButton);

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
                Toast.makeText(HomeScreen.this, "You clicked the camera!", Toast.LENGTH_SHORT).show();
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
            openOptions();
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
                computeVision.execute(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Starts the options activity
     */
    private void openOptions() {
        Intent intent = new Intent(this, OptionsActivity.class);
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (image != null) {
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
    public void sendResult(JSONObject obj) {
        Log.i("sendResult", "we got the obj");
        this.obj = obj;
        if (this.obj != null) {
            Log.i("sendResult", "not null!");
            System.out.println(this.obj.toString());
        } else {
            Log.i("sendResult", "null!");
        }
    }
}

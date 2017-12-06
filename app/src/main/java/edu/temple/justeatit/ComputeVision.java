package edu.temple.justeatit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by harrykunx2 on 11/26/2017.
 */

public class ComputeVision extends AsyncTask<File, Void, JSONObject> {

    public static final String subscriptionKey = "964f1c1fb54f402ca9202bd76c33d4b0";
    public static final String uriBase = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/analyze";
    public AsyncResponse result = null;

    @Override
    protected JSONObject doInBackground(File... files) {
        HttpClient client = new DefaultHttpClient();
//        HttpURLConnection httpclient = null;
        try {
            Log.i("ComputeVision", "Creating URI");
            URIBuilder builder = new URIBuilder(uriBase);
            builder.setParameter("visualFeatures","Tags");
            builder.setParameter("language","en");
            URI uri = builder.build();

            Log.i("ComputeVision", "Finished creating URI");

            HttpPost REQUEST = new HttpPost(uri);

            String boundary = "-------------" + System.currentTimeMillis();

            Log.i("ComputeVision", "Creating POST Request with uri");

            REQUEST.setHeader("Content-Type","multipart/form-data; boundary="+boundary);
            REQUEST.setHeader("Ocp-Apim-Subscription-Key",subscriptionKey);

            Log.i("ComputeVision", "Set request headers");

            File file = files[0];
            Bitmap bitmap = decodeFile(file);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            entityBuilder.setBoundary(boundary);
            entityBuilder.addBinaryBody("pic.jpeg", byteArray);
            REQUEST.setEntity(entityBuilder.build());

            Log.i("ComputeVision", "Set request body");

            HttpResponse response = client.execute(REQUEST);
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                Log.i("Entity", "Entity isn't null!");
                String jsonString = EntityUtils.toString(responseEntity);
                System.out.println(jsonString);
            } else {
                Log.i("Entity", "Entity is null");
            }

        } catch (URISyntaxException | IOException e) {
            Log.e("ComputeVision", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        result.sendResult(jsonObject);
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

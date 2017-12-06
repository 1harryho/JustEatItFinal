package edu.temple.justeatit;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by harrykunx2 on 11/26/2017.
 */

public class ComputeVision extends AsyncTask<Bitmap, Void, JSONObject> {

    public static final String subscriptionKey = "964f1c1fb54f402ca9202bd76c33d4b0";
    public static final String uriBase = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/analyze";
    public AsyncResponse result = null;

    @Override
    protected JSONObject doInBackground(Bitmap... bitmaps) {
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

            Log.i("ComputeVision", "Creating POST Request with uri");

//            REQUEST.setHeader("Content-Type","multipart/form-data");
            REQUEST.setHeader("Content-Type","application/json");
            REQUEST.setHeader("Ocp-Apim-Subscription-Key",subscriptionKey);

            Log.i("ComputeVision", "Set request headers");

//            Bitmap bitmap = bitmaps[0];
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//            byte[] byteArray = stream.toByteArray();
//            String s = new String(byteArray);
//            String s = Base64.encodeToString(byteArray, Base64.DEFAULT);
//            StringEntity entity = new StringEntity(s);
            StringEntity entity = new StringEntity("{\"url\":\"https://www.organicfacts.net/wp-content/uploads/2013/05/Banana3.jpg\"}");
            REQUEST.setEntity(entity);

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
//
//            Log.i("ComputeVision", "Connected");
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(httpclient.getInputStream()));

        } catch (URISyntaxException | IOException e) {
            Log.e("ComputeVision", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        result.sendResult(jsonObject);
    }
}

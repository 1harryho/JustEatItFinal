package edu.temple.justeatit;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

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
        HttpURLConnection httpclient = null;
        try {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("westcentralus.api.cognitive.microsoft.com")
                    .appendPath("vision")
                    .appendPath("v1.0")
                    .appendPath("analyze")
                    .appendQueryParameter("visualFeatures", "Tags")
                    .appendQueryParameter("language", "en");

            Log.i("ComputeVision", "Appended queries");

            URL url = new URL(builder.build().toString());
            httpclient = (HttpURLConnection) url.openConnection();



            httpclient.setRequestMethod("POST");
            httpclient.setRequestProperty("Content-Type", "multipart/form-data");
            httpclient.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);
            httpclient.setDoOutput(true);
            httpclient.setDoInput(true);
            httpclient.setUseCaches(false);

            OutputStream out = new BufferedOutputStream(httpclient.getOutputStream());
            BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));

            Log.i("ComputeVision", "Got writing streams");

            Bitmap bitmap = bitmaps[0];
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            String s = Base64.encodeToString(byteArray, Base64.DEFAULT);

            writer.write(s);

            writer.flush();

            writer.close();

            out.close();

            httpclient.connect();

            Log.i("ComputeVision", "Connected");

            BufferedReader reader = new BufferedReader(new InputStreamReader(httpclient.getInputStream()));

        } catch (IOException e) {
            Log.e("ComputeVision", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        result.sendResult(jsonObject);
    }
}

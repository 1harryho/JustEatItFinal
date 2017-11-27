package edu.temple.justeatit;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

            Log.i("ComputeVision", "Opened connection");

            httpclient.setRequestMethod("POST");
            httpclient.setRequestProperty("Content-Type", "multipart/form-data");
            httpclient.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);
            httpclient.setDoOutput(true);

            Log.i("ComputeVision", "Set POST request headers");
            Log.i("ComputeVision", "Beginning to write");

            DataOutputStream request = new DataOutputStream(httpclient.getOutputStream());
            request.writeBytes("\r\n");
            request.writeBytes("\r\n");

            Log.i("ComputeVision", "Wrote new lines");

            Bitmap bitmap = bitmaps[0];
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Log.i("ComputeVision", "Converted bitmap to bytes");

            request.write(byteArray);

            Log.i("ComputeVision", "Writing bytes");

            request.flush();
            request.close();

            Log.i("ComputeVision", "Cleaned outputstream");

            InputStream response = new BufferedInputStream(httpclient.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(response));

            Log.i("ComputeVision", "Beginning reading input");

            String line = "";
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();

            Log.i("ComputeVision", "Created string");

            String contents = sb.toString();
            JSONObject json = new JSONObject(contents);

            Log.i("ComputeVision", "Converting string to json");

            response.close();
            httpclient.disconnect();
            return json;

        } catch (IOException | JSONException e) {
            Log.i("ComputeVision", e.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        result.sendResult(jsonObject);
    }
}

package edu.temple.justeatit;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.apache.http.client.utils.URIBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class NutritionWebActivity extends AppCompatActivity {

    String base = "https://www.nutritionvalue.org/search.php";
    String food;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_web);

        Intent intent = getIntent();
        if (intent != null) {
            food = intent.getStringExtra("food_name");
        }

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Toast.makeText(NutritionWebActivity.this, "Could not open nutritional information!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

        });

        webView.loadUrl(createURL().toString());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private URL createURL() {
        URIBuilder builder = null;
        try {
            builder = new URIBuilder(base);
            builder.setParameter("food_query",food);
            builder.setParameter("page","1");
            URI uri = builder.build();
            return uri.toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

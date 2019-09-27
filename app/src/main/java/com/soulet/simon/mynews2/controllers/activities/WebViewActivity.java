package com.soulet.simon.mynews2.controllers.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.soulet.simon.mynews2.R;

public class WebViewActivity extends AppCompatActivity {

    private WebView webview;
    public static String WebContent = "WebContent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        String webContent = getIntent().getStringExtra(WebContent);

        webview = (WebView) findViewById(R.id.webview);
        webview.loadUrl(webContent);
    }
}

package com.example.aquaalert.Fragments;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.aquaalert.R;

public class Web extends AppCompatActivity {
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        logIn();
    }
    public void logIn() {
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.loadUrl("http://192.168.4.1/wifi");
        webView.setWebViewClient(new WebViewClient() {
            public void onPageCommitVisible(WebView view, String url) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}

package com.example.murahmad.asthma;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * Created by muradahmad on 31/07/2018.
 */

public class Consent extends AppCompatActivity {


    WebView webView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consent_webview);


        webView = (WebView) findViewById(R.id.webview);
        WebViewClient client = new WebViewClient();
        webView.setWebViewClient(client);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("https://goo.gl/forms/AZ4mSBppYWZl1vTB2");
    }
}

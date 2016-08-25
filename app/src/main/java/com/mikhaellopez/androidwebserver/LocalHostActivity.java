package com.mikhaellopez.androidwebserver;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.Button;

import java.io.IOException;

/**
 * Created by mo on 16-8-25.
 *
 * @author mo
 */

public class LocalHostActivity extends AppCompatActivity{


    private AndroidWebServer mAndroidWebServer;
    private WebView mWebView;
    private Button mServerSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_host);
        mAndroidWebServer = new AndroidWebServer(MainActivity.DEFAULT_PORT);
        try {
            mAndroidWebServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


        mWebView = (WebView) findViewById(R.id.local_web);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mWebView != null){
            mWebView.loadUrl("http://localhost:8080/com.example.vlc/update/output.m3u8");
        }
    }
}

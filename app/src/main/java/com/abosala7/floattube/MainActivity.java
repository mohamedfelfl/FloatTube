package com.abosala7.floattube;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.asksira.webviewsuite.WebViewSuite;


public class MainActivity extends AppCompatActivity {
    private static final int VIDEO_CLICKED_CODE = 100;
    public static final String VIDEO_LINK_KEY = "video-link-key";
    public static final int OVERLAY_RC = 55;
    private WebViewSuite webViewSuite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_web_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_RC);
        }
        webViewSuite = findViewById(R.id.web_view);
        webViewSuite.interfereWebViewSetup(new WebViewSuite.WebViewSetupInterference() {
            @Override
            public void interfereWebViewSetup(WebView webView) {
                webView.setWebChromeClient(new MyChromeClient());
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requestCode == OVERLAY_RC){
            if(!Settings.canDrawOverlays(this)){
                Toast.makeText(this, getString(R.string.overlay_warning), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!webViewSuite.goBackIfPossible()) super.onBackPressed();
    }
    private class MyChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            super.onProgressChanged(view, newProgress);
            if(newProgress == VIDEO_CLICKED_CODE && view.getUrl().contains("https://m.youtube.com/watch?v=")){
                startFloatTubeService(view.getUrl());
                webViewSuite.goBackIfPossible();
            }
        }
        private void startFloatTubeService(String url) {
            Intent i = new Intent(MainActivity.this, YoutubeFloatingWindowService.class);
            String id = url.replace("https://m.youtube.com/watch?v=","");
            i.putExtra(VIDEO_LINK_KEY, id);
            ContextCompat.startForegroundService(MainActivity.this, i);
        }
    }


}
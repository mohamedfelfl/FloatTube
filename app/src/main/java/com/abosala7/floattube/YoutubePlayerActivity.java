package com.abosala7.floattube;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import static com.abosala7.floattube.MainActivity.OVERLAY_RC;

public class YoutubePlayerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String videoLink = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent start = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(start, OVERLAY_RC);
        }else{
            Intent data = new Intent(this, YoutubeFloatingWindowService.class);
            data.putExtra(Intent.EXTRA_TEXT, videoLink);
            ContextCompat.startForegroundService(this, data);
        }
        finish();
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
}

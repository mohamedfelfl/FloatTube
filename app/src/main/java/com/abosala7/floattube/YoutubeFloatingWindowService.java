package com.abosala7.floattube;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.OnInitializedCallback;

import static com.abosala7.floattube.MainActivity.VIDEO_LINK_KEY;

public class YoutubeFloatingWindowService extends Service {
    //Variables Declarations
    public static final String TAG = "AST";
    private static final int NOTIFICATION_ID = 66;
    private static final String NOTIFICATION_CHANNEL_ID = "Tube-Float-Id";
    private static final CharSequence NOTIFICATION_NAME = "Youtube Playback in background";
    private BubblesManager manager;
    private WindowManager.LayoutParams defaultParams;
    private WindowManager mWindowManager;
    private View mActivity;
    private YouTubePlayerView youtubePlayerView;
    private YouTubePlayer player;
    private BubbleLayout bubbleLayout;
    private String videoId;
    private static final int VIDEO_ID_STARTING_INDEX = 17;
    private boolean firstTime = true;
    private boolean isSmallScreenActive = true;
    private WindowManager.LayoutParams fullScreenParams;

    public YoutubeFloatingWindowService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mActivity = LayoutInflater.from(this).inflate(R.layout.youtube_acitivity, null);
        ImageView close = mActivity.findViewById(R.id.close);
        ImageView minimize = mActivity.findViewById(R.id.minimize);
        final ImageView maximize = mActivity.findViewById(R.id.maximize);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });
        minimize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bubbleLayout.setVisibility(View.VISIBLE);
                mActivity.setVisibility(View.GONE);
            }});
        maximize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSmallScreenActive) {
                    maximize.setImageResource(R.drawable.exit_full_screen);
                    youtubePlayerView.enterFullScreen();
                    mWindowManager.updateViewLayout(mActivity, fullScreenParams);
                    isSmallScreenActive = false;
                } else {

                    maximize.setImageResource(R.drawable.maximize);
                    youtubePlayerView.exitFullScreen();
                    mWindowManager.updateViewLayout(mActivity, defaultParams);
                    isSmallScreenActive = true;
                }
            }
        });
        youtubePlayerView = mActivity.findViewById(R.id.player);
        youtubePlayerView.setEnableAutomaticInitialization(false);
        youtubePlayerView.enableBackgroundPlayback(true);
        youtubePlayerView.initialize(new YouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0);
                player = youTubePlayer;
            }

            @Override
            public void onStateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlayerState playerState) {

            }

            @Override
            public void onPlaybackQualityChange(YouTubePlayer youTubePlayer, PlayerConstants.PlaybackQuality playbackQuality) {

            }

            @Override
            public void onPlaybackRateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlaybackRate playbackRate) {

            }

            @Override
            public void onError(YouTubePlayer youTubePlayer, PlayerConstants.PlayerError playerError) {

            }

            @Override
            public void onCurrentSecond(YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoDuration(YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoLoadedFraction(YouTubePlayer youTubePlayer, float v) {

            }

            @Override
            public void onVideoId(YouTubePlayer youTubePlayer, String s) {

            }

            @Override
            public void onApiChange(YouTubePlayer youTubePlayer) {

            }
        });

        int LAYOUT_PARAMS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_PARAMS = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            LAYOUT_PARAMS = WindowManager.LayoutParams.TYPE_PHONE;
        }

        defaultParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_PARAMS,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        defaultParams.gravity = Gravity.CENTER;
        fullScreenParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                LAYOUT_PARAMS,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);


        defaultParams.gravity = Gravity.CENTER;
        /*params.x = 0;
        params.y = 100;*/

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mActivity, defaultParams);
        mActivity.setOnTouchListener(new View.OnTouchListener() {
            private int lastAction;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = defaultParams.x;
                        initialY = defaultParams.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        lastAction = event.getAction();
                        return true;
                    case MotionEvent.ACTION_UP:
                        //As we implemented on touch listener with ACTION_MOVE,
                        //we have to check if the previous action was ACTION_DOWN
                        //to identify if the user clicked the view or not.
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            //Open the chat conversation click.
                           /* Intent intent = new Intent(YoutubeFloatingWindowService.this, TubeBubble.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);*/

                            //close the service and remove the chat heads
                            // stopSelf();
                        }
                        lastAction = event.getAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        defaultParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                        defaultParams.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mActivity, defaultParams);
                        lastAction = event.getAction();
                        return true;
                }
                return false;
            }
        });
        initializeBubblesManager();
        startForeground(NOTIFICATION_ID, getNotification());
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkVideoId(intent);
        if(firstTime){
            firstTime = false;
        }else{
            player.loadVideo(videoId, 0);
        }
        return START_NOT_STICKY;
    }
    private Notification getNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_NONE);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.float_icon)
                .setPriority(NotificationCompat.PRIORITY_MIN);
        return builder.build();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("AST", "onDestroy: ");
        if (mActivity != null) mWindowManager.removeView(mActivity);
        manager.recycle();
        youtubePlayerView.release();
    }

    private void checkVideoId(Intent intent) {
        if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            videoId = intent.getStringExtra(Intent.EXTRA_TEXT).substring(VIDEO_ID_STARTING_INDEX);
        } else if(intent != null && intent.hasExtra(VIDEO_LINK_KEY)){
            String link = intent.getStringExtra(VIDEO_LINK_KEY);
            if(link.contains("&list=")){
                videoId = link.substring(0, 11);
            }else{
                videoId = link;
            }
        }
    }
    private void addNewBubble() {
        bubbleLayout = (BubbleLayout) LayoutInflater.from(this).inflate(R.layout.activity_tube_bubble, null);
        bubbleLayout.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
            @Override
            public void onBubbleRemoved(BubbleLayout bubble) {
                stopSelf();
            }
        });
        bubbleLayout.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {
            @Override
            public void onBubbleClick(BubbleLayout bubble) {
                mActivity.setVisibility(View.VISIBLE);
                bubbleLayout.setVisibility(View.GONE); }
        });
        bubbleLayout.setShouldStickToWall(true);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        manager.addBubble(bubbleLayout, width, height / 3);
        bubbleLayout.setVisibility(View.GONE);
    }
    private void initializeBubblesManager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } else {

            manager = new BubblesManager.Builder(this)
                    .setTrashLayout(R.layout.close_bubble)
                    .setInitializationCallback(new OnInitializedCallback() {
                        @Override
                        public void onInitialized() {
                            addNewBubble();
                        }
                    })
                    .build();
            manager.initialize();
        }

    }

}



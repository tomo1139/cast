package develop.beta1139.casttest;

import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.common.images.WebImage;
import com.google.android.libraries.cast.companionlibrary.cast.CastConfiguration;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumer;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.NoConnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.TransientNetworkDisconnectionException;
import com.google.android.libraries.cast.companionlibrary.widgets.IntroductoryOverlay;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private VideoCastManager mVideoCastManager;
    private VideoCastConsumer mVideoCastConsumer;
    private IntroductoryOverlay mOverlay;
    private MenuItem mMediaRouteMenuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initCastManager();
        mVideoCastConsumer = new VideoCastConsumerImpl() {
            @Override
            public void onCastAvailabilityChanged(boolean castPresent) {

                if (castPresent) {{
                    showOverlay();
                }}
            }
        };
        mVideoCastManager.addVideoCastConsumer(mVideoCastConsumer);

        setContentView(R.layout.activity_main);
        setupActionBar();

        setupButton();
    }

    private void setupButton() {
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/hls/DesigningForGoogleCast.m3u8";
                String imgUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/images/480x270/DesigningForGoogleCast2-480x270.jpg";
                String bigImageUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/CastVideos/images/780x1200/DesigningForGoogleCast-887x1200.jpg";
                String contentType = "application/x-mpegurl";

                MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
                movieMetadata.putString(MediaMetadata.KEY_TITLE, "title");
                movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, "subtitle");
                movieMetadata.addImage(new WebImage(Uri.parse(imgUrl)));
                movieMetadata.addImage(new WebImage(Uri.parse(bigImageUrl)));

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("description", "description");
                } catch (JSONException e) {

                }

                MediaInfo mediaInfo = new MediaInfo.Builder(url)
                        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                        .setContentType(contentType)
                        .setMetadata(movieMetadata)
                        .setCustomData(jsonObject)
                        .build();
                try {
                    mVideoCastManager.loadMedia(mediaInfo, true, 0);
                } catch (TransientNetworkDisconnectionException e) {
                    e.printStackTrace();
                } catch (NoConnectionException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showOverlay() {
        if (mOverlay != null) {
            mOverlay.remove();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mMediaRouteMenuItem.isVisible()) {
                    mOverlay = new IntroductoryOverlay.Builder(MainActivity.this)
                            .setMenuItem(mMediaRouteMenuItem)
                            .setTitleText(R.string.intro_overlay_text)
                            .setSingleTime()
                            .setOnDismissed(new IntroductoryOverlay.OnOverlayDismissedListener() {
                                @Override
                                public void onOverlayDismissed() {
                                    mOverlay = null;
                                }
                            })
                            .build();
                    mOverlay.show();
                }
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoCastManager.incrementUiCounter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoCastManager.decrementUiCounter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        mMediaRouteMenuItem = menu.findItem(R.id.media_route_menu_item);
        mVideoCastManager.addMediaRouterButton(menu, R.id.media_route_menu_item);
        return true;
    }

    private void setupActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
    }

    private void initCastManager() {
        CastConfiguration options = new CastConfiguration.Builder(getString(R.string.app_id))
                .enableAutoReconnect()
                .enableCaptionManagement()
                .enableDebug()
                .enableLockScreen()
                .enableWifiReconnection()
                .enableNotification()
                .addNotificationAction(CastConfiguration.NOTIFICATION_ACTION_PLAY_PAUSE,true)
                .addNotificationAction(CastConfiguration.NOTIFICATION_ACTION_DISCONNECT,true)
                .addNotificationAction(CastConfiguration.NOTIFICATION_ACTION_SKIP_PREVIOUS, false)
                .addNotificationAction(CastConfiguration.NOTIFICATION_ACTION_SKIP_NEXT, false)
                .addNotificationAction(CastConfiguration.NOTIFICATION_ACTION_FORWARD, false)
                .build();
        VideoCastManager.initialize(this,options);
        mVideoCastManager = VideoCastManager.getInstance().getInstance();
    }
}

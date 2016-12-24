package develop.beta1139.casttest;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.libraries.cast.companionlibrary.cast.CastConfiguration;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumer;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.google.android.libraries.cast.companionlibrary.widgets.IntroductoryOverlay;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private VideoCastManager mVideoCastManager;
    private VideoCastConsumer mVideoCastConsumer;
    private IntroductoryOverlay mOverlay;
    private MenuItem mMediaRouteMenuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupActionBar();
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
                .build();
        VideoCastManager.initialize(this,options);
        mVideoCastManager = VideoCastManager.getInstance().getInstance();
    }
}

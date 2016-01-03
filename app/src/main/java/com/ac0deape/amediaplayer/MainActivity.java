package com.ac0deape.amediaplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.MediaController;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";
    private MediaService.Controller mService = null;

    // floating action button
    private View.OnClickListener mFloatingActionButtonAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "connect " + name + " " + service);

            if (mService != null) {
                Log.w(TAG, "service already bound " + mService);
            }

            mService = (MediaService.Controller) service;

            FrameLayout controllerContainer = findViewInContent(R.id.media_controller);
            MediaController controller = new MediaController(MainActivity.this, true);
            controller.setMediaPlayer(mService);
            controller.setAnchorView(controllerContainer);

            controller.show(0);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "disconnect " + name);
            mService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupView();
        setupMediaService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cleanMediaService();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }


    private void setupMediaService() {
        Intent mediaService = new Intent(this, MediaService.class);
        bindService(mediaService, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void cleanMediaService() {
        if (mService != null) {
            unbindService(mServiceConnection);
        }
    }

    private void setupView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(mFloatingActionButtonAction);

        // MediaController
        FrameLayout controllerContainer = findViewInContent(R.id.media_controller);
        MediaController controller = new MediaController(this, true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private <V> V findViewInContent(int viewId) {
        View content = findViewById(R.id.layout_content);
        V v = (V) content.findViewById(viewId);
        return v;
    }
}

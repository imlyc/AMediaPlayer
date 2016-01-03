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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ac0deape.amediaplayer.base.MediaInfo;
import com.ac0deape.amediaplayer.view.CustomMediaController;
import com.ac0deape.amediaplayer.view.MediaListAdapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private MediaService.Player mService = null;

    private ArrayList<MediaInfo> mMediaInfos = new ArrayList<>();
    private MediaListAdapter mAdapter;

    private Timer mTimer = null;
    private CustomMediaController mMediaController;

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

            mService = (MediaService.Player) service;
            mService.setPlaylist(mMediaInfos);
            mService.addStateListener(mPlayerStateListener);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "disconnect " + name);
            mService = null;
        }
    };

    private CustomMediaController.MediaEventListener mMediaEventListener = new CustomMediaController.MediaEventListener() {
        @Override
        public void onResume() {
            mService.resume();
        }

        @Override
        public void onPause() {
            mService.pause();

        }

        @Override
        public void onPrevious() {
            mService.playPrevious();
        }

        @Override
        public void onNext() {
            mService.playNext();
        }

        @Override
        public void onRewind() {

        }

        @Override
        public void onFastForward() {

        }
    };

    private ListView.OnItemClickListener mItemClickListener = new ListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "onItemClick at " + position);
            mService.playMediaAtPosition(position);
        }
    };

    private MediaService.StateListener mPlayerStateListener = new MediaService.StateListener() {
        @Override
        public void onPrepared() {
            // update progress bar in Media Controller every 1000 ms(1s).
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int progress = mService.getProgress();
                            mMediaController.updateSeekBar(progress);
                            //Log.d(TAG, "progress " + progress);
                        }
                    });
                }
            }, 0, 1000);
        }

        @Override
        public void onComplete() {
            //cancel timer
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
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
            mService.removeStateListener(mPlayerStateListener);
            unbindService(mServiceConnection);
        }
    }

    private void setupView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(mFloatingActionButtonAction);

        mMediaController = findViewInContent(R.id.media_controller);
        mMediaController.setEventListener(mMediaEventListener);


        ListView mediaList = findViewInContent(R.id.media_list);
        mMediaInfos.add(new MediaInfo());
        mMediaInfos.add(new MediaInfo());
        mMediaInfos.add(new MediaInfo());

        mAdapter = new MediaListAdapter(mMediaInfos);
        mediaList.setAdapter(mAdapter);
        mediaList.setOnItemClickListener(mItemClickListener);
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

package com.ac0deape.amediaplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.ac0deape.amediaplayer.base.MediaInfo;
import com.ac0deape.amediaplayer.view.CustomMediaController;
import com.ac0deape.amediaplayer.view.MediaListAdapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = "MainActivity";

    private final static int REQ_CODE_PICK_MUSIC = 10;
    //Media Service contains Mediaplayer
    private MediaService mMediaService = null;
    //Download Service
    private DownloadService mDownloadService = new DownloadService();
    //backend data
    private ArrayList<MediaInfo> mMediaInfos = new ArrayList<>();
    //listview and adaptor
    private ListView mMediaList;
    private MediaListAdapter mAdapter;
    //media control bottom tool
    private CustomMediaController mMediaController;

    private Timer mTimer = null;


    //implement listen btn, download btn onclick listener in listview
    private MediaListAdapter.BtnClickListener mBtnClickListener = new MediaListAdapter.BtnClickListener(){

        @Override
        public void onListenBtnClick(int position) {
            mMediaService.playMediaAt(position);
            String playingMediaName = mMediaInfos.get(position).getTitle();
            mMediaController.setPlayingMediaName(playingMediaName);
        }

        @Override
        public void onDownloadBtnClick(int position) {
            MediaInfo mediaInfo = mMediaInfos.get(position);

            View listitem_view = getViewByPosition(position, mMediaList);
            ProgressBar progressBar = (ProgressBar)listitem_view.findViewById(R.id.progressBar);
            Log.d(TAG, "ProgressBar = " + progressBar);
            mDownloadService.queueDownload(mediaInfo.getUri(), progressBar);
        }
    };


    // "+" : floating action button
    private View.OnClickListener mFloatingActionButtonAction = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            //        .setAction("Action", null).show();
            Intent intentPick =
                    new Intent(Intent.ACTION_PICK,
                               android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentPick, REQ_CODE_PICK_MUSIC);
        }
    };


    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "connect " + name + " " + service);

            if (mMediaService != null) {
                Log.w(TAG, "service already bound " + mMediaService);
            }

            mMediaService = ((MediaService.MediaBinder) service).getMediaService();
            mMediaService.setPlaylist(mMediaInfos);
            mMediaService.setStateListener(mPlayerStateListener);
            //bind mediaService to adaptor
            mAdapter.setMediaService(mMediaService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "disconnect " + name);
            mMediaService = null;
        }
    };

    private CustomMediaController.MediaEventListener mMediaEventListener = new CustomMediaController.MediaEventListener() {
        @Override
        public void onResume() {
            mMediaService.resume();
        }

        @Override
        public void onPause() {
            mMediaService.pause();

        }

        @Override
        public void onPrevious() {
            mMediaService.playPrevious();
        }

        @Override
        public void onNext() {
            mMediaService.playNext();
        }

        @Override
        public void onRewind() {

        }

        @Override
        public void onFastForward() {

        }
    };

    //listview onItemClickListener
    private ListView.OnItemClickListener mItemClickListener = new ListView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "onItemClick at: " + position);
            mMediaService.playMediaAt(position);
        }
    };

    private MediaService.StateListener mPlayerStateListener = new MediaService.StateListener() {
        @Override
        public void onPrepared() {
            updateMediaControllerState(true);
        }

        @Override
        public void onComplete() {
            updateMediaControllerState(false);
        }

        @Override
        public void onStopped() {
            updateMediaControllerState(false);
        }

        @Override
        public void onError() {
            updateMediaControllerState(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupView();
        setupMediaService();

        //add runtime write file permission for API 23+.
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1){
            /*
            private static String[] PERMISSIONS_STORAGE = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };*/
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
            int permsRequestCode = 200;
            requestPermissions(perms, permsRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){
            case 200:
                boolean writeAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                break;
        }
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
        stopUpdateSeekBar();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQ_CODE_PICK_MUSIC) {
            Uri selected = data.getData();
            Log.d(TAG, "select " + selected);
            //MediaInfo info = MediaInfo.createLocal(selected);
            //mMediaInfos.add(info);
            mAdapter.notifyDataSetChanged();
        }
    }


    private void setupMediaService() {
        Intent mediaService = new Intent(this, MediaService.class);
        bindService(mediaService, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void cleanMediaService() {
        if (mMediaService != null) {
            mMediaService.setStateListener(null);
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

        //setup listview
        mMediaList = findViewInContent(R.id.media_list);
        //mMediaInfos.add(MediaInfo.createTest());
        mMediaInfos = MediaInfo.createAllMediaInfo();
        //bind backend data with adaptor
        mAdapter = new MediaListAdapter(this, mMediaInfos, mBtnClickListener);
        mMediaList.setAdapter(mAdapter);
        mMediaList.setOnItemClickListener(mItemClickListener);
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

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    private void updateMediaControllerState(boolean isPlaying) {
        mMediaController.updateButtonState(isPlaying);
        if (isPlaying) {
            startUpdateSeekBar();
        } else {
            stopUpdateSeekBar();
        }
    }

    private void startUpdateSeekBar() {
        // update progress bar in Media Controller every 1000 ms(1s).
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int progress = mMediaService.getProgress();
                        mMediaController.updateSeekBar(progress);
                        //Log.d(TAG, "progress " + progress);
                    }
                });
            }
        }, 0, 1000);
    }

    private void stopUpdateSeekBar() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}

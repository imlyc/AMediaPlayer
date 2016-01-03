package com.ac0deape.amediaplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.MediaController;

import com.ac0deape.amediaplayer.base.MediaInfo;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by imlyc on 1/2/16.
 */
public class MediaService extends Service {
    public interface StateListener {
        public void onPrepared();
        public void onComplete();
    }

    public class Player extends Binder {

        public void setPlaylist(ArrayList<MediaInfo> infos) {
            MediaService.this.setPlaylist(infos);
        }
        public void playMediaAtPosition(int position) {
            MediaService.this.playMediaAt(position);
        }
        public int getProgress() {
            return MediaService.this.getProgress();
        }

        public void pause() {
            MediaService.this.pause();
        }

        public void resume() {
            MediaService.this.resume();
        }

        public void playPrevious() {
            MediaService.this.playPrevious();
        }

        public void playNext() {
            MediaService.this.playNext();
        }

        public void addStateListener(StateListener listener) {
            MediaService.this.addStateListener(listener);
        }

        public void removeStateListener(StateListener listener) {
            MediaService.this.addStateListener(listener);
        }
    }

    private final static String TAG = "MediaService";
    private Player mBinder = new Player();
    private ArrayList<MediaInfo> mPlaylist = null;

    private MediaPlayer mMediaPlayer = null;
    private int mCurPlayingIndex = -1;

    private ArrayList<StateListener> mStateListeners = new ArrayList<>();

    private MediaPlayer.OnPreparedListener mMPPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            for (StateListener l : mStateListeners) {
                l.onPrepared();
            }

            mMediaPlayer.start();
        }
    };

    private MediaPlayer.OnCompletionListener mMPCompleteionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            for (StateListener l : mStateListeners) {
                l.onComplete();
            }
            playNext();
        }
    };

    private MediaPlayer.OnErrorListener mMPErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.d(TAG, "media player error happens");
            return false;

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind " + intent);
        return mBinder;
    }

    @Override
    public boolean onUnbind (Intent intent) {
        Log.d(TAG, "onUnbind " + intent);
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        Log.d(TAG, "onDestroy");
    }

    private void setPlaylist(ArrayList<MediaInfo> infos) {
        mPlaylist = infos;
    }

    private void playMediaAt(int index) {
        Log.d(TAG, "playMediaAt " + index);
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        } else {
            mMediaPlayer = new MediaPlayer();
        }
        mCurPlayingIndex = index;

        initMediaPlayer(mMediaPlayer);

        MediaInfo info = mPlaylist.get(index);
        prepareMediaPlayer(mMediaPlayer, info);

    }

    private void playNext() {
        if (mCurPlayingIndex == mPlaylist.size() - 1) {
            playMediaAt(0);
        } else {
            playMediaAt(mCurPlayingIndex + 1);
        }
    }

    private void playPrevious() {
        if (mCurPlayingIndex == 0) {
            playMediaAt(mPlaylist.size() - 1);
        } else {
            playMediaAt(mCurPlayingIndex - 1);
        }
    }

    private void prepareMediaPlayer(MediaPlayer mediaPlayer, MediaInfo info) {

        String url = "http://www.stephaniequinn.com/Music/Mozart%20-%20Presto.mp3";
        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.prepareAsync();
    }

    private void initMediaPlayer(MediaPlayer mediaPlayer) {
        mediaPlayer.setOnPreparedListener(mMPPreparedListener);
        mediaPlayer.setOnCompletionListener(mMPCompleteionListener);
        mediaPlayer.setOnErrorListener(mMPErrorListener);

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(false);
    }

    private int getProgress() {
        if (mMediaPlayer == null) {
            return 0;
        } else {
            int duration = mMediaPlayer.getDuration();
            int current = mMediaPlayer.getCurrentPosition();
            //Log.d(TAG, "duration " + duration + ", current " + current);
            return current * 100 / duration;
        }
    }

    private void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    private void resume() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    private void addStateListener(StateListener l) {
        if (!mStateListeners.contains(l)) {
            mStateListeners.add(l);
        }
    }

    private void removeStateListener(StateListener l) {
        mStateListeners.remove(l);
    }
}

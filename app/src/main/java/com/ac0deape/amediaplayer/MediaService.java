package com.ac0deape.amediaplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.ac0deape.amediaplayer.base.MediaInfo;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by imlyc on 1/2/16.
 */
public class MediaService extends Service {

    public interface StateListener {
        void onPrepared();
        void onComplete();
        void onStopped();
        void onError();
    }

    public class MediaBinder extends Binder {
        public MediaService getMediaService() {
            return MediaService.this;
        }
    }

    private final static String TAG = "MediaService";

    private MediaBinder mBinder = new MediaBinder();
    private ArrayList<MediaInfo> mPlaylist = null;
    private MediaPlayer mMediaPlayer = null;
    private int mCurPlayingIndex = -1;

    private StateListener mStateListener = null;


    private MediaPlayer.OnPreparedListener mMPPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (mStateListener != null) {
                mStateListener.onPrepared();
            }

            mp.start();
        }
    };

    private MediaPlayer.OnCompletionListener mMPCompleteionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mStateListener != null) {
                mStateListener.onComplete();
            }

            // Temporary disable play next song.
            // playNext();
        }
    };

    private MediaPlayer.OnErrorListener mMPErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.d(TAG, "media player error happens");
            if (mStateListener != null) {
                mStateListener.onError();
            }
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

    public void setPlaylist(ArrayList<MediaInfo> infos) {
        mPlaylist = infos;
    }

    public void playMediaAt(int index) {
        Log.d(TAG, "playMediaAt pos " + index);
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                if (mStateListener != null) {
                    mStateListener.onStopped();
                }
            }
            mMediaPlayer.release();
        }

        mMediaPlayer = new MediaPlayer();
        mCurPlayingIndex = index;

        initMediaPlayer(mMediaPlayer);

        MediaInfo info = mPlaylist.get(index);
        prepareMediaPlayer(mMediaPlayer, info);
    }

    public void playNext() {
        if (mCurPlayingIndex == mPlaylist.size() - 1) {
            playMediaAt(0);
        } else {
            playMediaAt(mCurPlayingIndex + 1);
        }
    }

    public void playPrevious() {
        if (mCurPlayingIndex == 0) {
            playMediaAt(mPlaylist.size() - 1);
        } else {
            playMediaAt(mCurPlayingIndex - 1);
        }
    }

    private void prepareMediaPlayer(MediaPlayer mediaPlayer, MediaInfo info) {

        try {
            mediaPlayer.setDataSource(getApplicationContext(), info.getUri());
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.prepareAsync();
    }

    private void initMediaPlayer(MediaPlayer mediaPlayer) {
        // Listeners
        mediaPlayer.setOnPreparedListener(mMPPreparedListener);
        mediaPlayer.setOnCompletionListener(mMPCompleteionListener);
        mediaPlayer.setOnErrorListener(mMPErrorListener);

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(false);
    }

    public int getProgress() {
        if (mMediaPlayer == null) {
            return 0;
        } else {
            int duration = mMediaPlayer.getDuration();
            int current = mMediaPlayer.getCurrentPosition();
            Log.d(TAG, "duration " + duration + ", current " + current);
            if(duration!=0){
                return current * 100 / duration;
            }
            return 0;
        }
    }

    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    public void resume() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    public void setStateListener(StateListener l) {
        mStateListener = l;
    }
}

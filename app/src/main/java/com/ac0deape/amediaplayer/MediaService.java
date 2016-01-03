package com.ac0deape.amediaplayer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.MediaController;

import java.io.FileDescriptor;

/**
 * Created by imlyc on 1/2/16.
 */
public class MediaService extends Service {
    public class Controller extends Binder implements MediaController.MediaPlayerControl {

        @Override
        public void start() {

        }

        @Override
        public void pause() {

        }

        @Override
        public int getDuration() {
            return 0;
        }

        @Override
        public int getCurrentPosition() {
            return 0;
        }

        @Override
        public void seekTo(int pos) {

        }

        @Override
        public boolean isPlaying() {
            return false;
        }

        @Override
        public int getBufferPercentage() {
            return 0;
        }

        @Override
        public boolean canPause() {
            return false;
        }

        @Override
        public boolean canSeekBackward() {
            return false;
        }

        @Override
        public boolean canSeekForward() {
            return false;
        }

        @Override
        public int getAudioSessionId() {
            return 0;
        }
    }

    private final static String TAG = "MediaService";
    private Controller mBinder = new Controller();

    @Nullable
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
        Log.d(TAG, "onDestroy");
    }
}

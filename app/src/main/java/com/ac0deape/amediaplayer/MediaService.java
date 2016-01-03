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

import java.io.FileDescriptor;

/**
 * Created by imlyc on 1/2/16.
 */
public class MediaService extends Service {
    public class Controller extends Binder {

    }

    private final static String TAG = "MediaService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind " + intent);
        return new Controller();
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

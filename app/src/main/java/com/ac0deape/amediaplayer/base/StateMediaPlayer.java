package com.ac0deape.amediaplayer.base;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by imlyc on 1/4/16.
 */
public class StateMediaPlayer extends MediaPlayer {
    public enum State {
        Idle, Initialized, Preparing, Prepared, Started, Paused, Stopped,
        PlaybackCompleted, End, Error
    }

    public interface StateChangeListener {
        void onStateChange(State oldState, State newState);
    }

    private State mState;
    private StateChangeListener mStateChangeListener = null;

    private MediaPlayer.OnPreparedListener mPreparedListener = null;
    private MediaPlayer.OnPreparedListener mSuperPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (mPreparedListener != null) {
                mPreparedListener.onPrepared(StateMediaPlayer.this);
            }
            changeState(State.Prepared);
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener = null;
    private MediaPlayer.OnCompletionListener mSuperCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (mCompletionListener != null) {
                mCompletionListener.onCompletion(StateMediaPlayer.this);
            }

            if (!isLooping()) {
                changeState(State.PlaybackCompleted);
            } else {
                changeState(State.Started);
            }
        }
    };

    private MediaPlayer.OnErrorListener mErrorListener = null;
    private MediaPlayer.OnErrorListener mSuperErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            boolean ret;
            if (mErrorListener != null) {
                ret = mErrorListener.onError(StateMediaPlayer.this, what, extra);
            } else {
                ret = false;
            }
            changeState(State.Error);
            return ret;
        }
    };

    public StateMediaPlayer() {
        super();
        mState = State.Idle;
        super.setOnPreparedListener(mSuperPreparedListener);
        super.setOnCompletionListener(mSuperCompletionListener);
        super.setOnErrorListener(mSuperErrorListener);
    }

    public void setStateChanedListener(StateChangeListener listener) {
        mStateChangeListener = listener;
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener listener) {
        mPreparedListener = listener;
    }

    @Override
    public void  setOnCompletionListener(OnCompletionListener listener) {
        mCompletionListener = listener;
    }

    @Override
    public void setOnErrorListener(OnErrorListener listener) {
        mErrorListener = listener;
    }

    @Override
    public void release() {
        super.release();
        changeState(State.End);
    }

    @Override
    public void reset() {
        super.reset();
        changeState(State.Idle);
    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException {
        super.setDataSource(context, uri);
        changeState(State.Initialized);
    }

    @Override
    public void prepareAsync() {
        super.prepareAsync();
        changeState(State.Preparing);
    }

    @Override
    public void prepare() throws IOException {
        super.prepare();
        changeState(State.Prepared);
    }

    @Override
    public void start() {
        super.start();
        changeState(State.Started);
    }

    @Override
    public void pause() {
        super.pause();
        changeState(State.Paused);
    }

    @Override
    public void stop() {
        super.stop();
        changeState(State.Stopped);
    }

    private void changeState(State newState) {
        State old = mState;
        mState = newState;

        // only fire listener if the state is changed
        if (old != mState && mStateChangeListener != null) {
            mStateChangeListener.onStateChange(old, mState);
        }
    }
}

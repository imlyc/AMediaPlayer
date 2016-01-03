package com.ac0deape.amediaplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.ac0deape.amediaplayer.R;

/**
 * Created by imlyc on 1/3/16.
 */
public class CustomMediaController extends LinearLayout {

    public interface MediaEventListener {
        public void onResume();
        public void onPause();
        public void onPrevious();
        public void onNext();
        public void onRewind();
        public void onFastForward();
    }

    private SeekBar mSeekBar;
    private ImageButton mButtonPrevious;
    private ImageButton mButtonNext;
    private ImageButton mButtonPlayPause;
    private ImageButton mButtonRewind;
    private ImageButton mButtonFastForward;

    private MediaEventListener mListener = null;


    public CustomMediaController(Context context) {
        super(context);
        init(context);
    }

    public CustomMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        initLayout(context);
    }

    public void setEventListener(MediaEventListener listener) {
        mListener = listener;
    }

    private void initLayout(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.media_controller, this, true);

        // progress seekBar
        mSeekBar = (SeekBar) findViewById(R.id.media_controller_seekbar);
        mSeekBar.setEnabled(false);

        // function controls
        mButtonPrevious = (ImageButton) findViewById(R.id.media_controller_previous);
        mButtonNext = (ImageButton) findViewById(R.id.media_controller_next);
        mButtonRewind = (ImageButton) findViewById(R.id.media_controller_rewind);
        mButtonFastForward = (ImageButton) findViewById(R.id.media_controller_fastforward);
        mButtonPlayPause = (ImageButton) findViewById(R.id.media_controller_play_pause);

        // true -> playing, false -> not playing
        mButtonPlayPause.setTag(false);

        mButtonPrevious.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPrevious();
                }
            }
        });

        mButtonNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onNext();
                }
            }
        });

        mButtonPlayPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean playing = (Boolean) mButtonPlayPause.getTag();
                if (playing) {
                    if (mListener != null) {
                        mListener.onPause();
                    }
                    mButtonPlayPause.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    if (mListener != null) {
                        mListener.onResume();
                    }
                    mButtonPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                }

                // Toggle state: playing -> not playing, not playing -> playing
                mButtonPlayPause.setTag(!playing);
            }
        });

        mButtonRewind.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onRewind();
                }
            }
        });

        mButtonFastForward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onFastForward();
                }
            }
        });
    }

    // progress%
    public void updateSeekBar(int progress) {
        if (!mSeekBar.isEnabled()) {
            mSeekBar.setEnabled(true);
        }

        if (progress >= 0 && progress <= 100) {
            mSeekBar.setProgress(progress);
        } else {
            int p = mSeekBar.getProgress();
            mSeekBar.setProgress(p + 1);
        }
    }
}

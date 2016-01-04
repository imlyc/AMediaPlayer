package com.ac0deape.amediaplayer.view;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ac0deape.amediaplayer.MediaService;
import com.ac0deape.amediaplayer.R;
import com.ac0deape.amediaplayer.base.MediaInfo;

import java.util.ArrayList;


public class MediaListAdapter extends ArrayAdapter {

    public interface BtnClickListener {
        public void onListenBtnClick(int position);
        public void onDownloadBtnClick(int position);
    }

    private String TAG = "MediaListAdapter";

    private Context mContext = null;
    private ArrayList<MediaInfo> mMediaInfos = null;
    private MediaService mMediaService = null;
    private BtnClickListener mBtnClickListener = null;


    public MediaListAdapter(Context context, ArrayList<MediaInfo> infos, BtnClickListener listener) {
        super(context, -1, infos);
        this.mContext = context;
        this.mMediaInfos = infos;
        mBtnClickListener = listener;
    }

    //bind media service to adaptor
    public void setMediaService(MediaService mediaService){
        this.mMediaService = mediaService;
    }


    @Override
    public int getCount() {
        return mMediaInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return mMediaInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.media_list_item, parent, false);
        }

        TextView media_index_txtView = (TextView)view.findViewById(R.id.media_index);
        TextView media_name_txtView = (TextView)view.findViewById(R.id.media_name);
        ImageButton listen_btn = (ImageButton)view.findViewById(R.id.listen_btn);
        TextView media_length_txtView = (TextView)view.findViewById(R.id.media_length);
        ImageButton download_btn = (ImageButton)view.findViewById(R.id.download_btn);
        ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progressBar);

        media_index_txtView.setText(Integer.toString(position + 1));
        media_name_txtView.setText(mMediaInfos.get(position).getTitle());
        media_name_txtView.setMaxLines(1);
        media_length_txtView.setText(mMediaInfos.get(position).getDuration());
        progressBar.setProgress(0);

        listen_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Listen button: onClick at " + position);
                if (mBtnClickListener != null) {
                    mBtnClickListener.onListenBtnClick(position);
                }
            }
        });

        download_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Download button: onClick at " + position);
                if (mBtnClickListener != null) {
                    mBtnClickListener.onDownloadBtnClick(position);
                }
            }
        });

        return view;
    }
}

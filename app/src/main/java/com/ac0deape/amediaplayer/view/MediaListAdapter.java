package com.ac0deape.amediaplayer.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ac0deape.amediaplayer.base.MediaInfo;

import java.util.ArrayList;

/**
 * Created by imlyc on 1/3/16.
 */
public class MediaListAdapter extends BaseAdapter {
    ArrayList<MediaInfo> mMediaInfos = null;
    public MediaListAdapter(ArrayList<MediaInfo> infos) {
        mMediaInfos = infos;
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv =new TextView(parent.getContext());
        tv.setText(mMediaInfos.get(position).getTitle());
        tv.setTextSize(20);

        return tv;
    }
}

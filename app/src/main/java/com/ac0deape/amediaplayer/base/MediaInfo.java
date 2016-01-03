package com.ac0deape.amediaplayer.base;

import android.net.Uri;

/**
 * Created by imlyc on 1/3/16.
 */
public class MediaInfo {
    public enum Type {
        LOCAL, STREAM, DOWNLOAD
    }

    private Type mType = Type.STREAM;
    private Uri mUri = Uri.parse("http://www.stephaniequinn.com/Music/Mozart%20-%20Presto.mp3");

    private String mTitle = "Default title";

    public static MediaInfo createLocal(Uri uri) {
        MediaInfo info = new MediaInfo();
        info.mType = Type.LOCAL;
        info.mUri = uri;

        info.mTitle = uri.getPath();

        return info;
    }

    public String getTitle() {
        return mTitle;
    }

    public Type getType() {
        return mType;
    }

    public Uri getUri() {
        return mUri;
    }
}

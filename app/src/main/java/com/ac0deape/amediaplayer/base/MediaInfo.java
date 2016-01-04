package com.ac0deape.amediaplayer.base;

import android.net.Uri;

/**
 * Created by imlyc on 1/3/16.
 */
public class MediaInfo {
    public enum Type {
        LOCAL, STREAM, DOWNLOAD
    }

    private Type mType;
    private Uri mUri;
    private String mTitle;

    private MediaInfo() {

    }

    public MediaInfo(Type type, Uri uri) {
        this.mType = type;
        this.mUri = uri;
        this.mTitle = uri.getPath();
    }

    public static MediaInfo createLocal(Uri uri) {
        MediaInfo info = new MediaInfo();
        info.mType = Type.LOCAL;
        info.mUri = uri;
        info.mTitle = uri.getPath();

        return info;
    }

    public static MediaInfo createStream(Uri uri) {
        MediaInfo info = new MediaInfo();
        info.mType = Type.STREAM;
        info.mUri = uri;
        info.mTitle = uri.getPath();

        return info;
    }

    public static MediaInfo createDownload(Uri uri) {
        MediaInfo info = new MediaInfo();
        info.mType = Type.DOWNLOAD;
        info.mUri = uri;
        info.mTitle = uri.getPath();

        return info;
    }

    public static MediaInfo createTest() {
        MediaInfo info = new MediaInfo();
        info.mType = Type.STREAM;
        info.mUri = Uri.parse("http://www.stephaniequinn.com/Music/Mozart%20-%20Presto.mp3");
        info.mTitle = "Test streaming unit";
        return info;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDuration(){
        return "3:56";
    }

    public Type getType() {
        return mType;
    }

    public Uri getUri() {
        return mUri;
    }
}

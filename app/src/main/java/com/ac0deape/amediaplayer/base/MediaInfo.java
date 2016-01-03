package com.ac0deape.amediaplayer.base;

/**
 * Created by imlyc on 1/3/16.
 */
public class MediaInfo {
    public enum Type {
        LOCAL, STREAM, DOWNLOAD
    }

    public String getTitle() {
        return "This is a media file name";
    }

    public Type getType() {
        return Type.STREAM;
    }
}

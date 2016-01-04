package com.ac0deape.amediaplayer.base;

import android.net.Uri;

import java.util.ArrayList;

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
    private String mDuration;

    private MediaInfo() {

    }

    public MediaInfo(Type type, Uri uri, String name, String duration) {
        this.mType = type;
        this.mUri = uri;
        this.mTitle = name;
        this.mDuration = duration;
    }

    public static MediaInfo createLocal(String name, Uri uri, String duration) {
        MediaInfo info = new MediaInfo();
        info.mType = Type.LOCAL;
        info.mUri = uri;
        info.mTitle = name;
        info.mDuration = duration;

        return info;
    }


    public static MediaInfo createStream(String name, Uri uri, String duration) {
        MediaInfo info = new MediaInfo();
        info.mType = Type.STREAM;
        info.mUri = uri;
        info.mTitle = name;
        info.mDuration = duration;

        return info;
    }

    public static MediaInfo createDownload(String name, Uri uri, String duration) {
        MediaInfo info = new MediaInfo();
        info.mType = Type.DOWNLOAD;
        info.mUri = uri;
        info.mTitle = name;
        info.mDuration = duration;

        return info;
    }


    public static MediaInfo createTest() {
        MediaInfo info = new MediaInfo();
        info.mType = Type.STREAM;
        //info.mUri = Uri.parse("http://www.stephaniequinn.com/Music/Mozart%20-%20Presto.mp3");
        info.mUri = Uri.parse("http://www.siberianhuskies.me/Back_To_December_-_Taylor_Swift.mp3");
        info.mTitle = "Test streaming unit";
        return info;
    }

    public static ArrayList<MediaInfo> createAllMediaInfo(){
        ArrayList<MediaInfo> res = new ArrayList<>();
        String[] names = {
                "Our Song",
                "Love Story",
                "Tim Mcgraw",
                "White Horse",
                "Should've Said No",
                "Tear Drops On My Guitar",
                "Back To December",
                "I Knew You Were Trouble",
                "Mine"
        };
        String[] paths = {
                "http://v.xnimg.cn/fmn039/audio/20100604/1545/a_409k016061.mp3",
                "http://photorespect.ru/open_air_foto/lovestory/Taylor_Swift_Love_Story.mp3",
                "http://smokeys-trail.com/Music-mp3/tim-mcgraw.mp3",
                "http://music1.franktownrocks.com/mp3s/Tswh.mp3",
                "http://ia801901.us.archive.org/31/items/LTH2013/09%20%20Taylor%20Swift%20-%20Should've%20Said%20No.mp3",
                "http://a.tumblr.com/tumblr_lhlusyR9iM1qczq42o1.mp3",
                "http://www.siberianhuskies.me/Back_To_December_-_Taylor_Swift.mp3",
                "http://www.slapthebass.com/wp-content/uploads/2012/12/Taylor-Swift-I-Knew-You-Were-Trouble.-PRFFTT-Svyable-Bootleg.mp3",
                "http://www.thejillboard.com/wp-content/uploads/2010/08/01-mine.mp3"
        };
        String[] durations = {
                "3:56",
                "4:03",
                "3:45",
                "3:58",
                "3:13",
                "4:12",
                "3:35",
                "3:28",
                "4:16"
        };
        for(int i=0;i<paths.length;++i){
            Uri path = Uri.parse(paths[i]);
            MediaInfo mediaItem = MediaInfo.createStream(names[i], path, durations[i]);
            res.add(mediaItem);
        }
        return res;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDuration(){
        return mDuration;
    }

    public Type getType() {
        return mType;
    }

    public Uri getUri() {
        return mUri;
    }
}

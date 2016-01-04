package com.ac0deape.amediaplayer.view;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xiangyixie on 1/4/16.
 */


public class DownloadService extends Service {
    private String TAG = "DownloadService";

    private static final long INTERVAL_BROADCAST = 800;
    //private long mLastUpdate = 0;

    private Hashtable<Uri, DownloadFileTask> downloadTable;
    //private LocalBroadcastManager broadcastManager;

    public DownloadService() {
        downloadTable = new Hashtable<>();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Uri uri = (Uri) intent.getSerializableExtra("MediaUri");
        ProgressBar progressBar = (ProgressBar) intent.getSerializableExtra("ProgressBar");
        queueDownload(uri, progressBar);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void queueDownload(Uri uri, ProgressBar progressBar) {
        if (downloadTable.containsKey(uri)) {
            DownloadFileTask downloadTask = downloadTable.get(uri);

            if (downloadTask.isCancelled()) {
                //restart task
                downloadTask = new DownloadFileTask(uri, progressBar);
                downloadTable.put(uri, downloadTask);
                startDownloadFileTask(downloadTask);
            } else {
                //cancel and remove task
                //downloadTask.cancel(true);
                //downloadTable.remove(uri);
                Log.d(TAG, "media file has been downloaded!");
            }
        } else {
            //start task
            DownloadFileTask downloadTask = new DownloadFileTask(uri, progressBar);
            downloadTable.put(uri, downloadTask);
            startDownloadFileTask(downloadTask);
        }
    }

    private class DownloadFileTask extends AsyncTask<Uri, Integer, Long> {
        private Uri uri = null;
        private ProgressBar progressBar = null;

        public Uri getUri() {
            return uri;
        }

        public DownloadFileTask(Uri uri, ProgressBar progressBar) {
            this.uri = uri;
            this.progressBar = progressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onProgressUpdate(Integer progress) {
            Log.d(TAG, "progress = " + progress);
            setProgressPercent(progress);
        }

        protected void onPostExecute(Long result) {
            Log.d(TAG, "Media file has been downloaded : " + result + " kB");
        }

        @Override
        protected Long doInBackground(Uri... uri) {
            String url_str = "http://www.siberianhuskies.me/Back_To_December_-_Taylor_Swift.mp3";
            Log.d(TAG, "url str = " + url_str);
            URL url = null;
            String file_name = null;
            File outputFile = null;
            long length = 0;

            try {
                url = new URL(url_str);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                Log.d(TAG, "url = " + url);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();
                InputStream in = c.getInputStream();
                // target download file store directory
                String PATH = Environment.getExternalStorageDirectory()
                        + "/download/";
                Log.d(TAG, "download PATH: " + PATH);
                File file = new File(PATH);
                if (!file.exists()) {
                    file.mkdirs();
                }
                // create new output file
                file_name = url_str.substring(url_str.lastIndexOf('/') + 1);
                outputFile = new File(file, file_name);
                FileOutputStream fos = new FileOutputStream(outputFile);
                // read and write
                byte[] buffer = new byte[1024];
                int len1 = 0;

                while ((len1 = in.read(buffer)) != -1 && !isCancelled()) {
                    fos.write(buffer, 0, len1);
                }
                if (in != null) {
                    in.close();
                }
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // read downloaded file size in KB
            length = outputFile.length();
            length = length / 1024;
            Log.i(TAG, "Check Your File.");

            return length;
        }

        protected void setProgressPercent(final int progress) {
            Log.d(TAG, "progress = " + progress);
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {

                @Override
                public void run() {
                    progressBar.setProgress(progress);
                    Log.d(TAG, "progress = " + progress);
                }
            };
            timer.schedule(timerTask, 0, 1000);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downloadTable = new Hashtable<Uri, DownloadFileTask>();
        //broadcastManager = LocalBroadcastManager.getInstance(this);
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void startDownloadFileTask(DownloadFileTask task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else {
            task.execute(task.getUri());
        }
    }

/*
    private void publishCurrentProgressOneShot(boolean forced) {
        if (forced || System.currentTimeMillis() - mLastUpdate > INTERVAL_BROADCAST) {
            mLastUpdate = System.currentTimeMillis();
            int[] progresses = new int[downloadTable.size()];
            String[] packetIds = new String[downloadTable.size()];
            int index = 0;
            Enumeration<URL> enumKey = downloadTable.keys();
            while (enumKey.hasMoreElements()) {
                String key = enumKey.nextElement();
                int val = downloadTable.get(key).progress;
                progresses[index] = val;
                packetIds[index++] = key;
            }
            Intent i = new Intent();
            i.setAction(PROGRESS_UPDATE_ACTION);
            i.putExtra("packetIds", packetIds);
            i.putExtra("progress", progresses);
            //mBroadcastManager.sendBroadcast(i);
        }
    }
    */
}

package com.ac0deape.amediaplayer;

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
                Log.d(TAG, "A media file has been downloaded !");
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
            Log.d(TAG, "Start downloading a media file....");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
           // Log.d(TAG, "progress = " + progress[0]);
            setProgressPercent(progress[0]);
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(Long result) {
            Log.d(TAG, "A media file has been downloaded : " + result + " kB");
            super.onPostExecute(result);
        }

        @Override
        protected Long doInBackground(Uri... uris) {
            Uri uri = uris[0];
            Log.d(TAG, "scheme = " + uri.getScheme() + ", host = " + uri.getHost() + ", path = " + uri.getPath());
            Log.d(TAG, "uri = " + uri.toString());
            String uriStr = uri.toString();
            //test String url_str = "http://www.siberianhuskies.me/Back_To_December_-_Taylor_Swift.mp3";
            URL url = null;
            String fileName = null;
            File outputFile = null;
            long length = 0;

            try {
                url = new URL(uriStr);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                Log.d(TAG, "url = " + url);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                //c.setDoOutput(true);
                c.connect();
                // expect HTTP 200 OK, so we don't mistakenly save error report instead of the file
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());
                    return length;
                }
                // this will be useful to display download percentage might be -1: server did not report the length
                Integer readFileLength = c.getContentLength();
                InputStream in = c.getInputStream();

                // target store file directory
                String PATH = Environment.getExternalStorageDirectory()
                        + "/download/media/";
                Log.d(TAG, "download PATH: " + PATH);
                File file = new File(PATH);
                if (!file.exists()) {
                    file.mkdirs();
                }
                // create new output file
                fileName = uriStr.substring(uriStr.lastIndexOf('/') + 1);
                outputFile = new File(file, fileName);
                FileOutputStream fos = new FileOutputStream(outputFile);
                // read and write
                byte[] buffer = new byte[1024];
                int size = 0, total = 0;
                Long lastTime = System.currentTimeMillis();

                while ((size = in.read(buffer)) != -1) {
                    Log.d(TAG, "read buffer size = " + size);
                    // back button : cancel download
                    if (isCancelled()) {
                        in.close();
                        return null;
                    }
                    fos.write(buffer, 0, size);
                    total += size;

                    if(readFileLength > 0){
                        Long nowTime = System.currentTimeMillis();
                        Long diff = nowTime - lastTime;
                        if(diff > 100){
                            lastTime = nowTime;
                            int progress = (int) (total * 100 / readFileLength);
                            //update progressBar on UI thread
                            publishProgress(progress);
                        }
                    }
                }
                //complete download file
                publishProgress(100);
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
            Log.i(TAG, "Check your downloaded file.");

            return length;
        }

        protected void setProgressPercent(final int progress) {
            Log.d(TAG, "progress = " + progress);
            progressBar.setProgress(progress);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downloadTable = new Hashtable<Uri, DownloadFileTask>();
        //broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    //execute DownloadFileTask (AsyncTask) on thread_pool_executor
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void startDownloadFileTask(DownloadFileTask task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, task.getUri());
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

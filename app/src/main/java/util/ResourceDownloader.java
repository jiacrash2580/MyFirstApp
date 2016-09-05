package util;

import android.content.Context;
import android.os.AsyncTask;

import com.tri.mobile.baselib.util.SmartUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ResourceDownloader extends AsyncTask<String, Long, ResourceDownloader.ResourceResult> {
    private static final ExecutorService mExecutor;

    static {
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public static class ResourceResult {
        public File file;
        public Boolean result;

        public ResourceResult(boolean result, File file) {
            this.result = result;
            this.file = file;
        }
    }
    public interface DownloadCallback {
        void onProgressUpdated(long curr, long total);
        void onCompleted(boolean success, File file);
    }

    private DownloadCallback listener;
    private Context context;
    private Call call;

    public ResourceDownloader(Context context, DownloadCallback callback) {
        this.listener = callback;
        this.context = context;
    }

    public void start(String url) {
        this.executeOnExecutor(mExecutor, url);
    }

    public void cancel() {
        super.cancel(true);
    }

    private String getFileName(String url) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(url.getBytes());
            byte[] bytes = messageDigest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getCachePath() {
        File cache = context.getCacheDir();
        if (cache == null) {
            cache = context.getExternalCacheDir();
        }
        if (cache != null) {
            return cache.getAbsolutePath();
        } else {
            String file = "/data/data/" + context.getPackageName() + "/cache";
            cache = new File(file);
            if (!cache.exists()) {
                cache.mkdirs();
            }
            return cache.getAbsolutePath();
        }
    }

    @Override
    protected ResourceResult doInBackground(String... params) {
        String url = params[0];
        String fileName = getCachePath() + "/" + getFileName(url);
        File file = new File(fileName);
        if (file.exists()) {
            return new ResourceResult(true, file);
        }

        try {
            call = SmartUtil.okHttpGet(url, null);
            Response response = call.execute();
            if (response.code() == 200) {
                InputStream inputStream = null;
                try {
                    inputStream = response.body().byteStream();
                    byte[] buff = new byte[1024 * 4];
                    long downloaded = 0;
                    long target = response.body().contentLength();

                    String tmpFileName = getCachePath() + "/" + getFileName(url) + ".tmp";
                    File tmpFile = new File(tmpFileName);
                    FileOutputStream outputStream = new FileOutputStream(tmpFile);

                    publishProgress(0L, target);
                    while (!isCancelled()) {
                        int read = inputStream.read(buff);
                        if (read == -1) {
                            break;
                        }
                        //write buff
                        downloaded += read;
                        publishProgress(downloaded, target);
                        outputStream.write(buff, 0, read);
                        outputStream.flush();
                        if (isCancelled()) {
                            return new ResourceResult(false, null);
                        }
                    }
                    outputStream.close();
                    if (downloaded == target && tmpFile.length() == target) {
                        boolean ret = tmpFile.renameTo(file);
                        if (ret && file.exists()) {
                            return new ResourceResult(true, file);
                        }
                    }
                    return new ResourceResult(false, null);
                } catch (IOException ignore) {
                    ignore.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResourceResult(false, null);
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        if (listener != null) {
            listener.onProgressUpdated(values[0], values[1]);
        }
    }

    @Override
    protected void onPostExecute(ResourceResult result) {
        if (listener != null) {
            listener.onCompleted(result.result, result.file);
        }
    }
}
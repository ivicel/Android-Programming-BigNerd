package info.ivicel.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.UiThread;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static info.ivicel.photogallery.BuildConfig.DEBUG;

/**
 * Created by Ivicel on 23/09/2017.
 */

public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    
    private Boolean mHasQuit = false;
    private Handler mRequestHandler;
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();
    private Handler mResponseHandler;
    private ThumbnailDownloadListener<T> mThumbnailDownloaderListener;
    
    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T photoHolder, Bitmap thumbnail);
    }
    
    public void setThumbnailDownloadListener(ThumbnailDownloadListener<T> listener) {
        mThumbnailDownloaderListener = listener;
    }
    
    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }
    
    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }
    
    public void queueThumbnail(T target, String url) {
        if (DEBUG) {
            Log.d(TAG, "Request " + url);
        }
        
        if (url == null) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T)msg.obj;
                    if (DEBUG) {
                        Log.i(TAG, "Got a request for URL: " + mRequestMap.get(msg.obj));
                    }
                    handleRequest(target);
                }
            }
        };
    }
    
    private void handleRequest(final T target) {
        try {
            final String url = mRequestMap.get(target);
            if (url == null) {
                return;
            }
            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            if (DEBUG) {
                Log.i(TAG, "Bitmap created");
            }
            mResponseHandler.post(new Runnable() {
                @UiThread
                @Override
                public void run() {
                    if (mRequestMap.get(target) == null || !mRequestMap.get(target).equals(url)
                            || mHasQuit) {
                        return;
                    }
                    mRequestMap.remove(target);
                    mThumbnailDownloaderListener.onThumbnailDownloaded(target, bitmap);
                }
            });
        } catch (IOException ioe) {
            if (DEBUG) {
                Log.e(TAG, "Error downloading image", ioe);
            }
        }
    }
    
    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
        mRequestMap.clear();
    }
}
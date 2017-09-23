package info.ivicel.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
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
    private static final int MESSAGE_DOWNLOAD = 1;
    
    private Handler mResponseHandler;
    private Handler mRequestHandler;
    private boolean mHasQuit = false;
    private ConcurrentMap<T, String> mRequestMap = new ConcurrentHashMap<>();
    private OnDownloadResponseListener<T> mOnDownloadResponseListener;
    
    public interface OnDownloadResponseListener<T> {
        void onDownloadResponse(T target, String url, Bitmap bitmap);
    }
    
    public void setOnDownloadResponseListener(OnDownloadResponseListener<T> listener) {
        mOnDownloadResponseListener = listener;
    }
    
    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        this.mResponseHandler = responseHandler;
    }
    
    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }
    
    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mRequestHandler = new Handler() {
            @SuppressWarnings("unchecked")
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T)msg.obj;
                    handleRequest(target);
                }
            }
        };
    }
    
    private void handleRequest(final T target) {
        final String url = mRequestMap.get(target);
        if (url == null) {
            return;
        }
        try {
            byte[] imageContent = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(imageContent, 0,
                    imageContent.length);
            if (bitmap != null) {
                mResponseHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mRequestMap.get(target) != null &&
                                mRequestMap.get(target).equals(url) && !mHasQuit) {
                            mOnDownloadResponseListener.onDownloadResponse(target, url, bitmap);
                        }
                    }
                });
            }
        } catch (IOException ioe) {
            if (DEBUG) {
                Log.e(TAG, "Failed to fetch image: " + url, ioe);
            }
        }
    }
    
    public void addToQueue(T target, String url) {
        if (url == null) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.put(target, url);
            if (DEBUG) {
                Log.i(TAG, "Request image from " + url);
            }
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        }
    }
    
    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
        mRequestMap.clear();
    }
}

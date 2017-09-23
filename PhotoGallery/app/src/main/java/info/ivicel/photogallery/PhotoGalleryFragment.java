package info.ivicel.photogallery;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static info.ivicel.photogallery.BuildConfig.DEBUG;

/**
 * Created by Ivicel on 21/09/2017.
 */

public class PhotoGalleryFragment extends Fragment {
    private static final String TAG = "PhotoGalleryFragment";
    
    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private boolean mIsFetching;
    private int mCurrentPage = 1;
    private int mPrevSize;
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;
    private LruCache<String, Bitmap> mImageCache;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setOnDownloadResponseListener(
                new ThumbnailDownloader.OnDownloadResponseListener<PhotoHolder>() {
                    @Override
                    public void onDownloadResponse(PhotoHolder target, String url, Bitmap bitmap) {
                        mImageCache.put(url, bitmap);
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        target.bindGalleryDrawable(drawable);
                    }
                });
        mThumbnailDownloader.start();
        new FetchItemTask().execute(mCurrentPage);
    
        ActivityManager am = (ActivityManager)getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        int size = am.getMemoryClass() / 8 * 1024 * 1024;
        if (DEBUG) {
            Log.d(TAG, "Image cache size is " + size);
        }
        mImageCache = new LruCache<String, Bitmap>(size) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView = (RecyclerView)v.findViewById(R.id.photo_recycler_view);
        
        mPhotoRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        DisplayMetrics dm = new DisplayMetrics();
                        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
                        int countSpan = Math.round(dm.widthPixels / dm.density / 120);
                        if (mPhotoRecyclerView.getViewTreeObserver().isAlive()) {
                            mPhotoRecyclerView.setLayoutManager(
                                    new GridLayoutManager(getContext(), countSpan));
                            setupAdapter();
                            mPhotoRecyclerView.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        }
                    }
                });
        mPhotoRecyclerView.addOnScrollListener(mListener);
        
        return v;
    }
    
    @Override
    public void onStop() {
        super.onStop();
        
        mPhotoRecyclerView.removeOnScrollListener(mListener);
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
    }
    
    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }
    
    private class FetchItemTask extends AsyncTask<Integer, Void, List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Integer... params) {
            mIsFetching = true;
            return new FlickrFetchr().fetchItems(params[0]);
        }
        
        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mPrevSize = mItems.size();
            mItems.addAll(items);
            setupAdapter();
            mIsFetching = false;
        }
    }
    
    private class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView mPhotoImageView;
        
        public PhotoHolder(View itemView) {
            super(itemView);
            
            mPhotoImageView = (ImageView)itemView.findViewById(R.id.photo_image_view);
        }
        
        public void bindGalleryDrawable(Drawable drawable) {
            mPhotoImageView.setImageDrawable(drawable);
        }
    }
    
    private class PhototAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;
        
        public PhototAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }
        
        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View view = inflater.inflate(R.layout.list_item_photo, parent, false);
            return new PhotoHolder(view);
        }
        
        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem item = mGalleryItems.get(position);
            Drawable drawable;
            
            Bitmap imageBitmap = mImageCache.get(item.getUrl());
            if (imageBitmap != null) {
                drawable = new BitmapDrawable(getResources(), imageBitmap);
                Log.e(TAG, "onBindViewHolder: get from cache");
            } else {
                if (Build.VERSION.SDK_INT >= 22) {
                    drawable = getResources().getDrawable(R.mipmap.ic_launcher_round, null);
                } else {
                    drawable = getResources().getDrawable(R.mipmap.ic_launcher_round);
                }
                mThumbnailDownloader.addToQueue(holder, item.getUrl());
            }
            holder.bindGalleryDrawable(drawable);
        }
        
        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }
    
    public void setupAdapter() {
        if (isAdded()) {
            if (mPhotoRecyclerView.getAdapter() != null) {
                mPhotoRecyclerView.getAdapter().notifyItemInserted(mPrevSize);
            } else {
                mPhotoRecyclerView.setAdapter(new PhototAdapter(mItems));
            }
        }
    }
    
    
    private RecyclerView.OnScrollListener mListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            int position = ((GridLayoutManager)recyclerView.getLayoutManager())
                    .findLastVisibleItemPosition();
            if (mIsFetching || newState != RecyclerView.SCROLL_STATE_IDLE ||
                    position != mItems.size() - 1) {
                return;
            }
            new FetchItemTask().execute(++mCurrentPage);
        }
        
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };
    
}

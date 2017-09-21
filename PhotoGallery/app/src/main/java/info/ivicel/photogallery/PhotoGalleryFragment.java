package info.ivicel.photogallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemTask().execute(mCurrentPage);
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
        private TextView mTitleTextView;
        
        public PhotoHolder(View itemView) {
            super(itemView);
            
            mTitleTextView = (TextView)itemView;
        }
        
        public void bindGalleryItem(GalleryItem item) {
            mTitleTextView.setText(item.toString());
        }
    }
    
    private class PhototAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;
        
        public PhototAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }
        
        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(getContext());
            return new PhotoHolder(textView);
        }
        
        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem item = mGalleryItems.get(position);
            holder.bindGalleryItem(item);
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

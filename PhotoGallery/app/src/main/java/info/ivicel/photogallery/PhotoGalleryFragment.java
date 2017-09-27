package info.ivicel.photogallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import static info.ivicel.photogallery.BuildConfig.BUILD_TYPE;
import static info.ivicel.photogallery.BuildConfig.DEBUG;

/**
 * Created by Ivicel on 21/09/2017.
 */

public class PhotoGalleryFragment extends Fragment {
    private static final String TAG = "PhotoGalleryFragment";
    
    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;
    
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);
    
        final MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView)searchItem.getActionView();
    
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setQueryHint("input what you want");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                QueryPreferences.setStoredQuery(getContext(), query);
                updateItems();
                return true;
            }
    
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPreferences.getStoredQuery(getContext());
                searchView.setQuery(query, false);
            }
        });
    
        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if (mServiceController.isServiceOn(getContext())) {
            toggleItem.setTitle(R.string.stop_polling);
        } else {
            toggleItem.setTitle(R.string.start_polling);
        }
        
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getContext(), null);
                updateItems();
                return true;
            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm = !mServiceController.isServiceOn(getContext());
                mServiceController.setServiceAlarm(getContext(), shouldStartAlarm);
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();
        
        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
                    @Override
                    public void onThumbnailDownloaded(PhotoHolder photoHolder, Bitmap thumbnail) {
                        Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                        photoHolder.bindDrawable(drawable);
                    }
                });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        
        mServiceController.setServiceAlarm(getContext(), true);
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        final Toolbar toolbar = (Toolbar)v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        mPhotoRecyclerView = (RecyclerView)v.findViewById(R.id.photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        setupAdapter();
        return v;
    }
    
    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }
    
    private class FetchItemTask extends AsyncTask<Void, Void, List<GalleryItem>> {
        private String mQuery;
    
        public FetchItemTask(String query) {
            super();
            mQuery = query;
        }
    
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            if (mQuery == null) {
                return new FlickrFetchr().fetchRecentPhotos();
            } else {
                return new FlickrFetchr().searchPhotos(mQuery);
            }
        }
    
        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            setupAdapter();
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        if (DEBUG) {
            Log.e(TAG, "onDestroyView: quit" );
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }
    
    private class PhotoHolder extends RecyclerView.ViewHolder {
        private ImageView mItemImageView;
    
        public PhotoHolder(View itemView) {
            super(itemView);
            
            mItemImageView = (ImageView)itemView.findViewById(R.id.item_image_view);
        }
    
        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }
    }
    
    private class PhototAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;
    
        public PhototAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }
    
        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_gallery,
                    parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem item = mGalleryItems.get(position);
            Drawable placeholder;
            if (Build.VERSION.SDK_INT >= 22) {
                placeholder = getResources().getDrawable(R.drawable.bill_up_close, null);
            } else {
                
                placeholder = getResources().getDrawable(R.drawable.bill_up_close);
            }
            holder.bindDrawable(placeholder);
            mThumbnailDownloader.queueThumbnail(holder, item.getUrl());
        }
    
        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }
    
    public void setupAdapter() {
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new PhototAdapter(mItems));
        }
    }
    
    private void updateItems() {
        String query = QueryPreferences.getStoredQuery(getContext());
        new FetchItemTask(query).execute();
    }
    
    
    private ServiceController mServiceController = new ServiceController() {
        @Override
        public boolean isServiceOn(Context context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return MyJobService.isJobStarted(context);
            } else {
                return PollService.isServiceAlarmOn(context);
            }
        }
    
        @Override
        public void setServiceAlarm(Context context, boolean isOn) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                MyJobService.setJobService(context, isOn);
            } else {
                PollService.setServiceAlarm(context, isOn);
            }
        }
    };
    
}

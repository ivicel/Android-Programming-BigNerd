package info.ivicel.locatr;

import android.net.Uri;

/**
 * Created by Ivicel on 21/09/2017.
 */

public class GalleryItem {
    private String mCaption;
    private String mId;
    private String mUrl;
    
    public String getOwner() {
        return mOwner;
    }
    
    public void setOwner(String owner) {
        mOwner = owner;
    }
    
    private String mOwner;
    
    public void setCaption(String caption) {
        mCaption = caption;
    }
    
    public void setId(String id) {
        mId = id;
    }
    
    public void setUrl(String url) {
        mUrl = url;
    }
    
    public String getCaption() {
        return mCaption;
    }
    
    public String getId() {
        return mId;
    }
    
    public String getUrl() {
        return mUrl;
    }
    
    public Uri getPhotoPageUri() {
        return Uri.parse("https://www.flickr.com/photos")
                .buildUpon()
                .appendPath(mOwner)
                .appendPath(mId)
                .build();
    }
    
    @Override
    public String toString() {
        return mCaption;
    }
}

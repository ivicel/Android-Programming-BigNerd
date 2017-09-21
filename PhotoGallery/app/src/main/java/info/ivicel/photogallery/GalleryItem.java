package info.ivicel.photogallery;

/**
 * Created by Ivicel on 21/09/2017.
 */

public class GalleryItem {
    private String mCaption;
    private String mId;
    private String mUrl;
    
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
    
    @Override
    public String toString() {
        return mCaption;
    }
}

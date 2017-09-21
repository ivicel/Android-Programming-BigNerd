package info.ivicel.photogallery;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Ivicel on 21/09/2017.
 */

public class ResponseGson {
    @SerializedName("photos")
    public Photos mPhotos;
    
    @SerializedName("pages")
    public int totalPages;
    
    @SerializedName("page")
    public int currentPage;
    
    public class Photos {
        @SerializedName("photo")
        public List<GalleryItemGson> photoItems;
    }
}

package info.ivicel.photogallery;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ivicel on 21/09/2017.
 */

public class GalleryItemGson {
    @SerializedName("title")
    public String title;

    @SerializedName("id")
    public String id;

    @SerializedName("url_s")
    public String urls;
}

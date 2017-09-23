package info.ivicel.photogallery;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static info.ivicel.photogallery.BuildConfig.DEBUG;

/**
 * Created by Ivicel on 21/09/2017.
 */

public class FlickrFetchr {
    private static final String TAG = "FlickrFetchr";
    
    private static final String API_KEY = BuildConfig.FLICKR_API_KEY;
    
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        if (DEBUG) {
            Log.i(TAG, "Request recent content from " + url);
        }
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
    
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " +
                        urlSpec);
            }
            
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
    
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
        
    private void parseItems(List<GalleryItem> items, String jsonString)
            throws IOException, JSONException {
        Gson gson = new Gson();
        ResponseGson response = gson.fromJson(jsonString, ResponseGson.class);
        List<GalleryItemGson> photoItems = response.mPhotos.photoItems;
        for (int i = 0; i < photoItems.size(); i++) {
            GalleryItemGson itemGson = photoItems.get(i);
            GalleryItem item = new GalleryItem();
            item.setId(itemGson.id);
            item.setCaption(itemGson.title);
            if (itemGson.urls == null) {
                continue;
            }
            item.setUrl(itemGson.urls);
            items.add(item);
        }
    }
    
    public List<GalleryItem> fetchItems(int page) {
        List<GalleryItem> items = new ArrayList<>();
        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .appendQueryParameter("page", String.valueOf(page))
                    .build()
                    .toString();
            String jsonString = getUrlString(url);
            parseItems(items, jsonString);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return items;
    }
}

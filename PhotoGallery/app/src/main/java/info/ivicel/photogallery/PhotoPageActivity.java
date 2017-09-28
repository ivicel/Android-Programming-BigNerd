package info.ivicel.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by Ivicel on 29/09/2017.
 */

public class PhotoPageActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        Uri uri = getIntent().getData();
        return PhotoPageFragment.newInstance(uri);
    }
    
    public static Intent newIntent(Context context, Uri photoPageUri) {
        Intent i = new Intent(context, PhotoPageActivity.class);
        i.setData(photoPageUri);
        return i;
    }
    
}

package info.ivicel.photogallery;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {
    
    @Override
    public Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
    
    public static Intent newIntent(Context context) {
        return new Intent(context, PhotoGalleryActivity.class);
    }
    
}

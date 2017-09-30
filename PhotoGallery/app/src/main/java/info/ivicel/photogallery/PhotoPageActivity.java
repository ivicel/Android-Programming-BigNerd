package info.ivicel.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by Ivicel on 29/09/2017.
 */

public class PhotoPageActivity extends SingleFragmentActivity {
    private OnBackPressedListener mOnBackPressedListener;
    
    public interface OnBackPressedListener {
        /* this should return false if overrided method
         * doesn't manipulate onBackPressed action
         */
        boolean onBackPressed();
    }
    
    @Override
    public Fragment createFragment() {
        Uri uri = getIntent().getData();
        return PhotoPageFragment.newInstance(uri);
    }
    
    @Override
    public void onBackPressed() {
        try {
            mOnBackPressedListener = (OnBackPressedListener)getFragment();
            if (!mOnBackPressedListener.onBackPressed()) {
                super.onBackPressed();
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException(getFragment().getClass().getSimpleName() +
                    " must implement OnBackPressedListener");
        }
    }
    
    public static Intent newIntent(Context context, Uri photoPageUri) {
        Intent i = new Intent(context, PhotoPageActivity.class);
        i.setData(photoPageUri);
        return i;
    }
}

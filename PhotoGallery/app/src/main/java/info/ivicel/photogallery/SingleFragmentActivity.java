package info.ivicel.photogallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Ivicel on 21/09/2017.
 */

public abstract class SingleFragmentActivity extends AppCompatActivity {
    public abstract Fragment createFragment();
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
    
        Fragment fragment = getFragment();
        if (fragment == null) {
            fragment = createFragment();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }
    
    protected Fragment getFragment() {
        FragmentManager fm = getSupportFragmentManager();
        return fm.findFragmentById(R.id.fragment_container);
    }
}

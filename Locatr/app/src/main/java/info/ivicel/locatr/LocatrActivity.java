package info.ivicel.locatr;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LocatrActivity extends SingleFragmentActivity {
    
    
    @Override
    public Fragment createFragment() {
        return LocatrFragment.newInstance();
    }
}

package info.ivicel.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by sedny on 11/09/2017.
 */

public class CrimeListActivity extends SingleFragmentActivity {
    
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}

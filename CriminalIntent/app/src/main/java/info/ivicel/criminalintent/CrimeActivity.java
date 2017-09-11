package info.ivicel.criminalintent;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CrimeActivity extends SingleFragmentActivity {
    private static final String TAG = "CrimeActivity";
    
    @Override
    protected Fragment createFragment() {
        return new CrimeFragment();
    }
}

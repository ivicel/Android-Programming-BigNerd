package info.ivicel.nerdlauncher;

import android.support.v4.app.Fragment;

/**
 * Created by Ivicel on 20/09/2017.
 */

public class NerdLauncherActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return NerdLauncherFragment.newInstance();
    }
}

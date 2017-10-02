package info.ivicel.draganddraw;

import android.support.v4.app.Fragment;

/**
 * Created by Ivicel on 30/09/2017.
 */

public class DragAndDrawActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return DragAndDrawFragment.newInstance();
    }
}

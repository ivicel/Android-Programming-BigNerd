package info.ivicel.photogallery;

import android.content.Context;

/**
 * Created by Ivicel on 27/09/2017.
 */

public interface ServiceController {
    boolean isServiceOn(Context context);
    
    void setServiceAlarm(Context context, boolean isOn);
}

package info.ivicel.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static info.ivicel.photogallery.BuildConfig.DEBUG;


/**
 * Created by Ivicel on 28/09/2017.
 */

public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isOn = QueryPreferences.isAlarmOn(context);
        PollService.setServiceAlarm(context, isOn);
    
        if (DEBUG) {
            Log.i(TAG, "Receive broadcast intent: " + intent.getAction());
            String status = isOn ? "on" : "off";
            Log.i(TAG, "Now Poll service is " + status);
        }
    }
}

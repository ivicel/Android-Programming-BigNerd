package info.ivicel.locatr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by Ivicel on 04/10/2017.
 */

public class PermissionsMsgDialogFragment extends DialogFragment {
    private static final String TAG = "PermissionsMsgDialogFragment";
      
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getContext())
                .setTitle("Location Permission request")
                .setMessage("This app will use your location information to find picture on flickr")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton("Ok", mListener)
                .create();
    }
    
    public static PermissionsMsgDialogFragment newInstance() {
        return new PermissionsMsgDialogFragment();
    }
    
    private DialogInterface.OnClickListener mListener;
    
    
    public void setOnPositiveButtonClickListener(DialogInterface.OnClickListener listener) {
        mListener = listener;
    }
}

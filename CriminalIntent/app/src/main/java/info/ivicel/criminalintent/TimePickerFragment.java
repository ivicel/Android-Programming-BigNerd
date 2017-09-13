package info.ivicel.criminalintent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by sedny on 13/09/2017.
 */

public class TimePickerFragment extends DialogFragment {
    private static final String TAG = "TimePickerFragment";
    public static final String EXTRA_TIME = "info.ivicel.criminalintent.extra_time";
    private TimePicker mTimePicker;
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date)getArguments().getSerializable(EXTRA_TIME);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        mTimePicker = (TimePicker)LayoutInflater.from(getContext())
                        .inflate(R.layout.dialog_time, null);
        setTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal.set(Calendar.MINUTE, minute);
            }
        });
        
        return new AlertDialog.Builder(getContext())
                .setView(mTimePicker)
                .setTitle(R.string.pick_time)
                .setCancelable(false)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK, cal.getTime());
                    }
                })
                .create();
    }
    
    
    public static TimePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TIME, date);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent data = new Intent();
        data.putExtra(EXTRA_TIME, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, data);
    }
    
    private void setTime(int hour, int minute) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentHour(minute);
        } else {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        }
    }
}

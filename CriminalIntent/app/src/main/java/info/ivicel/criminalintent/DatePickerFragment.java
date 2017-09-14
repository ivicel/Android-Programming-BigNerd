package info.ivicel.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by sedny on 12/09/2017.
 */

public class DatePickerFragment extends DialogFragment {
    private static final String ARG_DATE = "date";
    private DatePicker mDatePicker;
    public static final String EXTRA_DATE = "info.ivicel.criminalintent.date";
    
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date date = (Date)getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
    
        mDatePicker = (DatePicker)LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_date, null);
        mDatePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                sendResult(Activity.RESULT_OK, calendar.getTime());
            }
        });
        return  new AlertDialog.Builder(getContext())
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, null)
                .setView(mDatePicker)
                .create();
    }
    
    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }
    
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}

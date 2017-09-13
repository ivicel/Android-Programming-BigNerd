package info.ivicel.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by sedny on 12/09/2017.
 */

public class DatePickerFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "DatePickerFragment";
    private static final String ARG_DATE = "date";
    private DatePicker mDatePicker;
    public static final String EXTRA_DATE = "info.ivicel.criminalintent.date";
    private Calendar mCurCalendar;
    private boolean mIsLargeScreen;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        Date date = (Date)getArguments().getSerializable(ARG_DATE);
        mCurCalendar = Calendar.getInstance();
        mCurCalendar.setTime(date);
        int year = mCurCalendar.get(Calendar.YEAR);
        int month = mCurCalendar.get(Calendar.MONTH);
        int day = mCurCalendar.get(Calendar.DAY_OF_MONTH);
        
        View v = inflater.inflate(R.layout.dialog_date, container, false);
    
        mDatePicker = (DatePicker)v.findViewById(R.id.date_picker);
        mDatePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCurCalendar.set(Calendar.YEAR, year);
                mCurCalendar.set(Calendar.MONTH, monthOfYear);
                mCurCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            }
        });
    
        v.findViewById(R.id.dialog_negative_button).setOnClickListener(this);
        v.findViewById(R.id.dialog_positive_button).setOnClickListener(this);
        
        return v;
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    DisplayMetrics dm = getResources().getDisplayMetrics();
        mIsLargeScreen = dm.widthPixels / dm.density > 600;

        // WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        // params.width = (int)(dm.widthPixels / dm.density * 0.8f);
        // params.height = (int)(dm.heightPixels / dm.density * 0.8f);
        // getDialog().getWindow().setAttributes(params);
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
    
    private void sendActivityResult(int resultCode, Date date) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        getActivity().setResult(resultCode, intent);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_positive_button:
                if (mIsLargeScreen) {
                    sendResult(Activity.RESULT_OK, mCurCalendar.getTime());
                } else {
                    sendActivityResult(Activity.RESULT_OK, mCurCalendar.getTime());
                }
            case R.id.dialog_negative_button:
                if (mIsLargeScreen) {
                    getFragmentManager().beginTransaction().remove(DatePickerFragment.this).commit();
                } else {
                    getActivity().finish();
                }
                break;
            default:
                break;
        }
    }
}

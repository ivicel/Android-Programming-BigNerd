package info.ivicel.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Date;

public class DatePickerActivity extends SingleFragmentActivity {
    private static final String TAG = "DatePickerActivity";
    private static final String EXTRA_DATE = "info.ivicel.criminalintent.extra_date";
    
    @Override
    protected Fragment createFragment() {
        Date date = (Date)getIntent().getSerializableExtra(EXTRA_DATE);
        return DatePickerFragment.newInstance(date);
    }
    
    public static Intent newIntent(Context packageContext, Date date) {
        Intent data = new Intent(packageContext, DatePickerActivity.class);
        data.putExtra(EXTRA_DATE, date);
        return data;
    }
}

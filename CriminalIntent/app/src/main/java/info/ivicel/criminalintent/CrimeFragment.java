package info.ivicel.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

/**
 * Created by sedny on 11/09/2017.
 */

public class CrimeFragment extends Fragment {
    private static final String TAG = "CrimeFragment";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBoxk;
    private Button mGoToFisrtButton;
    private Button mGoToLastButton;
    private Button mPickTimeButton;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getContext()).getCrime(crimeId);
        setRetainInstance(true);
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
    
        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mDateButton = (Button)v.findViewById(R.id.crime_date);
        mSolvedCheckBoxk = (CheckBox)v.findViewById(R.id.crime_solved);
        mGoToFisrtButton = (Button)v.findViewById(R.id.jump_to_first);
        mGoToLastButton = (Button)v.findViewById(R.id.jump_to_last);
        mPickTimeButton = (Button)v.findViewById(R.id.pick_time);
    
        mTitleField.setText(mCrime.getTitle());
        updateDate();
        
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics d =  new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(d);
    
                if (d.widthPixels / d.density < 600) {
                    Intent data = DatePickerActivity.newIntent(getContext(), mCrime.getDate());
                    startActivityForResult(data, REQUEST_DATE);
                } else {
                    FragmentManager fm = getFragmentManager();
                    DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                    dialog.show(fm, DIALOG_DATE);
                }
            }
        });
        
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        
            }
    
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }
    
            @Override
            public void afterTextChanged(Editable s) {
                Intent data = new Intent();
                data.putExtra(ARG_CRIME_ID, mCrime.getId());
                getActivity().setResult(Activity.RESULT_OK, data);
            }
        });
        mSolvedCheckBoxk.setChecked(mCrime.isSolved());
        mSolvedCheckBoxk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });
    
        mOnNavButtonListener.onNavButton(mGoToFisrtButton, mGoToLastButton);
        
        mPickTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                TimePickerFragment fragment = TimePickerFragment.newInstance(mCrime.getDate());
                fragment.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                fragment.show(fm, DIALOG_TIME);
            }
        });
        
        return v;
    }
    
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
    
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static UUID getChangedCrimeId(Intent data) {
        return (UUID)data.getSerializableExtra(ARG_CRIME_ID);
    }
    
    private OnNavButtonListener mOnNavButtonListener;
    public interface OnNavButtonListener {
        void onNavButton(Button first, Button last);
    }
    
    public void setNavButtonListener(OnNavButtonListener listener) {
        mOnNavButtonListener = listener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }
        Log.d(TAG, "onActivityResult: " + REQUEST_TIME);
        if (requestCode == REQUEST_TIME) {
            Date date = (Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            Log.d(TAG, "onActivityResult: " + date);
            mCrime.setDate(date);
            updateDate();
        }
    }
    
    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }
}

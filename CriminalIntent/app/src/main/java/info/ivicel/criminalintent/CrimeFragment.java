package info.ivicel.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by sedny on 11/09/2017.
 */

public class CrimeFragment extends Fragment {
    private static final String TAG = "CrimeFragment";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_READ_CONTACT_PERMISSION = 1;
    private static final String ARG_PHONE_LIST = "phone_list";
    
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getContext()).getCrime(crimeId);
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);
    
        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mDateButton = (Button)v.findViewById(R.id.crime_date);
        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mReportButton = (Button)v.findViewById(R.id.crime_report);
        mSuspectButton = (Button)v.findViewById(R.id.crime_suspect);
        mCallSuspectButton = (Button)v.findViewById(R.id.call_suspect);
    
        mTitleField.setText(mCrime.getTitle());
        updateDate();
        
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
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
        
            }
        });
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });
        
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder.from(getActivity())
                        .setChooserTitle(R.string.send_report)
                        .setSubject(getResources().getString(R.string.crime_report_subject))
                        .setText(getCrimeReport())
                        .setType("text/plain")
                        .startChooser();
                        
            }
        });
        
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickContact = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                PackageManager pm = getActivity().getPackageManager();
                if (pm.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                    startActivityForResult(pickContact, REQUEST_CONTACT);
                } else {
                    Toast.makeText(getContext(), R.string.no_contact_respond, Toast.LENGTH_SHORT).show();
                }
            }
        });
    
        if (mCrime.getSuspect() != null) {
            Log.d(TAG, "onCreateView: " + mCrime.getSuspect());
            Log.d(TAG, "onCreateView: " + mCrime.getLookupKey());
            mSuspectButton.setText(mCrime.getSuspect());
        }
        
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCrime.getLookupKey() == null) {
                    Toast.makeText(getContext(), "no suspect is selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                reqeuestRuntimePermission();
            }
        });
        
        return v;
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
        } else if (requestCode == REQUEST_CONTACT) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.LOOKUP_KEY
            };
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields,
                    null, null, null);
            if (c == null || c.getCount() == 0) {
                return;
            }
            try {
                c.moveToFirst();
                String suspect = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String lookup = c.getString(c.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                Log.d(TAG, "onActivityResult: " + lookup);
                mCrime.setSuspect(suspect);
                mCrime.setLookupKey(lookup);
                CrimeLab.get(getContext()).updateCrime(mCrime);
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getContext()).updateCrime(mCrime);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACT_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    callCrimeSuspect();
                } else {
                    showDeniedCallPermission();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }
    
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }
    
    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }
    
    private String getCrimeReport() {
        String solvedString;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
    
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect);
        }
    
        String report = getString(R.string.crime_report, mCrime.getTitle(),
                dateString, solvedString, suspect);
        return report;
    }
    
    private void callCrimeSuspect() {
        List<String> phoneList = new ArrayList<>();
        ContentResolver resolver = getActivity().getContentResolver();
        Cursor cursor = resolver.query(Phone.CONTENT_URI, new String[] {Phone.NUMBER},
                Phone.LOOKUP_KEY + " = ?", new String[] {mCrime.getLookupKey()}, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            return;
        }
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String phoneNumber = cursor.getString(cursor.getColumnIndex(
                        Phone.NUMBER));
                cursor.moveToNext();
                phoneList.add(phoneNumber);
            }
        } finally {
            cursor.close();
        }
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_PHONE_LIST, (ArrayList<String>)phoneList);
        PhoneNumberFragment fragment = new PhoneNumberFragment();
        fragment.setArguments(args);
        fragment.show(getFragmentManager(), "phone_fragment");
    }
    
    private void showDeniedCallPermission() {
        Toast.makeText(getContext(), "You don't have permission to make a call",
                Toast.LENGTH_SHORT).show();
    }
    
    private void reqeuestRuntimePermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACT_PERMISSION);
        } else {
            callCrimeSuspect();
        }
    }
    
    public static class PhoneNumberFragment extends DialogFragment {
        private List<String> mPhoneList;
        
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
    
            mPhoneList = getArguments().getStringArrayList(ARG_PHONE_LIST);
        }
    
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getContext())
                    .setTitle("Select a phone number")
                    .setAdapter(new ArrayAdapter<>(getContext(),
                                    android.R.layout.simple_list_item_1, mPhoneList),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(Intent.ACTION_DIAL);
                                    i.setData(Uri.parse("tel:" + mPhoneList.get(which)));
                                    startActivity(i);
                                }
                            }
                    ).create();
        }
    }
}

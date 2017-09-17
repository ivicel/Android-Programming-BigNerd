package info.ivicel.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
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
    private static final int REQUEST_TAKE_PHOTO = 2;
    
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageView mPhotoView;
    private ImageButton mPhotoButton;
    private File mPhotoFile;
    private Callbacks mCallbacks;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getContext()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getContext()).getPhotoFile(mCrime);
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
        mPhotoButton = (ImageButton)v.findViewById(R.id.crime_camera);
        mPhotoView = (ImageView)v.findViewById(R.id.crime_photo);
    
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
                updateCrime();
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
                updateCrime();
            }
        });
        
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
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
            mSuspectButton.setText(mCrime.getSuspect());
        }
        
        final Intent imageCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (mPhotoFile == null ||
                imageCapture.resolveActivity(getActivity().getPackageManager()) == null) {
            mPhotoButton.setEnabled(false);
        }
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getContext(),
                        "info.ivicel.criminalintent.fileprovider", mPhotoFile);
                imageCapture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> resolveActivities = getActivity().getPackageManager()
                        .queryIntentActivities(imageCapture, PackageManager.MATCH_DEFAULT_ONLY);
                /* to backward compatible with kitkat and lower version
                 * we should grant the permission
                 */
                for (ResolveInfo resolveActivity : resolveActivities) {
                    getContext().grantUriPermission(resolveActivity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(imageCapture, REQUEST_TAKE_PHOTO);
            }
        });
        
        updatePhoto();
        
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
            updateCrime();
        } else if (requestCode == REQUEST_CONTACT) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields,
                    null, null, null);
            if (c == null || c.getCount() == 0) {
                return;
            }
            try {
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
                updateCrime();
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO) {
            /*
             * remove the permission, otherwise other apps will take this permission
             * util reboot the device
             */
            Uri uri = FileProvider.getUriForFile(getContext(),
                    "info.ivicel.criminalintent.fileprovider", mPhotoFile);
            getContext().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateCrime();
            updatePhoto();
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        // CrimeLab.get(getContext()).updateCrime(mCrime);
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallbacks = (Callbacks)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement CrimeFragment.Callbacks");
        }
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
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
    
        return getString(R.string.crime_report, mCrime.getTitle(),
                dateString, solvedString, suspect);
    }
    
    private void updatePhoto() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }
    
    private void updateCrime() {
        CrimeLab.get(getContext()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }
}

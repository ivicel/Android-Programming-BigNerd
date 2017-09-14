package info.ivicel.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {
    private static final String TAG = "CrimePagerActivity";
    private static final String EXTRA_CRIME_ID = "info.ivicel.criminalintent.crime_id";
    public static final String EXTRA_SUBTITLE_VISIBLE = "info.ivicel.criminalintent.subtitle_visible";
    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    private boolean mSubtitleVisible;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
    
        mViewPager = (ViewPager)findViewById(R.id.activity_crime_pager_view_pager);
        mCrimes = CrimeLab.get(this).getCrimes();
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }
    
            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        
        Intent intent = getIntent();
        mSubtitleVisible = intent.getBooleanExtra(EXTRA_SUBTITLE_VISIBLE, false);
        UUID crimeId = (UUID)intent.getSerializableExtra(EXTRA_CRIME_ID);
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
    
    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        Intent intent = super.getParentActivityIntent();
        if (intent != null) {
            intent.putExtra(EXTRA_SUBTITLE_VISIBLE, mSubtitleVisible);
        }
        return intent;
    }
    
    public static Intent newIntent(Context packageContext, UUID crimeId, boolean subtitleVisible) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        intent.putExtra(EXTRA_SUBTITLE_VISIBLE, subtitleVisible);
        return intent;
    }
}

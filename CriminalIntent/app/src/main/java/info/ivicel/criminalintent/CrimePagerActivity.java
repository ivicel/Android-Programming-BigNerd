package info.ivicel.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {
    private static final String TAG = "CrimePagerActivity";
    private static final String EXTRA_CRIME_ID = "info.ivicel.criminalintent.crime_id";
    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
    
        mViewPager = (ViewPager)findViewById(R.id.activity_crime_pager_view_pager);
        mCrimes = CrimeLab.get(this).getCrimes();
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(final int position) {
                Crime crime = mCrimes.get(position);
                Fragment fragment = CrimeFragment.newInstance(crime.getId());
                
                ((CrimeFragment)fragment).setNavButtonListener(
                        new CrimeFragment.OnNavButtonListener() {
                    @Override
                    public void onNavButton(Button first, Button last) {
                        if (position == 0) {
                            first.setEnabled(false);
                        }

                        if (position == (mCrimes.size() - 1)) {
                            last.setEnabled(false);
                        }
                        
                        first.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                jumpToFirstItem();
                            }
                        });
                        
                        last.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                jumpToLastItem();
                            }
                        });
                    }
                });
                
                return fragment;
            }
    
            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
    
        UUID crimeId = (UUID)getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
    
    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }
    
    public void jumpToFirstItem() {
        mViewPager.setCurrentItem(0, true);
    }
    
    public void jumpToLastItem() {
        mViewPager.setCurrentItem(mCrimes.size() - 1, true);
    }
}

package info.ivicel.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

/**
 * Created by sedny on 11/09/2017.
 */

public class CrimeListFragment extends Fragment {
    private static final String TAG = "CrimeListFragment";
    public static final int CRIME_REQUEST_CODE = 1;
    
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private int mChangedCrimePosition;
    
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);
    
        mCrimeRecyclerView = (RecyclerView)v.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        updateUI();
        return v;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
    
    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getContext());
        List<Crime> crimes = crimeLab.getCrimes();
    
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyItemChanged(mChangedCrimePosition);
        }
    }
    
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Crime mCrime;
        private ImageView mSolvedImageView;
    
        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
    
            mTitleTextView = (TextView)itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView)itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView)itemView.findViewById(R.id.crime_solved);
    
            itemView.setOnClickListener(this);
        }
    
        @Override
        public void onClick(View v) {
            Intent intent = CrimePagerActivity.newIntent(getContext(), mCrime.getId());
            startActivityForResult(intent, CRIME_REQUEST_CODE);
        }
    
        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(crime.getTitle());
            mDateTextView.setText(crime.getDate().toString());
            mSolvedImageView.setVisibility(mCrime.isSolved() ? View.VISIBLE : View.GONE);
        }
    }
    
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;
    
        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }
    
        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
    
            return new CrimeHolder(inflater, parent);
        }
    
        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            
            holder.bind(crime);
        }
    
        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
    
        if (requestCode == CRIME_REQUEST_CODE && data != null) {
            UUID crimeId = CrimeFragment.getChangedCrimeId(data);
            CrimeLab crimeLab = CrimeLab.get(getContext());
            List<Crime> crimes = crimeLab.getCrimes();
            mChangedCrimePosition = crimes.indexOf(crimeLab.getCrime(crimeId));
        }
    }
}

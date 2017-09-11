package info.ivicel.criminalintent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by sedny on 11/09/2017.
 */

public class CrimeListFragment extends Fragment {
    private static final String TAG = "CrimeListFragment";
    private static final int TYPE_CRIME_NEED_POLICE = 1;
    private static final int TYPE_CRIME_NO_NEED_POLICE = 2;
    
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    
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
    
    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getContext());
        List<Crime> crimes = crimeLab.getCrimes();
    
        mAdapter = new CrimeAdapter(crimes);
        mCrimeRecyclerView.setAdapter(mAdapter);
    }
    
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Crime mCrime;
    
        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
    
            mTitleTextView = (TextView)itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView)itemView.findViewById(R.id.crime_date);
            
            itemView.setOnClickListener(this);
        }
    
        @Override
        public void onClick(View v) {
            Toast.makeText(getContext(), mCrime.getTitle() + " clicked!", Toast.LENGTH_SHORT).show();
        }
    
        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(crime.getTitle());
            mDateTextView.setText(crime.getDate().toString());
            if (getItemViewType() == TYPE_CRIME_NEED_POLICE) {
                Button callPolice = (Button)itemView.findViewById(R.id.call_police);
                callPolice.setVisibility(View.VISIBLE);
                callPolice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), getResources().getString(R.string.polic_action),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
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
            CrimeHolder holder = new CrimeHolder(inflater, parent);
            
            return holder;
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
    
        @Override
        public int getItemViewType(int position) {
            int viewType;
            if (position % 2 == 0) {
                viewType = TYPE_CRIME_NEED_POLICE;
            } else {
                viewType = TYPE_CRIME_NO_NEED_POLICE;
            }
            
            return viewType;
        }
    }
}

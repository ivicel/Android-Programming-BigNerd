package info.ivicel.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by sedny on 11/09/2017.
 */

public class CrimeListFragment extends Fragment {
    private static final String TAG = "CrimeListFragment";
    private static final String SAVE_SUBTITLE_VISIBLE = "subtitle";
    
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    private Callbacks mCallBacks;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
    
        MenuItem subtitle = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitle.setTitle(R.string.hide_subtitle);
        } else {
            subtitle.setTitle(R.string.show_subtitle);
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getContext()).addCrime(crime);
                updateUI();
                mCallBacks.onCrimeSelected(crime);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);
    
        mCrimeRecyclerView = (RecyclerView)v.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVE_SUBTITLE_VISIBLE);
        }
        
        mDecoration.attachToRecyclerView(mCrimeRecyclerView);
        return v;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVE_SUBTITLE_VISIBLE, mSubtitleVisible);
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallBacks = (Callbacks)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.getClass().getSimpleName() +
                    " must implement CriminalFragment.Callbacks");
        }
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        mCallBacks = null;
    }
    
    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getContext());
        List<Crime> crimes = crimeLab.getCrimes();
    
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }
        
        updateSubtitle();
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
            mCallBacks.onCrimeSelected(mCrime);
        }
    
        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(crime.getTitle());
            mDateTextView.setText(crime.getDate().toString());
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }
        
        public Crime getCrime() {
            return mCrime;
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
    
        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }
    
        public void removeCrime(int position) {
            mCrimes.remove(position);
        }
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getContext());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getString(R.string.subtitle_format, crimeCount);
        if (!mSubtitleVisible) {
            subtitle = null;
        }
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(subtitle);
    }

    public interface Callbacks {
        void onCrimeSelected(Crime crime);
    }
    
    private ItemTouchHelper mDecoration = new ItemTouchHelper(
            new ItemTouchHelper.Callback() {
                @Override
                public int getMovementFlags(RecyclerView recyclerView,
                        RecyclerView.ViewHolder viewHolder) {
                    return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                }
                
                @Override
                public boolean onMove(RecyclerView recyclerView,
                        RecyclerView.ViewHolder viewHolder,
                        RecyclerView.ViewHolder target) {
                    return false;
                }
                
                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    removeItem((CrimeHolder)viewHolder);
                }
            });
    
    private void removeItem(CrimeHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        CrimeLab.get(getContext()).deleteCrime(viewHolder.getCrime());
        mAdapter.removeCrime(position);
        mAdapter.notifyItemRemoved(position);
    }
}

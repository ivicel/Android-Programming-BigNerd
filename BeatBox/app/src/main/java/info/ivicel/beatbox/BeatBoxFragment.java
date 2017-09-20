package info.ivicel.beatbox;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import java.util.List;

import info.ivicel.beatbox.databinding.FragmentBeatBoxBinding;
import info.ivicel.beatbox.databinding.ListItemSoundBinding;

/**
 * Created by Ivicel on 18/09/2017.
 */

public class BeatBoxFragment extends Fragment {
    private static final String TAG = "BeatBoxFragment";
    private BeatBox mBeatBox;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mBeatBox = new BeatBox(getContext());
    }
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final FragmentBeatBoxBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_beat_box, container, false);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.recyclerView.setAdapter(new SoundAdapter(mBeatBox.getSounds()));
        int progress = binding.seekBar.getProgress();
        binding.playbackSpeedText.setText(getString(R.string.playback_speed, progress / 10.0f));
        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.playbackSpeedText.setText(getString(R.string.playback_speed, progress / 10.0f));
                mBeatBox.setPlaybackSpeed(progress);
            }
    
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
        
            }
    
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
        
            }
        });
        return binding.getRoot();
    }
    
    @NonNull
    public static BeatBoxFragment newInstance() {
        return new BeatBoxFragment();
    }
    
    private class SoundHolder extends RecyclerView.ViewHolder {
        private ListItemSoundBinding mBinding;
    
        public SoundHolder(ListItemSoundBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
            mBinding.setViewModel(new SoundViewModel(mBeatBox));
        }
    
        public void bind(Sound sound) {
            mBinding.getViewModel().setSound(sound);
            mBinding.executePendingBindings();
        }
    }
    
    private class SoundAdapter extends RecyclerView.Adapter<SoundHolder> {
        private List<Sound> mSounds;
        
        public SoundAdapter(List<Sound> sounds) {
            mSounds = sounds;
        }
    
        @Override
        public SoundHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            ListItemSoundBinding binding = DataBindingUtil.inflate(inflater,
                    R.layout.list_item_sound, parent, false);
            
            return new SoundHolder(binding);
        }
    
        @Override
        public void onBindViewHolder(SoundHolder holder, int position) {
            Sound sound = mSounds.get(position);
            holder.bind(sound);
        }
    
        @Override
        public int getItemCount() {
            return mSounds.size();
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBeatBox.release();
    }
}

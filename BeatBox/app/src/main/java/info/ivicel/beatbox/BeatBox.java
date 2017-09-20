package info.ivicel.beatbox;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivicel on 18/09/2017.
 */

public class BeatBox {
    private static final String TAG = "BeatBox";
    private static final String SOUNDS_FOLDER = "sample_sounds";
    private static final int MAX_SOUNDS = 5;
    
    private AssetManager mAsset;
    private List<Sound> mSounds = new ArrayList<>();
    private SoundPool mSoundPool;
    private int playbackSpeed;
    
    public BeatBox(Context context) {
        mAsset = context.getAssets();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attrs = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build();
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(MAX_SOUNDS)
                    .setAudioAttributes(attrs)
                    .build();
        } else {
            mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        }
        loadSounds();
    }
    
    private void loadSounds() {
        String[] soundNames;
        try {
            soundNames = mAsset.list(SOUNDS_FOLDER);
            for (String filename : soundNames) {
                try {
                    String assetPath = SOUNDS_FOLDER + "/" + filename;
                    Sound sound = new Sound(assetPath);
                    load(sound);
                    mSounds.add(sound);
                } catch (IOException e) {
                    Log.e(TAG, "Cound not load cound " + filename, e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    
        for (String filename : soundNames) {
            String assetPath = SOUNDS_FOLDER + "/" + filename;
            Sound sound = new Sound(assetPath);
            mSounds.add(sound);
        }
    }
    
    public List<Sound> getSounds() {
        return mSounds;
    }
    
    private void load(Sound sound) throws IOException {
        AssetFileDescriptor afd = mAsset.openFd(sound.getAssetPath());
        int soundId = mSoundPool.load(afd, 1);
        sound.setSoundId(soundId);
    }
    
    public void play(Sound sound) {
        Integer soundId = sound.getSoundId();
        if (soundId == null) {
            return;
        }
        mSoundPool.play(soundId, 1.0f, 1.0f, 1, 0,
                playbackSpeed / 10);
    }
    
    public void setPlaybackSpeed(int playbackSpeed) {
        this.playbackSpeed = playbackSpeed;
    }
    
    public void release() {
        mSoundPool.release();
    }
}

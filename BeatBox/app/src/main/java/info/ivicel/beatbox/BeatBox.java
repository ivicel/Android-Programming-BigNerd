package info.ivicel.beatbox;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.SoundPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivicel on 18/09/2017.
 */

public class BeatBox {
    private static final String TAG = "BeatBox";
    private static final String SOUNDS_FOLDER = "sample_sounds";
    
    private AssetManager mAsset;
    private List<Sound> mSounds = new ArrayList<>();
    
    public BeatBox(Context context) {
        mAsset = context.getAssets();
        loadSounds();
    }
    
    private void loadSounds() {
        String[] soundNames;
        try {
            soundNames = mAsset.list(SOUNDS_FOLDER);
            
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
}

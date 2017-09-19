package info.ivicel.beatbox;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * Created by Ivicel on 18/09/2017.
 */

public class SoundViewModel extends BaseObservable {
    private Sound mSound;
    private BeatBox mBeatBox;
    
    public SoundViewModel(BeatBox beatBox) {
        mBeatBox = beatBox;
    }
    
    public Sound getSound() {
        return mSound;
    }
    
    public void setSound(Sound sound) {
        mSound = sound;
        notifyChange();
    }
    
    @Bindable
    public String getTitle() {
        return mSound.getName();
    }
    
}

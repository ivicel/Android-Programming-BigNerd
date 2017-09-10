package info.ivicel.geoquiz;

import android.util.Log;

/**
 * Created by sedny on 10/09/2017.
 */

public class Question {
    private static final String TAG = "Question";
    private int mTextResId;
    private boolean mAnswerTrue;
    private boolean mIsCheat;
    
    public Question(int textResId, boolean answerTrue) {
        mTextResId = textResId;
        mAnswerTrue = answerTrue;
        mIsCheat = false;
        Log.d(TAG, "Question: new question = " + mIsCheat);
    }
    
    public boolean isCheat() {
        return mIsCheat;
    }
    
    public void setCheat(boolean cheat) {
        mIsCheat = cheat;
    }
    
    public int getTextResId() {
        return mTextResId;
    }
    
    public void setTextResId(int textResId) {
        mTextResId = textResId;
    }
    
    public boolean isAnswerTrue() {
        return mAnswerTrue;
    }
    
    public void setAnswerTrue(boolean answerTrue) {
        mAnswerTrue = answerTrue;
    }
}

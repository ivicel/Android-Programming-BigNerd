package info.ivicel.geoquiz;

/**
 * Created by sedny on 10/09/2017.
 */

public class Question {
    private int mTextResId;
    private boolean mAnswerTrue;
    private boolean mIsAnswered;
    
    public Question(int textResId, boolean answerTrue) {
        mTextResId = textResId;
        mAnswerTrue = answerTrue;
        mIsAnswered = false;
    }
    
    public boolean isAnswered() {
        return mIsAnswered;
    }
    
    public void setAnswered(boolean answered) {
        mIsAnswered = answered;
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

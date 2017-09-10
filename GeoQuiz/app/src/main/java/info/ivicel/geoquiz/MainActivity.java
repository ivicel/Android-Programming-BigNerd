package info.ivicel.geoquiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String KEY_INDEX = "index";
    private static final String CHEAT_LIST_INDEX = "cheat_list_index";
    private static final String CHEAT_COUNT = "cheat_count";
    private static final int REQUEST_CHEAT_CODE = 0;
    
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;
    private boolean mIsCheat;
    private int mCheatCount = 0;
    
    private Question[] mQuestionBank = new Question[] {
        new Question(R.string.question_australia, true),
        new Question(R.string.question_oceans, true),
        new Question(R.string.question_mideast, false),
        new Question(R.string.question_africa, false),
        new Question(R.string.question_americas, true),
        new Question(R.string.question_asia, true)
    };
    
    private List<Integer> mCheatIndexList = new ArrayList<>();
    private int mCurrentIndex = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX);
            mCheatIndexList = savedInstanceState.getIntegerArrayList(CHEAT_LIST_INDEX);
            mCheatCount = savedInstanceState.getInt(CHEAT_COUNT);
            if (mCheatIndexList != null) {
                for (int index : mCheatIndexList) {
                    mQuestionBank[index].setCheat(true);
                }
            }
        }
    
        mNextButton = (Button)findViewById(R.id.next_button);
        mQuestionTextView = (TextView)findViewById(R.id.question_text_view);
        mTrueButton = (Button)findViewById(R.id.true_button);
        mFalseButton = (Button)findViewById(R.id.false_button);
        mNextButton = (Button)findViewById(R.id.next_button);
        mCheatButton = (Button)findViewById(R.id.cheat_button);
        updateQuestion();
    
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });
    
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });
    
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuestionBank[mCurrentIndex++].setCheat(mIsCheat);
                mCurrentIndex = mCurrentIndex % mQuestionBank.length;
                mIsCheat = false;
                updateQuestion();
            }
        });
        
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(MainActivity.this, answerIsTrue,
                        mCheatCount);
                startActivityForResult(intent, REQUEST_CHEAT_CODE);
            }
        });
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INDEX, mCurrentIndex);
        outState.putIntegerArrayList(CHEAT_LIST_INDEX, (ArrayList<Integer>)mCheatIndexList);
        outState.putInt(CHEAT_COUNT, mCheatCount);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CHEAT_CODE) {
            if (data == null) {
                return;
            }
            mIsCheat = CheatActivity.wasAnswerShown(data);
            if (mIsCheat) {
                mCheatIndexList.add(mCurrentIndex);
                mCheatCount++;
            }
        }
    }
    
    private void updateQuestion() {
        Question question = mQuestionBank[mCurrentIndex];
        mQuestionTextView.setText(question.getTextResId());
        mIsCheat = question.isCheat();
    }
    
    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId;
        
        if (mIsCheat) {
            messageResId = R.string.judgment_toast;
        } else {
            if (answerIsTrue == userPressedTrue) {
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        showToast(messageResId);
    }
    
    private void showToast(int message) {
        Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 300);
        toast.show();
    }
}

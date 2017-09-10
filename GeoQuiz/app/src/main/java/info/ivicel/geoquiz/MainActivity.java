package info.ivicel.geoquiz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionTextView;
    
    private Question[] mQuestionBank = new Question[] {
        new Question(R.string.question_australia, true),
        new Question(R.string.question_oceans, true),
        new Question(R.string.question_mideast, false),
        new Question(R.string.question_africa, false),
        new Question(R.string.question_americas, true),
        new Question(R.string.question_asia, true)
    };
    
    private int mCurrentIndex = 0;
    private int mAnswered = mQuestionBank.length;
    private int mAnsweredRight = 0;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    
        mNextButton = (ImageButton)findViewById(R.id.next_button);
        mQuestionTextView = (TextView)findViewById(R.id.question_text_view);
        mTrueButton = (Button)findViewById(R.id.true_button);
        mFalseButton = (Button)findViewById(R.id.false_button);
        
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });
        
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
    
        mNextButton = (ImageButton)findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });
    
        mPrevButton = (ImageButton)findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex == 0) ? (mQuestionBank.length - 1) :
                        (mCurrentIndex - 1);
                updateQuestion();
            }
        });
    
        updateQuestion();
    }
    
    private void updateQuestion() {
        Question question = mQuestionBank[mCurrentIndex];
        mQuestionTextView.setText(question.getTextResId());
        mTrueButton.setEnabled(!question.isAnswered());
        mFalseButton.setEnabled(!question.isAnswered());
    }
    
    private void checkAnswer(boolean userPressedTrue) {
        Question question = mQuestionBank[mCurrentIndex];
        boolean answerIsTrue = question.isAnswerTrue();
        int messageResId;
    
        if (answerIsTrue == userPressedTrue) {
            messageResId = R.string.correct_toast;
            mAnsweredRight++;
        } else {
            messageResId = R.string.incorrect_toast;
        }
        question.setAnswered(true);
        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
        showToast(messageResId);
    
        if (--mAnswered == 0) {
            String result = getResources().getString(R.string.answer_result,
                    ((float)mAnsweredRight / mQuestionBank.length));
            showToast(result);
        }
    }
    
    private void showToast(int msgResId) {
        Toast toast = Toast.makeText(MainActivity.this, msgResId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 300);
        toast.show();
    }
    
    private void showToast(String message) {
        Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 300);
        toast.show();
    }
}

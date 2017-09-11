package info.ivicel.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by sedny on 10/09/2017.
 */

public class CheatActivity extends AppCompatActivity {
    public static final String EXTRA_ANSWER_IS_TRUE = "info.ivicel.geoquiz.answer_is_true";
    public static final String EXTRA_ANSWER_SHOWN = "info.ivicel.geoquiz.answer_shown";
    public static final String EXTRA_CHEAT_COUNT = "info.ivicel.geoquiz.cheat_count";
    
    private static final int MAX_CHEAT_COUNT = 3;
    private boolean mAnswerIsTrue;
    private TextView mAnswerTextView;
    private Button mShowAnswerButton;
    private boolean mIsAnswerShown = false;
    private TextView mAPITextView;
    private int mCheatCount;
    
    public static Intent newIntent(Context packageContext, boolean answerIsTrue,
            int cheatCount) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        intent.putExtra(EXTRA_CHEAT_COUNT, cheatCount);
        return intent;
    }
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);
    
        if (savedInstanceState != null) {
            mIsAnswerShown = savedInstanceState.getBoolean(EXTRA_ANSWER_SHOWN);
            if (mIsAnswerShown) {
                setAnswerShownResult();
            }
            mCheatCount = savedInstanceState.getInt(EXTRA_CHEAT_COUNT);
        }
        
        Intent intent = getIntent();
        mAnswerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mCheatCount = intent.getIntExtra(EXTRA_CHEAT_COUNT, 0);
        mAnswerTextView = (TextView)findViewById(R.id.answer_text_view);
        mShowAnswerButton = (Button)findViewById(R.id.show_answer_button);
        mAPITextView = (TextView)findViewById(R.id.api_version);
        mAPITextView.setText(getResources().getString(R.string.api_level, Build.VERSION.SDK_INT));
    
        if (mCheatCount == MAX_CHEAT_COUNT) {
            mShowAnswerButton.setEnabled(false);
        }
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsAnswerShown) {
                    mCheatCount++;
                    Toast.makeText(CheatActivity.this, getResources().getString(
                            R.string.remain_cheat_count, MAX_CHEAT_COUNT - mCheatCount),
                            Toast.LENGTH_SHORT).show();
                }
                mIsAnswerShown = true;
                setAnswerShown();
                setAnswerShownResult();
    
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int cx = v.getWidth() / 2;
                    int cy = v.getWidth() / 2;
                    float radius = v.getWidth();
                    Animator animator = ViewAnimationUtils.createCircularReveal(v,
                            cx, cy, radius, 0);
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowAnswerButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    animator.start();
                } else {
                    v.setVisibility(View.INVISIBLE);
                }
            }
        });
    
        if (mIsAnswerShown) {
            setAnswerShown();
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_ANSWER_SHOWN, mIsAnswerShown);
        outState.putInt(EXTRA_CHEAT_COUNT, mCheatCount);
    }
    
    
    private void setAnswerShownResult() {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, mIsAnswerShown);
        setResult(RESULT_OK, data);
    }
    
    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }
    
    private void setAnswerShown() {
        if (mAnswerIsTrue) {
            mAnswerTextView.setText(R.string.true_button);
        } else {
            mAnswerTextView.setText(R.string.false_button);
        }
    }
}

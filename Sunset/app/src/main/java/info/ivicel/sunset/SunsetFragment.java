package info.ivicel.sunset;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;

/**
 * Created by Ivicel on 03/10/2017.
 */

public class SunsetFragment extends Fragment {
    private static final String TAG = "SunsetFragment";
    
    private View mSceneView;
    private View mSunView;
    private View mSkyView;
    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;
    private boolean mIsFallDown = false;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragemnt_sunset, container, false);
        
        mSceneView = view;
        mSkyView = view.findViewById(R.id.sky);
        mSunView = view.findViewById(R.id.sun);
    
        Resources resources = getResources();
        if (Build.VERSION.SDK_INT >= 23) {
            mBlueSkyColor = resources.getColor(R.color.blue_sky, null);
            mSunsetSkyColor = resources.getColor(R.color.sunset_sky, null);
            mNightSkyColor = resources.getColor(R.color.night_sky, null);
        } else {
            mBlueSkyColor = resources.getColor(R.color.blue_sky);
            mSunsetSkyColor = resources.getColor(R.color.sunset_sky);
            mNightSkyColor = resources.getColor(R.color.night_sky);
        }
        
        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation();
            }
        });
        
        return view;
    }
    
    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }
    
    private void startAnimation() {
        if (mIsFallDown) {
            sunriseAnimator();
        } else {
            sunsetAnimator();
        }
        mIsFallDown = !mIsFallDown;
    }
    
    private void sunsetAnimator() {
        float sunYStart = mSunView.getTop();
        float sunYEnd = mSkyView.getHeight();
    
        ObjectAnimator heightAnimator = ObjectAnimator.ofFloat(mSunView, "y", sunYStart, sunYEnd)
                .setDuration(3000);
    
        ObjectAnimator sunsetSkyAnimator = ObjectAnimator.ofInt(mSkyView, "backgroundColor",
                mBlueSkyColor, mSunsetSkyColor)
                .setDuration(3000);
        ObjectAnimator nightSkyAnimator = ObjectAnimator.ofInt(mSkyView, "backgroundColor",
                mSunsetSkyColor, mNightSkyColor)
                .setDuration(1500);
    
        heightAnimator.setInterpolator(new AccelerateInterpolator());
        sunsetSkyAnimator.setEvaluator(new ArgbEvaluator());
        nightSkyAnimator.setEvaluator(new ArgbEvaluator());
    
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(heightAnimator).with(sunsetSkyAnimator).before(nightSkyAnimator);
        animatorSet.start();
    }
    
    private void sunriseAnimator() {
        float sunriseYStart = mSkyView.getHeight();
        float sunriseYEnd = mSunView.getTop();
        
        ObjectAnimator heightAnimator = ObjectAnimator.ofFloat(mSunView, "y",
                sunriseYStart, sunriseYEnd)
                .setDuration(3000);
        ObjectAnimator sunriseSkyAnimator = ObjectAnimator.ofInt(mSkyView, "backgroundColor",
                mSunsetSkyColor, mBlueSkyColor)
                .setDuration(3000);
        ObjectAnimator daySkyAnimator = ObjectAnimator.ofInt(mSkyView, "backgroundColor",
                mNightSkyColor, mSunsetSkyColor)
                .setDuration(3000);
        
        heightAnimator.setInterpolator(new AccelerateInterpolator());
        sunriseSkyAnimator.setEvaluator(new ArgbEvaluator());
        daySkyAnimator.setEvaluator(new ArgbEvaluator());
    
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(daySkyAnimator).before(sunriseSkyAnimator).before(heightAnimator)
                ;
        animatorSet.start();
    }
}

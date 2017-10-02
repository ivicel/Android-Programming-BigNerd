package info.ivicel.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivicel on 30/09/2017.
 */

public class BoxDrawingView extends View {
    private static final String TAG = "BoxDrawingView";
    private static final String SUPER_STATE = "superState";
    private static final String BOXEN_LIST = "boxen_list";
    
    private Box mCurrentBox;
    private List<Box> mBoxen = new ArrayList<>();
    private Paint mBoxPaint;
    private Paint mBackgroundPaint;
    
    public BoxDrawingView(Context context) {
        super(context, null);
    }
    
    public BoxDrawingView(Context context,
            @Nullable AttributeSet attrs) {
        super(context, attrs);
        
        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);
        
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF current = new PointF(event.getX(), event.getY());
        
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mCurrentBox = new Box(current);
                mBoxen.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCurrentBox != null) {
                    mCurrentBox.setCurrent(current);
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                mCurrentBox = null;
                break;
        }
        
        return true;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(mBackgroundPaint);
        
        for (Box box : mBoxen) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);
            
            canvas.drawRect(left, top, right, bottom, mBoxPaint);
        }
        
        
    }
    
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable(SUPER_STATE, parcelable);
        bundle.putSerializable(BOXEN_LIST, (ArrayList<Box>)mBoxen);
        return bundle;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle)state;
        mBoxen = (ArrayList<Box>)bundle.getSerializable(BOXEN_LIST);
        super.onRestoreInstanceState(bundle.getParcelable(SUPER_STATE));
    }
}

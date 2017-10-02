package info.ivicel.draganddraw;

import android.graphics.PointF;

/**
 * Created by Ivicel on 30/09/2017.
 */

public class Box {
    private PointF mOrigin;
    private PointF mCurrent;
    
    public Box(PointF origin) {
        mOrigin = origin;
    }
    
    public PointF getCurrent() {
        return mCurrent;
    }
    
    public void setCurrent(PointF current) {
        mCurrent = current;
    }
    
    public PointF getOrigin() {
        return mOrigin;
    }
}

package com.spiderkids.swipecardview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

/**
 * Created by adityaprasetio on 8/17/15.
 */
public class SwipeCards extends RelativeLayout {
    private static final String DEBUG_TAG = "Velocity";
    private VelocityTracker mVelocityTracker = null;
    private int parentWidth;
    private int parentHeight;
    private int position=0;
    private float x,dx,rotation;
    private boolean isRight=false;
    public OnLastPageListener onLastPageListener;

    private BaseAdapter baseAdapter;

    public SwipeCards(Context context) {
        super(context);
        init();
    }

    public SwipeCards(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        for (int x=0;x<2;x++){
            addView(new View(getContext()),x);
        }

    }

    public void refreshView(){
        if (getChildCount()>1){
            removeViewAt(1);
        }
        if (position<baseAdapter.getCount()-1) {
            addView(baseAdapter.getView(position, null, this), getChildCount());
            removeViewAt(0);
            addView(baseAdapter.getView(position + 1, null, this), 0);
        }
        Log.e("",getChildCount()+" childcount");

        position+=1;
    }

    public void nextPage(){
        removeViewAt(getChildCount() - 1);
        if (position==baseAdapter.getCount()-1){
            onLastPageListener.OnLastPage();
        }
        refreshView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                Log.e("",x+"");
                break;
            case MotionEvent.ACTION_MOVE:
                getChildAt(getChildCount()-1).setPivotX(parentWidth / 2);
                getChildAt(getChildCount()-1).setPivotY(parentHeight);

                dx=x-event.getX();
                rotation=90/(320/Math.abs(dx));
                if (dx>0){
                    isRight=false;
                    rotation=-rotation;
                    Log.e("",getChildCount()+" childcount");
                    getChildAt(getChildCount()-1).setRotation(rotation);
                    Log.e("rotation ",""+rotation);

                }else{
                    isRight=true;
                    getChildAt(getChildCount()-1).setRotation(rotation);
                    Log.e("rotation ",""+rotation);
                }
                break;
            case MotionEvent.ACTION_UP:
                ObjectAnimator anim=null;
                if (rotation>-60 || rotation<60){
                    anim = ObjectAnimator.ofFloat(getChildAt(getChildCount() - 1), "rotation", rotation, 0);
                }
                if (rotation>60){
                    anim = ObjectAnimator.ofFloat(getChildAt(getChildCount() - 1), "rotation", rotation, 180);
                }
                if (rotation<-60){
                    anim = ObjectAnimator.ofFloat(getChildAt(getChildCount() - 1), "rotation", rotation, -180);
                }
                if (rotation<-60 || rotation>60){
                    anim.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            nextPage();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                }

                anim.setDuration(200);
                anim.start();
                break;
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setBaseAdapter(BaseAdapter baseAdapter) {
        this.baseAdapter = baseAdapter;
        refreshView();
    }

    public interface OnLastPageListener{
        public void OnLastPage();
    }

    public void setOnLastPageListener(OnLastPageListener onLastPageListener) {
        this.onLastPageListener = onLastPageListener;
    }
}

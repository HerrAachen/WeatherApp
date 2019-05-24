package aaa.weatherapp;

import android.view.MotionEvent;

import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

public class ChartGestureListener implements OnChartGestureListener {
    private final Runnable swipeLeftCallback;
    private final Runnable swipeRightCallback;

    public ChartGestureListener(Runnable swipeLeftCallback, Runnable swipeRightCallback) {
        this.swipeLeftCallback = swipeLeftCallback;
        this.swipeRightCallback = swipeRightCallback;
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        if (isSwipeLeft(me1, me2)) {
            swipeLeftCallback.run();
        }
        if (isSwipeRight(me1, me2)) {
            swipeRightCallback.run();
        }
    }

    private boolean isSwipeLeft(MotionEvent start, MotionEvent end) {
        return start.getX() - end.getX() > Math.abs(end.getY() - start.getY());
    }

    private boolean isSwipeRight(MotionEvent start, MotionEvent end) {
        return end.getX() - start.getX() > Math.abs(end.getY() - start.getY());
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }
}

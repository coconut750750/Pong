package com.brandon.pong;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Brandon on 9/21/16.
 */
public class GameTouchListener implements View.OnTouchListener {
    private Handler mHandler;
    int xBefore = 0;
    int x = 0;
    int positionBat;
    final int delay = 1;
    boolean isLeft;

    @Override public boolean onTouch(View v, MotionEvent event) {

        x = (int) event.getX();
        positionBat = GameState._bottomBatX+GameState._batLength/2;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mHandler != null) return true;
                mHandler = new Handler();
                if (x > positionBat) {
                    isLeft = false;
                } else {
                    isLeft = true;
                }
                mHandler.postDelayed(action, delay);
                xBefore = x;

                break;
            case MotionEvent.ACTION_UP:
                if (mHandler == null) return true;
                mHandler.removeCallbacks(action);
                mHandler = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (x > positionBat && xBefore <= positionBat) {
                    isLeft = false;
                } else if (x < positionBat && xBefore >= positionBat) {
                    isLeft = true;
                }
                xBefore = x;

                break;

        }
        return false;
    }
    Runnable action = new Runnable() {
        @Override public void run() {

            GameState.mKeyPressed(isLeft, x);
            mHandler.postDelayed(this, delay);

        }
    };
}

package com.brandon.pong;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/***
 * Created by Brandon on 9/21/16.
 */
public class GameTouchListener implements View.OnTouchListener {
    private Handler mHandler;
    private int x = 0;
    private int positionBat;
    final private int delay = 1;
    private int bat;
    //private boolean isLeft;

    public GameTouchListener(int bat){
        this.bat = bat;
    }

    @Override public boolean onTouch(View v, MotionEvent event) {

        x = (int) event.getX();
        positionBat = GameState._bottomBatX+GameState._batLength/2;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mHandler != null) return true;
                mHandler = new Handler();
                //isLeft = x <= positionBat;
                mHandler.postDelayed(action, delay);

                break;
            case MotionEvent.ACTION_UP:
                if (mHandler == null) return true;
                mHandler.removeCallbacks(action);
                mHandler = null;
                break;
            case MotionEvent.ACTION_MOVE:
                //isLeft = x <= positionBat;
                break;

        }
        return false;
    }
    private Runnable action = new Runnable() {
        @Override public void run() {

            //GameState.mKeyPressed(isLeft, x);
            GameState.mKeyPressed(x, bat);
            mHandler.postDelayed(this, delay);

        }
    };
}

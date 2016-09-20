package com.brandon.pong;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    static Button button;
    static GameView gameView;
    static int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button)findViewById(R.id.button);
        gameView = (GameView)findViewById(R.id.gameView);

        gameView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                height = gameView.getHeight();
            }
        });

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x;

        button.setOnTouchListener(new View.OnTouchListener() {
            private Handler mHandler;
            int xBefore = 0;
            int x = 0;
            int positionBat;
            final int delay = 1;

            @Override public boolean onTouch(View v, MotionEvent event) {

                x = (int) event.getX();
                positionBat = GameState._bottomBatX+GameState._batLength/2;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        if (x > positionBat) {
                            mHandler.postDelayed(actionRight, delay);
                        } else {
                            mHandler.postDelayed(actionLeft, delay);
                        }
                        xBefore = x;

                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(actionRight);
                        mHandler.removeCallbacks(actionLeft);
                        mHandler = null;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (x > positionBat && xBefore <= positionBat) {
                            mHandler.removeCallbacks(actionLeft);
                            mHandler.postDelayed(actionRight, delay);
                        } else if (x < positionBat && xBefore >= positionBat) {
                            mHandler.removeCallbacks(actionRight);
                            mHandler.postDelayed(actionLeft, delay);
                        }
                        xBefore = x;
                        break;

                }
                return false;
            }

            Runnable actionRight = new Runnable() {
                @Override public void run() {
                    GameState.mKeyPressed(false, x);
                    mHandler.postDelayed(this, delay);

                }
            };
            Runnable actionLeft = new Runnable() {
                @Override public void run() {
                    GameState.mKeyPressed(true, x);
                    mHandler.postDelayed(this, delay);

                }
            };
        });
    }

}

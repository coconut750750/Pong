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
            int xBefore = width/2;

            @Override public boolean onTouch(View v, MotionEvent event) {

                int x = (int) event.getX();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mHandler != null) return true;
                        mHandler = new Handler();
                        if (x > width / 2) {
                            mHandler.postDelayed(actionRight, 0);
                        } else {
                            mHandler.postDelayed(actionLeft, 0);
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        if (mHandler == null) return true;
                        mHandler.removeCallbacks(actionRight);
                        mHandler.removeCallbacks(actionLeft);
                        mHandler = null;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (x > width / 2 && xBefore < width / 2) {
                            mHandler.removeCallbacks(actionLeft);
                            mHandler.postDelayed(actionRight, 20);
                        } else if (x < width / 2 && xBefore > width / 2) {
                            mHandler.removeCallbacks(actionRight);
                            mHandler.postDelayed(actionLeft, 20);
                        }
                        xBefore = x;
                        break;

                }
                return false;
            }

            Runnable actionRight = new Runnable() {
                @Override public void run() {
                    GameState.mKeyPressed(false);
                    mHandler.postDelayed(this, 20);

                }
            };
            Runnable actionLeft = new Runnable() {
                @Override public void run() {
                    GameState.mKeyPressed(true);
                    mHandler.postDelayed(this, 20);

                }
            };
        });
    }

}

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

    final static String BALLX = "BALLX";
    final static String BALLY = "BALLY";
    final static String BALLVX = "BALLVX";
    final static String BALLVY = "BALLVY";
    final static String BATX = "BATX";
    final static String RESETBUFFER = "RESETBUFFER";

    static Bundle dataBundle;

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

        button.setOnTouchListener(new GameTouchListener());
    }

    public static void saveData() {
        dataBundle = new Bundle();
        dataBundle.putInt(BALLX, GameState._ballX);
        dataBundle.putInt(BALLY, GameState._ballY);
        dataBundle.putDouble(BALLVX, GameState._ballVelocityX);
        dataBundle.putDouble(BALLVY, GameState._ballVelocityY);
        dataBundle.putInt(BATX, GameState._bottomBatX);
        dataBundle.putInt(RESETBUFFER, GameState.resetBuffer1);
    }

    public static void getData(){
        if(dataBundle != null){
            GameState._ballX = dataBundle.getInt(BALLX);
            GameState._ballY = dataBundle.getInt(BALLY);
            GameState._ballVelocityX = dataBundle.getDouble(BALLVX);
            GameState._ballVelocityY = dataBundle.getDouble(BALLVY);
            GameState._bottomBatX = dataBundle.getInt(BATX);
            GameState._topBatX = dataBundle.getInt(BATX);
            GameState.resetBuffer1 = dataBundle.getInt(RESETBUFFER);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}

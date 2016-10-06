package com.brandon.pong;


import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    Button buttonBot;
    Button buttonTop;
    GameView gameView;
    static int height;

    final static String BALLX = "BALLX";
    final static String BALLY = "BALLY";
    final static String BALLVX = "BALLVX";
    final static String BALLVY = "BALLVY";
    final static String BATX = "BATX";
    final static String RESETBUFFER = "RESETBUFFER";
    final static String SCORE_TOP = "SCORETOP";
    final static String SCORE_BOT = "SCOREBOT";

    static Bundle dataBundle;

    final static String GAME_TYPE = "TYPE";
    final static String SINGLE_PLAYER = "SINGLE";
    final static String DOUBLE_PLAYER = "DOUBLE";

    final static String PLAYER_NUM = "PNUM";
    final static int PLAYER1 = 1;
    final static int PLAYER2 = 2;

    BluetoothSocketListener bsl;
    public static BluetoothSocket bluetoothSocket;
    public static Handler handler;

    public static boolean isDouble;
    public static int playerNum;
    final static String SEPARATOR = "~";
    final static String POSITION = "POS";
    final static String SCORE = "SCORE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String type = intent.getStringExtra(GAME_TYPE);

        bsl = null;
        bluetoothSocket = null;
        playerNum = 0;

        isDouble = false;
        if(type.equals(DOUBLE_PLAYER)){
            isDouble = true;
            bluetoothSocket = BluetoothFragment.socket;
            handler = BluetoothFragment.handler;
            BluetoothSocketListener bsl = new BluetoothSocketListener(bluetoothSocket, handler);
            Thread messageListener = new Thread(bsl);
            messageListener.start();
            playerNum = intent.getIntExtra(PLAYER_NUM, 0);
        }

        if(!isDouble){
            setContentView(R.layout.activity_main);
            buttonTop = (Button)findViewById(R.id.buttonTop);
            buttonTop.setOnTouchListener(new GameTouchListener(this, 0));
        } else {
            setContentView(R.layout.activity_main_double);
        }

        buttonBot = (Button)findViewById(R.id.buttonBot);
        buttonBot.setOnTouchListener(new GameTouchListener(this, 1));

        gameView = (GameView)findViewById(R.id.gameView);

        gameView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                height = gameView.getHeight();
            }
        });
    }

    public static void saveData() {
        dataBundle = GameState.saveData();
    }

    public static void getData(){
        GameState.getData(dataBundle);
    }

    public static void sendPos(double xPercent, double ballVelX, double ballVelY) {
        OutputStream outStream;
        try {
            outStream = bluetoothSocket.getOutputStream();
            byte[] byteString = (POSITION+SEPARATOR+xPercent+SEPARATOR+ ballVelX + SEPARATOR+ ballVelY+SEPARATOR).getBytes();
            outStream.write(byteString);
        } catch (IOException e) {
        }
    }
    public static void sendScore(int score1, int score2) {
        OutputStream outStream;
        try {
            outStream = bluetoothSocket.getOutputStream();
            byte[] byteString = (SCORE+SEPARATOR+score1+SEPARATOR+score2+SEPARATOR).getBytes();
            outStream.write(byteString);
        } catch (IOException e) {
        }
    }

}

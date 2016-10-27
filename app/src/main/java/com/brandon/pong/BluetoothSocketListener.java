package com.brandon.pong;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Brandon on 10/5/16.
 */

public class BluetoothSocketListener implements Runnable {
    private BluetoothSocket socket;
    private Handler handler;

    public BluetoothSocketListener(BluetoothSocket socket, Handler handler) {
        this.socket = socket;
        this.handler = handler;
    }

    public void run() {
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        try {
            InputStream instream = socket.getInputStream();
            int bytesRead;
            while (true) {
                String message = "";
                bytesRead = instream.read(buffer);
                if (bytesRead != -1) {
                    while ((bytesRead==bufferSize)&&(buffer[bufferSize-1] != 0)) {
                        message = message + new String(buffer, 0, bytesRead);
                        bytesRead = instream.read(buffer);
                    }
                    message = message + new String(buffer, 0, bytesRead -1);
                    String[] data = message.split(MainActivity.SEPARATOR);
                    final String type = data[0].trim();

                    Runnable r = new Runnable() {
                        @Override
                        public void run() {

                        }
                    };
                    if(type.equals(MainActivity.POSITION)) {
                        final double xPercent = Double.parseDouble(data[1].trim());
                        final double ballVelX = Double.parseDouble(data[2].trim());
                        final double ballVelY = Double.parseDouble(data[3].trim());
                        r = new Runnable() {
                            @Override
                            public void run() {
                                GameState.setBallData(xPercent, ballVelX, ballVelY);
                            }
                        };
                    } else if(type.equals(MainActivity.SHAKE)){
                        final String axis = data[1].trim();
                        final double vel = Double.parseDouble(data[2].trim());
                        if(axis == MainActivity.AXIS[0]){
                            GameState.setShakingX(vel);
                        } else{
                            GameState.setShakingY(vel);
                        }

                    } else if (type.equals(MainActivity.SCORE)) {
                        final int score1 = Integer.parseInt(data[2].trim());
                        final int score2 = Integer.parseInt(data[1].trim());
                        r = new Runnable() {
                            @Override
                            public void run() {
                                GameState.setScore(score2, score1);
                            }
                        };
                    } else if (type.equals(MainActivity.PAUSE)){
                        r = new Runnable() {
                            @Override
                            public void run() {
                                GameState.receiverPauseThread();
                                Log.d("paused","recieved");
                            }
                        };
                    }

                    handler.post(r);
                    socket.getInputStream();
                }
            }
        } catch (IOException e) {
            Log.d("BLUETOOTH_COMMS", e.getMessage());
        }
    }
}
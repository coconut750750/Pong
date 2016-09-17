package com.brandon.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.WindowManager;

import java.util.Random;

/***
 * Created by Brandon on 9/16/16.
 */
public class GameState {

    //screen width and height
    static int _screenWidth;
    static int _screenHeight;

    //The ball
    final int _ballSize = 50;
    int _ballX;
    int _ballY;

    double _ballVelocityX;
    double _ballVelocityY;

    //constants
    final static double multiplier = 1.05;
    final static int batBuffer = 10;

    //The bats
    final static int _batLength = 300;
    final static int _batHeight = 50;
    static int _topBatX;
    static int _topBatY;
    static int _bottomBatX;
    static int _bottomBatY;
    final static int _batSpeed = 20;

    //Origin
    static int originX;
    static int originY;

    public GameState(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        _screenWidth = size.x;
        originX = _screenWidth/2;

        float density = context.getResources().getDisplayMetrics().density;
        float px = 500 * density;
        _screenHeight = size.y - 200;
        originY = _screenHeight/2;
        _topBatY = 20;
        _bottomBatY = _screenHeight-20-_batHeight;
        Log.d("hi",""+_screenHeight);
        _topBatX = (_screenWidth/2) - (_batLength / 2);
        _bottomBatX = (_screenWidth/2) - (_batLength / 2);

        _ballX = originX;
        _ballY = originY;

        _ballVelocityX = getBallVelX();
        _ballVelocityY = 10;
    }

    //The update method
    public void update() {

        _ballX += _ballVelocityX;
        _ballY += _ballVelocityY;

        //DEATH!
        if(_ballY+_ballSize > _screenHeight || _ballY < 0) {
            _ballVelocityX = getBallVelX();

            if (_ballY+_ballSize > _screenHeight) {
                _ballVelocityY = 10;
            } else {
                _ballVelocityY = -10;
            }
            _ballX = originX;
            _ballY = originY;
        }


        //Collisions with the sides
        if(_ballX+_ballSize > _screenWidth || _ballX < 0)
            _ballVelocityX *= -1;

        //Collisions with the bottom bat
        if(_ballX > _topBatX && _ballX+_ballSize < _topBatX+_batLength && _ballY-_ballSize < _topBatY && _ballY-_ballSize > _topBatY-batBuffer){
            _ballVelocityX = _ballVelocityX*multiplier;
            _ballVelocityY = _ballVelocityY*-1*multiplier;
        }

        //Collisions with the top bat
        if(_ballX > _bottomBatX && _ballX+_ballSize < _bottomBatX+_batLength && _ballY+_ballSize > _bottomBatY && _ballY+_ballSize < _bottomBatY+batBuffer) {
            _ballVelocityX = _ballVelocityX*multiplier;
            _ballVelocityY = _ballVelocityY*-1*multiplier;
        }
    }

    public static boolean mKeyPressed(boolean isLeft)
    {
        if(isLeft) //left
        {
            if(_topBatX > 0) {
                _topBatX -= _batSpeed;
                _bottomBatX -= _batSpeed;
            }
        }

        else //right
        {
            if(_topBatX+_batLength < _screenWidth) {
                _topBatX += _batSpeed;
                _bottomBatX += _batSpeed;
            }
        }

        return true;
    }

    //the draw method
    public void draw(Canvas canvas, Paint paint) {

//Clear the screen
        canvas.drawRGB(20, 20, 20);

//set the colour
        paint.setARGB(200, 0, 200, 0);

//draw the ball
        canvas.drawRect(new Rect(_ballX,_ballY,_ballX + _ballSize,_ballY + _ballSize),
                paint);

//draw the bats
        canvas.drawRect(new Rect(_topBatX, _topBatY, _topBatX + _batLength,
                _topBatY + _batHeight), paint); //top bat
        canvas.drawRect(new Rect(_bottomBatX, _bottomBatY, _bottomBatX + _batLength,
                _bottomBatY + _batHeight), paint); //bottom bat

    }

    public static double getBallVelX(){
        double velX;
        Random r = new Random();
        velX = r.nextInt(13 - 5) + 5;

        r = new Random();
        int i = r.nextInt(3 - 1) + 1;
        if(i == 1){
            velX = -1*velX;
        }
        return velX;
    }
}
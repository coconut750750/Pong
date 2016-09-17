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

/**
 * Created by Brandon on 9/16/16.
 */
public class GameState {

    //screen width and height
    int _screenWidth;
    int _screenHeight;

    //The ball
    final int _ballSize = 50;
    int _ballX = 500;
    int _ballY = 500;

    double _ballVelocityX = 10;
    double _ballVelocityY = 10;

    //The bats
    final int _batLength = 300;
    final int _batHeight = 50;
    static int _topBatX;
    static int _topBatY;
    static int _bottomBatX;
    static int _bottomBatY;
    final static int _batSpeed = 20;

    public GameState(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        _screenWidth = size.x;

        float density = context.getResources().getDisplayMetrics().density;
        float px = 500 * density;
        _screenHeight = (int)px;
        _topBatY = 20;
        _bottomBatY = _screenHeight-20-_batHeight;
        Log.d("hi",""+_screenHeight);
        _topBatX = (_screenWidth/2) - (_batLength / 2);
        _bottomBatX = (_screenWidth/2) - (_batLength / 2);
    }

    //The update method
    public void update() {

        _ballX += _ballVelocityX;
        _ballY += _ballVelocityY;



        //DEATH!
        if(_ballY > _screenHeight || _ballY < 0)
        {_ballX = 500;
            _ballY = 500;
            _ballVelocityX = 10;
            _ballVelocityY = 10;}

        //Collisions with the sides
        if(_ballX > _screenWidth || _ballX < 0)
            _ballVelocityX *= -1;

        //Collisions with the bats
        if(_ballX > _topBatX && _ballX < _topBatX+_batLength && _ballY < _topBatY){
            _ballVelocityX = _ballVelocityX*1.05;
            _ballVelocityY = _ballVelocityY*-1.05;
        }

        //Collisions with the bats
        if(_ballX > _bottomBatX && _ballX < _bottomBatX+_batLength
                && _ballY > _bottomBatY) {
            _ballVelocityX = _ballVelocityX*1.05;
            _ballVelocityY = _ballVelocityY*-1.05;
        }
    }

    public static boolean mKeyPressed(boolean isLeft)
    {
        if(isLeft) //left
        {
            _topBatX += _batSpeed; _bottomBatX -= _batSpeed;
        }

        else //right
        {
            _topBatX -= _batSpeed; _bottomBatX += _batSpeed;
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
}
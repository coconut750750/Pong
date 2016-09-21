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
    static int _ballX;
    static int _ballY;

    static double _ballVelocityX;
    static double _ballVelocityY;

    //constants
    final static double multiplier = 1.05;
    final static int maxBallSpeed = 30;
    final static int resetBuffer = 50;
    static int resetBuffer1;
    static int ballDifferenceX;
    static int ballDifferenceY;
    static int batDifference;

    //The bats
    final static int _batLength = 300;
    final static int _batHeight = 50;
    static int _topBatX;
    static int _topBatY;
    static int _bottomBatX;
    static int _bottomBatY;
    final static int _batSpeed = 1;
    static boolean batEnabled;
    final static int batWallBuffer = 50;

    //Origin
    static int originX;
    static int originY;
    static int batOrigin;

    public GameState(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        _screenWidth = size.x;
        originX = _screenWidth/2-_ballSize/2;

        _screenHeight = MainActivity.height;
        originY = _screenHeight/2-_ballSize/2;
        _topBatY = batWallBuffer;
        _bottomBatY = _screenHeight-batWallBuffer-_batHeight;
        _topBatX = (_screenWidth/2) - (_batLength / 2);
        _bottomBatX = (_screenWidth/2) - (_batLength / 2);

        _ballX = originX;
        _ballY = originY;
        ballDifferenceX = 0;
        ballDifferenceY = 0;
        batOrigin = (_screenWidth/2) - (_batLength / 2);

        _ballVelocityX = getBallVelX();
        _ballVelocityY = Math.sqrt(100-_ballVelocityX*_ballVelocityX);


        resetBuffer1 = 1;
        batDifference = 0;
        batEnabled = true;
    }

    //The update method
    public void update() {

        if(resetBuffer1!= 0 && resetBuffer1!=resetBuffer){
            _bottomBatX -= batDifference/resetBuffer;
            _topBatX -= batDifference/resetBuffer;
            //_ballX -= ballDifferenceX/resetBuffer;
            //_ballY -= ballDifferenceY/resetBuffer;
            resetBuffer1++;
            return;
        } else if (resetBuffer1 == resetBuffer){
            resetBuffer1 = 0;
            batEnabled = true;
        }

        _ballX += _ballVelocityX;
        _ballY += _ballVelocityY;

        //DEATH!
        if(_ballY+_ballSize > _screenHeight || _ballY < 0) {
            _ballVelocityX = getBallVelX();

            _ballVelocityY = Math.sqrt(100-_ballVelocityX*_ballVelocityX);
            if (_ballY+_ballSize > _screenHeight) {
            } else {
                _ballVelocityY *= -1;
            }
            //reset ball
            _ballX = originX;
            _ballY = originY;

            //reset bat
            resetBuffer1++;
            batDifference = _bottomBatX-batOrigin;
            ballDifferenceX = _ballX-originX;
            ballDifferenceY = _ballY-originY;
            batEnabled = false;
        }


        //Collisions with the sides
        if(_ballX+_ballSize > _screenWidth || _ballX < 0)
            _ballVelocityX *= -1;

        double absBallVelY = Math.abs(_ballVelocityY);
        int afterMult = (int)Math.ceil(absBallVelY*multiplier)+2;

        //Collisions with the bottom bat
        boolean hitTop = false;
        boolean hitBot = false;
        double xAdd = 0;
        int centerBall = _ballX+_ballSize/2;
        int centerBat = 0;
        double batBuffer = _ballVelocityX;
        //maximum x add = 3;
        if(_ballX+_ballSize >= _topBatX-batBuffer && _ballX <= _topBatX+_batLength+batBuffer && _ballY < _topBatY+_batHeight && _ballY > _topBatY+_batHeight-afterMult){
            hitTop = true;
            centerBat = _topBatX+_batLength/2;
        }
        //Collisions with the top bat
        if(_ballX+_ballSize >= _bottomBatX-batBuffer && _ballX <= _bottomBatX+_batLength+batBuffer && _ballY+_ballSize > _bottomBatY && _ballY+_ballSize < _bottomBatY+afterMult) {
            hitBot = true;
            centerBat = _bottomBatX+_batLength/2;
        }

        if(hitTop || hitBot){
            double absoluteDif = centerBall - centerBat;
            double percentDef = absoluteDif/(_batLength/2.0);
            xAdd += percentDef*3;

            double _ballVelocity = Math.sqrt(_ballVelocityX*_ballVelocityX+_ballVelocityY*_ballVelocityY);

            double angle = Math.acos((_ballVelocityX+xAdd)/_ballVelocity);
            double angleMin = 30.0*Math.PI/180;
            double angleMax = 150.0*Math.PI/180;
            if(angle>angleMin && angle < angleMax){
                _ballVelocityX += xAdd;
            }
            _ballVelocityY = Math.sqrt(_ballVelocity*_ballVelocity-_ballVelocityX*_ballVelocityX);
            if(hitBot){
                _ballVelocityY = _ballVelocityY*-1;
            }
            if(_ballVelocity*multiplier < (double)maxBallSpeed) {
                _ballVelocityX = _ballVelocityX * multiplier;
                _ballVelocityY = _ballVelocityY * multiplier;
            }
        }
    }

    public static void mKeyPressed(boolean isLeft, int touchPos)
    {
        if(!batEnabled){
            return;
        }
        if(isLeft) //left
        {
            if(_bottomBatX > 0 && _bottomBatX+_batLength/2>touchPos) {
                _bottomBatX -= _batSpeed;
                _topBatX -= _batSpeed;
            }
        }

        else if(_bottomBatX+_batLength/2<touchPos) //right
        {
            if(_bottomBatX+_batLength < _screenWidth) {
                _bottomBatX += _batSpeed;
                _topBatX += _batSpeed;
            }
        }
    }

    //the draw method
    public void draw(Canvas canvas, Paint paint) {
        try{
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

            paint.setARGB(200,200,200,0);
            canvas.drawRect(new Rect(0, _screenHeight/2+10, _screenWidth,
                    _screenHeight/2-10), paint);

        } catch(NullPointerException e){

        }
    }

    public static double getBallVelX(){
        double velX;
        Random r = new Random();
        velX = r.nextInt(9 - 5) + 5; //range 5-8

        r = new Random();
        int i = r.nextInt(3 - 1) + 1;
        if(i == 1){
            velX = -1*velX;
        }
        return velX;
    }
}
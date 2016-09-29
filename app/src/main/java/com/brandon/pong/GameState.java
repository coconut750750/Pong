package com.brandon.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/***
 * Created by Brandon on 9/16/16.
 */
public class GameState {

    //screen width and height
    private static int _screenWidth;
    private static int _screenHeight;

    //The ball
    private final int _ballSize = 50;
    static int _ballX;
    static int _ballY;

    static double _ballVelocityX;
    static double _ballVelocityY;

    //constants
    private final static double multiplier = 1.05;
    private final static int maxBallSpeed = 30;
    private final static int resetBuffer = 50;
    static int resetBuffer1;
    private static int batDifference;
    private final static double angleMin = 30.0*Math.PI/180;
    private final static double angleMax = Math.PI - angleMin;

    //The bats
    final static int _batLength = 300;
    private final static int _batHeight = 50;
    static int _topBatX;
    private static int _topBatY;
    static int _bottomBatX;
    private static int _bottomBatY;
    private final static int _batSpeed = 2;
    private static boolean batEnabled;
    private final static int batWallBuffer = 50;

    //Origin
    private static int originX;
    private static int originY;
    private static int batOrigin;

    //Score
    private static int scoreBot;
    private static int scoreTop;
    private final static String[] keys = new String[]{"MID","TOP","BOT","TOPLEFT","TOPRIGHT","BOTLEFT","BOTRIGHT"};
    private final static HashMap<Integer, List<Integer>> parseScoreData = new HashMap<>();
    private static HashMap<String, Rect> rectangles = new HashMap<>();

    //Paint
    private static Paint white;

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


        _ballX = originX;
        _ballY = originY;

        batOrigin = (_screenWidth/2) - (_batLength / 2);
        _topBatX = batOrigin;
        _bottomBatX = batOrigin;

        _ballVelocityX = getBallVelX();
        _ballVelocityY = Math.sqrt(100-_ballVelocityX*_ballVelocityX);

        resetBuffer1 = 1;
        batDifference = 0;
        batEnabled = true;

        scoreBot = 0;
        scoreTop = 0;

        parseScoreData.put(0, new ArrayList<>(Arrays.asList(1,2,3,4,5,6)));
        parseScoreData.put(1, new ArrayList<>(Arrays.asList(4,6)));
        parseScoreData.put(2, new ArrayList<>(Arrays.asList(0,1,2,4,5)));
        parseScoreData.put(3, new ArrayList<>(Arrays.asList(0,1,2,4,6)));
        parseScoreData.put(4, new ArrayList<>(Arrays.asList(0,3,4,6)));
        parseScoreData.put(5, new ArrayList<>(Arrays.asList(0,1,2,3,6)));
        parseScoreData.put(6, new ArrayList<>(Arrays.asList(0,1,2,3,5,6)));
        parseScoreData.put(7, new ArrayList<>(Arrays.asList(1,4,6)));
        parseScoreData.put(8, new ArrayList<>(Arrays.asList(0,1,2,3,4,5,6)));
        parseScoreData.put(9, new ArrayList<>(Arrays.asList(0,1,2,3,4,6)));
        int rectLen = _screenWidth/12;
        int rectWid = rectLen/4;
        int SH4 = _screenHeight/4;
        rectangles.put(keys[0], new Rect(11*rectLen/2,SH4-rectWid/2, 13*rectLen/2, SH4+rectWid/2));
        rectangles.put(keys[1], new Rect(11*rectLen/2,SH4-3*rectWid/2-rectLen, 13*rectLen/2, SH4-rectWid/2-rectLen));
        rectangles.put(keys[2], new Rect(11*rectLen/2,SH4+rectWid/2+rectLen, 13*rectLen/2, SH4+3*rectWid/2+rectLen));
        rectangles.put(keys[3], new Rect(11*rectLen/2-rectWid,SH4-rectWid/2-rectLen, 11*rectLen/2, SH4-rectWid/2));
        rectangles.put(keys[4], new Rect(13*rectLen/2+rectWid,SH4-rectWid/2-rectLen, 13*rectLen/2, SH4-rectWid/2));
        rectangles.put(keys[5], new Rect(11*rectLen/2-rectWid,SH4+rectWid/2+rectLen, 11*rectLen/2, SH4+rectWid/2));
        rectangles.put(keys[6], new Rect(13*rectLen/2+rectWid,SH4+rectWid/2+rectLen, 13*rectLen/2, SH4+rectWid/2));
        white = new Paint();
        white.setARGB(255,200,200,200);
    }

    //The update method
    public void update() {

        if(resetBuffer1!= 0 && resetBuffer1!=resetBuffer){
            _bottomBatX -= batDifference/resetBuffer;
            _topBatX -= batDifference/resetBuffer;
            resetBuffer1++;
            return;
        } else if (resetBuffer1 == resetBuffer){
            resetBuffer1 = 0;
            batEnabled = true;
            _topBatX = batOrigin;
            _bottomBatX = batOrigin;
        }

        _ballX += _ballVelocityX;
        _ballY += _ballVelocityY;

        //DEATH!
        if(_ballY+_ballSize > _screenHeight || _ballY < 0) {
            _ballVelocityX = getBallVelX();
            _ballVelocityY = Math.sqrt(100-_ballVelocityX*_ballVelocityX);

            if (_ballY+_ballSize > _screenHeight) {
                scoreTop += 1;
            } else {
                _ballVelocityY *= -1;
                scoreBot += 1;
            }
            if(scoreTop > 9 || scoreBot > 9){
                scoreTop = 0;
                scoreBot = 0;
            }

            //reset ball
            _ballX = originX;
            _ballY = originY;

            //reset bat
            resetBuffer1++;
            batDifference = _bottomBatX-batOrigin;
            batEnabled = false;
        }


        //Collisions with the sides
        if(_ballX+_ballSize > _screenWidth || _ballX < 0)
            _ballVelocityX *= -1;

        int afterMult = (int)Math.ceil(Math.abs(_ballVelocityY)*multiplier)+2;

        //maximum x add = 3;
        if(_ballX+_ballSize >= _topBatX-_ballVelocityX && _ballX <= _topBatX+_batLength+_ballVelocityX){
            boolean hitTop = _ballY < _topBatY+_batHeight && _ballY > _topBatY+_batHeight-afterMult;
            boolean hitBot = _ballY+_ballSize > _bottomBatY && _ballY+_ballSize < _bottomBatY+afterMult;
            if(hitTop || hitBot){
                int centerBall = _ballX+_ballSize/2;

                double tempVelX = _ballVelocityX+6*(centerBall-_bottomBatX)/_batLength - 3; //add percentage ball is off from bat center * 3

                double _ballVelocity = Math.sqrt(_ballVelocityX*_ballVelocityX+_ballVelocityY*_ballVelocityY);
                double angle = Math.acos((tempVelX)/_ballVelocity);

                if(angle>angleMin && angle <angleMax){
                    _ballVelocityX = tempVelX;
                    _ballVelocityY = Math.sqrt(_ballVelocity*_ballVelocity-_ballVelocityX*_ballVelocityX);
                }

                if(hitBot){
                    _ballVelocityY = _ballVelocityY*-1;
                }
                if(_ballVelocity*multiplier < (double)maxBallSpeed) {
                    _ballVelocityX = _ballVelocityX * multiplier;
                    _ballVelocityY = _ballVelocityY * multiplier;
                }
            }
        }
    }

    public static void mKeyPressed(int touchPos)
    {
        if(!batEnabled || _bottomBatX+_batLength/2 == touchPos){
            return;
        }
        else if(_bottomBatX+_batLength/2>touchPos && _bottomBatX>0) //left
        {
            _bottomBatX -= _batSpeed;
            _topBatX -= _batSpeed;
        }

        else if (_bottomBatX+_batLength<_screenWidth) //right
        {
            _bottomBatX += _batSpeed;
            _topBatX += _batSpeed;

        }
    }

    //the draw method
    public void draw(Canvas canvas, Paint paint) {
        try{
            //Clear the screen
            canvas.drawRGB(20, 20, 20);
            //draw points
            drawPoints(canvas);

            paint.setARGB(200, 0, 200, 0);

            //draw the ball
            canvas.drawRect(new Rect(_ballX,_ballY,_ballX + _ballSize,_ballY + _ballSize), paint);
            //draw the bats
            canvas.drawRect(new Rect(_topBatX, _topBatY, _topBatX + _batLength, _topBatY + _batHeight), paint); //top bat
            canvas.drawRect(new Rect(_bottomBatX, _bottomBatY, _bottomBatX + _batLength, _bottomBatY + _batHeight), paint); //bottom bat

            //draw middle line
            paint.setARGB(200,200,200,0);
            canvas.drawRect(new Rect(0, _screenHeight/2+10, _screenWidth, _screenHeight/2-10), paint);



        } catch(NullPointerException e){

        }
    }

    private  static double getBallVelX(){
        int[] possible = new int[]{-5,-6,-7,-8,5,6,7,8};

        int rnd = new Random().nextInt(possible.length);
        return possible[rnd];
    }

    private  void drawPoints(Canvas canvas){

        List<Integer> topData = parseScoreData.get(scoreTop);
        List<Integer> botData = parseScoreData.get(scoreBot);

        for(int i = 0; i <keys.length; i++){
            int j = i%keys.length;
            Rect rect = rectangles.get(keys[j]);
            if(topData.contains(j)){
                canvas.drawRect(rect, white);
            }
            rect.offset(0,_screenHeight/2);
            if(botData.contains(j)){
                canvas.drawRect(rect, white);
            }
            rect.offset(0,-1*_screenHeight/2);

        }
    }
}
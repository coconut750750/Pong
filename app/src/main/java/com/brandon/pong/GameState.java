package com.brandon.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
    private static int _ballSize;
    private static int _ballX;
    private static int _ballY;

    private static double _ballVelocityX;
    private static double _ballVelocityY;

    //constants
    private final static double multiplier = 1.05;
    private final static double multiplierDouble = 1.10;
    private final static int maxBallSpeed = 40;
    private final static int maxBallSpeedDouble = 60;
    private final static int initialBallSpeed = 15;
    private final static int initialBallSpeedDouble = 20;
    private final static int resetBuffer = 50;
    private static int resetBuffer1;
    private static int batDifferenceBot;
    private static int batDifferenceTop;
    private final static double maxAngle = 20*Math.PI/180;

    //The bats
    private static int _batLength;
    private static int _batHeight = 50;
    private static int _topBatX;
    private static int _topBatY;
    private static int _topBatMoving;
    private static int _bottomBatX;
    private static int _bottomBatY;
    private static int _botBatMoving;
    private final static int _batSpeed = 2;
    private final static int _cpuSpeedMult = 7;
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

    //Drawing
    private static Paint white;
    private Paint amber;
    private Paint green;
    private Paint pauseColor;
    private static boolean isPaused;
    private static boolean isDouble;
    private static int playerNum;
    private static boolean ballIsVisible;
    private static int shakingY;
    private static int shakingX;
    private final static int[] shakingProcess = new int[]{0,7,11,13,14,13,11,7,0,-4,-6,-7,-6,-4,0,2,3,2,0,-1};
    private int[][] ballShadows;
    private int ballShadowIndex;
    private boolean ballShadowOn;

    private Context context;

    public GameState(Context context)
    {
        this.context = context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        _screenWidth = size.x;
        _screenHeight = MainActivity.height;
        reset();

        resetShadows();

        setDataForPoints();

        setColors();
    }

    public static void reset(){
        isDouble = MainActivity.isDouble;

        playerNum = MainActivity.playerNum;
        ballIsVisible = true;

        _batLength = _screenWidth/4;
        _batHeight = _batLength/6;
        _ballSize = _batLength/5;


        if(!isDouble) {
            originX = _screenWidth / 2 - _ballSize / 2;
            originY = _screenHeight / 2 - _ballSize / 2;
        } else {
            originX = _screenWidth / 2 - _ballSize / 2;
            originY =  0 - _ballSize / 2;
        }

        _topBatY = batWallBuffer;
        _bottomBatY = _screenHeight-batWallBuffer-_batHeight;

        _topBatMoving = 0;
        _botBatMoving = 0;

        _ballX = originX;
        _ballY = originY;

        batOrigin = (_screenWidth/2) - (_batLength / 2);
        _topBatX = batOrigin;
        _bottomBatX = batOrigin;

        if(playerNum == 1 || !isDouble) {
            _ballVelocityX = getBallVelX();
            if(!isDouble) {
                _ballVelocityY = Math.sqrt(initialBallSpeed * initialBallSpeed - _ballVelocityX * _ballVelocityX);
            } else{
                _ballVelocityY = Math.sqrt(initialBallSpeedDouble * initialBallSpeedDouble - _ballVelocityX * _ballVelocityX);
            }
        } else {
            _ballVelocityY = 0;
            _ballVelocityX = 0;
            ballIsVisible = false;
        }

        resetBuffer1 = 1;
        batDifferenceBot = 0;
        batDifferenceTop = 0;
        batEnabled = true;

        scoreBot = 0;
        scoreTop = 0;

        isPaused = false;
        shakingY = 0;
        shakingX = 0;
    }

    public void resetShadows(){
        ballShadows = new int[10][2];
        ballShadowIndex = 0;
        ballShadowOn = false;
    }

    public void setDataForPoints(){
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
    }

    public void setColors(){
        white = new Paint();
        white.setARGB(200,220,220,220);
        amber = new Paint();
        amber.setColor(ContextCompat.getColor(context, R.color.colorAccent));
        green = new Paint();
        green.setColor(ContextCompat.getColor(context, R.color.colorPrimaryLight));
        pauseColor = new Paint();
        pauseColor.setColor(ContextCompat.getColor(context, R.color.darkWhite));
    }

    //The update method
    public void update() {

        if (isPaused){
            return;
        }

        if(resetBuffer1!= 0 && resetBuffer1!=resetBuffer){
            _bottomBatX -= batDifferenceBot/resetBuffer;
            _topBatX -= batDifferenceTop/resetBuffer;
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

        if(!isDouble) {
            ballShadows[ballShadowIndex][0] = _ballX;
            ballShadows[ballShadowIndex][1] = _ballY;
            ballShadowIndex = (ballShadowIndex + 1) % ballShadows.length;
            if (ballShadowIndex == 0) {
                ballShadowOn = true;
            }
        }

        //DEATH!
        if (_ballY + _ballSize > _screenHeight || (_ballY < 0 && !isDouble)) {
            _ballVelocityX = getBallVelX();
            if(isDouble) {
                _ballVelocityY = Math.sqrt(initialBallSpeedDouble*initialBallSpeedDouble - _ballVelocityX * _ballVelocityX);
            } else{
                _ballVelocityY = Math.sqrt(initialBallSpeed*initialBallSpeed - _ballVelocityX * _ballVelocityX);
            }

            if (_ballY + _ballSize > _screenHeight) {
                scoreTop += 1;
            } else {
                _ballVelocityY *= -1;
                scoreBot += 1;
            }
            if (scoreTop > 9 || scoreBot > 9) {
                scoreTop = 0;
                scoreBot = 0;
            }

            //reset ball
            _ballX = originX;
            _ballY = originY;

            //reset bat
            resetBuffer1++;
            batDifferenceBot = _bottomBatX - batOrigin;
            batDifferenceTop = _topBatX - batOrigin;
            batEnabled = false;
            if(isDouble){
                MainActivity.sendScore(scoreBot, scoreTop);
            }
            resetShadows();
        } else if (_ballY < 0 && isDouble && _ballVelocityY < 0) {
            double xPercent = (_ballX+_ballSize/2)/(double)_screenWidth;
            MainActivity.sendPos(xPercent, _ballVelocityX/_screenWidth, _ballVelocityY/_screenHeight);
            _ballVelocityY = 0;
            _ballVelocityX = 0;
            ballIsVisible = false;
        }

        //Collisions with the sides
        if(_ballX+_ballSize > _screenWidth || _ballX < 0) {
            _ballVelocityX *= -1;
            shakingX = 1;
        }

        int afterMult = (int)Math.ceil(Math.abs(_ballVelocityY)*multiplier)+2;

        double batBuffer = Math.abs(_ballVelocityX);

        if(!isDouble) {
            if (_ballX + _ballSize >= _topBatX - batBuffer && _ballX <= _topBatX + _batLength + batBuffer) {
                boolean hitTop = _ballY < _topBatY + _batHeight && _ballY > _topBatY + _batHeight - afterMult;
                if (hitTop) {
                    bounce(true);
                }
            }
        }
        if (_ballX+_ballSize >= _bottomBatX-batBuffer && _ballX <= _bottomBatX+_batLength+batBuffer){
            boolean hitBot = _ballY+_ballSize > _bottomBatY && _ballY+_ballSize < _bottomBatY+afterMult;
            if(hitBot){
                bounce(false);
            }
        }

        //cpu moves bat
        if(_ballVelocityY < 0) {
            for(int i = 0; i<_cpuSpeedMult; i++) {
                mKeyPressed(_ballX, 0);
            }
        }
    }

    public void bounce(boolean hitTop){
        double tempx = _ballVelocityX;

        double _ballVelocity = getBallVelocity();
        double perc = _ballVelocityY/_ballVelocity;
        double angleAdd;
        if(hitTop) {
            angleAdd = maxAngle * perc * _topBatMoving;
        } else {
            angleAdd = maxAngle * perc * _botBatMoving;
        }
        addAngle(-1*angleAdd);

        _ballVelocityY = Math.abs(_ballVelocityY);
        if(!hitTop) {
            _ballVelocityY = _ballVelocityY * -1;
        }

        if(tempx<0){
            _ballVelocityX *= -1;
        }

        if(!isDouble && _ballVelocity*multiplier < (double)maxBallSpeed) {
            _ballVelocityX = _ballVelocityX * multiplier;
            _ballVelocityY = _ballVelocityY * multiplier;
        }
        if(isDouble && _ballVelocity*multiplierDouble < (double)maxBallSpeedDouble){
            _ballVelocityX = _ballVelocityX * multiplierDouble;
            _ballVelocityY = _ballVelocityY * multiplierDouble;
        }

        shakingY = 1;

    }

    public static void mKeyPressed(int touchPos, int bat)
    {
        if (isPaused){
            return;
        }
        if(bat == 0){
            int topX =  move(touchPos, _topBatX);
            if(topX == _topBatX){
                _topBatMoving = 0;
            } else if (topX > _topBatX) {
                _topBatMoving = 1;
            } else {
                _topBatMoving = -1;
            }
            _topBatX = topX;
        } else{
            int botX =  move(touchPos, _bottomBatX);
            if(botX == _bottomBatX){
                _botBatMoving = 0;
            } else if (botX > _bottomBatX) {
                _botBatMoving = 1;
            } else {
                _botBatMoving = -1;
            }
            _bottomBatX = botX;
        }
    }

    public static int move(int touchPos, int batX){
        if(!batEnabled || batX+_batLength/2 == touchPos){
            return batX;
        }
        else if(batX+_batLength/2>touchPos && batX>0) //left
        {
            batX -= _batSpeed;
        }

        else if (batX+_batLength<_screenWidth) //right
        {
            batX += _batSpeed;
        }
        return batX;
    }

    public static void stopBat(int bat){
        if(bat == 0){
            _topBatMoving = 0;
        } else {
            _botBatMoving = 0;
        }
    }

    //the draw method
    public void draw(Canvas canvas, Paint paint) {
        try{
            //Clear the screen
            canvas.drawRGB(20, 20, 20);
            //draw points

            int shakeY = 0;
            if(shakingY != 0){
                shakeY = shakingProcess[shakingY]*(int)_ballVelocityY/10*-1;
                shakingY++;
                if(shakingY >= shakingProcess.length){
                    shakingY = 0;
                }
            }
            int shakeX = 0;
            if (shakingX != 0) {
                shakeX = shakingProcess[shakingX]*(int)_ballVelocityX/20;
                shakingX++;
                if(shakingX >= shakingProcess.length){
                    shakingX = 0;
                }
            }

            drawPoints(canvas, shakeY, shakeX);

            //draw middle line
            if(!isDouble) {
                canvas.drawRect(new Rect(0, _screenHeight / 2 + 10+shakeY, _screenWidth, _screenHeight / 2 - 10 +shakeY), amber);
            } else {
                canvas.drawRect(new Rect(0, 10+shakeY, _screenWidth, shakeY), amber);
            }

            //draw the ball
            if(ballIsVisible) {
                canvas.drawRect(new Rect(_ballX, _ballY, _ballX + _ballSize, _ballY + _ballSize), green);
                drawShadow(canvas);
            }
            green.setAlpha(255);
            //draw the bats
            if(!isDouble){
                canvas.drawRect(new Rect(_topBatX, _topBatY+shakeY, _topBatX + _batLength, _topBatY + _batHeight +shakeY), green); //top bat
            }

            canvas.drawRect(new Rect(_bottomBatX, _bottomBatY+shakeY, _bottomBatX + _batLength, _bottomBatY + _batHeight +shakeY), green); //bottom bat

            if (isPaused){
                //draw pause sign
                canvas.drawRect(new Rect(_screenWidth/18*5,_screenHeight/3,_screenWidth/18*7,_screenHeight/3*2), pauseColor);
                canvas.drawRect(new Rect(_screenWidth/18*11,_screenHeight/3,_screenWidth/18*13,_screenHeight/3*2), pauseColor);
                MainActivity._thread.onPause();

            }
        } catch(NullPointerException e){

        }
    }

    private  void drawPoints(Canvas canvas, int shakeY, int shakeX){

        List<Integer> topData = parseScoreData.get(scoreTop);
        List<Integer> botData = parseScoreData.get(scoreBot);

        for(int i = 0; i <keys.length; i++){
            int j = i%keys.length;
            Rect rect = rectangles.get(keys[j]);
            rect.offset(shakeX,shakeY);
            if(topData.contains(j)){
                canvas.drawRect(rect, white);
            }
            rect.offset(0,_screenHeight/2);
            if(botData.contains(j)){
                canvas.drawRect(rect, white);
            }
            rect.offset(0,-1*_screenHeight/2);
            rect.offset(-1*shakeX,-1*shakeY);

        }
    }

    private void drawShadow(Canvas canvas){
        if(!ballShadowOn){
            return;
        }
        Paint green2 = green;
        int index = ballShadowIndex;
        for(int i = 9; i > 0; i--){
            index -= 1;
            if(index == -1){
                index = 9;
            }
            int x = ballShadows[index][0];
            int y = ballShadows[index][1];
            green2.setAlpha(255*i/10);
            canvas.drawRect(new Rect(x, y, x + _ballSize, y + _ballSize), green2);
        }

    }

    private  static double getBallVelX(){
        int[] possible = new int[]{-6,-7,-8,-9,6,7,8,9};

        int rnd = new Random().nextInt(possible.length);
        if(isDouble){
            return possible[rnd]*2;
        } else {
            return possible[rnd];
        }
    }

    public static void receiverPauseThread(){
        isPaused = !isPaused;
        if(isPaused){
            //pauses game thread in the update method after drawing signs
            MainActivity.pausedPlayer = 3-MainActivity.playerNum;
        } else {
            MainActivity._thread.onResume();
            MainActivity.pausedPlayer = 0;
        }
    }

    public static void toggleGameState(){
        isPaused = !isPaused;
        if(isPaused){
            //pauses game thread in the update method after drawing signs
            MainActivity.pausedPlayer = playerNum;
        } else {
            MainActivity._thread.onResume();
            MainActivity.pausedPlayer = 0;
        }
        if(isDouble){
            MainActivity.sendPause();
        }
    }

    public static boolean getIsPaused(){
        return isPaused;
    }

    public static void setBallData(double ballXPercent, double ballVelX, double ballVelY){
        _ballX = (int)((1-ballXPercent)*_screenWidth)-_ballSize/2;
        _ballY = originY;
        _ballVelocityX = -1*ballVelX*_screenWidth;
        _ballVelocityY = -1*ballVelY*_screenHeight;
        ballIsVisible = true;

    }

    public static void setScore(int scoreT, int scoreB){
        scoreTop = scoreT;
        scoreBot = scoreB;
    }

    public static Bundle saveData(){
        Bundle dataBundle = new Bundle();
        dataBundle.putInt(MainActivity.BALLX, GameState._ballX);
        dataBundle.putInt(MainActivity.BALLY, GameState._ballY);
        dataBundle.putDouble(MainActivity.BALLVX, GameState._ballVelocityX);
        dataBundle.putDouble(MainActivity.BALLVY, GameState._ballVelocityY);
        dataBundle.putInt(MainActivity.BATX, GameState._bottomBatX);
        dataBundle.putInt(MainActivity.RESETBUFFER, GameState.resetBuffer1);
        dataBundle.putInt(MainActivity.SCORE_TOP, GameState.scoreTop);
        dataBundle.putInt(MainActivity.SCORE_BOT, GameState.scoreBot);
        return dataBundle;
    }

    public static void getData(Bundle dataBundle){
        if(dataBundle != null){
            GameState._ballX = dataBundle.getInt(MainActivity.BALLX);
            GameState._ballY = dataBundle.getInt(MainActivity.BALLY);
            GameState._ballVelocityX = dataBundle.getDouble(MainActivity.BALLVX);
            GameState._ballVelocityY = dataBundle.getDouble(MainActivity.BALLVY);
            GameState._bottomBatX = dataBundle.getInt(MainActivity.BATX);
            GameState._topBatX = dataBundle.getInt(MainActivity.BATX);
            GameState.resetBuffer1 = dataBundle.getInt(MainActivity.RESETBUFFER);
            GameState.scoreTop = dataBundle.getInt(MainActivity.SCORE_TOP);
            GameState.scoreBot = dataBundle.getInt(MainActivity.SCORE_BOT);
        }
    }

    public static double getBallAngle(){
        return Math.atan(_ballVelocityY/_ballVelocityX);
    }

    public static void setBallAngle(double angle){
        double _ballVelocity = getBallVelocity();
        _ballVelocityX = _ballVelocity*Math.cos(angle);
        _ballVelocityY = _ballVelocity*Math.sin(angle);
    }

    public static void addAngle(double angle){
        setBallAngle(angle + getBallAngle());
    }

    public static double getBallVelocity(){
        return  Math.sqrt(_ballVelocityX*_ballVelocityX+_ballVelocityY*_ballVelocityY);
    }
}
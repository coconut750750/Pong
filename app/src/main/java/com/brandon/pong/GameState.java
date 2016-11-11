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
class GameState {

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
    private final static int resetBuffer = 100;
    private final static int displayMsgEvery = 20;
    private static int resetBuffer1;
    private static int batDifferenceBot;
    private static int batDifferenceTop;
    private final static double maxAngle = 20*Math.PI/180;

    //The bats
    private static Paddle topPaddle;
    private static Paddle botPaddle;
    private static Monkey monkey;
    final static int _batSpeed = 2;
    private final static int _cpuSpeedMult = 7;
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

    private final static int winKeys = 10;
    private static HashMap<Integer, Rect> winRectangles = new HashMap<>();

    private final static int loseKeys = 15;
    private static HashMap<Integer, Rect> loseRectangles = new HashMap<>();

    private int rectLen;
    private int rectWid;
    private int SH4;
    private int midX;

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
    private final static int[] shakingProcess = new int[]{7,11,13,14,13,11,7,0,-4,-6,-7,-6,-4,0,2,3,2,0,-1};
    private final static int numShadows = 10;
    private static int[][] ballShadows;
    private static int ballShadowIndex;
    private static boolean win;
    private static boolean lose;
    private static boolean displayMsg;
    private static boolean displayScore;

    private Context context;

    GameState(Context context)
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
        setDataForWin();
        setDataForLose();

        setColors();
    }

    static void reset(){
        isDouble = MainActivity.isDouble;

        playerNum = MainActivity.playerNum;
        ballIsVisible = true;

        Paddle.setLength(_screenWidth/4);
        Paddle.setHeight(Paddle.getLength()/6);
        _ballSize = Paddle.getLength()/5;


        if(!isDouble) {
            originX = _screenWidth / 2 - _ballSize / 2;
            originY = _screenHeight / 2 - _ballSize / 2;
        } else {
            originX = _screenWidth / 2 - _ballSize / 2;
            originY =  0 - _ballSize / 2;
        }

        _ballX = originX;
        _ballY = originY;

        batOrigin = (_screenWidth/2) - (Paddle.getLength() / 2);

        topPaddle = new Paddle(batOrigin, batWallBuffer);
        botPaddle = new Paddle(batOrigin, _screenHeight-batWallBuffer-Paddle.getHeight());
        monkey = new Monkey(0, _screenHeight/2-Paddle.getHeight()/2);
        monkey.setMonkeyLength(Paddle.getLength()/2);
        monkey.setVelocity(5);

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

        scoreBot = 0;
        scoreTop = 0;

        isPaused = false;
        shakingY = 0;
        shakingX = 0;

        win = false;
        lose = false;
        displayMsg = false;
        displayScore = true;
    }

    static void resetShadows(){
        ballShadows = new int[numShadows][2];
        ballShadowIndex = 0;
    }

    private void setDataForPoints(){
        rectLen = _screenWidth/12;
        rectWid = rectLen/4;
        midX = _screenWidth/2;
        SH4 = _screenHeight/4;

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

        rectangles.put(keys[0], new Rect(11*rectLen/2,SH4-rectWid/2, 13*rectLen/2, SH4+rectWid/2));
        rectangles.put(keys[1], new Rect(11*rectLen/2,SH4-3*rectWid/2-rectLen, 13*rectLen/2, SH4-rectWid/2-rectLen));
        rectangles.put(keys[2], new Rect(11*rectLen/2,SH4+rectWid/2+rectLen, 13*rectLen/2, SH4+3*rectWid/2+rectLen));
        rectangles.put(keys[3], new Rect(11*rectLen/2-rectWid,SH4-rectWid/2-rectLen, 11*rectLen/2, SH4-rectWid/2));
        rectangles.put(keys[4], new Rect(13*rectLen/2+rectWid,SH4-rectWid/2-rectLen, 13*rectLen/2, SH4-rectWid/2));
        rectangles.put(keys[5], new Rect(11*rectLen/2-rectWid,SH4+rectWid/2+rectLen, 11*rectLen/2, SH4+rectWid/2));
        rectangles.put(keys[6], new Rect(13*rectLen/2+rectWid,SH4+rectWid/2+rectLen, 13*rectLen/2, SH4+rectWid/2));
    }

    private void setDataForWin(){
        winRectangles.put(0, new Rect(midX-rectWid/2, SH4-rectLen/2, midX+rectWid/2,SH4+rectLen/2));

        winRectangles.put(1, new Rect(midX-5*rectWid/2, SH4-rectLen/2, midX-3*rectWid/2,SH4+rectLen/2));
        winRectangles.put(2, new Rect(midX-7*rectWid/2, SH4+rectLen/4, midX-5*rectWid/2,SH4+rectLen/2));
        winRectangles.put(3, new Rect(midX-9*rectWid/2, SH4-rectLen/2, midX-7*rectWid/2,SH4+rectLen/2));
        winRectangles.put(4, new Rect(midX-11*rectWid/2, SH4+rectLen/4, midX-9*rectWid/2,SH4+rectLen/2));
        winRectangles.put(5, new Rect(midX-13*rectWid/2, SH4-rectLen/2, midX-11*rectWid/2,SH4+rectLen/2));

        winRectangles.put(6, new Rect(midX+3*rectWid/2, SH4-rectLen/2, midX+5*rectWid/2,SH4+rectLen/2));
        winRectangles.put(7, new Rect(midX+5*rectWid/2, SH4-rectLen/2, midX+7*rectWid/2,SH4));
        winRectangles.put(8, new Rect(midX+7*rectWid/2, SH4, midX+9*rectWid/2,SH4+rectLen/2));
        winRectangles.put(9, new Rect(midX+9*rectWid/2, SH4-rectLen/2, midX+11*rectWid/2,SH4+rectLen/2));
    }

    private void setDataForLose(){
        loseRectangles.put(0, new Rect(midX-3*rectWid/2, SH4-rectLen/2, midX-rectWid/2,SH4+rectLen/2));
        loseRectangles.put(1, new Rect(midX-5*rectWid/2, SH4+rectLen/4, midX-3*rectWid/2,SH4+rectLen/2));
        loseRectangles.put(2, new Rect(midX-5*rectWid/2, SH4-rectLen/2, midX-3*rectWid/2,SH4-rectLen/4));
        loseRectangles.put(3, new Rect(midX-7*rectWid/2, SH4-rectLen/2, midX-5*rectWid/2,SH4+rectLen/2));

        loseRectangles.put(4, new Rect(midX-13*rectWid/2, SH4-rectLen/2, midX-11*rectWid/2,SH4+rectLen/2));
        loseRectangles.put(5, new Rect(midX-11*rectWid/2, SH4+rectLen/4, midX-9*rectWid/2,SH4+rectLen/2));

        loseRectangles.put(6, new Rect(midX+rectWid/2, SH4-3*rectLen/10, midX+3*rectWid/2,SH4-rectLen/10));
        loseRectangles.put(7, new Rect(midX+rectWid/2, SH4-rectLen/2, midX+5*rectWid/2,SH4-3*rectLen/10));
        loseRectangles.put(8, new Rect(midX+rectWid/2, SH4-rectLen/10, midX+5*rectWid/2,SH4+rectLen/10));
        loseRectangles.put(9, new Rect(midX+rectWid/2, SH4+3*rectLen/10, midX+5*rectWid/2,SH4+rectLen/2));
        loseRectangles.put(10, new Rect(midX+3*rectWid/2, SH4+rectLen/10, midX+5*rectWid/2,SH4+3*rectLen/10));

        loseRectangles.put(11, new Rect(midX+7*rectWid/2, SH4-rectLen/2, midX+9*rectWid/2,SH4+rectLen/2));
        loseRectangles.put(12, new Rect(midX+9*rectWid/2, SH4-rectLen/2, midX+11*rectWid/2,SH4-3*rectLen/10));
        loseRectangles.put(13, new Rect(midX+9*rectWid/2, SH4-rectLen/10, midX+11*rectWid/2,SH4+rectLen/10));
        loseRectangles.put(14, new Rect(midX+9*rectWid/2, SH4+3*rectLen/10, midX+11*rectWid/2,SH4+rectLen/2));

    }

    private void setColors(){
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
    void update() {

        if (isPaused){
            return;
        }

        if(resetBuffer1!= 0 && resetBuffer1!=resetBuffer){
            botPaddle.move(-1*batDifferenceBot/resetBuffer);
            topPaddle.move(-1*batDifferenceTop/resetBuffer);
            resetBuffer1++;
            if(win || lose) {
                int resetAt = resetBuffer1 % displayMsgEvery;
                if (resetAt == displayMsgEvery / 2) {
                    displayMsg = true;
                } else if (resetAt == 0) {
                    displayMsg = false;
                }
            }
            return;
        } else if (resetBuffer1 == resetBuffer){
            resetBuffer1 = 0;
            Paddle.enable();
            topPaddle.setX(batOrigin);
            botPaddle.setX(batOrigin);
            win = lose = false;
            displayScore = true;
        }

        if(!ballIsVisible){
            return;
        }

        _ballX += _ballVelocityX;
        _ballY += _ballVelocityY;
        monkey.move(monkey.getVelocity());

        ballShadows[ballShadowIndex][0] = _ballX;
        ballShadows[ballShadowIndex][1] = _ballY;
        ballShadowIndex = (ballShadowIndex + 1) % numShadows;


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

            //reset ball
            _ballX = originX;
            _ballY = originY;

            //reset bat
            returnBats();

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

        if (scoreTop > 9 || scoreBot > 9) {
            if(scoreTop > scoreBot){
                lose = true;
            } else{
                win = true;

            }
            scoreTop = 0;
            scoreBot = 0;
            displayScore = false;
        }

        //Collisions with the sides
        if(_ballX+_ballSize > _screenWidth || _ballX < 0) {
            if(_ballX < 0){
                _ballX = 0;
            } else{
                _ballX = _screenWidth-_ballSize;
            }

            _ballVelocityX *= -1;
            shakingX = 1;
            if(isDouble){
                MainActivity.sendShake(MainActivity.AXIS[0],_ballVelocityX);
            }
        }

        if(monkey.getX()+monkey.getMonkeyLength() > _screenWidth || monkey.getX() < 0){
            monkey.bounce();
            if(monkey.getX() < 0){
                monkey.setX(0);
            } else{
                monkey.setX(_screenWidth-monkey.getMonkeyLength());
            }
        }

        int afterMult = (int)Math.ceil(Math.abs(_ballVelocityY)*multiplier)+2;

        double batBuffer = Math.abs(_ballVelocityX);


        if(!isDouble) {
            if (_ballY <= topPaddle.getY() + Paddle.getHeight() && _ballY >= topPaddle.getY() + Paddle.getHeight() - afterMult
                    && _ballX + _ballSize >= topPaddle.getX() - batBuffer && _ballX <= topPaddle.getX() + Paddle.getLength() + batBuffer) {
                bounce(true);
            }
        }
        if (_ballY+_ballSize >= botPaddle.getY() && _ballY+_ballSize <= botPaddle.getY()+afterMult
                && _ballX+_ballSize >= botPaddle.getX()-batBuffer && _ballX <= botPaddle.getX()+Paddle.getLength()+batBuffer){
            bounce(false);
        }
        if (_ballY+_ballSize >= monkey.getY() && _ballY <= monkey.getY()+Paddle.getHeight()
                && _ballX+_ballSize >= monkey.getX() && _ballX <= monkey.getX()+monkey.getMonkeyLength()){
            _ballVelocityY *= -1;
            if(_ballVelocityY > 0){
                _ballY = monkey.getY()+Paddle.getHeight();
            } else {
                _ballY = monkey.getY()-_ballSize;
            }

        }

        //cpu moves bat
        if(_ballVelocityY < 0) {
            mKeyPressed(_ballX, 0, _batSpeed*_cpuSpeedMult);
        }
    }

    private void bounce(boolean hitTop){
        double tempx = _ballVelocityX;

        double _ballVelocity = getBallVelocity();
        double perc = _ballVelocityY/_ballVelocity;
        double angleAdd;
        if(hitTop) {
            angleAdd = maxAngle * perc * topPaddle.getMoving();
        } else {
            angleAdd = maxAngle * perc * botPaddle.getMoving();
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
        if(isDouble){
            MainActivity.sendShake(MainActivity.AXIS[1],_ballVelocityY);
        }
    }

    static void mKeyPressed(int touchPos, int bat, int speed) {
        if (isPaused){
            return;
        }

        if(bat == 0){
            int topX =  move(touchPos, topPaddle.getX(), speed);
            topPaddle.setMoving(0);
            /*if(topX == _topBatX){
                _topBatMoving = 0;
            } else if (topX > _topBatX) {
                _topBatMoving = 1;
            } else {
                _topBatMoving = -1;
            }*/
            topPaddle.setX(topX);
            if(topPaddle.getX()<0)
                topPaddle.setX(0);
        } else{
            int botX =  move(touchPos, botPaddle.getX(), speed);
            if(botX == botPaddle.getX()){
                botPaddle.setMoving(0);
            } else if (botX > botPaddle.getX()) {
                botPaddle.setMoving(1);
            } else {
                botPaddle.setMoving(-1);
            }
            botPaddle.setX(botX);
            if(botPaddle.getX()<0)
                botPaddle.setX(0);
        }
    }

    private static int move(int touchPos, int batX, int speed){
        int length = Paddle.getLength();
        if(!Paddle.getEnabled() || batX+length/2 == touchPos){
            return batX;
        }
        else if(batX+length/2>touchPos && batX>0) //left
        {
            int midPos = batX+length/2-speed;
            if(midPos<touchPos){
                batX = touchPos-length/2;
            } else {
                batX -= speed;
            }
        }

        else if (batX+length<_screenWidth) //right
        {
            int midPos = batX+length/2+speed;
            if(midPos>touchPos){
                batX = touchPos-length/2;
            } else {
                batX += speed;
            }
        }
        return batX;
    }

    static void stopBat(int bat){
        if(bat == 0){
            topPaddle.setMoving(0);
        } else {
            botPaddle.setMoving(0);
        }
    }

    //the draw method
    void draw(Canvas canvas) {
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

            if(displayScore){
                drawPoints(canvas, shakeY, shakeX);
            }

            if(displayMsg) {
                drawMsg(canvas, shakeY, shakeX, win);
            }

            //draw middle line
            if(!isDouble) {
                canvas.drawRect(new Rect(0, _screenHeight / 2 + 10+shakeY, _screenWidth, _screenHeight / 2 - 10 +shakeY), amber);
            } else {
                canvas.drawRect(new Rect(0, 10+shakeY, _screenWidth, shakeY), amber);
            }

            //draw monkey
            canvas.drawRect(new Rect(monkey.getX(), monkey.getY()+shakeY, monkey.getX() + monkey.getMonkeyLength(), monkey.getY() + Paddle.getHeight()+shakeY), green);

            //draw the ball
            if(ballIsVisible) {
                canvas.drawRect(new Rect(_ballX, _ballY, _ballX + _ballSize, _ballY + _ballSize), green);
                drawShadow(canvas);
            }
            green.setAlpha(255);
            //draw the bats
            if(!isDouble){
                canvas.drawRect(new Rect(topPaddle.getX(), topPaddle.getY()+shakeY, topPaddle.getX() + Paddle.getLength(), topPaddle.getY() + Paddle.getHeight() +shakeY), green); //top bat
            }

            canvas.drawRect(new Rect(botPaddle.getX(), botPaddle.getY()+shakeY, botPaddle.getX() + Paddle.getLength(), botPaddle.getY() + Paddle.getHeight() +shakeY), green); //bottom bat

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

    private void drawMsg(Canvas canvas, int shakeY, int shakeX, boolean playerWon){
        HashMap<Integer, Rect> rects;
        int keys;
        if (playerWon) {
            keys = winKeys;
            rects = winRectangles;
        } else {
            keys = loseKeys;
            rects = loseRectangles;
        }
        for(int i = 0; i <keys; i++){
            Rect rect = rects.get(i);
            rect.offset(shakeX,shakeY);
            canvas.drawRect(rect, white);

            rect.offset(0,_screenHeight/2);
            canvas.drawRect(rect, white);

            rect.offset(0,-1*_screenHeight/2);
            rect.offset(-1*shakeX,-1*shakeY);

        }
    }

    private void drawShadow(Canvas canvas){
        Paint green2 = green;
        int index = ballShadowIndex;
        for(int i = numShadows-1; i > 0; i--){
            index = (index+numShadows-1)%numShadows;

            if(ballShadows[index][0] == 0 && ballShadows[index][1] == 0){
                return;
            }
            int x = ballShadows[index][0];
            int y = ballShadows[index][1];
            green2.setAlpha(255*i/numShadows);
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

    static void receiverPauseThread(){
        isPaused = !isPaused;
        if(isPaused){
            //pauses game thread in the update method after drawing signs
            MainActivity.pausedPlayer = 3-MainActivity.playerNum;
        } else {
            MainActivity._thread.onResume();
            MainActivity.pausedPlayer = 0;
        }
    }

    static void toggleGameState(){
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

    static boolean getIsPaused(){
        return isPaused;
    }

    static void setBallData(double ballXPercent, double ballVelX, double ballVelY){
        _ballX = (int)((1-ballXPercent)*_screenWidth-_ballSize/2);
        _ballY = originY;
        _ballVelocityX = -1*ballVelX*_screenWidth;
        _ballVelocityY = -1*ballVelY*_screenHeight;
        ballIsVisible = true;
        resetShadows();
    }

    static void setScore(int scoreT, int scoreB){
        scoreTop = scoreT;
        scoreBot = scoreB;
        win = scoreB > 9;
        lose = scoreT > 9;
        if(win || lose) {
            scoreTop = 0;
            scoreBot = 0;
            displayScore = false;
        }
    }

    static void returnBats(){
        resetBuffer1++;
        batDifferenceBot = botPaddle.getX() - batOrigin;
        batDifferenceTop = topPaddle.getX() - batOrigin;
        Paddle.disable();
    }

    static Bundle saveData(){
        Bundle dataBundle = new Bundle();
        dataBundle.putInt(MainActivity.BALLX, GameState._ballX);
        dataBundle.putInt(MainActivity.BALLY, GameState._ballY);
        dataBundle.putDouble(MainActivity.BALLVX, GameState._ballVelocityX);
        dataBundle.putDouble(MainActivity.BALLVY, GameState._ballVelocityY);
        dataBundle.putInt(MainActivity.BATBX, botPaddle.getX());
        dataBundle.putInt(MainActivity.BATTX, topPaddle.getX());
        dataBundle.putInt(MainActivity.RESETBUFFER, GameState.resetBuffer1);
        dataBundle.putInt(MainActivity.SCORE_TOP, GameState.scoreTop);
        dataBundle.putInt(MainActivity.SCORE_BOT, GameState.scoreBot);
        return dataBundle;
    }

    static void getData(Bundle dataBundle){
        if(dataBundle != null){
            GameState._ballX = dataBundle.getInt(MainActivity.BALLX);
            GameState._ballY = dataBundle.getInt(MainActivity.BALLY);
            GameState._ballVelocityX = dataBundle.getDouble(MainActivity.BALLVX);
            GameState._ballVelocityY = dataBundle.getDouble(MainActivity.BALLVY);
            botPaddle.setX(dataBundle.getInt(MainActivity.BATBX));
            topPaddle.setX(dataBundle.getInt(MainActivity.BATTX));

            GameState.resetBuffer1 = dataBundle.getInt(MainActivity.RESETBUFFER);
            GameState.scoreTop = dataBundle.getInt(MainActivity.SCORE_TOP);
            GameState.scoreBot = dataBundle.getInt(MainActivity.SCORE_BOT);
        }
    }

    private static double getBallAngle(){
        return Math.atan(_ballVelocityY/_ballVelocityX);
    }

    private static void setBallAngle(double angle){
        double _ballVelocity = getBallVelocity();
        _ballVelocityX = _ballVelocity*Math.cos(angle);
        _ballVelocityY = _ballVelocity*Math.sin(angle);
    }

    private static void addAngle(double angle){
        setBallAngle(angle + getBallAngle());
    }

    private static double getBallVelocity(){
        return  Math.sqrt(_ballVelocityX*_ballVelocityX+_ballVelocityY*_ballVelocityY);
    }

    static boolean getIsDouble(){
        return isDouble;
    }

    static void setShakingX(double vel){
        _ballVelocityX = -1*vel;
        shakingX = 1;
    }
    static void setShakingY(double vel){
        _ballVelocityY = -1*vel;
        shakingY = 1;
    }
}
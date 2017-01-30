package com.brandon.pong;

/***
 * Created by Brandon on 11/11/16.
 */

class Paddle {
    private int xPos;
    private int yPos;
    private int isMoving;
    private static boolean isEnabled;

    private int length;
    private static int height;

    Paddle(int xPos, int yPos){
        this.xPos = xPos;
        this.yPos = yPos;
        isMoving = 0;
        isEnabled = false;
        height = 50;
    }

    int getX(){
        return xPos;
    }

    void setX(int x){
        xPos = x;
    }

    int getY(){
        return yPos;
    }

    int getMoving(){
        return isMoving;
    }

    void setMoving(int moving){
        isMoving = moving;
    }

    static boolean getEnabled(){
        return isEnabled;
    }

    static void disable(){
        isEnabled = false;
    }

    static void enable(){
        isEnabled = true;
    }

    void move(int speed){
        xPos += speed;
    }

    void setLength(int length){
        this.length = length;
    }

    int getLength(){
        return length;
    }

    static void setHeight(int height){
        Paddle.height = height;
    }

    static int getHeight(){
        return height;
    }
}

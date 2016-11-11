package com.brandon.pong;

/***
 * Created by Brandon on 11/11/16.
 */

public class Paddle {
    private int xPos;
    private int yPos;
    private int isMoving;
    private static boolean isEnabled;

    public Paddle(int xPos, int yPos){
        this.xPos = xPos;
        this.yPos = yPos;
        isMoving = 0;
        isEnabled = false;
    }

    public int getX(){
        return xPos;
    }

    public void setX(int x){
        xPos = x;
    }

    public int getY(){
        return yPos;
    }

    public int getMoving(){
        return isMoving;
    }

    public void setMoving(int moving){
        isMoving = moving;
    }

    public static boolean getEnabled(){
        return isEnabled;
    }

    public static void disable(){
        isEnabled = false;
    }

    public static void enable(){
        isEnabled = true;
    }

    public void move(int speed){
        xPos += speed;
    }
}

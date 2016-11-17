package com.brandon.pong;

/***
 * Created by Brandon on 11/17/16.
 */

class Ball {
    private int xPos;
    private int yPos;
    private static int size;
    private double xVel;
    private double yVel;

    Ball(int x, int y){
        this.xPos = x;
        this.yPos = y;
        xVel = 0;
        yVel = 0;
    }

    static void setSize(int size){
        Ball.size = size;
    }

    static int getSize(){
        return size;
    }

    int getX(){
        return xPos;
    }

    void addX(double x) {
        xPos += x;
    }

    void setX(int x){
        xPos = x;
    }

    int getY(){
        return yPos;
    }

    void addY(double y){
        yPos += y;
    }

    void setY(int y){
        yPos = y;
    }

    double getXVel(){
        return xVel;
    }

    void setXVel(double xVel){
        this.xVel = xVel;
    }

    double getYVel(){
        return yVel;
    }

    void setYVel(double yVel){
        this.yVel = yVel;
    }
}

package com.example.kirtan.allaroundtheworld;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 *class for player movement and interactions
 */
public class Player extends GameObject{
    private Bitmap spritesheet;
    public static int score;
    private boolean up;
    private boolean playing;
    private Animation animation=new Animation();
    private long startTime;

    /**
     * constructor for class
     * @param res
     * @param w
     * @param h
     * @param numFrames
     */
    public Player(Bitmap res, int w, int h, int numFrames)
    {
        x=100;
        y=Game.HEIGHT/2;
        dy=0;
        score=0;
        height=h;
        width=w;
        Bitmap[]image=new Bitmap[numFrames];
        spritesheet=res;

        for(int i=0;i<image.length;i++)
        {
            image[i]=Bitmap.createBitmap(spritesheet,i*width,0,width,height);
        }

        animation.setFrames(image);
        animation.setDelay(10);
        startTime=System.nanoTime();
    }

    /**
     * sets touching screen as up
     * @param b
     */
    public void setUp(boolean b){up=b;}

    /**
     * udates score every millisecond
     * as well as moves player by 2 depending on touch
     * also flips through the animations of the helicopter
     */
    public void update()
    {
        long elapsed=(System.nanoTime()-startTime)/1000000;
        //Score increases
        if(elapsed>100)
        {
            score++;
            startTime=System.nanoTime();
        }
        animation.update();

        if(up)
        {
            dy-=2;
        }
        else
        {
            dy+=2;
        }

        if(dy>15)
        {
            dy=15;
        }

        if(dy<-15)
        {
            dy=-15;
        }

        y+=dy*2;
        dy=0; //remove if want heli to move faster
    }

    /**
     * draws the helicopter
     * @param canvas
     */
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(animation.getImage(),x,y,null);
    }

    /**
     * gets score
     * @return score
     */
    public int getScore()
    {
        return score;
    }

    /**
     * gets if playing or not
     * @return playing
     */
    public boolean getPlaying()
    {
        return playing;
    }

    /**
     * sets playing
     * @param b
     */
    public void setPlaying(boolean b)
    {
        playing=b;
    }

    /**
     * resets the position of helicopter
     */
    public void resetDY()
    {
        dy=0;
    }

    /**
     * resets the score
     */
    public void resetScore()
    {
        score=0;
    }
}

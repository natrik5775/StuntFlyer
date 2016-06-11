package com.example.kirtan.allaroundtheworld;

import android.graphics.Bitmap;

/**
 * class for animations and the frames of the game
 */
public class Animation {
    private Bitmap[] frames;
    private int currentFrame;
    private long startTime;
    private long delay;
    private boolean playedOnce;

    /**
     * sets the frames of the game
     * @param frames
     */
    public void setFrames(Bitmap[] frames)
    {
        this.frames=frames;
        currentFrame=0;
        startTime=System.nanoTime();
    }

    /**
     * sets a delay for the animations
     * @param d
     */
    public void setDelay(long d)
    {
        delay=d;
    }

    /**
     * sets the frames
     * @param i
     */
    public void setFrame(int i)
    {
        currentFrame=i;
    }

    /**
     * checks for if game has ended
     */
    public void update()
    {
        long elapsed=(System.nanoTime()-startTime)/1000000;

        if(elapsed>delay)
        {
            currentFrame++;
            startTime=System.nanoTime();
        }
        if(currentFrame==frames.length)
        {
            currentFrame=0;
            playedOnce=true;
        }
    }

    /**
     * gets the image
     * @return frames
     */
    public Bitmap getImage()
    {
        return frames[currentFrame];
    }

    /**
     * gets the frames of the game
     * @return currentFrame
     */
    public int getFrame()
    {
        return currentFrame;
    }

    /**
     * gets if played once
     * @return playedOnce
     */
    public boolean playedOnce()
    {
        return playedOnce;
    }
}

package com.example.kirtan.allaroundtheworld;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * class for scrolling background
 */
public class Background {
    private Bitmap image;
    private int x;
    private int y;
    private int dx;

    /**
     * constructor for background
     * moves the background fromleft to right
     * @param img
     */
    public Background(Bitmap img)
    {
        image=img;
        //erases a little bit of the background each update
        dx=Game.MOVESPEED;
    }

    /**
     * keeps the background moving
     */
    public void update()
    {
        x+=dx;
        if(x<-Game.WIDTH)
        {
            x=0;
        }
    }

    /**
     * draws the background
     * if background is less then original it will repeat the background
     * @param canvas
     */
    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(image,x,y,null);
        if(x<0)
        {
            canvas.drawBitmap(image,x+Game.WIDTH,y,null);
        }
    }
}

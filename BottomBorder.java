package com.example.kirtan.allaroundtheworld;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * class for bottom border to prevent player from going off screen
 */
public class BottomBorder extends GameObject{
    private Bitmap image;

    /**
     * constructor for bottom border
     * @param res
     * @param x
     * @param y
     */
    public BottomBorder(Bitmap res,int x,int y)
    {
        height=200;
        width=20;
        this.x=x;
        this.y=y;
        dx=Game.MOVESPEED;
        image= Bitmap.createBitmap(res,0,0,width,height);
    }

    /**
     * updates bottom border to keep up with scrolling background
     */
    public void update()
    {
        x+=dx;
    }

    /**
     * draws the border
     * @param canvas
     */
    public void draw(Canvas canvas)
    {
            canvas.drawBitmap(image,x,y,null);
    }
}

package com.example.kirtan.allaroundtheworld;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

/**
 * class for top border to prevent the player from going off screen
 */
public class TopBorder extends GameObject{
    private Bitmap image;

    /**
     * constructor for TopBorder
     * @param res
     * @param x
     * @param y
     * @param h
     */
    public TopBorder(Bitmap res,int x,int y,int h)
    {
        height=h;
        width=20;
        this.x=x;
        this.y=y;
        dx=Game.MOVESPEED;
        image= Bitmap.createBitmap(res,0,0,width,height);
    }

    /**
     * updates the top border to keep it up with scrolling screen
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
        try
        {
           canvas.drawBitmap(image,x,y,null);
        }
        catch(Exception e){}
    }
}

package com.example.kirtan.allaroundtheworld;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * class for smoke puffs behind player
 */
public class SmokePuff extends GameObject{
    public int radius;

    /**
     * constructor for class
     * @param x
     * @param y
     */
    public SmokePuff(int x, int y)
    {
        radius = 5;
        super.x = x;
        super.y = y;
    }

    /**
     * sets the smoke puffs behind player
     */
    public void update()
    {
        x-=10;
    }

    /**
     * draws the smoke puffs
     * @param canvas
     */
    public void draw(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(x-radius, y-radius, radius, paint);
        canvas.drawCircle(x-radius+2, y-radius-2,radius,paint);
        canvas.drawCircle(x-radius+4, y-radius+1, radius, paint);
    }

}
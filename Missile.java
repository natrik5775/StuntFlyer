package com.example.kirtan.allaroundtheworld;

/**
 * Created by Kirtan on 2016-06-05.
 */
import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

public class Missile extends GameObject{
    private int score;
    private int speed;
    private Random rand = new Random();
    private Animation animation = new Animation();
    private Bitmap spritesheet;

    public Missile(Bitmap res, int x, int y, int w, int h, int s, int numFrames)
    {
        super.x = x;
        super.y = y;
        width = w;
        height = h;
        score = s;

        //The less the base speed the more random
        speed = 10 + (int) (rand.nextDouble()*score/7);

        //cap missile speed
        if(speed>70)speed = 70;

        Bitmap[] image = new Bitmap[numFrames];

        spritesheet = res;

        for(int i = 0; i<image.length;i++)
        {
            image[i] = Bitmap.createBitmap(spritesheet, 0, i*height, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(100-speed);

    }
    public void update()
    {
        x-=speed;
        animation.update();
    }
    public void draw(Canvas canvas)
    {
        try{
            canvas.drawBitmap(animation.getImage(),x,y,null);
        }catch(Exception e){}
    }

    @Override
    public int getWidth()
    {
        return width-10;
    }

}


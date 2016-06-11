package com.example.kirtan.allaroundtheworld;

import android.graphics.Rect;

/**
 * abstract class to initialize general variables
 */
public abstract class GameObject {
    protected int x;
    protected int y;
    protected int dy;
    protected int dx;
    protected int width;
    protected int height;

    /**
     * sets x
     * @param x
     */
    public void setX(int x)
    {
        this.x=x;
    }

    /**
     * sets y
     * @param y
     */
    public void setY(int y)
    {
        this.y=y;
    }

    /**
     * gets x
     * @return x
     */
    public int getX()
    {
        return x;
    }

    /**
     * gets y
     * @return y
     */
    public int getY()
    {
        return y;
    }

    /**
     * gets height
     * @return height
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * gets width
     * @return width
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * gets rectangle for missile
     * @return Rect(x,y,x+width,y+height)
     */
    public Rect getRectangle()
    {
        return new Rect(x,y,x+width,y+height);
    }
}

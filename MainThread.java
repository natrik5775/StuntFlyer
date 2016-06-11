package com.example.kirtan.allaroundtheworld;

import android.graphics.Canvas;
import android.provider.Settings;
import android.view.SurfaceHolder;

/**
 * class to have a stable FPS for game
 * to make it smooth
 */
public class MainThread extends Thread{
    private int FPS=30;
    private double avgFPS;
    private SurfaceHolder surfaceHolder;
    private Game game;
    private boolean running;
    private Canvas canvas;

    /**
     * constructor for class
     * @param surfaceHolder
     * @param game
     */
    public MainThread(SurfaceHolder surfaceHolder, Game game)
    {
        super();
        this.surfaceHolder=surfaceHolder;
        this.game=game;
    }

    @Override
    /**
     * game loop for running the game smoothly
     */
    public void run()
    {
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime=0;
        int frameCount=0;
        long targetTime=1000/FPS;

        while(running)
        {
            startTime= System.nanoTime();
            canvas=null;

            try{
                canvas=this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder)
                {
                    this.game.update();
                    this.game.draw(canvas);
                }
            }
            catch(Exception e){}
            finally {
                if(canvas!=null)
                {
                    try{
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                    catch (Exception e){e.printStackTrace();}
                }
            }
            timeMillis= (System.nanoTime()-startTime)/1000000;
            waitTime=targetTime-timeMillis;

            try{
                this.sleep(waitTime);
            }
            catch (Exception e){}

            totalTime += System.nanoTime()-startTime;
            frameCount++;

            if(frameCount==FPS)
            {
                avgFPS=1000/((totalTime/frameCount)/1000000);
                frameCount=0;
                totalTime=0;
                System.out.println(avgFPS);
            }
        }
    }

    /**
     * sets the game running smoothly
     * @param b
     */
    public void setRunning(boolean b)
    {
        running=b;
    }
}

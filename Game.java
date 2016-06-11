package com.example.kirtan.allaroundtheworld;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.Random;



public class Game extends SurfaceView implements SurfaceHolder.Callback
{
    public static final int WIDTH=780;
    public static final int HEIGHT=264;
    public static final int MOVESPEED=-5;
    private long smokeStartTime;
    private long missileStartTime;
    private MainThread thread;
    private Background background;
    private Player player;
    private ArrayList<SmokePuff> smoke;
    private ArrayList<Missile> missiles;
    private ArrayList<TopBorder> topborder;
    private ArrayList<BottomBorder> bottomborder;
    private Random rand = new Random();
    private int maxBorderHeight;
    private int minBorderHeight;
    private boolean topDown=true;
    private boolean botDown=true;
    private boolean newGameCreated;
    private SharedPreferences prefs;

    //increase to slow down difficulty progression, decrease to speed up difficulty progression
    private int progressDiff=20;

    private Explosion explosion;
    private long startReset;
    private boolean reset;
    private boolean dissapear;
    private boolean started;
    private int best;
    private String saveScore="best";



    public Game(Context context)
    {
        super(context);
        prefs=context.getSharedPreferences("com.ecample.kirtan.allaroundtheworld",context.MODE_PRIVATE);
        String spackage="com.example.kirtan.allroundtheworld";
        best=prefs.getInt(saveScore,0);

        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);
        //make gamePanel focusable so it can handle events
        setFocusable(true);
    }

    @Override
    /**
     * applied when surface is changed(new window)
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry=true;
        int counter=0;
        while(retry&&counter<1000)
        {
            counter++;
            try{thread.setRunning(false);
                thread.join();
                retry=false;
                thread=null;
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }

        }
        prefs.edit().putInt(saveScore,best).commit();

    }

    @Override
    /**
     * Gets the game started at app opening
     */
    public void surfaceCreated(SurfaceHolder holder){

        background=new Background(BitmapFactory.decodeResource(getResources(), R.drawable.bgfinal));
        player=new Player(BitmapFactory.decodeResource(getResources(), R.drawable.helicopter), 65, 25, 3);
        smoke=new ArrayList<SmokePuff>();
        missiles=new ArrayList<Missile>();
        topborder=new ArrayList<TopBorder>();
        bottomborder=new ArrayList<BottomBorder>();
        smokeStartTime=System.nanoTime();
        missileStartTime=System.nanoTime();
        thread = new MainThread(getHolder(),this);

        //Start the game loop
        thread.setRunning(true);
        thread.start();

    }

    @Override
    /**
     * When screen is touched
     * if first touch then starts the game
     * as long as finger is on screen player will go up, if not then player goes down
     */
    public boolean onTouchEvent(MotionEvent event)
    {
        //When screen touched
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            if(!player.getPlaying() && newGameCreated && reset)
            {
                player.setPlaying(true);
                player.setUp(true);
            }
            if(player.getPlaying())
            {

                if(!started)started = true;
                reset = false;
                player.setUp(true);
            }
            return true;
        }
        if(event.getAction()==MotionEvent.ACTION_UP)
        {
            player.setUp(false);
            return true;
        }

        return super.onTouchEvent(event);
    }

    /**
     * updates game
     * sets score to high score if its > then previous high score
     * keeps the background scrolling
     * keeps the borders moving to stop player from cheating
     * also updates when player is going to die
     * updates to let the missile move randomly
     * checks for player death
     * adds a trail of smoke for helicopter
     * resets to a new game when player dies
     */
    public void update()
    {
        if(player.getPlaying()) {

            if(player.getScore()>=best)
            {
                best =player.getScore();

            }

            if(bottomborder.isEmpty())
            {
                player.setPlaying(false);
                return;
            }
            if(topborder.isEmpty())
            {
                player.setPlaying(false);
                return;
            }

            background.update();
            player.update();

            //calculate the threshold of height the border can have based on the score
            //max and min border heart are updated, and the border switched direction when either max or
            //min is met

            maxBorderHeight=30+player.getScore()/progressDiff;
            //cap max border height so that borders can only take up a total of 1/2 the screen
            if(maxBorderHeight>HEIGHT/4)maxBorderHeight =HEIGHT/4;
            minBorderHeight=5+player.getScore()/progressDiff;

            //check bottom border collision
            for(int i=0;i<bottomborder.size();i++)
            {
                if(collision(bottomborder.get(i), player))
                    player.setPlaying(false);
            }

            //check top border collision
            for(int i=0;i<topborder.size();i++)
            {
                if(collision(topborder.get(i),player))
                    player.setPlaying(false);
            }

            //update top border
            this.updateTopBorder();

            //udpate bottom border
            this.updateBottomBorder();

            //add missiles on timer
            long missileElapsed=(System.nanoTime()-missileStartTime)/1000000;
            if(missileElapsed>(2000-player.getScore()/4)){


                //first missile always goes down the middle
                if(missiles.size()==0)
                {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.
                            missile),WIDTH+10,HEIGHT/2,45,15,player.getScore(),13));
                }
                else
                {
                    missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile),
                            WIDTH+10,(int)(rand.nextDouble()*(HEIGHT-(maxBorderHeight*2))+maxBorderHeight),45,15,player.getScore(),13));
                }

                //reset timer
                missileStartTime=System.nanoTime();
            }
            //loop through every missile and check collision and remove
            for(int i= 0;i<missiles.size();i++)
            {
                //update missile
                missiles.get(i).update();

                if(collision(missiles.get(i),player))
                {
                    missiles.remove(i);
                    player.setPlaying(false);
                    break;
                }
                //remove missile if it is way off the screen
                if(missiles.get(i).getX()<-100)
                {
                    missiles.remove(i);
                    break;
                }
            }

            //add smoke puffs on timer
            long elapsed = (System.nanoTime()-smokeStartTime)/1000000;
            if(elapsed>120){
                smoke.add(new SmokePuff(player.getX(),player.getY()+10));
                smokeStartTime=System.nanoTime();
            }

            for(int i=0;i<smoke.size();i++)
            {
                smoke.get(i).update();
                if(smoke.get(i).getX()<-10)
                {
                    smoke.remove(i);
                }
            }
        }
        else{
            player.resetDY();
            if(!reset)
            {
                newGameCreated=false;
                startReset=System.nanoTime();
                reset=true;
                dissapear=true;
                explosion=new Explosion(BitmapFactory.decodeResource(getResources(),R.drawable.explosion),player.getX(),
                        player.getY()-30,100,100,25);
            }

            explosion.update();
            long resetElapsed=(System.nanoTime()-startReset)/1000000;

            if(resetElapsed>2500&&!newGameCreated)
            {
                newGame();
            }


        }

    }

    /**
     *
     * @param a
     * @param b
     * @return false or true
     * checks for missile or wall collision
     */
    public boolean collision(GameObject a,GameObject b)
    {
        if(Rect.intersects(a.getRectangle(),b.getRectangle()))
        {
            return true;
        }
        return false;
    }
    @Override
    /**
     * draws the borders, smoke puffs, missiles, and explosion
     */
    public void draw(Canvas canvas)
    {
        final float scaleFactorX=getWidth()/(WIDTH*1.f);
        final float scaleFactorY=getHeight()/(HEIGHT*1.f);

        if(canvas!=null) {
            final int savedState=canvas.save();
            canvas.scale(scaleFactorX, scaleFactorY);
            background.draw(canvas);
            if(!dissapear) {
                player.draw(canvas);
            }
            //draw smokepuffs
            for(SmokePuff sp:smoke)
            {
                sp.draw(canvas);
            }
            //draw missiles
            for(Missile m:missiles)
            {
                m.draw(canvas);
            }


            //draw topborder
            for(TopBorder tb:topborder)
            {
                tb.draw(canvas);
            }

            //draw bottomborder
            for(BottomBorder bb:bottomborder)
            {
                bb.draw(canvas);
            }
            //draw explosion
            if(started)
            {
                explosion.draw(canvas);
            }
            drawText(canvas);
            canvas.restoreToCount(savedState);

        }
    }

    /**
     * every 50 points the top border will randomly move more down or up to provide more of a challenge to the player
     */
    public void updateTopBorder()
    {
        //every 50 points, insert randomly placed top blocks that break the pattern
        if(player.getScore()%50==0)
        {
            topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick
            ),topborder.get(topborder.size()-1).getX()+20,0,(int)((rand.nextDouble()*(maxBorderHeight
            ))+1)));
        }
        for(int i=0;i<topborder.size();i++)
        {
            topborder.get(i).update();
            if(topborder.get(i).getX()<-20)
            {
                //remove element of arraylist, replace it by adding a new one
                topborder.remove(i);

                //calculate topdown which determines the direction the border is moving (up or down)
                if(topborder.get(topborder.size()-1).getHeight()>=maxBorderHeight)
                {
                    topDown=false;
                }
                if(topborder.get(topborder.size()-1).getHeight()<=minBorderHeight)
                {
                    topDown=true;
                }
                //new border added will have larger height
                if(topDown)
                {
                    topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick),topborder.get(topborder.size()-1).getX()+20,
                            0, topborder.get(topborder.size()-1).getHeight()+1));
                }
                //new border added wil have smaller height
                else
                {
                    topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),
                            R.drawable.brick),topborder.get(topborder.size()-1).getX()+20,
                            0, topborder.get(topborder.size()-1).getHeight()-1));
                }
            }
        }

    }

    /**
     * every 50 points the bottom border will randomly move up or down to provide difficulty for the player
     */
    public void updateBottomBorder()
    {
        //every 50 points, insert randomly placed bottom blocks that break pattern
        if(player.getScore()%50==0)
        {
            bottomborder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick),
                    bottomborder.get(bottomborder.size()-1).getX()+20,(int)((rand.nextDouble()
                    *maxBorderHeight)+(HEIGHT-maxBorderHeight))));
        }

        //update bottom border
        for(int i=0;i<bottomborder.size();i++)
        {
            bottomborder.get(i).update();

            //if border is moving off screen, remove it and add a corresponding new one
            if(bottomborder.get(i).getX()<-20)
            {
                bottomborder.remove(i);


                //determine if border will be moving up or down
                if (bottomborder.get(bottomborder.size()-1).getY()<=HEIGHT-maxBorderHeight)
                {
                    botDown=true;
                }
                if (bottomborder.get(bottomborder.size()-1).getY()>=HEIGHT-minBorderHeight)
                {
                    botDown=false;
                }

                if (botDown) {
                    bottomborder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick
                    ), bottomborder.get(bottomborder.size()-1).getX()+20, bottomborder.get(bottomborder.size()-1
                    ).getY()+1));
                } else {
                    bottomborder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick
                    ), bottomborder.get(bottomborder.size()-1).getX()+20, bottomborder.get(bottomborder.size()-1
                    ).getY()-1));
                }
            }
        }
    }

    /**
     * will reset the game when the player dies
     */
    public void newGame()
    {
        dissapear = false;

        bottomborder.clear();
        topborder.clear();

        missiles.clear();
        smoke.clear();

        minBorderHeight = 5;
        maxBorderHeight=30;

        player.resetDY();

        player.setY(HEIGHT/2);

        player.resetScore();
        //create initial borders

        //initial top border
        for(int i=0;i*20<WIDTH+40;i++)
        {
            //first top border create
            if(i==0)
            {
                topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick
                ),i*20,0,10));
            }
            else
            {
                topborder.add(new TopBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick
                ),i*20,0,topborder.get(i-1).getHeight()+1));
            }
        }
        //initial bottom border
        for(int i = 0; i*20<WIDTH+40; i++)
        {
            //first border ever created
            if(i==0)
            {
                bottomborder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(),R.drawable.brick)
                        ,i*20,HEIGHT-minBorderHeight));
            }
            //adding borders until the initial screen is filed
            else
            {
                bottomborder.add(new BottomBorder(BitmapFactory.decodeResource(getResources(), R.drawable.brick),
                        i*20,bottomborder.get(i - 1).getY()-1));
            }
        }

        newGameCreated = true;


    }

    /**
     * draws the text for the starting screen of the game
     * while game not playing
     * @param canvas
     */
    public void drawText(Canvas canvas)
    {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("DISTANCE: " + (player.getScore()), 10, HEIGHT - 10, paint);
        canvas.drawText("BEST: " + best, WIDTH - 215, HEIGHT - 10, paint);

        if(!player.getPlaying()&&newGameCreated&&reset)
        {
            Paint paint1 = new Paint();
            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            paint1.setTextSize(55);
            canvas.drawText("Stunt Flyer",WIDTH/2-350,HEIGHT/2-20,paint1);
            paint1.setTextSize(40);
            canvas.drawText("PRESS TO START", WIDTH/2-50, HEIGHT/2, paint1);

            paint1.setTextSize(20);
            canvas.drawText("PRESS AND HOLD TO GO UP", WIDTH/2-50, HEIGHT/2 + 20, paint1);
            canvas.drawText("RELEASE TO GO DOWN", WIDTH/2-50, HEIGHT/2 + 40, paint1);
        }
    }


}
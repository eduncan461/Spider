package com.example.eric.spider;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class SpiderGame extends Activity {

    // gameView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize gameView and set it as the view
        gameView = new GameView(this);
        setContentView(gameView);
    }

    // Here is our implementation of GameView

    // Notice we implement runnable so we have
    // A thread and can override the run method.
    class GameView extends SurfaceView implements Runnable {

        Thread gameThread = null;

        private Context context;

        SurfaceHolder ourHolder;

        // when the game is running- or not.
        volatile boolean playing;

        // Game is paused at the start
        boolean paused = false;

        // A Canvas and a Paint object
        Canvas canvas;
        Paint paint;

        WaterDrop[] waterDrops;
        int waterDropCount = 3;
        private Spider[] spiders;
        int spiderCount = 3;

        // This variable tracks the game frame rate
        long fps;

        // This is used to help calculate the fps
        private long timeThisFrame;

        // The size of the screen in pixels
        float screenX;
        float screenY;

        boolean lost = false;
        boolean losingOn = false;

        int lives = 3;

        GameColumn left, middleLeft, middleRight, right;

        int fpsCount = 0, secondsCount = 0;

        // When the we initialize (call new()) on gameView
        // This special constructor method runs
        public GameView(Context context) {
            // The next line of code asks the
            // SurfaceView class to set up our object.
            super(context);
            this.context = context;

            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();

            // Get a Display object to access screen details
            Display display = getWindowManager().getDefaultDisplay();
            // Load the resolution into a Point object
            Point size = new Point();
            display.getSize(size);

            screenX = size.x;
            screenY = size.y;

            spiders = new Spider[spiderCount];

            waterDrops = new WaterDrop[waterDropCount];
            for (int i = 0; i < waterDropCount; i++) {
                waterDrops[i] = new WaterDrop(context, screenX);
            }

            left = new GameColumn(context, screenX, screenY, 0);
            middleLeft = new GameColumn(context, screenX, screenY, 2);
            middleRight = new GameColumn(context, screenX, screenY, 4);
            right = new GameColumn(context, screenX, screenY, 6);

            // Loading sounds goes here: WIP

            prepareLevel();

        }

        public void prepareLevel()
        {
            losingOn = false;
            lost = false;
            for (int i = 0; i < spiderCount; i++)
            {
                spiders[i] = new Spider(context, screenX, screenY);
            }
            for (int i = 0; i < waterDropCount; i++)
            {
                waterDrops[i].reset(screenY);
            }
        }

        @Override
        public void run() {
            while (playing) {
                //int i = 0;
                // Capture the current time in milliseconds in startFrameTime
                long startFrameTime = System.currentTimeMillis();

                fpsCount += 1;
                if(secondsCount >= 5)
                {
                    /*
                    if(i < spiderCount)
                    {

                        if(!spiders[i].getIsActive()) //if not active, spawn
                        {
                            spiders[i].spawnSpider(screenY);
                        }
                        i++;
                    }
                    else
                    {
                        i = 0;
                    }*/
                    losingOn = true;
                    secondsCount = 0;
                }

                // Update the frame
                if (!paused) {
                    update();
                }

                // Draw the frame
                draw();

                // Calculate the fps this frame
                // We can then use the result to
                // time animations and more.
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }

                if(fpsCount >= fps)
                {
                    secondsCount += 1;
                    fpsCount = 0;
                }
            }

        }

        // Everything that needs to be updated goes in here
        // Movement, collision detection etc.
        public void update() {

            for (int i = 0; i < spiderCount; i++)
            {
                spiders[i].update(fps);
                if (spiders[i].getY() + (spiders[i].getHeight()/5) < 0) //if spider reaches the top of the screen
                {
                    //lives

                    //lost = true;
                    spiders[i].reset(screenY);
                    if(losingOn == true)
                    {
                        lost = true;
                    }
                    //paused = true;
                }
                if(spiders[i].getY()+spiders[i].getHeight() < screenY && spiders[i].getHeight() > 0) //if spider is in the screen
                {
                    spiders[i].setIsActive(); //set to active
                }
                if(spiders[i].getY() >= screenY && spiders[i].getIsDead()) //if spider is off the screen and is dead, reset
                {
                    spiders[i].reset(screenY);
                }


            }

            for (int i = 0; i < waterDropCount; i++)
            {
                waterDrops[i].update(fps);
                if (waterDrops[i].getImpactPointY() > screenY)
                {
                    waterDrops[i].reset(screenY);
                }

                for (int j = 0; j < spiderCount; j++)
                {
                    if(spiders[j].getIsActive())
                    {
                        if (RectF.intersects(waterDrops[i].getRect(), spiders[j].getRect()) && waterDrops[i].getStatus())
                        {
                            spiders[j].setIsDead();
                            //soundPool.play(invaderExplodeID, 1, 1, 0, 0, 1);
                            waterDrops[i].setInactive();
                            waterDrops[i].setInvisible();
                            waterDrops[i].reset(screenY);
                        }
                    }
                }
            }

        }

        // Draw the newly updated scene
        public void draw()
        {
            // Make sure our drawing surface is valid or we crash
            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();



                // Draw the background color white
                canvas.drawColor(Color.argb(255, 202, 204, 206));

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 255, 255, 255));


                for (int i = 0; i < waterDropCount; i++) {
                    if (waterDrops[i].getVisibility()) {
                        canvas.drawBitmap(waterDrops[i].getBitmap(), waterDrops[i].getX(), waterDrops[i].getY(), paint);
                    }
                }

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 255, 255, 255));

                // Now draw the player spaceship

                //drawing the spiders
                for (int i = 0; i < spiderCount; i++)
                {
                    if (!spiders[i].getIsDead())
                    {
                        canvas.drawBitmap(spiders[i].getBitmap(), spiders[i].getX(), spiders[i].getY(), paint);
                    }
                    else
                    {
                        canvas.drawBitmap(spiders[i].getBitmapWet(), spiders[i].getX(), spiders[i].getY(), paint);
                    }

                }


                // Change the brush color for drawing
                paint.setColor(Color.argb(255, 249, 129, 0));

                canvas.drawBitmap(left.getBitmap(), null, left.getRect(), null);
                //canvas.drawRect(left.getRect(), paint);
                canvas.drawBitmap(middleLeft.getBitmap(), null, middleLeft.getRect(), null);
                //canvas.drawRect(middleLeft.getRect(), paint);
                canvas.drawBitmap(middleRight.getBitmap(), null, middleRight.getRect(), null);
                //canvas.drawRect(middleRight.getRect(), paint);
                canvas.drawBitmap(right.getBitmap(), null, right.getRect(), null);
                //canvas.drawRect(right.getRect(), paint);


                // Draw the HUD
                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 139, 0, 0));


                // Has the player lost?


                if(lost == true)
                {
                    paint.setTextSize(60);

                    drawCenter(canvas, paint, "YOU HAVE LOST!");
                    //canvas.drawText("YOU HAVE LOST!", xPos, yPos, paint);
                    paused = true;

                    //prepareLevel();

                }

                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void createSpiders(Context context)
        {
            for (int i = 0; i < spiderCount; i++) {
                spiders[i] = new Spider(context, screenX, screenY);
            }
        }

        private void drawCenter(Canvas canvas, Paint paint, String text) {
            Rect r = new Rect(0, 0, (int) screenX, (int) screenY);
            canvas.getClipBounds(r);
            int cHeight = r.height();
            int cWidth = r.width();
            paint.setTextAlign(Paint.Align.LEFT);
            paint.getTextBounds(text, 0, text.length(), r);
            float x = cWidth / 2f - r.width() / 2f - r.left;
            float y = cHeight / 2f + r.height() / 2f - r.bottom;
            canvas.drawText(text, x, y, paint);
        }

        // If paused/stopped shutdown our thread.
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }

        // If SimpleGameEngine Activity is started theb
        // start our thread.
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        // The SurfaceView class implements onTouchListener
        // So we can override this method and detect screen touches.
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                // Player has touched the screen
                case MotionEvent.ACTION_DOWN:

                    paused = false;
                    if(lost == true)
                    {
                        prepareLevel();
                    }
                    else {

                        if ((motionEvent.getX() > screenX / 7) && (motionEvent.getX() < 2 * screenX / 7)) //first column from the left
                        {
                            for (int i = 0; i < waterDropCount; i++) {
                                if (!waterDrops[i].getStatus())  // check each water drop, if waterDrop[i] isn't active, shoot it, else do nothing.
                                {
                                    waterDrops[i].shoot(5 * screenX / 28);
                                    break;
                                } else ;
                            }
                        } else if ((motionEvent.getX() > 3 * screenX / 7) && (motionEvent.getX() < 4 * screenX / 7)) {
                            for (int i = 0; i < waterDropCount; i++) {
                                if (!waterDrops[i].getStatus()) {
                                    waterDrops[i].shoot(13 * screenX / 28);
                                    break;
                                } else ;
                            }
                        } else if ((motionEvent.getX() > 5 * screenX / 7) && (motionEvent.getX() < 6 * screenX / 7)) {
                            for (int i = 0; i < waterDropCount; i++) {
                                if (!waterDrops[i].getStatus()) {
                                    waterDrops[i].shoot(21 * screenX / 28);
                                    break;
                                } else ;
                            }

                        } else ;

                        break;
                    }
                // Player has removed finger from screen
                case MotionEvent.ACTION_UP:

                    //paddle.setMovementState(paddle.STOPPED);
                    break;
            }
            return true;
        }

    }
    // This is the end of our BreakoutView inner class

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        gameView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        gameView.pause();
    }

}
package com.example.eric.spider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import java.util.concurrent.ThreadLocalRandom;

import java.util.Random;

public class Spider {

    RectF rect;

    Random generator = new Random();

    // The player ship will be represented by a Bitmap
    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private Bitmap bitmapWet;

    // How long and high our paddle will be
    private float length;
    private float height;

    // X is the far left of the rectangle which forms our paddle
    private float x;

    // Y is the top coordinate
    private float y;

    // This will hold the pixels per second speedthat the paddle will move
    private float speed;

    //private int column = -1;

    // Is the ship moving and in which direction
    //private int shipMoving = RIGHT;

    boolean isDead;
    boolean isActive;

    public Spider(Context context, float screenX, float screenY) {

        //int column = randomColumn();

        // Initialize a blank RectF
        rect = new RectF();

        length = screenX / 7;
        height = length;
        isDead = false;
        isActive = false;

        //int padding = screenX / 25;


        reset(screenY);
        //x = randomColumn() * length;
        //y = screenY + randomY(screenY);

        // Initialize the bitmap
        bitmap1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.spiderbasefinal);
        bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.spider2);
        bitmapWet = BitmapFactory.decodeResource(context.getResources(), R.drawable.spiderwetbase);


        // stretch the first bitmap to a size appropriate for the screen resolution
        bitmap1 = Bitmap.createScaledBitmap(bitmap1,
                (int) (length),
                (int) (height),
                false);

        // stretch the first bitmap to a size appropriate for the screen resolution
        bitmap2 = Bitmap.createScaledBitmap(bitmap2,
                (int) (length),
                (int) (height),
                false);

        bitmapWet = Bitmap.createScaledBitmap(bitmapWet,
                (int) (length),
                (int) (height),
                false);



        // How fast the spider is in pixels per second
        speed = 200;
    }

    public void setIsDead(){
        isDead = true;
    }
    public void setIsActive() { isActive = true; }

    public boolean getIsDead()
    {
        return isDead;
    }

    public boolean getIsActive()
    {
        return isActive;
    }

    public RectF getRect(){
        return rect;
    }

    public Bitmap getBitmap(){
        return bitmap1;
    }

    public Bitmap getBitmap2(){
        return bitmap2;
    }

    public Bitmap getBitmapWet() { return bitmapWet; }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getLength(){
        return length;
    }


    public void update(long fps)
    {
        if(!isDead) //if not dead
        {
            y = y - speed / fps;
        }
        else
        {
            //y = y + speed / fps;
            y = y + (speed / fps) + (3/2)*y / fps;
        }
        // Update rect which is used to detect hits
        rect.top = y;
        rect.bottom = y + height;
        rect.left = x;
        rect.right = x + length;
    }

    public int randomColumn()
    {
        Random generator = new Random();
        int answer = generator.nextInt(3);

        if(answer == 0){
            return 1;
        }
        else if(answer == 1)
        {
            return 3;
        }
        else
        {
            return 5;
        }
    }

    public float randomY(float screenY)
    {
        Random generator = new Random();
        int answer = generator.nextInt(15) + 2;

        return screenY / answer;
    }

    public int randomSpeed()
    {
        Random generator = new Random();
        int answer = generator.nextInt(5);

        return answer;
    }

    public float getHeight()
    {
        return height;
    }

    public void reset(float screenY)
    {
        isDead = false;
        isActive = false;
        x = randomColumn() * length;
        y = screenY + randomY(screenY);
        //speed = speed * randomSpeed();
        speed = speed*10/9;
    }
/*
    public void spawnSpider(float screenY)
    {
        isActive = true;
        x = randomColumn() * length;
        //x = 1 * length;

        y = screenY + randomY(screenY);
    }
    */
}
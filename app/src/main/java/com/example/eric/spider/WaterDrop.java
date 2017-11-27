package com.example.eric.spider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

import java.util.Random;

//RectF(float left, float top, float right, float bottom)

public class WaterDrop {

    private float x;
    private float y;

    private RectF rect;

    float speed =  350;

    float waterWidth;
    float waterHeight;

    private boolean isActive;
    private boolean isVisible;

    private Bitmap water;

    public WaterDrop(Context context, float screenX)
    {
        // code for size of Water Drop based on screen/column width
        waterWidth = screenX/14;
        waterHeight = waterWidth;

        isActive = false;
        isVisible = false;

        // Initialize the bitmap
        water = BitmapFactory.decodeResource(context.getResources(), R.drawable.water);

        // stretch the first bitmap to a size appropriate for the screen resolution
        water = Bitmap.createScaledBitmap(water,
                (int) (waterWidth),
                (int) (waterHeight),
                false);

        rect = new RectF();
    }

    public boolean shoot(float startX)
    {
        if (!isActive)
        {
            x = startX;
            y = 0 - waterHeight;
            isActive = true;
            isVisible = true;
            return true;
        }

        // water drop already active
        return false;
    }

    public void update(long fps)
    {
        y = y + (speed / fps) + (3/2)*y / fps;

        rect.left = x;
        rect.top = y - waterHeight;
        rect.right = x + waterWidth;
        //rect.bottom = y + waterHeight;
        rect.bottom = y;
    }

    public Bitmap getBitmap(){
        return water;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public RectF getRect()
    {
        return rect;
    }

    public boolean getStatus()
    {
        return isActive;
    }

    public boolean getVisibility() { return isVisible; }

    public void setInactive()
    {
        isActive = false;
    }

    public void setInvisible() { isVisible = false; }

    public float getImpactPointY()
    {
        return y;
    }

    public void reset(float screenY)
    {
        isActive = false;

        //rect = new RectF();
    }
}

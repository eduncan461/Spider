package com.example.eric.spider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

//RectF(float left, float top, float right, float bottom)

public class GameColumn {

    private RectF rect;

    private boolean isVisible;

    private float width;

    private Bitmap columnImage;

    public GameColumn(Context context, float screenX, float screenY, int column){

        isVisible = true;

        width = screenX / 7;

        // Initialize the bitmap
        columnImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.column);

        // stretch the first bitmap to a size appropriate for the screen resolution
        columnImage = Bitmap.createScaledBitmap(columnImage,
                (int) (width),
                (int) (screenY),
                false);

        rect = new RectF(column * width, 0, column * width + width, screenY);
        //rect = new RectF();
    }

    public RectF getRect(){
        return this.rect;
    }

    public Bitmap getBitmap()
    {
        return columnImage;
    }

    public void setInvisible(){
        isVisible = false;
    }

    public boolean getVisibility(){
        return isVisible;
    }
}
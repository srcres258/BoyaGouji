package com.thws.boyaddz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

public class DianAnimation extends BaseAnimation {
    Bitmap dianPic;
    int dx, dy, size = 128;
    Paint paint = new Paint();

    public DianAnimation(Context context, int destX, int destY) {
        super(20);
        dianPic = BitmapFactory.decodeResource(context.getResources(), R.drawable.dian);
        dx = destX;
        dy = destY;
    }

    @Override
    protected void onRender(Canvas canvas) {
        float tx = (canvas.getWidth() - size) / 2f;
        float ty = (canvas.getHeight() - size) / 2f;
        float pDeltaSize = size / 20f;

        float pDeltaX = (dx - tx) / 20f;
        float pDeltaY = (dy - ty) / 20f;
        RectF rect = new RectF();
        rect.left = tx + pDeltaX * currentFrame;
        rect.top = ty + pDeltaY * currentFrame;
        rect.right = tx +  pDeltaX * currentFrame + pDeltaSize * (20 - currentFrame);
        rect.bottom = ty + pDeltaY * currentFrame + pDeltaSize * (20 - currentFrame);
        canvas.drawBitmap(dianPic, null, rect, paint);
        Log.i("DianAnimation", rect.toString());
    }
}

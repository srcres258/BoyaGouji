package com.thws.boyaddz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

public class MenAnimation extends BaseAnimation {
    Bitmap menPic;
    Bitmap beimenPic;
    int sx, sy, dx, dy, size = 128;
    Paint paint = new Paint();

    public MenAnimation(Context context, int srcX, int srcY, int destX, int destY) {
        super(60);
        menPic = BitmapFactory.decodeResource(context.getResources(), R.drawable.men);
        beimenPic = BitmapFactory.decodeResource(context.getResources(), R.drawable.beimen);
        sx = srcX;
        sy = srcY;
        dx = destX;
        dy = destY;
    }

    @Override
    protected void onRender(Canvas canvas) {
        float tx = (canvas.getWidth() - size) / 2f;
        float ty = (canvas.getHeight() - size) / 2f;
        float pDeltaSize = size / 20f;
        if (currentFrame >= 40) {
            float pDeltaX = (dx - tx) / 20f;
            float pDeltaY = (dy - ty) / 20f;
            RectF rect = new RectF();
            rect.left = tx + pDeltaX * (currentFrame - 40);
            rect.top = ty + pDeltaY * (currentFrame - 40);
            rect.right = tx +  pDeltaX * (currentFrame - 40) + pDeltaSize * (60 - currentFrame);
            rect.bottom = ty + pDeltaY * (currentFrame - 40) + pDeltaSize * (60 - currentFrame);
            canvas.drawBitmap(beimenPic, null, rect, paint);
            Log.i("MenAnimation", rect.toString());
        } else if (currentFrame <= 20) {
            float pDeltaX = (tx - sx) / 20f;
            float pDeltaY = (ty - sy) / 20f;
            RectF rect = new RectF();
            rect.left = sx + pDeltaX * currentFrame;
            rect.top = sy + pDeltaY * currentFrame;
            rect.right = sx + pDeltaX * currentFrame + pDeltaSize * currentFrame;
            rect.bottom = sy + pDeltaY * currentFrame + pDeltaSize * currentFrame;
            canvas.drawBitmap(menPic, null, rect, paint);
            Log.i("MenAnimation", rect.toString());
        } else {
            RectF rect = new RectF();
            rect.left = tx;
            rect.top = ty;
            rect.right = tx + size;
            rect.bottom = ty + size;
            canvas.drawBitmap(menPic, null, rect, paint);
            Log.i("MenAnimation", rect.toString());
        }
        Log.i("MenAnimation", String.format("sx %d sy %d dx %d dy %d tx %f ty %f",
                sx, sy, dx, dy, tx, ty));
    }
}

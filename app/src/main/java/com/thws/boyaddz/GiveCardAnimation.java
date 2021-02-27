package com.thws.boyaddz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class GiveCardAnimation extends BaseAnimation {
    private int card, sx, sy, dx, dy;
    private Bitmap cardPic;
    private Paint paint = new Paint();

    public GiveCardAnimation(Context context, int card, int srcX, int srcY, int destX, int destY) {
        super(30);
        int row = CardsManager.getImageRow(card);
        int col = CardsManager.getImageCol(card);
        cardPic = BitmapFactory.decodeResource(context.getResources(), CardImage.cardImages[row][col]);
        sx = srcX;
        sy = srcY;
        dx = destX;
        dy = destY;
    }

    @Override
    protected void onRender(Canvas canvas) {
        int pDeltaX = (dx - sx) / 30;
        int pDeltaY = (dy - sy) / 30;
        RectF rect = new RectF(sx + pDeltaX * currentFrame, sy + pDeltaY * currentFrame,
                (float) (sx + pDeltaX * currentFrame + 40 * MainActivity.SCALE_HORIAONTAL),
                (float) (sy + pDeltaY * currentFrame + 60 * MainActivity.SCALE_VERTICAL));
        canvas.drawBitmap(cardPic, null, rect, paint);
    }
}

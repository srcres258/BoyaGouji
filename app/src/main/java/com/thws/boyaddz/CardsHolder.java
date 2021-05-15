package com.thws.boyaddz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

public class CardsHolder {

	int value = 0;
	int cardsType = 0;
	int[] cards;
	Bitmap cardImage;
	int playerId;
	Context context;

	public CardsHolder(int[] cards, int id, Context context) {
		this.playerId = id;
		this.cards = cards;
		this.context = context;
		cardsType = CardsManager.getType(cards);
		value = CardsManager.getValue(cards);
		if (cardsType == CardsType.huojian || cardsType == CardsType.zhadan) {
			Desk.multiple *= 2;
		}
	}

	public void paint(Canvas canvas, int left, int top, int dir) {

		Rect src = new Rect();
		Rect des = new Rect();
		Paint paint = new Paint();
		paint.setStyle(Style.STROKE);
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(1);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		for (int i = 0; i < cards.length; i++) {
			int row = CardsManager.getImageRow(cards[i]);
			int col = CardsManager.getImageCol(cards[i]);
			cardImage = BitmapFactory.decodeResource(context.getResources(),
					CardImage.cardImages[row][col]);
			if (dir == CardsType.direction_Vertical) {
				row = CardsManager.getImageRow(cards[i]);
				col = CardsManager.getImageCol(cards[i]);
				src.set(0, 0, cardImage.getWidth(), cardImage.getHeight());
				des.set((int) (left * MainActivity.SCALE_HORIAONTAL),
						(int) ((top + i * CardImage.CARD_PRINT_VOFFSET) * MainActivity.SCALE_VERTICAL),
						(int) ((left + CardImage.CARD_PRINT_WIDTH) * MainActivity.SCALE_HORIAONTAL),
						(int) ((top + CardImage.CARD_PRINT_HEIGHT + i * CardImage.CARD_PRINT_VOFFSET) * MainActivity.SCALE_VERTICAL));
			}
			else {
				row = CardsManager.getImageRow(cards[i]);
				col = CardsManager.getImageCol(cards[i]);
				src.set(0, 0, cardImage.getWidth(), cardImage.getHeight());
				des.set((int) ((left + i * CardImage.CARD_PRINT_HOFFSET) * MainActivity.SCALE_HORIAONTAL),
						(int) (top * MainActivity.SCALE_VERTICAL),
						(int) ((left + CardImage.CARD_PRINT_WIDTH + i * CardImage.CARD_PRINT_HOFFSET) * MainActivity.SCALE_HORIAONTAL),
						(int) ((top + CardImage.CARD_PRINT_HEIGHT) * MainActivity.SCALE_VERTICAL));
			}
			RectF rectF = new RectF(des);
			canvas.drawRoundRect(rectF, 5, 5, paint);
			canvas.drawBitmap(cardImage, src, des, paint);

		}

	}

	public void playSound() {
		SoundManager.playCardsTypeSound(cardsType);
	}

	public int getDCount() {
		int r = 0;
		for (int c : cards) {
			if (CardsManager.getCardNumber(c) == 17)
				r++;
		}
		return r;
	}

	public int getXCount() {
		int r = 0;
		for (int c : cards) {
			if (CardsManager.getCardNumber(c) == 16)
				r++;
		}
		return r;
	}
}

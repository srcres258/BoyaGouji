package com.thws.boyaddz;

import android.annotation.SuppressLint;
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
import android.graphics.Typeface;

import com.blankj.utilcode.util.ArrayUtils;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Supplier;

public class Player {

	public enum DiscardState {
		CHUPAI,
		GUOPAI,
		RANGPAI
	}

	int[] cards;

	boolean[] cardsFlag;

	int playerId;

	int currentId;

	int currentCircle;

	int top, left;

	Desk desk;

	CardsHolder latestCards;

	CardsHolder cardsOnDesktop;

	// Context
	Context context;

	int paintDirection = CardsType.direction_Vertical;
	Bitmap cardImage;

	boolean dianFlag = false;
	boolean dian = false;
	boolean qidian = false;
	boolean shao = false;
	boolean beishao = false;
	boolean men = false;
	boolean beimen = false;
	boolean wutou = false;
	DiscardState state = DiscardState.CHUPAI;

	public Player getOpposite() {
		return opposite;
	}

	public Player getLastMate() {
		return lastMate;
	}

	public Player getLast() {
		return last;
	}

	public Player getNext() {
		return next;
	}

	public Player getNextMate() {
		return nextMate;
	}

	private Player opposite;
	private Player lastMate;
	private Player last;
	private Player next;
	private Player nextMate;

	private ReentrantLock dataLock = new ReentrantLock();

	public Player(int[] cards, int left, int top, int paintDir, int id, Desk desk, Context context) {
		this.desk = desk;
		this.playerId = id;
		this.cards = cards;
		this.context = context;
		cardsFlag = new boolean[cards.length];
		this.setLeftAndTop(left, top);
		this.paintDirection = paintDir;
	}

	public Lock getDataLock() {
		return dataLock;
	}

	public void setLeftAndTop(int left, int top) {
		this.left = left;
		this.top = top;
	}

	public void setRelations(Player last, Player next, Player opposite, Player lastMate, Player nextMate) {
		this.last = last;
		this.next = next;
		this.opposite = opposite;
		this.lastMate = lastMate;
		this.nextMate = nextMate;
	}

	public void setCards(int[] cards) {
		this.cards = cards;
		cardsFlag = new boolean[cards.length];
	}

	public void paint(Canvas canvas) {
		Function<Integer, Boolean> cardsFlagF = (i) -> {
			boolean ret;
			try {
				dataLock.lock();
				ret = cardsFlag[i];
			} finally {
				dataLock.unlock();
			}
			return ret;
		};
//		System.out.println("id:" + playerId);
		Rect src = new Rect();
		Rect des = new Rect();

		int row;
		int col;

//		if (paintDirection == CardsType.direction_Vertical) {
		if (playerId != Desk.boss) {
//			Paint paint = new Paint();
//			paint.setStyle(Style.STROKE);
//			paint.setColor(Color.BLACK);
//			paint.setStrokeWidth(1);
//			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//			Bitmap backImage = BitmapFactory.decodeResource(context.getResources(),
//					R.drawable.card_bg);
//
//			src.set(0, 0, backImage.getWidth(), backImage.getHeight());
//			des.set((int) (left * MainActivity.SCALE_HORIAONTAL),
//					(int) (top * MainActivity.SCALE_VERTICAL),
//					(int) ((left + 40) * MainActivity.SCALE_HORIAONTAL),
//					(int) ((top + 60) * MainActivity.SCALE_VERTICAL));
//			RectF rectF = new RectF(des);
//			canvas.drawRoundRect(rectF, 5, 5, paint);
//			canvas.drawBitmap(backImage, src, des, paint);
//
//			paint.setStyle(Style.FILL);
//			paint.setColor(Color.WHITE);
//			paint.setTextSize((int) (20 * MainActivity.SCALE_HORIAONTAL));
//			try {
//				dataLock.lock();
////				if (!desk.biesanMode)
//					canvas.drawText("" + cards.length, (int) (left * MainActivity.SCALE_HORIAONTAL),
//							(int) ((top + 80) * MainActivity.SCALE_VERTICAL), paint);
//			} finally {
//				dataLock.unlock();
//			}
		} else {
			Paint paint = new Paint();
			paint.setStyle(Style.STROKE);
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(1);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			try {
				dataLock.lock();
				for (int i = 0; i < cards.length; i++) {
					try {
						dataLock.lock();
						row = CardsManager.getImageRow(cards[i]);
						col = CardsManager.getImageCol(cards[i]);
					} finally {
						dataLock.unlock();
					}
					cardImage = BitmapFactory.decodeResource(context.getResources(),
							CardImage.cardImages[row][col]);
					int select = 0;
					dataLock.lock();
					if (cardsFlagF.apply(i)) {
						select = 10;
					}
					dataLock.unlock();
					src.set(0, 0, cardImage.getWidth(), cardImage.getHeight());
					des.set((int) ((left + i * CardImage.CARD_PRINT_HOFFSET) * MainActivity.SCALE_HORIAONTAL),
							(int) ((top - select) * MainActivity.SCALE_VERTICAL),
							(int) ((left + 40 + i * CardImage.CARD_PRINT_HOFFSET) * MainActivity.SCALE_HORIAONTAL), (int) ((top
									- select + 60) * MainActivity.SCALE_VERTICAL));
					RectF rectF = new RectF(des);
					canvas.drawRoundRect(rectF, 5, 5, paint);
					canvas.drawBitmap(cardImage, src, des, paint);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			} finally {
				dataLock.unlock();
			}
		}
	}
	public void paintResultCards(Canvas canvas) {
		// TODO Auto-generated method stub
		Rect src = new Rect();
		Rect des = new Rect();
		int row;
		int col;
		Supplier<Integer> getLen = () -> {
			int ret;
			try {
				dataLock.lock();
				ret = cards.length;
			} finally {
				dataLock.unlock();
			}
			return ret;
		};

		try {
			dataLock.lock();
			for (int i = 0; i < getLen.get(); i++) {
				try {
					dataLock.lock();
					row = CardsManager.getImageRow(cards[i]);
					col = CardsManager.getImageCol(cards[i]);
				} finally {
					dataLock.unlock();
				}
				cardImage = BitmapFactory.decodeResource(context.getResources(),
						CardImage.cardImages[row][col]);
				Paint paint = new Paint();
				paint.setStyle(Style.STROKE);
				paint.setColor(Color.BLACK);
				paint.setStrokeWidth(1);
				paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
				if (paintDirection == CardsType.direction_Vertical) {
					src.set(0, 0, cardImage.getWidth(), cardImage.getHeight());
					des.set((int) (left * MainActivity.SCALE_HORIAONTAL),
							(int) ((top - 40 + i * CardImage.CARD_PRINT_VOFFSET) * MainActivity.SCALE_VERTICAL),
							(int) ((left + CardImage.CARD_PRINT_WIDTH) * MainActivity.SCALE_HORIAONTAL),
							(int) ((top - CardImage.CARD_PRINT_HEIGHT + 20 + i * CardImage.CARD_PRINT_VOFFSET) * MainActivity.SCALE_VERTICAL));
					RectF rectF = new RectF(des);
					canvas.drawRoundRect(rectF, 5, 5, paint);
					canvas.drawBitmap(cardImage, src, des, paint);

				}
				else {
					src.set(0, 0, cardImage.getWidth(), cardImage.getHeight());
					des.set((int) ((left + 40 + i * CardImage.CARD_PRINT_HOFFSET) * MainActivity.SCALE_HORIAONTAL),
							(int) (top * MainActivity.SCALE_VERTICAL),
							(int) ((left + 40 + CardImage.CARD_PRINT_WIDTH + i * CardImage.CARD_PRINT_HOFFSET) * MainActivity.SCALE_HORIAONTAL),
							(int) ((top + CardImage.CARD_PRINT_HEIGHT) * MainActivity.SCALE_VERTICAL));
					RectF rectF = new RectF(des);
					canvas.drawRoundRect(rectF, 5, 5, paint);
					canvas.drawBitmap(cardImage, src, des, paint);

				}
			}
		} finally {
			dataLock.unlock();
		}
	}

	private boolean has4() {
		GJPlayerCardsAnalyzer ana = GJPlayerCardsAnalyzer.obtain();
		ana.setPokes(cards);
		return ana.getCountOfType(4) > 0;
	}

	private boolean hasPureGouji() {
		if (CardsManager.getCardsAmount(cards, 14) >= 2)
			return true;
		if (CardsManager.getCardsAmount(cards, 13) >= 2)
			return true;
		if (CardsManager.getCardsAmount(cards, 12) >= 3)
			return true;
		if (CardsManager.getCardsAmount(cards, 11) >= 4)
			return true;
		if (CardsManager.getCardsAmount(cards, 10) >= 5)
			return true;
		return false;
	}

	private int[] outCardDian() {
		try {
			dataLock.lock();
			int[] result = new int[0];
			for (int card : cards) {
				if (CardsManager.getCardNumber(card) == 4)
					result = ArrayUtils.add(result, card);
			}
			return result;
		} finally {
			dataLock.unlock();
		}
	}

	private boolean judgeQidian() {
		if (qidian)
			return true;
		if (!hasPureGouji())
			return true;
		try {
			dataLock.lock();
			for (int card : cards) {
				if (CardsManager.getCardNumber(card) > 4)
					return false;
			}
		} finally {
			dataLock.unlock();
		}
		return true;
	}

	public CardsHolder chupaiAI(CardsHolder card) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		int[] pokeWanted = null;

		if (card == null) {
			if (has4() && (dianFlag || judgeQidian()))
				pokeWanted = outCardDian();
			else
				pokeWanted = CardsManager.outCardByItsself(cards, last, next);
		}
		else {
			pokeWanted = CardsManager.findTheRightCard(card, cards, last, next);
		}
		if (pokeWanted == null) {
			return null;
		}
		for (int i = 0; i < pokeWanted.length; i++) {
			for (int j = 0; j < cards.length; j++) {
				if (cards[j] == pokeWanted[i]) {
					cards[j] = -1;
					break;
				}
			}
		}
		int[] newpokes = new int[0];
		if (cards.length - pokeWanted.length > 0) {
			newpokes = new int[cards.length - pokeWanted.length];
		}
		int j = 0;
		for (int i = 0; i < cards.length; i++) {
			if (cards[i] != -1) {
				newpokes[j] = cards[i];
				j++;
			}
		}
		this.cards = newpokes;
		CardsHolder thiscard = new CardsHolder(pokeWanted, playerId, context);
		// �����������һ����
//		Desk.cardsOnDesktop = thiscard;
		this.latestCards = thiscard;
		for (int c : pokeWanted) {
			if (CardsManager.getCardNumber(c) == 4) {
				if (dianFlag)
					desk.setDian(playerId);
				else if (!qidian)
					desk.setQidian(playerId);
				break;
			}
		}
		dianFlag = false;
		return thiscard;
	}

	public CardsHolder chupaiSan(CardsHolder card) {
		desk.setShouldPaintButtons(false);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		CardsHolder ret = new CardsHolder(cards, playerId, context);
		cards = new int[0];
		latestCards = ret;
		desk.setShouldPaintButtons(true);
		return ret;
	}

	@SuppressLint("ShowToast")
	public CardsHolder chupai(CardsHolder card) {
		int count = 0;
		Function<Integer, Boolean> cardsFlagF = (i) -> {
			boolean ret;
			try {
				dataLock.lock();
				ret = cardsFlag[i];
			} finally {
				dataLock.unlock();
			}
			return ret;
		};
		try {
			dataLock.lock();
			for (int i = 0; i < cards.length; i++) {
				if (cardsFlagF.apply(i)) {
					count++;
					System.out.println("���ƣ�" + String.valueOf(CardsManager.getCardNumber(cards[i])));
					if (desk.biesanMode && CardsManager.getCardNumber(cards[i]) == 3) {
						MainActivity.handler.sendEmptyMessage(MainActivity.SAN_CARD);
						return null;
					}
				}
			}
		} finally {
			dataLock.unlock();
		}
		int[] chupaiPokes = new int[count];
		int j = 0;
		try {
			dataLock.lock();
			for (int i = 0; i < cards.length; i++) {
				if (cardsFlagF.apply(i)) {
					chupaiPokes[j] = cards[i];
					j++;
				}
			}
		} finally {
			dataLock.unlock();
		}
//		int cardType = CardsManager.getType(chupaiPokes);
//		System.out.println("cardType:" + cardType);
		boolean correct = CardsManager.isCardsCorrect(chupaiPokes);
		if (!correct) {
			if (chupaiPokes.length != 0) {
				MainActivity.handler.sendEmptyMessage(MainActivity.WRONG_CARD);
			}
			else {
				MainActivity.handler.sendEmptyMessage(MainActivity.EMPTY_CARD);
			}
			return null;
		}
		CardsHolder newLatestCardsHolder = new CardsHolder(chupaiPokes, playerId, context);
		if (card == null) {
//			Desk.cardsOnDesktop = newLatestCardsHolder;
			this.latestCards = newLatestCardsHolder;

			int[] newPokes = new int[cards.length - count];
			int k = 0;
			for (int i = 0; i < cards.length; i++) {
				if (!cardsFlagF.apply(i)) {
					newPokes[k] = cards[i];
					k++;
				}

			}
			this.cards = newPokes;
			try {
				dataLock.lock();
				this.cardsFlag = new boolean[cards.length];
			} finally {
				dataLock.unlock();
			}
		}
		else {

			if (CardsManager.compare(newLatestCardsHolder, card) == 1) {
//				Desk.cardsOnDesktop = newLatestCardsHolder;
				this.latestCards = newLatestCardsHolder;

				int[] newPokes = new int[cards.length - count];
				int ni = 0;
				for (int i = 0; i < cards.length; i++) {
					if (!cardsFlagF.apply(i)) {
						newPokes[ni] = cards[i];
						ni++;
					}
				}
				this.cards = newPokes;
				this.cardsFlag = new boolean[cards.length];
			}
			if (CardsManager.compare(newLatestCardsHolder, card) == 0) {
				MainActivity.handler.sendEmptyMessage(MainActivity.SMALL_CARD);
				return null;
			}
			if (CardsManager.compare(newLatestCardsHolder, card) == -1) {
				MainActivity.handler.sendEmptyMessage(MainActivity.WRONG_CARD);
				return null;
			}
		}
		for (int c : chupaiPokes) {
			if (CardsManager.getCardNumber(c) == 4) {
				if (dianFlag)
					desk.setDian(playerId);
				else if (!qidian)
					desk.setQidian(playerId);
				break;
			}
		}
		dianFlag = false;
		return newLatestCardsHolder;
	}

	private boolean shaopaiAvaliable(int[] cards) {
		// TODO
//		int dCount = 0, xCount = 0, moneyCount = 0;
//		ArrayList<Integer> analyzed = new ArrayList<>();
//		for (int card : cards) {
//			if (CardsManager.getCardNumber(card) == 17)
//				dCount++;
//			else if (CardsManager.getCardNumber(card) == 16)
//				xCount++;
//			else if (CardsManager.getCardNumber(card) == 15)
//				moneyCount++;
//			else {
//				if (analyzed.contains(CardsManager.getCardNumber(card)))
//					continue;
//				analyzed.add(CardsManager.getCardNumber(card));
//				int amount = CardsManager.getCardsAmount(cards, card);
//			}
//		}
		return true;
	}

	public boolean onAskingForShaopai(int[] cards) {
		//
		if (shaopaiAvaliable(cards)) {
			if (playerId == 0) {
				desk.shaopaiDecisionParkingThread = Thread.currentThread();
				desk.shaopaiDecisionWaiting = true;
//				desk.shouldPaintButtons = true;
				LockSupport.park();
				return desk.shaopaiDecisionResult;
			} else {
				return false;
			}
		}
		return false;
	}

	public void onTuch(int x, int y) {
		try {
			dataLock.lock();
			for (int i = 0; i < cards.length; i++) {
				// �ж��������Ʊ�ѡ�У����ñ�־
				if (i != cards.length - 1) {
					if (CardsManager.inRect(x, y,
							(int) ((left + i * CardImage.CARD_PRINT_HOFFSET) * MainActivity.SCALE_HORIAONTAL),
							(int) ((top - (cardsFlag[i] ? 10 : 0)) * MainActivity.SCALE_VERTICAL),
							(int) (20 * MainActivity.SCALE_HORIAONTAL),
							(int) (60 * MainActivity.SCALE_VERTICAL))) {
						onCardClicked(i);
						break;
					}
				} else {
					if (CardsManager.inRect(x, y,
							(int) ((left + i * CardImage.CARD_PRINT_HOFFSET) * MainActivity.SCALE_HORIAONTAL),
							(int) ((top - (cardsFlag[i] ? 10 : 0)) * MainActivity.SCALE_VERTICAL),
							(int) (40 * MainActivity.SCALE_HORIAONTAL),
							(int) (60 * MainActivity.SCALE_VERTICAL))) {
						onCardClicked(i);
						break;
					}
				}

			}
		} finally {
			dataLock.unlock();
		}
	}

	private void onCardClicked(int cardIndex) {
		int card = cards[cardIndex];
		if (CardsManager.getCardNumber(card) == 4) {
			int i = 0;
			for (int c : cards) {
				if (CardsManager.getCardNumber(c) == 4) {
					cardsFlag[i] = !cardsFlag[i];
				}
				i++;
			}
		} else {
			cardsFlag[cardIndex] = !cardsFlag[cardIndex];
		}
	}

	public void redo() {
		// TODO Auto-generated method stub
		for (int i = 0; i < cardsFlag.length; i++) {
			cardsFlag[i] = false;
		}
	}

}

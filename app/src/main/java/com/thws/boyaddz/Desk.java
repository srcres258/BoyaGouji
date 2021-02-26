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
import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;

import java.util.ArrayList;
import java.util.Locale;

public class Desk {
    public static int winId = -1;
    Bitmap cardImg;
    Bitmap redoImage;
    Bitmap passImage;
    Bitmap chuPaiImage;
    Bitmap tiShiImage;
    Bitmap farmerImage;
    Bitmap landlordImage;
    Context context;
    private int[] scores = new int[3];
    private int[] threeCards = new int[3];
    private int[][] threeCardsPosition = {{170, 10}, {220, 10}, {270, 10}};
    private int[][] timeLimitePosition = {{130, 190}, {80, 80}, {360, 80}};
    private int[][] passPosition = {{130, 190}, {80, 80}, {360, 80}};
    private int[][] playerLatestCardsPosition = {{130, 140}, {80, 60}, {360, 60}};
    private int[][] playerCardsPosition = {{30, 210}, {30, 60}, {410, 60}};
    private int[][] scorePosition = {{70, 290}, {70, 30}, {340, 30}};
    private int[][] iconPosition = {{30, 270}, {30, 10}, {410, 10}};
    private int buttonPosition_X = 240;
    private int buttonPosition_Y = 160;
    private boolean[] canPass = new boolean[3];
    private int[][] playerCards = new int[3][17];
    private boolean canDrawLatestCards = false;
    private int[] allCards = new int[54];
    private int currentScore = 10;
    private int currentId = 0;
    private int currentCircle = 0;
    public static CardsHolder cardsOnDesktop = null;
    private int timeLimite = 300;
    private int result[] = new int[3];
    /**
     * * -1:���¿�ʼ 0:��Ϸ�� 1:���ֽ���
     */
    private int op = -1;
    public static Player[] players = new Player[3];
    public static int multiple = 1;
    public static int boss = 0;
    public boolean ifClickChupai = false;
    public boolean biesanMode = true;
    private ArrayList<Integer> beimenPlayerIds = new ArrayList<>();

    public Desk(Context context) {
        this.context = context;
        redoImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_redo);
        passImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_pass);
        chuPaiImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_chupai);
        tiShiImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_tishi);
        farmerImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_farmer);
        landlordImage = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.icon_landlord);
    }

    public void gameLogic() {
        switch (op) {
            case -1:
                init();
                op = 0;
                break;
            case 0:
                checkGameOver();
                break;
            case 1:
                break;
        }
    }

    public void controlPaint(Canvas canvas) {
        switch (op) {
            case -1:
                break;
            case 0:
                paintGaming(canvas);
                break;
            case 1:
                paintResult(canvas);
                break;

        }
    }

    private boolean judgeSan() {
        boolean res = true;
        for (int i : players[currentId].cards)
            res &= i < 4;
        return res;
    }

    private boolean judgeBeimen(Player lastPlayer) {
        boolean res = true;
        for (int i : lastPlayer.cards)
            res &= i < 4;
        return res;
    }

    private void calculateResult() {
        boolean pingju = false;
        if (boss == winId) {
            for (int i = 0; i < 3; i++) {
                if (i == boss) {
                    result[i] = currentScore * multiple * 2;
                    scores[i] += currentScore * multiple * 2;
                } else {
                    result[i] = -currentScore * multiple;
                    scores[i] -= currentScore * multiple;
                }
            }
        } else {
            for (int i = 0; i < 3; i++) {
                if (beimenPlayerIds.contains(1) || beimenPlayerIds.contains(2)) {
                    pingju = true;
                    result[i] = 0;
                } else if (i != boss) {
                    result[i] = currentScore * multiple;
                    scores[i] += currentScore * multiple;
                } else {
                    result[i] = -currentScore * multiple * 2;
                    scores[i] -= currentScore * multiple * 2;
                }
            }
        }
        if (pingju)
            SoundManager.playVictorySound();
        else if (winId == 0)
            SoundManager.playVictorySound();
        else
            SoundManager.playFailureSound();
    }

    private void checkGameOver() {
        for (int k = 0; k < 3; k++) {
            if (players[k].cards.length == 0) {
                op = 1;
                winId = k;
                calculateResult();
                return;
            }
        }
        if (biesanMode) {
            for (int i : beimenPlayerIds)
                if (i == boss) {
                    op = 1;
                    if (i == 2)
                        winId = 0;
                    else
                        winId = i + 1;
                    calculateResult();
                    return;
                }
        }

        if (biesanMode && beimenPlayerIds.contains(currentId)) {
//            if (currentId == cardsOnDesktop.playerId) {
//                currentCircle = 0;
//                cardsOnDesktop = null;
//                players[currentId].latestCards = null;
//            }
//        	nextPerson();
            beimenNext();
        	return;
		}

        if (judgeSan()) {
            CardsHolder tempcard = players[currentId].chupaiSan(cardsOnDesktop);
            if (tempcard != null) {
                cardsOnDesktop = tempcard;
                cardsOnDesktop.playSound();
                nextPerson();
            } else {
                buyao();
            }
        } else if (currentId == 1 || currentId == 2) {
			if (timeLimite <= 300 && timeLimite >= 0) {
				CardsHolder tempcard = players[currentId].chupaiAI(cardsOnDesktop);
				if (tempcard != null) {
                    if (cardsOnDesktop != null && judgeBeimen(players[cardsOnDesktop.playerId])) {
                        beimenPlayerIds.add(cardsOnDesktop.playerId);
                        SoundManager.playMenSound();
                    }
					cardsOnDesktop = tempcard;
					cardsOnDesktop.playSound();
					nextPerson();
				}
				else {
					buyao();
				}
			}
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            buyao();
        } else {
            if (timeLimite <= 300 && timeLimite >= 0) {
                if (ifClickChupai == true) {
                    CardsHolder card = players[0].chupai(cardsOnDesktop);
                    if (card != null) {
                        if (cardsOnDesktop != null && judgeBeimen(players[cardsOnDesktop.playerId])) {
                            beimenPlayerIds.add(cardsOnDesktop.playerId);
                            SoundManager.playMenSound();
                        }
                        cardsOnDesktop = card;
                        cardsOnDesktop.playSound();
                        nextPerson();
                    }
                    ifClickChupai = false;
                }

            } else {
                if (currentCircle != 0) {
                    buyao();
                } else {
                    CardsHolder autoCard = players[currentId].chupaiAI(cardsOnDesktop);
                    cardsOnDesktop = autoCard;
                    cardsOnDesktop.playSound();
                    nextPerson();

                }

            }

        }
        timeLimite -= 2;
        canDrawLatestCards = true;

    }

    public void init() {
        allCards = new int[54];
        playerCards = new int[3][17];
        threeCards = new int[3];
        winId = -1;
        currentScore = 3;
        multiple = 1;
        cardsOnDesktop = null;
        currentCircle = 0;
        currentId = 0;
        for (int i = 0; i < 3; i++) {
            scores[i] = 50;
        }
        for (int i = 0; i < 3; i++) {
            canPass[i] = false;
        }
        for (int i = 0; i < allCards.length; i++) {
            allCards[i] = i;
        }
        CardsManager.shuffle(allCards);
        fapai(allCards);
        chooseBoss();
        CardsManager.sort(playerCards[0]);
        CardsManager.sort(playerCards[1]);
        CardsManager.sort(playerCards[2]);
        if (biesanMode) {
            for (int i = 0; i < 3; i++) {
                int sanAmount = 0;
                for (int c : playerCards[i]) {
                    if (c < 4)
                        sanAmount++;
                }
                if (sanAmount == 0)
                    maisan(i);
            }
        }
        players[0] = new Player(playerCards[0], playerCardsPosition[0][0],
                playerCardsPosition[0][1], CardsType.direction_Horizontal, 0, this, context);
        players[1] = new Player(playerCards[1], playerCardsPosition[1][0],
                playerCardsPosition[1][1], CardsType.direction_Vertical, 1, this, context);
        players[2] = new Player(playerCards[2], playerCardsPosition[2][0],
                playerCardsPosition[2][1], CardsType.direction_Vertical, 2, this, context);
        players[0].setLastAndNext(players[1], players[2]);
        players[1].setLastAndNext(players[2], players[0]);
        players[2].setLastAndNext(players[0], players[1]);
    }

    public void fapai(int[] cards) {
        for (int i = 0; i < 51; i++) {
            playerCards[i / 17][i % 17] = cards[i];
        }
        threeCards[0] = cards[51];
        threeCards[1] = cards[52];
        threeCards[2] = cards[53];
    }

    private void maisan(int id) {
        Log.i("BoYaDDZ", "maisan: " + id);
        int targetId = -1;
        // Player objects hasn't been initialized yet, so use numeric IDs.
        for (int i = 0; i < 3; i++) {
            if (id == i)
                continue;
            int sanAmount = 0;
            for (int c : playerCards[i]) {
                if (c < 4)
                    sanAmount++;
            }
            if (sanAmount > 1) {
                targetId = i;
                break;
            }
        }
        // Swap cards and then resort.
        int c1 = playerCards[targetId][playerCards[targetId].length - 1];
        int c2 = playerCards[id][0];
        playerCards[targetId][playerCards[targetId].length - 1] = playerCards[id][0];
        playerCards[id][0] = c1;
        CardsManager.sort(playerCards[id]);
        CardsManager.sort(playerCards[targetId]);
        ToastUtils.showLong(String.format(Locale.getDefault(), "玩家%d用%s向玩家%d买了1张3",
                id, CardsManager.getCardString(CardsManager.getCardNumber(c2)), targetId));
    }

    private void chooseBoss() {
        currentId = boss;
        int[] diZhuCards = new int[20];
        for (int i = 0; i < 17; i++) {
            diZhuCards[i] = playerCards[boss][i];
        }
        diZhuCards[17] = threeCards[0];
        diZhuCards[18] = threeCards[1];
        diZhuCards[19] = threeCards[2];
        playerCards[boss] = diZhuCards;
    }

    private void buyao() {
        players[currentId].latestCards = null;
        canPass[currentId] = true;
        nextPerson();
        if (cardsOnDesktop != null && currentId == cardsOnDesktop.playerId) {
            currentCircle = 0;
            cardsOnDesktop = null;
            players[currentId].latestCards = null;
        }
        SoundManager.playGuopaiSound();
    }

    private void beimenNext() {
//        players[currentId].latestCards = null;
        canPass[currentId] = true;
        nextPerson();
        if (cardsOnDesktop != null && currentId == cardsOnDesktop.playerId) {
            currentCircle = 0;
            cardsOnDesktop = null;
            players[currentId].latestCards = null;
        }
    }

    private void nextPerson() {
        switch (currentId) {
            case 0:
                currentId = 2;
                break;
            case 1:
                currentId = 0;
                break;
            case 2:
                currentId = 1;
                break;
        }
        currentCircle++;
        timeLimite = 300;
    }

    private void paintGaming(Canvas canvas) {

        players[0].paint(canvas);
        players[1].paint(canvas);
        players[2].paint(canvas);
        paintThreeCards(canvas);
        paintIconAndScore(canvas);
        paintTimeLimite(canvas);

        if (currentId == 0) {
            Rect src = new Rect();
            Rect dst = new Rect();

            src.set(0, 0, chuPaiImage.getWidth(), chuPaiImage.getHeight());
            dst.set((int) (buttonPosition_X * MainActivity.SCALE_HORIAONTAL),
                    (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
                    (int) ((buttonPosition_X + 80) * MainActivity.SCALE_HORIAONTAL),
                    (int) ((buttonPosition_Y + 40) * MainActivity.SCALE_VERTICAL));
            canvas.drawBitmap(chuPaiImage, src, dst, null);

            if (currentCircle != 0) {
                src.set(0, 0, passImage.getWidth(), passImage.getHeight());
                dst.set((int) ((buttonPosition_X - 80) * MainActivity.SCALE_HORIAONTAL),
                        (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
                        (int) ((buttonPosition_X) * MainActivity.SCALE_HORIAONTAL),
                        (int) ((buttonPosition_Y + 40) * MainActivity.SCALE_VERTICAL));
                canvas.drawBitmap(passImage, src, dst, null);
            }

            src.set(0, 0, redoImage.getWidth(), redoImage.getHeight());
            dst.set((int) ((buttonPosition_X + 80) * MainActivity.SCALE_HORIAONTAL),
                    (int) ((buttonPosition_Y) * MainActivity.SCALE_VERTICAL),
                    (int) ((buttonPosition_X + 160) * MainActivity.SCALE_HORIAONTAL),
                    (int) ((buttonPosition_Y + 40) * MainActivity.SCALE_VERTICAL));
            canvas.drawBitmap(redoImage, src, dst, null);

            src.set(0, 0, tiShiImage.getWidth(), tiShiImage.getHeight());
            dst.set((int) ((buttonPosition_X + 160) * MainActivity.SCALE_HORIAONTAL),
                    (int) ((buttonPosition_Y) * MainActivity.SCALE_VERTICAL),
                    (int) ((buttonPosition_X + 240) * MainActivity.SCALE_HORIAONTAL),
                    (int) ((buttonPosition_Y + 40) * MainActivity.SCALE_VERTICAL));
            canvas.drawBitmap(tiShiImage, src, dst, null);

        }

        for (int i = 0; i < 3; i++) {
            if (currentId != i && players[i].latestCards != null && canDrawLatestCards == true) {
                players[i].latestCards.paint(canvas, playerLatestCardsPosition[i][0],
                        playerLatestCardsPosition[i][1], players[i].paintDirection);
            }
            if (currentId != i && players[i].latestCards == null && canPass[i] == true) {
                paintPass(canvas, i);
            }
        }

        if (biesanMode) {
        	for (int i : beimenPlayerIds) {
        		paintBeimen(canvas, i);
			}
		}
    }

    private void paintTimeLimite(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setTextSize((int) (16 * MainActivity.SCALE_HORIAONTAL));
        for (int i = 0; i < 3; i++) {
            if (i == currentId) {
                canvas.drawText("" + (timeLimite / 10),
                        (int) (timeLimitePosition[i][0] * MainActivity.SCALE_HORIAONTAL),
                        (int) (timeLimitePosition[i][1] * MainActivity.SCALE_VERTICAL), paint);
            }
        }
    }

    private void paintBeimen(Canvas canvas, int id) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setTextSize((int) (16 * MainActivity.SCALE_HORIAONTAL));
        canvas.drawText("被闷", (int) (passPosition[id][0] * MainActivity.SCALE_HORIAONTAL),
                (int) (passPosition[id][1] * MainActivity.SCALE_VERTICAL), paint);
    }

    private void paintPass(Canvas canvas, int id) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setTextSize((int) (16 * MainActivity.SCALE_HORIAONTAL));
        canvas.drawText("过牌", (int) (passPosition[id][0] * MainActivity.SCALE_HORIAONTAL),
                (int) (passPosition[id][1] * MainActivity.SCALE_VERTICAL), paint);

    }

    private void paintIconAndScore(Canvas canvas) {

        Paint paint = new Paint();
        paint.setTextSize((int) (16 * MainActivity.SCALE_VERTICAL));
        Rect src = new Rect();
        Rect dst = new Rect();
        for (int i = 0; i < 3; i++) {
            if (boss == i) {
                paint.setStyle(Style.STROKE);
                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(1);
                paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
                src.set(0, 0, landlordImage.getWidth(), landlordImage.getHeight());
                dst.set((int) (iconPosition[i][0] * MainActivity.SCALE_HORIAONTAL),
                        (int) (iconPosition[i][1] * MainActivity.SCALE_VERTICAL),
                        (int) ((iconPosition[i][0] + 40) * MainActivity.SCALE_HORIAONTAL),
                        (int) ((iconPosition[i][1] + 40) * MainActivity.SCALE_VERTICAL));
                RectF rectF = new RectF(dst);
                canvas.drawRoundRect(rectF, 5, 5, paint);
                canvas.drawBitmap(landlordImage, src, dst, paint);

                paint.setStyle(Style.FILL);
                paint.setColor(Color.WHITE);
                canvas.drawText("玩家" + i,
                        (int) (scorePosition[i][0] * MainActivity.SCALE_HORIAONTAL),
                        (int) (scorePosition[i][1] * MainActivity.SCALE_VERTICAL), paint);
                canvas.drawText("积分" + scores[i],
                        (int) (scorePosition[i][0] * MainActivity.SCALE_HORIAONTAL),
                        (int) ((scorePosition[i][1] + 20) * MainActivity.SCALE_VERTICAL), paint);
            } else {
                paint.setStyle(Style.STROKE);
                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(1);
                paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
                src.set(0, 0, farmerImage.getWidth(), farmerImage.getHeight());
                dst.set((int) (iconPosition[i][0] * MainActivity.SCALE_HORIAONTAL),
                        (int) (iconPosition[i][1] * MainActivity.SCALE_VERTICAL),
                        (int) ((iconPosition[i][0] + 40) * MainActivity.SCALE_HORIAONTAL),
                        (int) ((iconPosition[i][1] + 40) * MainActivity.SCALE_VERTICAL));
                RectF rectF = new RectF(dst);
                canvas.drawRoundRect(rectF, 5, 5, paint);
                canvas.drawBitmap(farmerImage, src, rectF, paint);

                paint.setStyle(Style.FILL);
                paint.setColor(Color.WHITE);
                canvas.drawText("玩家" + i,
                        (int) (scorePosition[i][0] * MainActivity.SCALE_HORIAONTAL),
                        (int) (scorePosition[i][1] * MainActivity.SCALE_VERTICAL), paint);
                canvas.drawText("积分" + scores[i],
                        (int) (scorePosition[i][0] * MainActivity.SCALE_HORIAONTAL),
                        (int) ((scorePosition[i][1] + 20) * MainActivity.SCALE_VERTICAL), paint);
            }
        }

        paint.setStyle(Style.FILL);
        paint.setColor(Color.WHITE);
        if (biesanMode)
            canvas.drawText("憋3模式(最后出3)" +
                            "  分数" + currentScore + "  倍数" + multiple,
                    (int) (150 * MainActivity.SCALE_HORIAONTAL),
                    (int) (150 * MainActivity.SCALE_VERTICAL), paint);
        else
            canvas.drawText("分数" + currentScore + "  倍数" + multiple,
                    (int) (150 * MainActivity.SCALE_HORIAONTAL),
                    (int) (150 * MainActivity.SCALE_VERTICAL), paint);
    }

    private void paintResult(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize((int) (20 * MainActivity.SCALE_HORIAONTAL));
        for (int i = 0; i < 4; i++) {
            if (i < 3)
                canvas.drawText("玩家" + i + "得分" + result[i] + "   当前积分" + scores[i],
                        (int) (110 * MainActivity.SCALE_HORIAONTAL),
                        (int) ((96 + i * 30) * MainActivity.SCALE_VERTICAL), paint);
            else
                canvas.drawText("点击屏幕开始下一局",
                        (int) (110 * MainActivity.SCALE_HORIAONTAL),
                        (int) ((96 + i * 30) * MainActivity.SCALE_VERTICAL), paint);
        }
        for (int i = 0; i < 3; i++) {
            players[i].paintResultCards(canvas);
        }

    }

    private void paintThreeCards(Canvas canvas) {
        Rect src = new Rect();
        Rect dst = new Rect();
        Paint paint = new Paint();
        paint.setStyle(Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        for (int i = 0; i < 3; i++) {
            int row = CardsManager.getImageRow(threeCards[i]);
            int col = CardsManager.getImageCol(threeCards[i]);
            Bitmap image = BitmapFactory.decodeResource(context.getResources(),
                    CardImage.cardImages[row][col]);
            src.set(0, 0, image.getWidth(), image.getHeight());
            dst.set((int) (threeCardsPosition[i][0] * MainActivity.SCALE_HORIAONTAL),
                    (int) (threeCardsPosition[i][1] * MainActivity.SCALE_VERTICAL),
                    (int) ((threeCardsPosition[i][0] + 40) * MainActivity.SCALE_HORIAONTAL),
                    (int) ((threeCardsPosition[i][1] + 60) * MainActivity.SCALE_VERTICAL));
            RectF rectF = new RectF(dst);
            canvas.drawBitmap(image, src, dst, paint);
            canvas.drawRoundRect(rectF, 5, 5, paint);
        }
    }

    public void restart() {
        op = 1;
    }

    public void onTuch(int x, int y) {

        if (op == 1) {
            beimenPlayerIds = new ArrayList<>();
            op = -1;
        }
        players[0].onTuch(x, y);
        if (currentId == 0) {

            if (CardsManager.inRect(x, y, (int) (buttonPosition_X * MainActivity.SCALE_HORIAONTAL),
                    (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
                    (int) (80 * MainActivity.SCALE_HORIAONTAL),
                    (int) (40 * MainActivity.SCALE_VERTICAL))) {
                System.out.println("����");
                ifClickChupai = true;

            }
            if (currentCircle != 0) {
                if (CardsManager.inRect(x, y,
                        (int) ((buttonPosition_X - 80) * MainActivity.SCALE_HORIAONTAL),
                        (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
                        (int) (80 * MainActivity.SCALE_HORIAONTAL),
                        (int) (40 * MainActivity.SCALE_VERTICAL))) {
                    System.out.println("��Ҫ");
                    buyao();
                }
            }
            if (CardsManager.inRect(x, y,
                    (int) ((buttonPosition_X + 80) * MainActivity.SCALE_HORIAONTAL),
                    (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
                    (int) (80 * MainActivity.SCALE_HORIAONTAL),
                    (int) (40 * MainActivity.SCALE_VERTICAL))) {
                System.out.println("��ѡ");
                players[0].redo();
            }
            if (CardsManager.inRect(x, y,
                    (int) ((buttonPosition_X + 160) * MainActivity.SCALE_HORIAONTAL),
                    (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
                    (int) (80 * MainActivity.SCALE_HORIAONTAL),
                    (int) (40 * MainActivity.SCALE_VERTICAL))) {
                System.out.println("��ʾ�����£�");
                restart();
            }
        }
    }
}

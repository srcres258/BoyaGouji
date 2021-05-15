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

import com.blankj.utilcode.util.ArrayUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public class Desk {
    static class DianReport {
        int id = -1;
        boolean dian = false;
    }
    static class MenReport {
        int menId = -1;
        int beimenId = -1;
    }

    public static int winId = -1;
    Bitmap cardImg;
    Bitmap redoImage;
    Bitmap passImage;
    Bitmap chuPaiImage;
    Bitmap tiShiImage;
    Bitmap farmerImage;
    Bitmap landlordImage;
    Context context;
    GameView gv;
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
    private int[][] playerCards = null;
    private boolean canDrawLatestCards = false;
    private int[] allCards = null;
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
    private ReentrantLock dataLock = new ReentrantLock();
    private boolean gongCompleted = false;
    private boolean shouldPaintButtons = false;
    private LinkedList<DianReport> dianReportList = new LinkedList<>();
    private LinkedList<MenReport> menReportList = new LinkedList<>();
    private CharSequence gongText = "";
    private LinkedList<Integer> doneIdList = new LinkedList<>();

    public Desk(Context context, GameView gv) {
        this.context = context;
        this.gv = gv;
        redoImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_redo);
        passImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_pass);
        chuPaiImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_chupai);
        tiShiImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_tishi);
        farmerImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_farmer);
        landlordImage = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.icon_landlord);
    }

    public Lock getDataLock() {
        return dataLock;
    }

    public void gameLogic() {
        switch (op) {
            case -1:
                init();
                op = 0;
                break;
            case 0:
                if (biesanMode && !gongCompleted) {
                    shouldPaintButtons = false;
                    maisan();
                    maisi();
                    jingong();
                    gongCompleted = true;
                    shouldPaintButtons = true;
                }
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
        if (doneIdList.contains(lastPlayer.playerId))
            return false;
        boolean res = true;
        for (int i : lastPlayer.cards)
            res &= i < 4;
        return res;
    }

    private void submitMaiAnimation(int buyerId, int sellerId, int usingCard, int sanCard) {
        GiveCardAnimation a1 = new GiveCardAnimation(context, usingCard,
                (int) (iconPosition[buyerId][0] * MainActivity.SCALE_HORIAONTAL),
                (int) (iconPosition[buyerId][1] * MainActivity.SCALE_VERTICAL),
                (int) (iconPosition[sellerId][0] * MainActivity.SCALE_HORIAONTAL),
                (int) (iconPosition[sellerId][1] * MainActivity.SCALE_VERTICAL));
        GiveCardAnimation a2 = new GiveCardAnimation(context, sanCard,
                (int) (iconPosition[sellerId][0] * MainActivity.SCALE_HORIAONTAL),
                (int) (iconPosition[sellerId][1] * MainActivity.SCALE_VERTICAL),
                (int) (iconPosition[buyerId][0] * MainActivity.SCALE_HORIAONTAL),
                (int) (iconPosition[buyerId][1] * MainActivity.SCALE_VERTICAL));
        gv.submitAnimation(a1);
        gv.submitAnimation(a2);
        gv.waitForAnimation(a1, a2);
    }

    private void submitMenAnimation(int srcId, int destId) {
        MenAnimation anim = new MenAnimation(context, (int) (iconPosition[srcId][0] * MainActivity.SCALE_HORIAONTAL),
                (int) (iconPosition[srcId][1] * MainActivity.SCALE_VERTICAL),
                (int) (iconPosition[destId][0] * MainActivity.SCALE_HORIAONTAL),
                (int) (iconPosition[destId][1] * MainActivity.SCALE_VERTICAL));
        gv.submitAnimation(anim);
    }

    private void submitDianAnimation(int destId) {
        DianAnimation anim = new DianAnimation(context,
                (int) (iconPosition[destId][0] * MainActivity.SCALE_HORIAONTAL),
                (int) (iconPosition[destId][1] * MainActivity.SCALE_VERTICAL));
        gv.submitAnimation(anim);
    }

    private void submitJingongAnimation(int srcId, int destId, int card) {
        GiveCardAnimation anim = new GiveCardAnimation(context, card,
                (int) (iconPosition[srcId][0] * MainActivity.SCALE_HORIAONTAL),
                (int) (iconPosition[srcId][1] * MainActivity.SCALE_VERTICAL),
                (int) (iconPosition[destId][0] * MainActivity.SCALE_HORIAONTAL),
                (int) (iconPosition[destId][1] * MainActivity.SCALE_VERTICAL));
        gv.submitAnimation(anim);
        gv.waitForAnimation(anim);
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
//        for (int k = 0; k < 3; k++) {
//            if (players[k].cards.length == 0) {
//                op = 1;
//                winId = k;
//                calculateResult();
//                return;
//            }
//        }
//        if (biesanMode) {
//            for (int i : beimenPlayerIds)
//                if (i == boss) {
//                    op = 1;
//                    if (i == 2)
//                        winId = 0;
//                    else
//                        winId = i + 1;
//                    calculateResult();
//                    return;
//                }
//        }
        if (doneIdList.size() + beimenPlayerIds.size() == 2) {
            doneIdList.add(currentId);
            op = 1;
            winId = doneIdList.get(0);
            calculateResult();
            return;
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
		} else if (doneIdList.contains(currentId)) {
            zoukeNext();
            return;
        }

        if (judgeSan()) {
            CardsHolder tempcard = players[currentId].chupaiSan(cardsOnDesktop);
//            if (tempcard != null) {
                cardsOnDesktop = tempcard;
                cardsOnDesktop.playSound();
                doneIdList.add(currentId);
                nextPerson();
//            } else {
//                buyao();
//            }
        } else if (currentId == 1 || currentId == 2) {
			if (timeLimite <= 300 && timeLimite >= 0) {
				CardsHolder tempcard = players[currentId].chupaiAI(cardsOnDesktop);
				if (tempcard != null) {
                    if (cardsOnDesktop != null && judgeBeimen(players[cardsOnDesktop.playerId])) {
                        beimenPlayerIds.add(cardsOnDesktop.playerId);
                        MenReport report = new MenReport();
                        report.menId = currentId;
                        report.beimenId = cardsOnDesktop.playerId;
                        menReportList.add(report);
                        submitMenAnimation(currentId, cardsOnDesktop.playerId);
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
//            if (timeLimite <= 300 && timeLimite >= 0) {
                if (ifClickChupai == true) {
                    CardsHolder card = players[0].chupai(cardsOnDesktop);
                    if (card != null) {
                        if (cardsOnDesktop != null && judgeBeimen(players[cardsOnDesktop.playerId])) {
                            beimenPlayerIds.add(cardsOnDesktop.playerId);
                            MenReport report = new MenReport();
                            report.menId = currentId;
                            report.beimenId = cardsOnDesktop.playerId;
                            menReportList.add(report);
                            submitMenAnimation(currentId, cardsOnDesktop.playerId);
                            SoundManager.playMenSound();
                        }
                        cardsOnDesktop = card;
                        cardsOnDesktop.playSound();
                        nextPerson();
                    }
                    ifClickChupai = false;
                }

//            } else {
//                if (currentCircle != 0) {
//                    buyao();
//                } else {
//                    CardsHolder autoCard = players[currentId].chupaiAI(cardsOnDesktop);
//                    cardsOnDesktop = autoCard;
//                    cardsOnDesktop.playSound();
//                    nextPerson();
//
//                }
//
//            }

        }
        timeLimite -= 2;
        canDrawLatestCards = true;

    }

    public void setDian(int playerId) {
        for (DianReport report : dianReportList) {
            if (report.id == playerId)
                report.dian = true;
        }
        SoundManager.playDianSound();
        submitDianAnimation(playerId);
        Log.i("BoYaDDZ", playerId + " kaidian");
    }

    public boolean queryDian(int playerId) {
        for (DianReport report : dianReportList) {
            if (report.id == playerId)
                return report.dian;
        }
        throw new IllegalStateException("DianReport isn't existing for player "
                + playerId + ", which is impossible.");
    }

    public void init() {
        allCards = new int[162];
        playerCards = new int[3][54];
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
            if (i >= 108)
                allCards[i] = i - 108;
            else if (i >= 54)
                allCards[i] = i - 54;
            else
                allCards[i] = i;
        }
        ArrayUtils.Closure<Integer> cl = new ArrayUtils.Closure<Integer>() {
            int[] tmp = ArrayUtils.copy(allCards);
            int count = 0;

            @Override
            public void execute(int index, Integer item) {
                if (count == 9)
                    return;
                if (CardsManager.getCardNumber(item) == 3) {
                    tmp = ArrayUtils.remove(tmp, index - count);
                    count++;
                }
            }
        }
        ArrayUtils.forAllDo(allCards, cl);
        allCards = cl.tmp;
        CardsManager.shuffle(allCards);
        fapai(allCards);
        chooseBoss();
        CardsManager.sort(playerCards[0]);
        CardsManager.sort(playerCards[1]);
        CardsManager.sort(playerCards[2]);
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
        for (int i = 51; i < 102; i++) {
            playerCards[(i - 54) / 17][(i - 54) % 17 + 17] = cards[i];
        }
        for (int i = 102; i < 153; i++) {
            playerCards[(i - 102) / 17][(i - 102) % 17 + 34] = cards[i];
        }
        threeCards[0] = cards[159];
        threeCards[1] = cards[160];
        threeCards[2] = cards[161];
    }

    private void maisan() {
        gongText = "等待玩家买3";
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 3; i++) {
            int sanAmount = 0;
            for (int c : playerCards[i]) {
                if (c < 4)
                    sanAmount++;
            }
            if (sanAmount == 0)
                maisanFor(i);
        }
    }

    private void maisanFor(int id) {
        Log.i("BoYaDDZ", "maisan: " + id);
        int targetId = -1;
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
//        try {
//            players[id].getDataLock().lock();
//            players[id].cards = playerCards[id];
//        } finally {
//            players[id].getDataLock().unlock();
//        }
        submitMaiAnimation(id, targetId, c2, c1);
//        ToastUtils.showLong(String.format(Locale.getDefault(), "玩家%d用%s向玩家%d买了1张3",
//                id, CardsManager.getCardString(CardsManager.getCardNumber(c2)), targetId));
    }

    private void maisi() {
        gongText = "等待玩家买4";
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 3; i++) {
            int siAmount = 0;
            for (int c : playerCards[i]) {
                if (CardsManager.getCardNumber(c) == 4)
                    siAmount++;
            }
            if (siAmount == 0)
                maisiFor(i);
        }
    }

    private void maisiFor(int id) {
        Log.i("BoYaDDZ", "maisi: " + id);
        int targetId = -1;
        for (int i = 0; i < 3; i++) {
            if (id == i)
                continue;
            int siAmount = 0;
            for (int c : playerCards[i]) {
                if (CardsManager.getCardNumber(c) == 4)
                    siAmount++;
            }
            if (siAmount > 1) {
                targetId = i;
                break;
            }
        }
        int index = -1;
        for (int i = 0; i < playerCards[targetId].length; i++) {
            if (CardsManager.getCardNumber(playerCards[targetId][i]) == 4) {
                index = i;
                break;
            }
        }
        // Swap cards and then resort.
        int c1 = playerCards[targetId][index];
        int c2 = playerCards[id][0];
        playerCards[targetId][index] = playerCards[id][0];
        playerCards[id][0] = c1;
        CardsManager.sort(playerCards[id]);
        CardsManager.sort(playerCards[targetId]);
//        try {
//            players[id].getDataLock().lock();
//            players[id].cards = playerCards[id];
//        } finally {
//            players[id].getDataLock().unlock();
//        }
        submitMaiAnimation(id, targetId, c2, c1);
//        ToastUtils.showLong(String.format(Locale.getDefault(), "玩家%d用%s向玩家%d买了1张3",
//                id, CardsManager.getCardString(CardsManager.getCardNumber(c2)), targetId));
    }

    private void jingong() {
        gongText = "等待玩家进点贡";
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (DianReport r1 : dianReportList) {
            if (r1.dian) {
                for (DianReport r2 : dianReportList) {
                    if (r2.id == r1.id)
                        continue;
                    if (r2.dian)
                        continue;
                    jingongSingleFor(r2.id, r1.id);
                }
            }
        }
        dianReportList.clear();
        for (int i = 0; i < 3; i++) {
            DianReport report = new DianReport();
            report.id = i;
            dianReportList.add(report);
        }
        gongText = "等待玩家进闷贡";
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (MenReport report : menReportList) {
            jingongSingleFor(report.beimenId, report.menId);
        }
        menReportList.clear();
        if (doneIdList.isEmpty() && beimenPlayerIds.isEmpty())
            return;
        gongText = "等待玩家进落贡";
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (beimenPlayerIds.isEmpty()) {
            jingongSingleFor(doneIdList.get(doneIdList.size() - 1), doneIdList.get(0));
        } else {
            jingongSingleFor(beimenPlayerIds.get(0), doneIdList.get(0));
        }
        beimenPlayerIds.clear();
        doneIdList.clear();
    }

    private void jingongSingleFor(int srcId, int destId) {
        Log.i("BoYaDDZ", "jingong: from " + srcId + " to " + destId);
        int c = playerCards[srcId][0];
        try {
            players[srcId].getDataLock().lock();
            playerCards[srcId] = ArrayUtils.remove(playerCards[srcId], 0);
            CardsManager.sort(playerCards[srcId]);
            players[srcId].setCards(playerCards[srcId]);
        } finally {
            players[srcId].getDataLock().unlock();
        }
        try {
            players[destId].getDataLock().lock();
            playerCards[destId] = ArrayUtils.add(playerCards[destId], c);
            CardsManager.sort(playerCards[destId]);
            players[destId].setCards(playerCards[destId]);
        } finally {
            players[destId].getDataLock().unlock();
        }
        submitJingongAnimation(srcId, destId, c);
    }

    private void chooseBoss() {
        currentId = boss;
        int[] diZhuCards = new int[playerCards[boss].length];
        for (int i = 0; i < playerCards[boss].length - 3; i++) {
            diZhuCards[i] = playerCards[boss][i];
        }
        diZhuCards[playerCards[boss].length - 3] = threeCards[0];
        diZhuCards[playerCards[boss].length - 2] = threeCards[1];
        diZhuCards[playerCards[boss].length - 1] = threeCards[2];
        playerCards[boss] = diZhuCards;
    }

    private void buyao() {
        players[currentId].latestCards = null;
        canPass[currentId] = true;
        nextPerson();
        if (cardsOnDesktop != null && currentId == cardsOnDesktop.playerId) {
//            switch (cardsOnDesktop.cardsType) {
//                case CardsType.danshun:
//                case CardsType.shuangshun:
//                case CardsType.sanshun:
//                case CardsType.feiji:
//                case CardsType.sidaier:
//                    players[currentId].dianFlag = true;
//            }
            if (GJCardsAnalyzer.judgePureGouji(cardsOnDesktop.cards))
                players[currentId].dianFlag = true;
            currentCircle = 0;
            cardsOnDesktop = null;
            players[currentId].latestCards = null;
        }
        SoundManager.playGuopaiSound();
    }

    private void zoukeNext() {
        beimenNext();
    }

    private void beimenNext() {
//        players[currentId].latestCards = null;
        canPass[currentId] = true;
        nextPerson();
        if (cardsOnDesktop != null && currentId == cardsOnDesktop.playerId) {
//            switch (cardsOnDesktop.cardsType) {
//                case CardsType.danshun:
//                case CardsType.shuangshun:
//                case CardsType.sanshun:
//                case CardsType.feiji:
//                case CardsType.sidaier:
//                    players[currentId].dianFlag = true;
//            }
            if (GJCardsAnalyzer.judgePureGouji(cardsOnDesktop.cards))
                players[currentId].dianFlag = true;
            currentCircle = 0;
            cardsOnDesktop = null;
            players[currentId].latestCards = null;
        }
    }

    private void nextPerson() {
        if (doneIdList.size() + beimenPlayerIds.size() == 3)
            return;
        Function<Integer, Integer> getNext = (m) -> {
            switch (m) {
                case 0:
                    return 2;
                case 1:
                    return 0;
                case 2:
                    return 1;
                default:
                    throw new IllegalArgumentException("Player ID is invalid");
            }
        };
        int id = currentId;
        do {
            id = getNext.apply(id);
        } while (doneIdList.contains(id) || beimenPlayerIds.contains(id));
        currentId = id;
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
        if (biesanMode && !gongCompleted)
            paintGongText(canvas);

        if (currentId == 0) {
            Rect src = new Rect();
            Rect dst = new Rect();

            if (shouldPaintButtons) {
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
            int m = 2;
        	for (int i : beimenPlayerIds) {
        		paintBeimen(canvas, i);
        		paintDoneOrder(canvas, i, m);
        		m--;
			}
        	m = 0;
            for (int i : doneIdList) {
                paintDoneOrder(canvas, i, m);
                m++;
            }
		}
    }

    private void paintGongText(Canvas canvas) {
        Paint paint = new Paint();
        paint.setTextSize((int) (16 * MainActivity.SCALE_VERTICAL));
        paint.setStyle(Style.FILL);
        paint.setColor(Color.RED);
        canvas.drawText(gongText.toString(),
                (int) (150 * MainActivity.SCALE_HORIAONTAL),
                (int) (100 * MainActivity.SCALE_VERTICAL), paint);
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
        paint.setColor(Color.GRAY);
        paint.setTextSize((int) (16 * MainActivity.SCALE_HORIAONTAL));
        canvas.drawText("闷", (int) (iconPosition[id][0] * MainActivity.SCALE_HORIAONTAL),
                (int) (iconPosition[id][1] * MainActivity.SCALE_VERTICAL), paint);
    }

    private void paintPass(Canvas canvas, int id) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setTextSize((int) (16 * MainActivity.SCALE_HORIAONTAL));
        canvas.drawText("过牌", (int) (passPosition[id][0] * MainActivity.SCALE_HORIAONTAL),
                (int) (passPosition[id][1] * MainActivity.SCALE_VERTICAL), paint);
    }

    private void paintDoneOrder(Canvas canvas, int id, int doneOrder) {
        String str;
        switch (doneOrder) {
            case 0:
                str = "头科";
                break;
            case 1:
                str = "二科";
                break;
            default:
                str = "三科";
        }
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setTextSize((int) (16 * MainActivity.SCALE_HORIAONTAL));
        canvas.drawText(str, (int) (passPosition[id][0] * MainActivity.SCALE_HORIAONTAL),
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
        if (biesanMode && !gongCompleted)
            return;

        if (op == 1) {
            shouldPaintButtons = false;
            gongCompleted = false;
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

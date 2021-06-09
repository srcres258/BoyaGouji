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
import android.graphics.Typeface;
import android.util.Log;

import com.blankj.utilcode.util.ArrayUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public class Desk {
    static class DianReport {
        int id = -1;
        boolean dian = false;
    }
    static class ShaoReport {
        int shaoId = -1;
        int beishaoId = -1;
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
    Bitmap rangpaiImage;
    Bitmap shaopaiImage;
    Bitmap bushaoImage;
    Bitmap farmerImage;
    Bitmap landlordImage;
    Context context;
    GameView gv;
    private int[] scores = new int[6];
    private int[] threeCards = new int[6];
    private int[][] threeCardsPosition = {{170, 10}, {270, 90}, {270, 10}};
//    private int[][] timeLimitePosition = {{130, 190}, {360, 140}, {360, 80}};
    private int[][] playerLatestCardsPosition = {{130, 140}, {300, 120}, {300, 60}, {180, 30}, {80, 60}, {80, 120}};
    private int[][] timeLimitePosition = ArrayUtils.copy(playerLatestCardsPosition);
    private int[][] passPosition = ArrayUtils.copy(timeLimitePosition);
//    private int[][] playerCardsPosition = {{30, 210}, {30, 60}, {410, 60}};
    private int[][] playerCardsPosition = ArrayUtils.copy(playerLatestCardsPosition);
    private int[][] scorePosition = {{70, 290}, {340, 90}, {340, 30}, {180, 15}, {70, 30}, {70, 90}};
    private int[][] iconPosition = {{30, 270}, {410, 100}, {410, 40}, {120, 20}, {30, 40}, {30, 100}};
    private String[] playerNicknames = {"印尼宽带值得信赖", "孙笑川258", "饮茶先啦", "伏拉夫爱中国",
            "两只老虎爱够级", "cheems"};
    private Bitmap[] playerAvatars = new Bitmap[6];
    private int buttonPosition_X = 240;
    private int buttonPosition_Y = 160;
    private boolean[] canPass = new boolean[6];
    private int[][] playerCards = null;
    private boolean canDrawLatestCards = false;
    private int[] allCards = null;
    private int currentScore = 10;
    private int currentId = 0;
    private int currentCircle = 0;
    public static CardsHolder cardsOnDesktop = null;
    private int timeLimite = 300;
    private int result[] = new int[6];
    private boolean burning = false;
    private int burningId = 0;
    private int burnedId = 0;
    private boolean sihuluanchan = false;
    private int rangpaiId = -1;
    boolean shaopaiDecisionWaiting = false;
    boolean shaopaiDecisionResult = false;
    Thread shaopaiDecisionParkingThread = null;
    /**
     * * -1:���¿�ʼ 0:��Ϸ�� 1:���ֽ���
     */
    private int op = -1;
    public static Player[] players = new Player[6];
    public static int multiple = 1;
    public static int boss = 0;
    public boolean ifClickChupai = false;
    public boolean biesanMode = true;
    private ArrayList<Integer> beimenPlayerIds = new ArrayList<>();
    private ReentrantLock dataLock = new ReentrantLock();
    private boolean gongCompleted = false;

    boolean shouldPaintButtons = false;
    private LinkedList<DianReport> dianReportList = new LinkedList<>();
    private LinkedList<ShaoReport> shaoReportList = new LinkedList<>();
    private LinkedList<MenReport> menReportList = new LinkedList<>();
    private CharSequence gongText = "";
    private LinkedList<Integer> doneIdList = new LinkedList<>();
    private StringBuffer jingongLog = new StringBuffer();

    public boolean isShouldPaintButtons() {
        return shouldPaintButtons;
    }

    public void setShouldPaintButtons(boolean shouldPaintButtons) {
        this.shouldPaintButtons = shouldPaintButtons;
    }

    public Desk(Context context, GameView gv) {
        this.context = context;
        this.gv = gv;
        redoImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_redo);
        passImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_pass);
        chuPaiImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_chupai);
        tiShiImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_tishi);
        rangpaiImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_rangpai);
        shaopaiImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_shaopai);
        bushaoImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.btn_bushao);
        farmerImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_farmer);
        landlordImage = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.icon_landlord);
        for (int i = 0; i < 6; i++) {
            try {
                Field f = R.drawable.class.getField("avatar" + i);
                playerAvatars[i] = BitmapFactory.decodeResource(context.getResources(), (Integer) f.get(null));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
        playerCardsPosition[0] = new int[] {30, 210};
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
        if (doneIdList.size() + beimenPlayerIds.size() == 5) {
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

//        if (!sihuluanchan && cardsOnDesktop != null
//                && GJCardsAnalyzer.judgeGouji(cardsOnDesktop.cards)
//                && !(burning && currentId == burningId)) {
//            if (currentId != players[cardsOnDesktop.playerId].getOpposite().playerId
//                    && !players[currentId].wutou) {
//                nextPerson();
//                return;
//            }
//        }

        if (judgeSan()) {
            CardsHolder tempcard = players[currentId].chupaiSan(cardsOnDesktop);
//            if (tempcard != null) {
                cardsOnDesktop = tempcard;
                cardsOnDesktop.playSound();
                doneIdList.add(currentId);
                onPlayerDone(currentId, false);
                SoundManager.playDoneSound(doneIdList.size() <= 1);
                players[currentId].state = Player.DiscardState.CHUPAI;
                if (burning) {
                    burning = false;
                    ShaoReport report = new ShaoReport();
                    report.shaoId = burningId;
                    report.beishaoId = burnedId;
                    shaoReportList.add(report);
                    players[burningId].shao = true;
                    players[burnedId].beishao = true;
                }
                nextPerson();
//            } else {
//                buyao();
//            }
//        } else if (true) {
        } else if (currentId != 0) {
			if (timeLimite <= 300 && timeLimite >= 0) {
				CardsHolder tempcard = players[currentId].chupaiAI(cardsOnDesktop);
				if (tempcard != null) {
                    if (cardsOnDesktop != null && judgeBeimen(players[cardsOnDesktop.playerId])) {
                        beimenPlayerIds.add(cardsOnDesktop.playerId);
                        MenReport report = new MenReport();
                        report.menId = currentId;
                        report.beimenId = cardsOnDesktop.playerId;
                        menReportList.add(report);
                        players[currentId].men = true;
                        players[cardsOnDesktop.playerId].beimen = true;
                        submitMenAnimation(currentId, cardsOnDesktop.playerId);
                        onPlayerDone(cardsOnDesktop.playerId, true);
                        SoundManager.playMenSound();
                        SoundManager.playDoneSound(false);
                    }
					cardsOnDesktop = tempcard;
					cardsOnDesktop.playSound();
                    players[currentId].state = Player.DiscardState.CHUPAI;
                    if (rangpaiId >= 0) {
                        players[rangpaiId].state = Player.DiscardState.GUOPAI;
                        rangpaiId = -1;
                        SoundManager.playGuopaiSound();
                    }
                    if (burning && burningId != currentId) {
                        burning = false;
                        SoundManager.playJieshaoSound();
                    }
                    if (!askForShaopai(currentId))
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
                            players[currentId].men = true;
                            players[cardsOnDesktop.playerId].beimen = true;
                            submitMenAnimation(currentId, cardsOnDesktop.playerId);
                            onPlayerDone(cardsOnDesktop.playerId, true);
                            SoundManager.playMenSound();
                            SoundManager.playDoneSound(false);
                        }
                        cardsOnDesktop = card;
                        cardsOnDesktop.playSound();
                        players[currentId].state = Player.DiscardState.CHUPAI;
                        if (rangpaiId >= 0) {
                            players[rangpaiId].state = Player.DiscardState.GUOPAI;
                            rangpaiId = -1;
                            SoundManager.playGuopaiSound();
                        }
                        if (burning && burningId != currentId) {
                            burning = false;
                            SoundManager.playJieshaoSound();
                        }
                        if (!askForShaopai(currentId))
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

    private boolean askForShaopai(int curId) {
        if (!GJCardsAnalyzer.judgeGouji(cardsOnDesktop.cards))
            return false;
        if (players[curId].wutou)
            return false;
        Function<Integer, Integer> getNext = (m) -> {
            switch (m) {
                case 0:
                    return 1;
                case 1:
                    return 2;
                case 2:
                    return 3;
                case 3:
                    return 4;
                case 4:
                    return 5;
                case 5:
                    return 0;
                default:
                    throw new IllegalArgumentException("Player ID is invalid");
            }
        };
        int id = curId;
        id = getNext.apply(id);
        do {
            if (doneIdList.contains(id) || beimenPlayerIds.contains(id)) {
                id = getNext.apply(id);
                continue;
            }
            if (id == players[curId].getOpposite().playerId) {
                id = getNext.apply(id);
                continue;
            }
            if (!(players[id].dian || players[id].qidian)) {
                id = getNext.apply(id);
                continue;
            }
            if (players[id].state.equals(Player.DiscardState.GUOPAI)) {
                id = getNext.apply(id);
                continue;
            }
            if (players[id].wutou) {
                id = getNext.apply(id);
                continue;
            }
            if (players[id].onAskingForShaopai(cardsOnDesktop.cards)) {
                break;
            } else if (players[id].getLast().playerId == curId
                    || players[id].getLastMate().playerId == curId) {
                players[id].state = Player.DiscardState.GUOPAI;
            }
            id = getNext.apply(id);
        } while (id != curId);
        if (id == curId) {
            return false;
        } else {
            burning = true;
            burningId = id;
            burnedId = cardsOnDesktop.playerId;
            currentId = id;
            SoundManager.playShaopaiSound();
            return true;
        }
    }

    public void setDian(int playerId) {
        for (DianReport report : dianReportList) {
            if (report.id == playerId)
                report.dian = true;
        }
        players[playerId].dian = true;
        SoundManager.playDianSound();
        submitDianAnimation(playerId);
        Log.i("BoYaDDZ", playerId + " kaidian");
    }

    public void setQidian(int playerId) {
        if (!players[playerId].dian && !players[playerId].qidian) {
            players[playerId].qidian = true;
            SoundManager.playQidianSound();
            Log.i("BoYaDDZ", playerId + " qidian");
        }
    }

    public void onPlayerDone(int playerId, boolean men) {
        players[playerId].getOpposite().wutou = true;
        if (!players[playerId].getOpposite().dian && !players[playerId].getOpposite().qidian)
            setQidian(players[playerId].getOpposite().playerId);
        if (doneIdList.size() + beimenPlayerIds.size() >= 2) {
            sihuluanchan = true;
            for (int i = 0; i < 6; i++) {
                if (!players[i].dian) {
                    setQidian(i);
                }
            }
        }
    }

    public void requestRangpai() {
        players[currentId].latestCards = null;
        players[currentId].state = Player.DiscardState.RANGPAI;
        canPass[currentId] = true;
        rangpaiId = currentId;
        if (!askForShaopai(currentId))
            nextPerson();
        // TODO: rangpai sound
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
        class MyClosure implements ArrayUtils.Closure<Integer> {
            int[] tmp = ArrayUtils.copy(allCards);
            int count = 0;

            @Override
            public void execute(int index, Integer item) {
                if (count == 18)
                    return;
                if (CardsManager.getCardNumber(item) == 3) {
                    tmp = ArrayUtils.remove(tmp, index - count);
                    count++;
                }
            }
        }
        allCards = new int[324];
        playerCards = new int[6][51];
        threeCards = new int[6];
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
            if (i >= 270)
                allCards[i] = i - 270;
            else if (i >= 216)
                allCards[i] = i - 216;
            else if (i >= 162)
                allCards[i] = i - 162;
            else if (i >= 108)
                allCards[i] = i - 108;
            else if (i >= 54)
                allCards[i] = i - 54;
            else
                allCards[i] = i;
        }
        MyClosure cl = new MyClosure();
        ArrayUtils.forAllDo(allCards, cl);
        allCards = cl.tmp;
        CardsManager.shuffle(allCards);
        fapai(allCards);
        chooseBoss();
        CardsManager.sort(playerCards[0]);
        CardsManager.sort(playerCards[1]);
        CardsManager.sort(playerCards[2]);
        CardsManager.sort(playerCards[3]);
        CardsManager.sort(playerCards[4]);
        CardsManager.sort(playerCards[5]);
        players[0] = new Player(playerCards[0], playerCardsPosition[0][0],
                playerCardsPosition[0][1], CardsType.direction_Horizontal, 0, this, context);
        players[1] = new Player(playerCards[1], playerCardsPosition[1][0],
                playerCardsPosition[1][1], CardsType.direction_Horizontal, 1, this, context);
        players[2] = new Player(playerCards[2], playerCardsPosition[2][0],
                playerCardsPosition[2][1], CardsType.direction_Horizontal, 2, this, context);
        players[3] = new Player(playerCards[3], playerCardsPosition[3][0],
                playerCardsPosition[3][1], CardsType.direction_Horizontal, 3, this, context);
        players[4] = new Player(playerCards[4], playerCardsPosition[4][0],
                playerCardsPosition[4][1], CardsType.direction_Horizontal, 4, this, context);
        players[5] = new Player(playerCards[5], playerCardsPosition[5][0],
                playerCardsPosition[5][1], CardsType.direction_Horizontal, 5, this, context);
        players[0].setRelations(players[5], players[1], players[3], players[4], players[2]);
        players[1].setRelations(players[0], players[2], players[4], players[5], players[3]);
        players[2].setRelations(players[1], players[3], players[5], players[0], players[4]);
        players[3].setRelations(players[2], players[4], players[0], players[1], players[5]);
        players[4].setRelations(players[3], players[5], players[1], players[2], players[0]);
        players[5].setRelations(players[4], players[0], players[2], players[3], players[1]);
    }

    public void fapai(int[] cards) {
//        for (int i = 0; i < 51; i++) {
//            playerCards[i / 17][i % 17] = cards[i];
//        }
//        for (int i = 51; i < 102; i++) {
//            playerCards[(i - 51) / 17][(i - 51) % 17 + 17] = cards[i];
//        }
//        for (int i = 102; i < 153; i++) {
//            playerCards[(i - 102) / 17][(i - 102) % 17 + 34] = cards[i];
//        }
//        for (int i = 153; i < 204; i++) {
//            playerCards[(i - 153) / 17][(i - 153) % 17 + 51] = cards[i];
//        }
//        for (int i = 204; i < 255; i++) {
//            playerCards[(i - 204) / 17][(i - 204) % 17 + 68] = cards[i];
//        }
//        for (int i = 255; i < 306; i++) {
//            playerCards[(i - 255) / 17][(i - 255) % 17 + 85] = cards[i];
//        }
        for (int i = 0; i < 306; i++) {
            playerCards[i % 6][i / 6] = cards[i];
        }
//        threeCards[0] = cards[159];
//        threeCards[1] = cards[160];
//        threeCards[2] = cards[161];
//        threeCards[0] = cards[150];
//        threeCards[1] = cards[151];
//        threeCards[2] = cards[152];
    }

    private void maisan() {
        gongText = "等待玩家买3";
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 6; i++) {
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
        for (int i = 0; i < 6; i++) {
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
        int c2Index = CardsManager.getCardFirstIndex(playerCards[id], 15);
        if (c2Index == -1)
            c2Index = CardsManager.getCardFirstIndex(playerCards[id], 16);
        if (c2Index == -1)
            c2Index = CardsManager.getCardFirstIndex(playerCards[id], 17);
        int c1 = playerCards[targetId][playerCards[targetId].length - 1];
        if (c2Index == -1) {
            playerCards[id] = ArrayUtils.add(playerCards[id], c1);
            submitJingongAnimation(id, targetId, c1);
        } else {
            int c2 = playerCards[id][c2Index];
            playerCards[targetId][playerCards[targetId].length - 1] = c2;
            playerCards[id][c2Index] = c1;
            submitMaiAnimation(id, targetId, c2, c1);
        }
        CardsManager.sort(playerCards[id]);
        CardsManager.sort(playerCards[targetId]);
//        try {
//            players[id].getDataLock().lock();
//            players[id].cards = playerCards[id];
//        } finally {
//            players[id].getDataLock().unlock();
//        }
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
        for (int i = 0; i < 6; i++) {
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
        for (int i = 0; i < 6; i++) {
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
        int c2Index = CardsManager.getCardFirstIndex(playerCards[id], 15);
        if (c2Index == -1)
            c2Index = CardsManager.getCardFirstIndex(playerCards[id], 16);
        if (c2Index == -1)
            c2Index = CardsManager.getCardFirstIndex(playerCards[id], 17);
        int c1 = playerCards[targetId][index];
        if (c2Index == -1) {
            playerCards[id] = ArrayUtils.add(playerCards[id], c1);
            submitJingongAnimation(id, targetId, c1);
        } else {
            int c2 = playerCards[id][c2Index];
            playerCards[targetId][index] = c2;
            playerCards[id][c2Index] = c1;
            submitMaiAnimation(id, targetId, c2, c1);
        }
        CardsManager.sort(playerCards[id]);
        CardsManager.sort(playerCards[targetId]);
//        try {
//            players[id].getDataLock().lock();
//            players[id].cards = playerCards[id];
//        } finally {
//            players[id].getDataLock().unlock();
//        }
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
        boolean[] dianReports = new boolean[6];
        for (DianReport r1 : dianReportList) {
            dianReports[r1.id] = r1.dian;
        }
        for (int i = 0; i < 6; i++) {
            if (dianReports[i]) {
                if (!dianReports[players[i].getOpposite().playerId]) {
                    jingongSingleFor(players[i].getOpposite().playerId, i, "点贡");
                }
            }
        }
        dianReportList.clear();
        for (int i = 0; i < 6; i++) {
            DianReport report = new DianReport();
            report.id = i;
            dianReportList.add(report);
        }
        gongText = "等待玩家进烧贡";
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (ShaoReport report : shaoReportList) {
            jingongSingleFor(report.beishaoId, report.shaoId, "烧贡");
        }
        shaoReportList.clear();
        gongText = "等待玩家进闷贡";
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (MenReport report : menReportList) {
            jingongSingleFor(report.beimenId, report.menId, "闷贡");
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
        int[] order = new int[6];
        int i = 5;
        for (int id : beimenPlayerIds) {
            order[i] = id;
            i--;
        }
        i = 0;
        for (int id : doneIdList) {
            order[i] = id;
            i++;
        }
        jingongSingleFor(order[5], order[0], "落贡");
        jingongSingleFor(order[5], order[0], "落贡");
        jingongSingleFor(order[4], order[1], "落贡");
        beimenPlayerIds.clear();
        doneIdList.clear();
    }

    private void jingongSingleFor(int srcId, int destId, String gongName) {
        Log.i("BoYaDDZ", "jingong: from " + srcId + " to " + destId);
        int c = playerCards[srcId][0];
        if (CardsManager.getCardNumber(c) < 15) {
            jingongLog.append(playerNicknames[srcId]);
            jingongLog.append("无2无王，不再向");
            jingongLog.append(playerNicknames[destId]);
            jingongLog.append("进");
            jingongLog.append(gongName);
            jingongLog.append("\n");
        }
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
//        int[] diZhuCards = new int[playerCards[boss].length];
//        for (int i = 0; i < playerCards[boss].length - 3; i++) {
//            diZhuCards[i] = playerCards[boss][i];
//        }
//        diZhuCards[playerCards[boss].length - 3] = threeCards[0];
//        diZhuCards[playerCards[boss].length - 2] = threeCards[1];
//        diZhuCards[playerCards[boss].length - 1] = threeCards[2];
//        playerCards[boss] = diZhuCards;
    }

    private void buyao() {
        players[currentId].latestCards = null;
        players[currentId].state = Player.DiscardState.GUOPAI;
        canPass[currentId] = true;
        nextPerson();
//        boolean flag = false;
        if (cardsOnDesktop != null && currentId == cardsOnDesktop.playerId) {
//            switch (cardsOnDesktop.cardsType) {
//                case CardsType.danshun:
//                case CardsType.shuangshun:
//                case CardsType.sanshun:
//                case CardsType.feiji:
//                case CardsType.sidaier:
//                    players[currentId].dianFlag = true;
//            }
//            flag = true;
            endCircle();
        }
        SoundManager.playGuopaiSound();
//        if (flag) {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
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
        if (doneIdList.size() + beimenPlayerIds.size() == 6)
            return;
        Function<Integer, Integer> getNext = (m) -> {
            switch (m) {
                case 0:
                    return 1;
                case 1:
                    return 2;
                case 2:
                    return 3;
                case 3:
                    return 4;
                case 4:
                    return 5;
                case 5:
                    return 0;
                default:
                    throw new IllegalArgumentException("Player ID is invalid");
            }
        };
        Function<Integer, Boolean> isOfRule = (id) -> {
            if (!sihuluanchan && id != players[cardsOnDesktop.playerId].getOpposite().playerId
                    && players[id].state.equals(Player.DiscardState.GUOPAI))
                return false;
            if (!sihuluanchan && cardsOnDesktop != null
                    && GJCardsAnalyzer.judgeGouji(cardsOnDesktop.cards)
                    && !(burning && id == burningId)
                    && !players[cardsOnDesktop.playerId].wutou) {
                if (id != players[cardsOnDesktop.playerId].getOpposite().playerId
                        && !players[id].wutou) {
                    return false;
                }
            }
            return true;
        };
        int id = currentId;
        boolean flag = false;
        do {
            id = getNext.apply(id);
            if (cardsOnDesktop != null && id == cardsOnDesktop.playerId) {
                flag = true;
//            switch (cardsOnDesktop.cardsType) {
//                case CardsType.danshun:
//                case CardsType.shuangshun:
//                case CardsType.sanshun:
//                case CardsType.feiji:
//                case CardsType.sidaier:
//                    players[currentId].dianFlag = true;
//            }
                currentId = id;
                endCircle();
                break;
            }
        } while (!isOfRule.apply(id) || doneIdList.contains(id) || beimenPlayerIds.contains(id));
        if (!flag) {
            currentId = id;
            currentCircle++;
            timeLimite = 300;
        }
    }

    private void endCircle() {
        if (rangpaiId >= 0) {
            currentId = rangpaiId;
            timeLimite = 300;
            rangpaiId = -1;
            return;
        }
        if (!sihuluanchan && !players[currentId].qidian && GJCardsAnalyzer.judgePureGouji(cardsOnDesktop.cards))
            players[currentId].dianFlag = true;
        currentCircle = 0;
        cardsOnDesktop = null;
        players[currentId].latestCards = null;
        for (int i = 0; i < 6; i++) {
            players[i].latestCards = null;
            players[i].state = Player.DiscardState.CHUPAI;
        }
    }

    private void paintGaming(Canvas canvas) {

        players[0].paint(canvas);
        players[1].paint(canvas);
        players[2].paint(canvas);
        players[3].paint(canvas);
        players[4].paint(canvas);
        players[5].paint(canvas);
//        paintThreeCards(canvas);
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

                if (cardsOnDesktop != null
                        && players[0].getOpposite().playerId == cardsOnDesktop.playerId
                        && analyzeRangpai(0)) {
                    src.set(0, 0, rangpaiImage.getWidth(), rangpaiImage.getHeight());
                    dst.set((int) ((buttonPosition_X - 160) * MainActivity.SCALE_HORIAONTAL),
                            (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
                            (int) ((buttonPosition_X - 80) * MainActivity.SCALE_HORIAONTAL),
                            (int) ((buttonPosition_Y + 40) * MainActivity.SCALE_VERTICAL));
                    canvas.drawBitmap(rangpaiImage, src, dst, null);
                }

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
        } else if (shaopaiDecisionWaiting) {
            Rect src = new Rect();
            Rect dst = new Rect();

            src.set(0, 0, shaopaiImage.getWidth(), shaopaiImage.getHeight());
            dst.set((int) ((buttonPosition_X - 160) * MainActivity.SCALE_HORIAONTAL),
                    (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
                    (int) ((buttonPosition_X - 80) * MainActivity.SCALE_HORIAONTAL),
                    (int) ((buttonPosition_Y + 40) * MainActivity.SCALE_VERTICAL));
            canvas.drawBitmap(shaopaiImage, src, dst, null);

            src.set(0, 0, bushaoImage.getWidth(), bushaoImage.getHeight());
            dst.set((int) ((buttonPosition_X - 80) * MainActivity.SCALE_HORIAONTAL),
                    (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
                    (int) (buttonPosition_X * MainActivity.SCALE_HORIAONTAL),
                    (int) ((buttonPosition_Y + 40) * MainActivity.SCALE_VERTICAL));
            canvas.drawBitmap(bushaoImage, src, dst, null);
        }

        for (int i = 0; i < 6; i++) {
            if ((currentId != i || shaopaiDecisionWaiting) && players[i].latestCards != null && canDrawLatestCards) {
                players[i].latestCards.paint(canvas, playerLatestCardsPosition[i][0],
                        playerLatestCardsPosition[i][1], CardsType.direction_Horizontal);
            }
//            if (currentId != i && players[i].latestCards == null && canPass[i]) {
            if (currentId != i && players[i].state.equals(Player.DiscardState.GUOPAI) && canPass[i]) {
                paintPass(canvas, i);
            }
            if (currentId != i && players[i].state.equals(Player.DiscardState.RANGPAI) && canPass[i]) {
                paintRandpai(canvas, i);
            }
        }

        if (biesanMode) {
            int m = 5;
        	for (int i : beimenPlayerIds) {
//        		paintBeimen(canvas, i);
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

    private boolean analyzeRangpai(int playerId) {
        if (sihuluanchan)
            return false;
        if (GJCardsAnalyzer.judgeGouji(cardsOnDesktop.cards)) {
            if ((players[playerId].getNext().dian || players[playerId].getNext().qidian)
                    && !players[playerId].getNext().state.equals(Player.DiscardState.GUOPAI))
                return true;
            return (players[playerId].getNextMate().dian || players[playerId].getNextMate().qidian)
                    && !players[playerId].getNextMate().state.equals(Player.DiscardState.GUOPAI);
        } else {
            return !(players[playerId].getNext().state.equals(Player.DiscardState.GUOPAI)
                    && players[playerId].getNextMate().state.equals(Player.DiscardState.GUOPAI));
        }
    }

    private void paintGongText(Canvas canvas) {
        Paint paint = new Paint();
        paint.setTextSize((int) (16 * MainActivity.SCALE_VERTICAL));
        paint.setStyle(Style.FILL);
        paint.setColor(Color.RED);
        StringBuilder b = new StringBuilder();
        b.append(gongText);
        b.append("\n");
        b.append(jingongLog);
        canvas.drawText(b.toString(),
                (int) (150 * MainActivity.SCALE_HORIAONTAL),
                (int) (100 * MainActivity.SCALE_VERTICAL), paint);
    }

    private void paintTimeLimite(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setTextSize((int) (16 * MainActivity.SCALE_HORIAONTAL));
        for (int i = 0; i < 6; i++) {
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

    private void paintRandpai(Canvas canvas, int id) {
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setTextSize((int) (16 * MainActivity.SCALE_HORIAONTAL));
        canvas.drawText("让牌", (int) (passPosition[id][0] * MainActivity.SCALE_HORIAONTAL),
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
            case 2:
                str = "三科";
                break;
            case 3:
                str = "四科";
                break;
            case 4:
                str = "二落";
                break;
            default:
                str = "大落";
        }
        Paint paint = new Paint();
        if (doneOrder >= 4) {
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setColor(Color.GRAY);
        } else if (doneOrder <= 1) {
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setColor(Color.RED);
        } else {
            paint.setColor(Color.RED);
        }
        paint.setTextSize((int) (16 * MainActivity.SCALE_HORIAONTAL));
        canvas.drawText(str, (int) (passPosition[id][0] * MainActivity.SCALE_HORIAONTAL),
                (int) (passPosition[id][1] * MainActivity.SCALE_VERTICAL), paint);
    }

    private void paintIconAndScore(Canvas canvas) {

        Paint paint = new Paint();
        paint.setTextSize((int) (16 * MainActivity.SCALE_VERTICAL));
        Rect src = new Rect();
        Rect dst = new Rect();
        for (int i = 0; i < 6; i++) {
//            if (boss == i) {
//                paint.setStyle(Style.STROKE);
//                paint.setColor(Color.BLACK);
//                paint.setStrokeWidth(1);
//                paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//                src.set(0, 0, playerAvatars[i].getWidth(), playerAvatars[i].getHeight());
//                dst.set((int) (iconPosition[i][0] * MainActivity.SCALE_HORIAONTAL),
//                        (int) (iconPosition[i][1] * MainActivity.SCALE_VERTICAL),
//                        (int) ((iconPosition[i][0] + 40) * MainActivity.SCALE_HORIAONTAL),
//                        (int) ((iconPosition[i][1] + 40) * MainActivity.SCALE_VERTICAL));
//                RectF rectF = new RectF(dst);
//                canvas.drawRoundRect(rectF, 5, 5, paint);
//                canvas.drawBitmap(playerAvatars[i], src, dst, paint);
//
//                paint.setStyle(Style.FILL);
//                paint.setColor(Color.WHITE);
//                canvas.drawText("玩家" + i,
//                        (int) (scorePosition[i][0] * MainActivity.SCALE_HORIAONTAL),
//                        (int) (scorePosition[i][1] * MainActivity.SCALE_VERTICAL), paint);
//                canvas.drawText("积分" + scores[i],
//                        (int) (scorePosition[i][0] * MainActivity.SCALE_HORIAONTAL),
//                        (int) ((scorePosition[i][1] + 20) * MainActivity.SCALE_VERTICAL), paint);
//            } else {
//                paint.setStyle(Style.STROKE);
//                paint.setColor(Color.BLACK);
//                paint.setStrokeWidth(1);
//                paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//                src.set(0, 0, playerAvatars[i].getWidth(), playerAvatars[i].getHeight());
//                dst.set((int) (iconPosition[i][0] * MainActivity.SCALE_HORIAONTAL),
//                        (int) (iconPosition[i][1] * MainActivity.SCALE_VERTICAL),
//                        (int) ((iconPosition[i][0] + 40) * MainActivity.SCALE_HORIAONTAL),
//                        (int) ((iconPosition[i][1] + 40) * MainActivity.SCALE_VERTICAL));
//                RectF rectF = new RectF(dst);
//                canvas.drawRoundRect(rectF, 5, 5, paint);
//                canvas.drawBitmap(playerAvatars[i], src, rectF, paint);
//
//                paint.setStyle(Style.FILL);
//                paint.setColor(Color.WHITE);
//                canvas.drawText("玩家" + i,
//                        (int) (scorePosition[i][0] * MainActivity.SCALE_HORIAONTAL),
//                        (int) (scorePosition[i][1] * MainActivity.SCALE_VERTICAL), paint);
//                canvas.drawText("积分" + scores[i],
//                        (int) (scorePosition[i][0] * MainActivity.SCALE_HORIAONTAL),
//                        (int) ((scorePosition[i][1] + 20) * MainActivity.SCALE_VERTICAL), paint);
//            }
            paint.setStyle(Style.STROKE);
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(1);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            src.set(0, 0, playerAvatars[i].getWidth(), playerAvatars[i].getHeight());
            dst.set((int) (iconPosition[i][0] * MainActivity.SCALE_HORIAONTAL),
                    (int) (iconPosition[i][1] * MainActivity.SCALE_VERTICAL),
                    (int) ((iconPosition[i][0] + 40) * MainActivity.SCALE_HORIAONTAL),
                    (int) ((iconPosition[i][1] + 40) * MainActivity.SCALE_VERTICAL));
            RectF rectF = new RectF(dst);
            canvas.drawRoundRect(rectF, 5, 5, paint);
            canvas.drawBitmap(playerAvatars[i], src, dst, paint);

            paint.setStyle(Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawText(playerNicknames[i],
                    (int) (scorePosition[i][0] * MainActivity.SCALE_HORIAONTAL),
                    (int) (scorePosition[i][1] * MainActivity.SCALE_VERTICAL), paint);
            canvas.drawText("积分" + scores[i],
                    (int) (scorePosition[i][0] * MainActivity.SCALE_HORIAONTAL),
                    (int) ((scorePosition[i][1] + 20) * MainActivity.SCALE_VERTICAL), paint);

            paintPlayerStatus(canvas, i);
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

    private void paintPlayerStatus(Canvas canvas, int id) {
        LinkedList<String> strList = new LinkedList<>();
        LinkedList<String> colorList = new LinkedList<>();
        if (players[id].dian) {
            strList.add("点");
            colorList.add("RED");
        }
        if (players[id].qidian) {
            strList.add("点");
            colorList.add("GRAY");
        }
        if (players[id].shao) {
            strList.add("烧");
            colorList.add("RED");
        }
        if (players[id].beishao) {
            strList.add("烧");
            colorList.add("GRAY");
        }
        if (players[id].men) {
            strList.add("闷");
            colorList.add("RED");
        }
        if (players[id].beimen) {
            strList.add("闷");
            colorList.add("GRAY");
        }
        Paint paint = new Paint();
        paint.setTextSize((int) (16 * MainActivity.SCALE_VERTICAL));
        paint.setStyle(Style.FILL);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        int xOff = 0;
        while (!strList.isEmpty()) {
            try {
                String str = strList.poll();
                String color = colorList.poll();
                Field f = Color.class.getField(color);
                int colornum = (Integer) f.get(null);
                paint.setColor(colornum);
                canvas.drawText(str,
                        (int) (iconPosition[id][0] * MainActivity.SCALE_HORIAONTAL) + xOff,
                        (int) (iconPosition[id][1] * MainActivity.SCALE_VERTICAL) + paint.getTextSize(),
                        paint);
                xOff += paint.getTextSize();
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (players[id].cards.length < 10) {
            paint.setColor(Color.BLUE);
            canvas.drawText("少于十张",
                    (int) (iconPosition[id][0] * MainActivity.SCALE_HORIAONTAL),
                    (int) (iconPosition[id][1] * MainActivity.SCALE_VERTICAL) + paint.getTextSize() * 2,
                    paint);
        }
        if (burning && id == burningId) {
            paint.setColor(Color.RED);
            canvas.drawText("烧牌",
                    (int) (iconPosition[id][0] * MainActivity.SCALE_HORIAONTAL),
                    (int) (iconPosition[id][1] * MainActivity.SCALE_VERTICAL) + paint.getTextSize() * 3,
                    paint);
        }
        if (burning && id == burnedId) {
            paint.setColor(Color.GRAY);
            canvas.drawText("被烧",
                    (int) (iconPosition[id][0] * MainActivity.SCALE_HORIAONTAL),
                    (int) (iconPosition[id][1] * MainActivity.SCALE_VERTICAL) + paint.getTextSize() * 3,
                    paint);
        }
    }

    private void paintResult(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize((int) (20 * MainActivity.SCALE_HORIAONTAL));
        for (int i = 0; i < 7; i++) {
            if (i < 6)
                canvas.drawText("玩家" + i + "得分" + result[i] + "   当前积分" + scores[i],
                        (int) (110 * MainActivity.SCALE_HORIAONTAL),
                        (int) ((96 + i * 30) * MainActivity.SCALE_VERTICAL), paint);
            else
                canvas.drawText("点击屏幕开始下一局",
                        (int) (110 * MainActivity.SCALE_HORIAONTAL),
                        (int) ((96 + i * 30) * MainActivity.SCALE_VERTICAL), paint);
        }
        for (int i = 0; i < 6; i++) {
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
        sihuluanchan = false;
    }

    public void onTuch(int x, int y) {
        if (biesanMode && !gongCompleted)
            return;

        if (op == 1) {
            shouldPaintButtons = false;
            gongCompleted = false;
            sihuluanchan = false;
            jingongLog = new StringBuffer();
            op = -1;
        }
        players[0].onTuch(x, y);
        if (currentId == 0 && shouldPaintButtons) {

            if (CardsManager.inRect(x, y, (int) (buttonPosition_X * MainActivity.SCALE_HORIAONTAL),
                    (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
                    (int) (80 * MainActivity.SCALE_HORIAONTAL),
                    (int) (40 * MainActivity.SCALE_VERTICAL))) {
                System.out.println("����");
                ifClickChupai = true;

            }
            if (cardsOnDesktop != null
                    && players[0].getOpposite().playerId == cardsOnDesktop.playerId
                    && analyzeRangpai(0)) {
                if (CardsManager.inRect(x, y,
                        (int) ((buttonPosition_X - 160) * MainActivity.SCALE_HORIAONTAL),
                        (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
                        (int) (80 * MainActivity.SCALE_HORIAONTAL),
                        (int) (40 * MainActivity.SCALE_VERTICAL))) {
                    requestRangpai();
                }
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
        } else if (shaopaiDecisionWaiting) {
            if (CardsManager.inRect(x, y,
                    (int) ((buttonPosition_X - 160) * MainActivity.SCALE_HORIAONTAL),
                    (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
                    (int) (80 * MainActivity.SCALE_HORIAONTAL),
                    (int) (40 * MainActivity.SCALE_VERTICAL))) {
                shaopaiDecisionResult = true;
                shaopaiDecisionWaiting = false;
//                    shouldPaintButtons = false;
                LockSupport.unpark(shaopaiDecisionParkingThread);
            }
            if (CardsManager.inRect(x, y,
                    (int) ((buttonPosition_X - 80) * MainActivity.SCALE_HORIAONTAL),
                    (int) (buttonPosition_Y * MainActivity.SCALE_VERTICAL),
                    (int) (80 * MainActivity.SCALE_HORIAONTAL),
                    (int) (40 * MainActivity.SCALE_VERTICAL))) {
                shaopaiDecisionResult = false;
                shaopaiDecisionWaiting = false;
//                    shouldPaintButtons = false;
                LockSupport.unpark(shaopaiDecisionParkingThread);
            }
        }
    }
}

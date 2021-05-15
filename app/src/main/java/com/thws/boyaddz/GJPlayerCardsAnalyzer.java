package com.thws.boyaddz;

import com.blankj.utilcode.util.ArrayUtils;

public class GJPlayerCardsAnalyzer {
    private int[] cards;
    private int dCount = 0;
    private int xCount = 0;
    private int moneyCount = 0;

    public static GJPlayerCardsAnalyzer obtain() {
        return new GJPlayerCardsAnalyzer();
    }

    private GJPlayerCardsAnalyzer() {
    }

    public void setPokes(int[] pokes) {
        CardsManager.sort(pokes);
        cards = pokes;
        analyze();
    }

    private void analyze() {
        for (int c : cards) {
            int cn = CardsManager.getCardNumber(c);
            switch (cn) {
                case 17:
                    dCount++;
                    break;
                case 16:
                    xCount++;
                    break;
                case 15:
                    moneyCount++;
                    break;
            }
        }
    }

    public int[] getMinCards() {
        int curType = 0;
        int[] curCards = new int[0];
        for (int c : cards) {
            int cn = CardsManager.getCardNumber(c);
            if (cn == 4 || cn == 3)
                continue;
            if (curType != cn) {
                curType = cn;
                curCards = new int[0];
            }
            curCards = ArrayUtils.add(curCards, c);
        }
        return curCards;
    }

    public int getCountOfType(int type) {
        int n = 0;
        for (int c : cards) {
            if (CardsManager.getCardNumber(c) == type)
                n++;
        }
        return n;
    }

    public int getDCount() {
        return dCount;
    }

    public int getXCount() {
        return xCount;
    }

    public int getMoneyCount() {
        return moneyCount;
    }
}

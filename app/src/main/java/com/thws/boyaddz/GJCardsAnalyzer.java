package com.thws.boyaddz;

import com.blankj.utilcode.util.ArrayUtils;

public class GJCardsAnalyzer {
    private int[] cards;
    private int dCount = 0;
    private int xCount = 0;
    private int baseType = 0;
    private int baseCount = 0;
    private int moneyCount = 0;
    private int totalCount = 0;

    public static GJCardsAnalyzer obtain() {
        return new GJCardsAnalyzer();
    }

    public static boolean judgeGouji(int[] cards) {
        GJCardsAnalyzer ana = obtain();
        ana.setPokes(cards);
        return ana.isGouji();
    }

    public static boolean judgePureGouji(int[] cards) {
        GJCardsAnalyzer ana = obtain();
        ana.setPokes(cards);
        return ana.isPureGouji();
    }

    private GJCardsAnalyzer() {
    }

    public void setPokes(int[] pokes) {
        CardsManager.sort(pokes);
        cards = pokes;
        analyze();
    }

    private void analyze() {
        if (cards.length == 0)
            throw new IllegalGJCardsException();
        int card2Count = 0;
        boolean all2 = true;
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
                    card2Count++;
                    break;
                default:
                    if (all2)
                        all2 = false;
                    if (baseType == 0) {
                        baseType = cn;
                        baseCount++;
                    } else if (baseType != cn) {
                        throw new IllegalGJCardsException();
                    } else {
                        baseCount++;
                    }
            }
        }
        if (all2) {
            baseType = 15;
            baseCount = card2Count;
            moneyCount = 0;
        } else {
            moneyCount = card2Count;
        }
        totalCount = moneyCount + baseCount + xCount + dCount;
    }

    public int getDCount() {
        return dCount;
    }

    public int getXCount() {
        return xCount;
    }

    public int getBaseType() {
        return baseType;
    }

    public int getBaseCount() {
        return baseCount;
    }

    public int getMoneyCount() {
        return moneyCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int[] getLargerCards(int[] pCards) {
        //TODO
        if (dCount > 0 || xCount > 0 || baseType == 15)
            return null;
        int targetType = 0;
        for (int c : pCards) {
            int cn = CardsManager.getCardNumber(c);
            if (cn == 17 || cn == 16)
                continue;
            if (cn == 4)
                continue;
            if (cn <= baseType)
                break;
            targetType = cn;
        }
        if (targetType == 0) {
            return null;
        } else {
            GJPlayerCardsAnalyzer pAna = GJPlayerCardsAnalyzer.obtain();
            pAna.setPokes(pCards);
            int baseAmount = pAna.getCountOfType(targetType);
            int xAmount = pAna.getCountOfType(16);
            int dAmount = pAna.getCountOfType(17);
            int moneyAmount = pAna.getCountOfType(15);
            if (baseAmount >= moneyCount + baseCount) {
                baseAmount = moneyCount + baseCount;
                moneyAmount = xAmount = dAmount = 0;
            } else {
                if (targetType == 15) {
                    moneyAmount = 0;
                    if (xAmount >= moneyCount + baseCount - (moneyAmount + baseAmount)) {
                        xAmount = totalCount - (moneyAmount + baseAmount);
                        dAmount = 0;
                    } else {
                        if (dAmount >= xCount + moneyCount + baseCount - (xAmount + moneyAmount + baseAmount)) {
                            dAmount = xCount + moneyCount + baseCount - (xAmount + moneyAmount + baseAmount);
                        } else {
                            return null;
                        }
                    }
                } else {
                    if (moneyAmount >= moneyCount + baseCount - baseAmount) {
                        moneyAmount = moneyCount + baseCount - baseAmount;
                        xAmount = dAmount = 0;
                    } else {
                        if (xAmount >= moneyCount + baseCount - (moneyAmount + baseAmount)) {
                            xAmount = totalCount - (moneyAmount + baseAmount);
                            dAmount = 0;
                        } else {
                            if (dAmount >= xCount + moneyCount + baseCount - (xAmount + moneyAmount + baseAmount)) {
                                dAmount = xCount + moneyCount + baseCount - (xAmount + moneyAmount + baseAmount);
                            } else {
                                return null;
                            }
                        }
                    }
                }
            }
            int[] rCards = new int[0];
            int curD = 0, curX = 0, curM = 0, curB = 0;
            for (int c : pCards) {
                int cn = CardsManager.getCardNumber(c);
                switch (cn) {
                    case 17:
                        if (curD < dAmount) {
                            rCards = ArrayUtils.add(rCards, c);
                            curD++;
                        }
                        break;
                    case 16:
                        if (curX < xAmount) {
                            rCards = ArrayUtils.add(rCards, c);
                            curX++;
                        }
                        break;
                    case 15:
                        if (targetType == 15 && curB < baseAmount) {
                            rCards = ArrayUtils.add(rCards, c);
                            curB++;
                        } else if (curM < moneyAmount) {
                            rCards = ArrayUtils.add(rCards, c);
                            curM++;
                        }
                        break;
                    default:
                        if (cn == targetType && curB < baseAmount) {
                            rCards = ArrayUtils.add(rCards, c);
                            curB++;
                        }
                }
            }
            if (rCards.length == 0)
                return null;
            return rCards;
        }
    }

    public boolean isGouji() {
        if (dCount > 0 || xCount > 0)
            return true;
        if (baseType == 15)
            return true;
        if (baseType == 14 && totalCount >= 2)
            return true;
        if (baseType == 13 && totalCount >= 2)
            return true;
        if (baseType == 12 && totalCount >= 3)
            return true;
        if (baseType == 11 && totalCount >= 4)
            return true;
        return baseType == 10 && totalCount >= 5;
    }

    public boolean isPureGouji() {
        if (dCount > 0 || xCount > 0 || moneyCount > 0)
            return false;
        if (baseType == 15)
            return false;
        if (baseType == 14 && baseCount >= 2)
            return true;
        if (baseType == 13 && baseCount >= 2)
            return true;
        if (baseType == 12 && baseCount >= 3)
            return true;
        if (baseType == 11 && baseCount >= 4)
            return true;
        return baseType == 10 && baseCount >= 5;
    }
}

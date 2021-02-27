package com.thws.boyaddz;

import java.util.Random;
import java.util.Vector;

public class CardsManager {

    public static Random rand = new Random();

    public static boolean inRect(int x, int y, int rectX, int rectY, int rectW, int rectH) {
        if (x <= rectX || x >= rectX + rectW || y <= rectY || y >= rectY + rectH) {
            return false;
        }
        return true;
    }

    public static void shuffle(int[] cards) {
        int len = cards.length;
        for (int l = 0; l < len; l++) {
            int des = rand.nextInt(54);
            int temp = cards[l];
            cards[l] = cards[des];
            cards[des] = temp;
        }
    }

    public static int getBoss() {
        return rand.nextInt(3);
    }

    public static void sort(int[] cards) {
        for (int i = 0; i < cards.length; i++) {
            for (int j = i + 1; j < cards.length; j++) {
                if (cards[i] < cards[j]) {
                    int temp = cards[i];
                    cards[i] = cards[j];
                    cards[j] = temp;
                }
            }
        }
    }

    public static int getImageRow(int poke) {
        return poke / 4;
    }

    public static int getImageCol(int poke) {
        return poke % 4;
    }

    public static int getCardNumber(int card) {
        if (card == 52) {
            return 16;
        }
        if (card == 53) {
            return 17;
        }
        return getImageRow(card) + 3;
    }

    public static String getCardString(int cardNumber) {
    	switch (cardNumber) {
			case 3:
				return "3";
			case 4:
				return "4";
			case 5:
				return "5";
			case 6:
				return "6";
			case 7:
				return "7";
			case 8:
				return "8";
			case 9:
				return "9";
			case 10:
				return "10";
			case 11:
				return "J";
			case 12:
				return "Q";
			case 13:
				return "K";
			case 14:
				return "A";
			case 15:
				return "2";
			case 16:
				return "小王";
			case 17:
				return "大王";
			default:
				return null;
		}
	}

    public static boolean isDanShun(int[] cards) {
        int start = getCardNumber(cards[0]);
        if (start > 14) {
            return false;
        }
        int next;
        for (int i = 1; i < cards.length; i++) {
            next = getCardNumber(cards[i]);
            if (start - next != 1) {
                return false;
            }
            start = next;
        }
        return true;
    }

    public static boolean isShuangShun(int[] cards) {
        int start = getCardNumber(cards[0]);
        if (start > 14) {
            return false;
        }
        if (cards.length % 2 != 0) {
            return false;
        }
        int next;
        for (int i = 2; i < cards.length; i += 2) {
            next = getCardNumber(cards[i]);
            if (start != getCardNumber(cards[i - 1])) {
                return false;
            }
            if (next != getCardNumber(cards[i + 1])) {
                return false;
            }
            if (start - next != 1) {
                return false;
            }
            start = next;
        }
        return true;
    }

    public static boolean isSanShun(int[] cards) {
        int start = getCardNumber(cards[0]);
        if (start > 14) {
            return false;
        }
        if (cards.length % 3 != 0) {
            return false;
        }
        int next;
        for (int i = 3; i < cards.length; i += 3) {
            next = getCardNumber(cards[i]);
            if (start != getCardNumber(cards[i - 1])) {
                return false;
            }
            if (next != getCardNumber(cards[i + 2])) {
                return false;
            }
            if (start - next != 1) {
                return false;
            }
            start = next;
        }
        return true;
    }

    public static boolean isFeiJi(int[] cards) {
        if (cards.length == 8 || cards.length == 10 || cards.length == 12 || cards.length == 15
                || cards.length == 16 || cards.length == 20) {
            int sanshun = 1;
            int key = 0;
            boolean ifFound = false;
            for (int i = 0; i <= cards.length - 6; ) {
                if (getCardNumber(cards[i]) == getCardNumber(cards[i + 3])) {
                    return false;
                } else if (getCardNumber(cards[i]) == getCardNumber(cards[i + 2])
                        && getCardNumber(cards[i + 3]) == getCardNumber(cards[i + 5])
                        && getCardNumber(cards[i]) - getCardNumber(cards[i + 3]) == 1) {
                    if (ifFound == false) {
                        key = i;
                    }
                    ifFound = true;
                    sanshun++;
                    i += 3;
                } else {
                    if (ifFound == false) {
                        i++;
                    } else {
                        break;
                    }
                }
            }

            if (ifFound == true) {
                int[] otherCards = new int[cards.length - sanshun * 3];
                for (int i = 0; i < key; i++) {
                    otherCards[i] = cards[i];
                }
                for (int i = key + sanshun * 3; i < cards.length; i++) {
                    otherCards[i - sanshun * 3] = cards[i];
                }
                if (isDissimilar(otherCards)) {
                    if (sanshun == otherCards.length) {
                        return true;
                    }
                }
                if (isTwined(otherCards)) {
                    if (sanshun == otherCards.length / 2) {
                        return true;
                    }
                }
            }

        }
        return false;
    }

    public static boolean isDissimilar(int[] cards) {
        for (int i = 0; i < cards.length - 1; i++) {
            for (int j = i + 1; j < cards.length; j++) {
                if (getCardNumber(cards[i]) == getCardNumber(cards[j])) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isTwined(int[] cards) {
        for (int i = 0; i <= cards.length - 2; i += 2) {
            if (getCardNumber(cards[i]) != getCardNumber(cards[i + 1])) {
                return false;
            }
            if (i <= cards.length - 4) {
                if (getCardNumber(cards[i]) == getCardNumber(cards[i + 2])) {
                    return false;
                }

            }
        }
        return true;
    }

    public static int getType(int[] cards) {
        int len = cards.length;

        boolean has4 = false, all4 = true;
        for (int card : cards) {
            if (getCardNumber(card) == 4)
                has4 = true;
            all4 &= getCardNumber(card) == 4;
        }
        if (has4 && !all4) {
            return CardsType.error;
        }

        if (len == 1) {
            return CardsType.danpai;
        }

        if (len == 2) {
            if (cards[0] == 53 && cards[1] == 52) {
                return CardsType.huojian;
            }
            if (getCardNumber(cards[0]) == getCardNumber(cards[1])) {
                return CardsType.duipai;
            }
        }

        if (len == 3) {
            if (getCardNumber(cards[0]) == getCardNumber(cards[2])) {
                return CardsType.sanzhang;
            }
        }

        if (len == 4) {
            if (getCardNumber(cards[0]) == getCardNumber(cards[2])
                    || getCardNumber(cards[1]) == getCardNumber(cards[3])) {
                if (getCardNumber(cards[0]) == getCardNumber(cards[3])) {
                    return CardsType.zhadan;
                } else {
                    return CardsType.sandaiyi;
                }
            }
        }

        if (len >= 5) {
            if (isDanShun(cards)) {
                return CardsType.danshun;
            }
        }

        if (len == 5) {
            if (getCardNumber(cards[0]) == getCardNumber(cards[2])
                    && getCardNumber(cards[3]) == getCardNumber(cards[4])) {
                return CardsType.sandaiyi;
            }
            if (getCardNumber(cards[0]) == getCardNumber(cards[1])
                    && getCardNumber(cards[2]) == getCardNumber(cards[4])) {
                return CardsType.sandaiyi;
            }
        }

        if (len >= 6) {
            if (isShuangShun(cards)) {
                return CardsType.shuangshun;
            }
            if (isSanShun(cards)) {
                return CardsType.sanshun;
            }

        }

        if (len == 6) {
            if (getCardNumber(cards[0]) == getCardNumber(cards[3])
                    || getCardNumber(cards[1]) == getCardNumber(cards[4])
                    || getCardNumber(cards[2]) == getCardNumber(cards[5])) {
                return CardsType.sidaier;
            }
        }


        if (len >= 8) {
            if (isFeiJi(cards)) {
                return CardsType.feiji;
            }
        }

        if (len == 8) {
            int key = 0;
            boolean ifFound = false;
            for (int i = 0; i <= cards.length - 4; i++) {
                if (getCardNumber(cards[i]) == getCardNumber(cards[i + 3])) {
                    ifFound = true;
                    key = i;
                    break;
                }
            }
            if (ifFound == true) {
                int[] otherCards = new int[4];
                for (int i = 0; i < key; i++) {
                    otherCards[i] = cards[i];
                }
                for (int i = key + 4; i < cards.length; i++) {
                    otherCards[i - 4] = cards[i];
                }
                if (isTwined(otherCards)) {
                    return CardsType.sidaier;

                }
            }

        }
        return CardsType.error;
    }

    public static int getValue(int[] cards) {
        int type;
        type = getType(cards);
        if (type == CardsType.danpai || type == CardsType.duipai || type == CardsType.sanzhang
                || type == CardsType.danshun || type == CardsType.shuangshun
                || type == CardsType.sanshun || type == CardsType.zhadan) {
            return getCardNumber(cards[0]);
        }
        if (type == CardsType.sandaiyi || type == CardsType.feiji) {
            for (int i = 0; i <= cards.length - 3; i++) {
                if (getCardNumber(cards[i]) == getCardNumber(cards[i + 2])) {
                    return getCardNumber(cards[i]);
                }
            }
        }
        if (type == CardsType.sidaier) {
            for (int i = 0; i <= cards.length - 4; i++) {
                if (getCardNumber(cards[i]) == getCardNumber(cards[i + 3])) {
                    return getCardNumber(cards[i]);
                }
            }
        }
        return 0;
    }

    /**
     * �ǲ���һ����Ч������
     *
     * @param cards
     * @return
     */
    public static boolean isCard(int[] cards) {
        if (getType(cards) == CardsType.error)
            return false;
        return true;
    }

    /**
     * ����true ǰ���ƴ�
     *
     * @param f
     * @param s
     * @return
     */
    public static int compare(CardsHolder f, CardsHolder s) {
        if (f.cardsType == s.cardsType) {
            if (f.cards.length != s.cards.length)
                return -1;
            else {
                if (f.value > s.value) {
                    return 1;
                } else {
                    return 0;
                }
            }

        }
        if (f.cardsType == CardsType.huojian) {
            return 1;
        }
        if (s.cardsType == CardsType.huojian) {
            return 0;
        }
        if (f.cardsType == CardsType.zhadan) {
            return 1;
        }
        if (s.cardsType == CardsType.zhadan) {
            return 0;
        }
        return -1;
    }

    public static int[] outCardByItsself(int cards[], Player last, Player next) {
        CardsAnalyzer analyze = CardsAnalyzer.getInstance();
        analyze.setPokes(cards);
        int cardArray[] = null;
        Vector<int[]> card_danpai = analyze.getCard_danpai();
        Vector<int[]> card_sanshun = analyze.getCard_sanshun();

        int danpai = card_danpai.size();
        int sanshun = card_sanshun.size();

        int[] miniType = analyze.getMinType(last, next);
        System.out.println("miniType:" + miniType[0] + "," + miniType[1]);
        switch (miniType[0]) {
            case CardsType.sanshun:
                System.out.println("sanshun is over");
                if (sanshun > 0) {
                    cardArray = card_sanshun.elementAt(miniType[1]);

                    if (cardArray.length / 3 < danpai) {
                        int[] desArray = new int[cardArray.length / 3 * 4];
                        for (int i = 0; i < cardArray.length; i++) {
                            desArray[i] = cardArray[i];
                        }
                        for (int j = 0; j < cardArray.length / 3; j++) {
                            desArray[cardArray.length + j] = card_danpai.elementAt(j)[0];
                        }
                        CardsManager.sort(desArray);
                        return desArray;
                    } else {
                        return cardArray;
                    }
                }
                break;
            case CardsType.shuangshun:
                System.out.println("shuangshun is over");
                Vector<int[]> card_shuangshun = analyze.getCard_shuangshun();
                System.out.println("shuangshun:" + card_shuangshun.size());
                if (card_shuangshun.size() > 0) {
                    cardArray = card_shuangshun.elementAt(miniType[1]);
                    return cardArray;
                }
                break;
            case CardsType.danshun:
                System.out.println("danshun is over");
                Vector<int[]> card_danshun = analyze.getCard_danshun();
                if (card_danshun.size() > 0) {
                    return card_danshun.elementAt(miniType[1]);
                }
                break;
            case CardsType.sanzhang:
                System.out.println("sanzhang is over");
                Vector<int[]> card_sanzhang = analyze.getCard_sanzhang();
                if (card_sanzhang.size() > 0) {
                    int[] sanzhangArray = card_sanzhang.elementAt(miniType[1]);
                    if (danpai > 0) {
                        int newA[] = new int[]{sanzhangArray[0], sanzhangArray[1],
                                sanzhangArray[2], card_danpai.elementAt(0)[0]};
                        CardsManager.sort(newA);
                        return newA;
                    } else {
                        return sanzhangArray;
                    }
                }
                break;
            case CardsType.duipai:
                System.out.println("duipai is over");
                Vector<int[]> card_duipai = analyze.getCard_duipai();
                if (card_duipai.size() > 0) {
                    return card_duipai.elementAt(miniType[1]);
                }
                break;
            case CardsType.danpai:
                System.out.println("danpai is over");
                if (danpai > 0) {
                    return card_danpai.elementAt(miniType[1]);
                }
                break;
        }

        Vector<int[]> card_zhadan = analyze.getCard_zhadan();
        if (card_zhadan.size() > 0) {
            return card_zhadan.elementAt(0);
        }
        return new int[]{cards[0]};
    }

    public static int[] findTheRightCard(CardsHolder card, int cards[], Player last, Player next) {
        CardsAnalyzer cardsAnalyzer = CardsAnalyzer.getInstance();
        cardsAnalyzer.setPokes(cards);
        int c = cardsAnalyzer.remainCount();
        if (c == 1) {
            return findBigerCards(card, cards, 100);
        }

        if (Desk.boss != last.playerId && Desk.boss != next.playerId) {
            int pokeLength = Desk.players[card.playerId].cards.length;
            int must = pokeLength * 100 / 17;
            if (pokeLength <= 2) {
                must = 100;
            }
            return findBigerCards(card, cards, must);

        }

        if (Desk.boss == last.playerId) {
            if (card.playerId == last.playerId) {
                int pokeLength = Desk.players[card.playerId].cards.length;
                int must = pokeLength * 100 / 17;
                if (pokeLength <= 2) {
                    must = 100;
                }
                return findBigerCards(card, cards, must);
            } else if (card.playerId == next.playerId) {
                if (c <= 3) {
                    return findBigerCards(card, cards, 100);
                }
                return null;
            }
        }

        if (Desk.boss == next.playerId) {
            if (card.playerId == last.playerId) {
                if (card.value < 12) {
                    int pokeLength = Desk.players[card.playerId].cards.length;
                    int must = 100 - pokeLength * 100 / 17;
                    if (pokeLength <= 4) {
                        must = 0;
                    }
                    CardsAnalyzer ana = CardsAnalyzer.getInstance();
                    ana.setPokes(next.cards);
                    if (ana.remainCount() <= 1) {
                        if (ana.lastCardTypeEq(card.cardsType)
                                && (Desk.boss == next.playerId || (Desk.boss != next.playerId && Desk.boss != last.playerId))) {
                            return findBigerCards(card, cards, 100);
                        }
                    } else {
                        return findBigerCards(card, cards, must);
                    }

                } else {
                    return null;
                }
            } else if (card.playerId == next.playerId) {
                int pokeLength = Desk.players[card.playerId].cards.length;
                int must = pokeLength * 100 / 17;
                if (pokeLength <= 2) {
                    must = 100;
                }
                return findBigerCards(card, cards, must);
            }
        }
        return null;
    }

    public static int[] findBigerCards(CardsHolder card, int cards[], int must) {
        try {
            int[] cardPokes = card.cards;
            int cardValue = card.value;
            int cardType = card.cardsType;
            int cardLength = cardPokes.length;
            CardsAnalyzer analyzer = CardsAnalyzer.getInstance();
            analyzer.setPokes(cards);

            Vector<int[]> temp;
            int size = 0;
            switch (cardType) {
                case CardsType.danpai:
                    temp = analyzer.getCard_danpai();
                    size = temp.size();
                    for (int i = 0; i < size; i++) {
                        int[] cardArray = temp.get(i);
                        int v = CardsManager.getCardNumber(cardArray[0]);
                        if (v > cardValue) {
                            return cardArray;
                        }
                    }
                    int st = 0;
                    if (analyzer.getCountWang() == 2) {
                        st += 2;
                    }
                    if (analyzer.getCount2() == 4) {
                        st += 4;
                    }
                    if (CardsManager.getCardNumber(cards[st]) > cardValue)
                        return new int[]{cards[st]};


                    break;
                case CardsType.duipai:
                    temp = analyzer.getCard_duipai();
                    size = temp.size();

                    for (int i = 0; i < size; i++) {
                        int[] cardArray = temp.get(i);
                        int v = CardsManager.getCardNumber(cardArray[0]);
                        if (v > cardValue) {
                            return cardArray;
                        }
                    }

                    temp = analyzer.getCard_shuangshun();
                    size = temp.size();
                    for (int i = 0; i < size; i++) {
                        int[] cardArray = temp.get(i);
                        for (int j = cardArray.length - 1; j > 0; j--) {
                            int v = CardsManager.getCardNumber(cardArray[j]);
                            if (v > cardValue) {
                                return new int[]{cardArray[j], cardArray[j - 1]};
                            }
                        }
                    }
                    temp = analyzer.getCard_sanzhang();
                    size = temp.size();
                    for (int i = 0; i < size; i++) {
                        int[] cardArray = temp.get(i);
                        int v = CardsManager.getCardNumber(cardArray[0]);
                        if (v > cardValue) {
                            return new int[]{cardArray[0], cardArray[1]};
                        }
                    }

                    break;
                case CardsType.sanzhang:
                    temp = analyzer.getCard_sanzhang();
                    size = temp.size();
                    for (int i = 0; i < size; i++) {
                        int[] cardArray = temp.get(i);
                        int v = CardsManager.getCardNumber(cardArray[0]);
                        if (v > cardValue) {
                            return cardArray;
                        }
                    }
                    break;
                case CardsType.sandaiyi:
                    if (cards.length < 4) {
                        break;
                    }
                    boolean find = false;
                    if (cardLength == 4) {
                        int[] sandaiyi = new int[4];
                        temp = analyzer.getCard_sanzhang();
                        size = temp.size();
                        for (int i = size - 1; i >= 0; i--) {
                            int[] cardArray = temp.get(i);
                            int v = CardsManager.getCardNumber(cardArray[0]);
                            if (v > cardValue) {
                                for (int j = 0; j < cardArray.length; j++) {
                                    sandaiyi[j] = cardArray[j];
                                    find = true;
                                }
                            }
                        }
                        if (!find) {
                            break;
                        }
                        temp = analyzer.getCard_danpai();
                        size = temp.size();
                        if (size > 0) {
                            int[] t = temp.get(0);
                            sandaiyi[3] = t[0];
                        } else {
                            temp = analyzer.getCard_danshun();
                            size = temp.size();
                            for (int i = 0; i < size; i++) {
                                int[] danshun = temp.get(i);
                                if (danshun.length >= 6) {
                                    sandaiyi[3] = danshun[0];
                                }
                            }
                        }
                        if (sandaiyi[3] == 0) {
                            for (int i = cards.length - 1; i >= 0; i--) {
                                if (CardsManager.getCardNumber(cards[i]) != CardsManager
                                        .getCardNumber(sandaiyi[0])) {
                                    sandaiyi[3] = cards[i];
                                }
                            }
                        }
                        if (sandaiyi[3] != 0) {
                            CardsManager.sort(sandaiyi);
                            return sandaiyi;
                        }
                    }
                    if (cardLength == 5) {
                        int[] sandaidui = new int[5];
                        temp = analyzer.getCard_sanzhang();
                        size = temp.size();
                        for (int i = size - 1; i >= 0; i--) {
                            int[] cardArray = temp.get(i);
                            int v = CardsManager.getCardNumber(cardArray[0]);
                            if (v > cardValue) {
                                for (int j = 0; j < cardArray.length; j++) {
                                    sandaidui[j] = cardArray[j];
                                    find = true;
                                }
                            }
                        }
                        if (!find) {
                            break;
                        }
                        temp = analyzer.getCard_duipai();
                        size = temp.size();
                        if (size > 0) {
                            int[] t = temp.get(0);
                            sandaidui[3] = t[0];
                            sandaidui[4] = t[1];
                        } else {
                            temp = analyzer.getCard_shuangshun();
                            size = temp.size();
                            for (int i = 0; i < size; i += 2) {
                                int[] shuangshun = temp.get(i);
                                if (shuangshun.length >= 8) {
                                    sandaidui[3] = shuangshun[0];
                                    sandaidui[4] = shuangshun[1];
                                }
                            }
                        }
                        if (sandaidui[3] == 0) {
                            for (int i = cards.length - 1; i > 0; i--) {
                                if (CardsManager.getCardNumber(cards[i]) != CardsManager
                                        .getCardNumber(sandaidui[0])
                                        && CardsManager.getCardNumber(cards[i]) == CardsManager
                                        .getCardNumber(cards[i - 1])) {
                                    sandaidui[3] = cards[i];
                                    sandaidui[4] = cards[i - 1];
                                }
                            }
                        }
                        if (sandaidui[3] != 0) {
                            CardsManager.sort(sandaidui);
                            return sandaidui;
                        }
                    }
                    break;
                case CardsType.danshun:
                    temp = analyzer.getCard_danshun();
                    size = temp.size();
                    for (int i = 0; i < size; i++) {
                        int[] danshun = temp.get(i);
                        if (danshun.length == cardLength) {
                            if (cardValue < CardsManager.getCardNumber(danshun[0])) {
                                return danshun;
                            }
                        }
                    }
                    for (int i = 0; i < size; i++) {
                        int[] danshun = temp.get(i);
                        if (danshun.length > cardLength) {
                            if (danshun.length < cardLength || danshun.length - cardLength >= 3) {
                                if (rand.nextInt(100) < must) {
                                    if (cardValue >= CardsManager.getCardNumber(danshun[0])) {
                                        continue;
                                    }

                                    int index = 0;
                                    for (int k = 0; k < danshun.length; k++) {
                                        if (cardValue < CardsManager.getCardNumber(danshun[k])) {
                                            index = k;
                                        } else {
                                            break;
                                        }
                                    }

                                    if (index + cardLength > danshun.length) {
                                        index = danshun.length - cardLength;
                                    }
                                    int[] newArray = new int[cardLength];
                                    int n = 0;
                                    for (int m = index; m < danshun.length; m++) {
                                        newArray[n++] = danshun[m];
                                    }
                                    return newArray;
                                }
                                break;
                            }
                            if (cardValue >= CardsManager.getCardNumber(danshun[0])) {
                                continue;
                            }
                            int start = 0;
                            if (danshun.length - cardLength == 1) {
                                if (cardValue < CardsManager.getCardNumber(danshun[1])) {
                                    start = 1;
                                } else {
                                    start = 0;
                                }
                            } else if (danshun.length - cardLength == 2) {
                                if (cardValue < CardsManager.getCardNumber(danshun[2])) {
                                    start = 2;
                                } else if (cardValue < CardsManager.getCardNumber(danshun[1])) {
                                    start = 1;
                                } else {
                                    start = 0;
                                }
                            }
                            int[] dan = new int[cardLength];
                            int m = 0;
                            for (int k = start; k < danshun.length; k++) {
                                dan[m++] = danshun[k];
                            }
                            return dan;
                        }
                    }
                    break;
                case CardsType.shuangshun:
                    temp = analyzer.getCard_shuangshun();
                    size = temp.size();
                    for (int i = size - 1; i >= 0; i--) {
                        int cardArray[] = temp.get(i);
                        if (cardArray.length < cardLength) {
                            continue;
                        }

                        if (cardValue < CardsManager.getCardNumber(cardArray[0])) {
                            if (cardArray.length == cardLength) {
                                return cardArray;
                            } else {
                                int index = 0;
                                for (int j = cardArray.length - 1; j >= 0; j--) {
                                    if (cardValue < CardsManager.getCardNumber(cardArray[j])) {
                                        index = j / 2;
                                        break;
                                    }
                                }

                                int total = cardArray.length / 2;
                                int cardTotal = cardLength / 2;
                                if (index + cardTotal > total) {
                                    index = total - cardTotal;
                                }
                                int shuangshun[] = new int[cardLength];
                                int m = 0;
                                for (int k = index * 2; k < cardArray.length; k++) {
                                    shuangshun[m++] = cardArray[k];
                                }
                                return shuangshun;
                            }
                        }
                    }
                    break;
                case CardsType.sanshun:
                    temp = analyzer.getCard_sanshun();
                    size = temp.size();
                    for (int i = size - 1; i >= 0; i--) {
                        int[] cardArray = temp.get(i);
                        if (cardLength > cardArray.length) {
                            continue;
                        }

                        if (cardValue < CardsManager.getCardNumber(cardArray[0])) {
                            if (cardLength == cardArray.length) {
                                return cardArray;
                            } else {
                                int[] newArray = new int[cardLength];
                                for (int k = 0; k < cardLength; k++) {
                                    newArray[k] = cardArray[k];
                                }
                                return newArray;
                            }
                        }
                    }
                    break;
                case CardsType.feiji:
                    break;
                case CardsType.zhadan:
                    temp = analyzer.getCard_zhadan();
                    size = temp.size();
                    int zd[] = null;
                    if (size > 0) {
                        for (int i = 0; i < size; i++) {
                            zd = temp.elementAt(i);
                            if (cardValue < CardsManager.getCardNumber(zd[0])) {
                                return zd;
                            }
                        }
                    }
                    break;
                case CardsType.huojian:
                    return null;
                case CardsType.sidaier:
                    break;
            }
            boolean needZd = false;
            if (must < 90) {
                must *= 0.2;
                if (rand.nextInt(100) < must) {
                    needZd = true;
                }
            } else {
                needZd = true;
            }
            if (needZd) {
                temp = analyzer.getCard_zhadan();
                size = temp.size();
                if (size > 0) {
                    return temp.elementAt(size - 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

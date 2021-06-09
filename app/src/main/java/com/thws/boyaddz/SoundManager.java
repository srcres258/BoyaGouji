package com.thws.boyaddz;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

public class SoundManager {
    private static class SoundID {
        static int danpai = -1;
        static int duipai = -1;
        static int sanzhang = -1;
        static int sandaiyi = -1;
        static int danshun = -1;
        static int shuangshun = -1;
        static int sanshun = -1;
        static int feiji = -1;
        static int sidaier = -1;
        static int zhadan = -1;
        static int huojian = -1;
        static int victory = -1;
        static int failure = -1;
        static int chupai1 = -1;
        static int chupai2 = -1;
        static int chupai3 = -1;
        static int guopai = -1;
        static int beimen = -1;
        static int dian = -1;
        static int qidian = -1;
        static int done = -1;
        static int done1 = -1;
        static int shaopai = -1;
        static int jieshao = -1;
    }

    static SoundPool pool;

    public static void init(Context context) {
        AudioAttributes attrs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        pool = new SoundPool.Builder()
                .setMaxStreams(16)
                .setAudioAttributes(attrs)
                .build();
        SoundID.danpai = pool.load(context, R.raw.danpai, 1);
        SoundID.duipai = pool.load(context, R.raw.duipai, 1);
        SoundID.sanzhang = pool.load(context, R.raw.sanzhang, 1);
        SoundID.sandaiyi = pool.load(context, R.raw.sandaiyi, 1);
        SoundID.danshun = pool.load(context, R.raw.danshun, 1);
        SoundID.shuangshun = pool.load(context, R.raw.shuangshun, 1);
        SoundID.sanshun = pool.load(context, R.raw.sanshun, 1);
        SoundID.feiji = pool.load(context, R.raw.feiji, 1);
        SoundID.sidaier = pool.load(context, R.raw.sidaier, 1);
        SoundID.zhadan = pool.load(context, R.raw.zhadan, 1);
        SoundID.huojian = pool.load(context, R.raw.huojian, 1);
        SoundID.victory = pool.load(context, R.raw.victory, 1);
        SoundID.failure = pool.load(context, R.raw.failure, 1);
        SoundID.chupai1 = pool.load(context, R.raw.chupai1, 1);
        SoundID.chupai2 = pool.load(context, R.raw.chupai2, 1);
        SoundID.chupai3 = pool.load(context, R.raw.chupai3, 1);
        SoundID.guopai = pool.load(context, R.raw.guopai, 1);
        SoundID.beimen = pool.load(context, R.raw.beimen, 1);
        SoundID.dian = pool.load(context, R.raw.dian, 1);
        SoundID.qidian = pool.load(context, R.raw.qidian, 1);
        SoundID.done = pool.load(context, R.raw.done, 1);
        SoundID.done1 = pool.load(context, R.raw.done1, 1);
        SoundID.shaopai = pool.load(context, R.raw.shaopai, 1);
        SoundID.jieshao = pool.load(context, R.raw.jieshao, 1);
    }

//    public static void playCardsTypeSound(int type) {
////        switch (type) {
////            case CardsType.danpai:
////                pool.play(SoundID.danpai, 1, 1, 1, 0, 1);
////                break;
////            case CardsType.duipai:
////                pool.play(SoundID.duipai, 1, 1, 1, 0, 1);
////                break;
////            case CardsType.sanzhang:
////                pool.play(SoundID.sanzhang, 1, 1, 1, 0, 1);
////                break;
////            case CardsType.sandaiyi:
////                pool.play(SoundID.sandaiyi, 1, 1, 1, 0, 1);
////                break;
////            case CardsType.danshun:
////                pool.play(SoundID.danshun, 1, 1, 1, 0, 1);
////                break;
////            case CardsType.shuangshun:
////                pool.play(SoundID.shuangshun, 1, 1, 1, 0, 1);
////                break;
////            case CardsType.sanshun:
////                pool.play(SoundID.sanshun, 1, 1, 1, 0, 1);
////                break;
////            case CardsType.feiji:
////                pool.play(SoundID.feiji, 1, 1, 1, 0, 1);
////                break;
////            case CardsType.sidaier:
////                pool.play(SoundID.sidaier, 1, 1, 1, 0, 1);
////                break;
////            case CardsType.zhadan:
////                pool.play(SoundID.zhadan, 1, 1, 1, 0, 1);
////                break;
////            case CardsType.huojian:
////                pool.play(SoundID.huojian, 1, 1, 1, 0, 1);
////                break;
////            default:
////                Log.e("BoYaDDZ-SoundManager", "Invalid CardsType value!");
////        }
//        switch (type) {
//            case CardsType.danpai:
//            case CardsType.duipai:
//            case CardsType.sanzhang:
//            case CardsType.sandaiyi:
//                pool.play(SoundID.chupai1, 1, 1, 1, 0, 1);
//                break;
//            case CardsType.danshun:
//            case CardsType.shuangshun:
//            case CardsType.sanshun:
//            case CardsType.feiji:
//            case CardsType.sidaier:
//                pool.play(SoundID.chupai2, 1, 1, 1, 0, 1);
//                break;
//            case CardsType.zhadan:
//            case CardsType.huojian:
//                pool.play(SoundID.chupai3, 1, 1, 1, 0, 1);
//                break;
//            default:
//                Log.e("BoYaDDZ-SoundManager", "Invalid CardsType value!");
//        }
//    }

    public static void playCardsTypeSound(int type) {
        switch (type) {
            case GJCardsType.chupai:
                pool.play(SoundID.chupai1, 1, 1, 1, 0, 1);
                break;
            case GJCardsType.gouji:
                pool.play(SoundID.chupai2, 1, 1, 1, 0, 1);
                break;
            case GJCardsType.guaxiaowang:
            case GJCardsType.guadawang:
                pool.play(SoundID.chupai3, 1, 1, 1, 0, 1);
                break;
            default:
                Log.e("BoYaDDZ-SoundManager", "Invalid CardsType value!");
        }
    }

    public static void playVictorySound() {
        pool.play(SoundID.victory, 1, 1, 1, 0, 1);
    }

    public static void playFailureSound() {
        pool.play(SoundID.failure, 1, 1, 1, 0, 1);
    }

    public static void playGuopaiSound() {
        pool.play(SoundID.guopai, 1, 1, 1, 0, 1);
    }

    public static void playMenSound() {
        pool.play(SoundID.beimen, 1, 1, 1, 0, 1);
    }

    public static void playDianSound() {
        pool.play(SoundID.dian, 1, 1, 1, 0, 1);
    }

    public static void playQidianSound() {
        pool.play(SoundID.qidian, 1, 1, 1, 0, 1);
    }

    public static void playDoneSound(boolean firstDone) {
        if (firstDone)
            pool.play(SoundID.done1, 1, 1, 1, 0, 1);
        else
            pool.play(SoundID.done, 1, 1, 1, 0, 1);
    }

    public static void playShaopaiSound() {
        pool.play(SoundID.shaopai, 1, 1, 1, 0, 1);
    }
    public static void playJieshaoSound() {
        pool.play(SoundID.jieshao, 1, 1, 1, 0, 1);
    }
}

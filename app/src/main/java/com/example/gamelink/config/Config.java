package com.example.gamelink.config;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/12/30.
 */
public class Config extends Application {
    // 当前关卡
    public static int gameLevel;
    // 当前得分
    public static int gameScore;
    // 最高分
    public static int gameRecord;
    // 游戏行列数
    public static int gameLines;
    // 游戏倒计时
    public static int gameTime;
    // 游戏背景音乐
    public static boolean gameSoundBg;
    public static String gameSoundBgName;
    // 游戏音效
    public static boolean gameSoundAct;
    public static String gameSoundActName;
    public static SharedPreferences sp;
    public static final String SP_NAME = "sp_name";
    public static final String KEY_GAME_RECORD = "game_record";
    public static final String KEY_GAME_LINES = "game_lines";
    public static final String KEY_GAME_TIME = "game_time";
    public static final String KEY_GAME_SOUND_BG = "game_sound_bg";
    public static final String KEY_GAME_SOUND_BG_NAME = "game_sound_bg_name";
    public static final String KEY_GAME_SOUND_ACT = "game_sound_act";
    public static final String KEY_GAME_SOUND_ACT_NAME = "game_sound_act_name";

    @Override
    public void onCreate() {
        super.onCreate();
        gameLevel = 1;
        gameScore = 0;
        sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        gameRecord = sp.getInt(KEY_GAME_RECORD, 0);
        gameLines = sp.getInt(KEY_GAME_LINES, 6);
        gameTime = sp.getInt(KEY_GAME_TIME, 30);
        gameSoundBg = sp.getBoolean(KEY_GAME_SOUND_BG, false);
        gameSoundBgName = sp.getString(KEY_GAME_SOUND_BG_NAME, "一人我饮酒醉");
        gameSoundAct = sp.getBoolean(KEY_GAME_SOUND_ACT, false);
        gameSoundActName = sp.getString(KEY_GAME_SOUND_ACT_NAME, "音效1");
    }
}

package com.example.gamelink.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gamelink.R;
import com.example.gamelink.config.Config;
import com.example.gamelink.service.SoundService;
import com.example.gamelink.view.GameView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTvLevel, mTvScore, mTvRecord;
    // 按钮
    private Button mBtnTime, mBtnRestart, mBtnConfig;
    // 游戏面板
    private GameView mGameView;
    private static final int REQUEST_CODE = 100;
    private Map<String, Integer> mSoundBgMap;
    // 有没有改变游戏行列数和游戏倒计时
    private boolean mChangeFlag1;
    // 有没有改变游戏背景音乐
    private boolean mChangeFlag2;
    // 第一次播放背景音乐
    private boolean mFirstSoundBg = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        mGameView = new GameView(this);
        RelativeLayout gamePanel = (RelativeLayout) findViewById(R.id.id_rl_game_panel);
        gamePanel.addView(mGameView);

        mTvLevel.setText("" + Config.gameLevel);
        mTvScore.setText("" + Config.gameScore);
        mTvRecord.setText("" + Config.gameRecord);
        mBtnRestart.setOnClickListener(this);
        mBtnConfig.setOnClickListener(this);

        // 背景音乐
        initSoundBgMap();
    }

    /**
     * 初始化背景音乐
     */
    private void initSoundBgMap() {
        mSoundBgMap = new HashMap<>();
        mSoundBgMap.put("一人我饮酒醉", R.raw.sound_bg_1);
        mSoundBgMap.put("你还要我怎样", R.raw.sound_bg_2);
        mSoundBgMap.put("没有你陪伴真的好孤单", R.raw.sound_bg_3);
        mSoundBgMap.put("演员", R.raw.sound_bg_4);
        mSoundBgMap.put("走着走着就散了", R.raw.sound_bg_5);
        mSoundBgMap.put("逆流成河", R.raw.sound_bg_6);
    }

    private void initViews() {
        mTvLevel = (TextView) findViewById(R.id.id_tv_level);
        mTvScore = (TextView) findViewById(R.id.id_tv_score);
        mTvRecord = (TextView) findViewById(R.id.id_tv_record);
        mBtnTime = (Button) findViewById(R.id.id_btn_time);
        mBtnRestart = (Button) findViewById(R.id.id_btn_restart);
        mBtnConfig = (Button) findViewById(R.id.id_btn_config);
    }

    public void setLevel(int level) {
        mTvLevel.setText("" + level);
    }

    public void setScore(int score) {
        mTvScore.setText("" + score);
    }

    public void setRecord(int record) {
        mTvRecord.setText("" + record);
    }

    public void setTime(int time) {
        mBtnTime.setText("" + time);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.id_btn_restart:
                mGameView.startGame(GameView.FIRST_LEVEL);
                break;
            case R.id.id_btn_config:
                Intent intent = new Intent(this, ConfigActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) {
            return;
        }
        switch(requestCode) {
            case REQUEST_CODE:
                // 有没有改变游戏行列数和游戏倒计时
                mChangeFlag1 = data.getBooleanExtra(ConfigActivity.EXTRA_CHANGE_FLAG_1, false);
                // 有没有改变游戏背景音乐
                mChangeFlag2 = data.getBooleanExtra(ConfigActivity.EXTRA_CHANGE_FLAG_2, false);
                if(mChangeFlag1) {
                    mGameView.startGame(GameView.FIRST_LEVEL);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 背景音乐
        Intent intent = new Intent(this, SoundService.class);
        if(Config.gameSoundBg) {
            // 开启背景音乐
            if(mChangeFlag2 || mFirstSoundBg) {
                // 换歌曲了，新歌曲重头播放
                intent.putExtra(SoundService.EXTRA_OPERATE, SoundService.PLAY);
                intent.putExtra(SoundService.EXTRA_SOUND_BG_RES_ID, mSoundBgMap.get(Config.gameSoundBgName));
                startService(intent);
                mFirstSoundBg = false;
            } else {
                // 没换歌曲，歌曲继续播放
                intent.putExtra(SoundService.EXTRA_OPERATE, SoundService.PLAY);
                startService(intent);
            }
        } else {
            // 关闭背景音乐
            stopService(intent);
        }
        // 倒计时
        mGameView.mTimeChange = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 背景音乐
        if(Config.gameSoundBg) {
            Intent intent = new Intent(this, SoundService.class);
            intent.putExtra(SoundService.EXTRA_OPERATE, SoundService.PAUSE);
            startService(intent);
        }
        // 倒计时
        mGameView.mTimeChange = false;
    }

    @Override
    protected void onDestroy() {
        // 背景音乐
        Intent intent = new Intent(this, SoundService.class);
        stopService(intent);
        super.onDestroy();
    }
}

package com.example.gamelink.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.example.gamelink.R;
import com.example.gamelink.config.Config;

public class ConfigActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBtnLines, mBtnTime, mBtnSoundBg, mBtnSoundAct, mBtnBack, mBtnDone;
    private AlertDialog.Builder mBuilder;
    private String[] mArrayLines, mArrayTime, mArraySoundBg, mArraySoundAct;
    private Switch mSwitchSoundBg, mSwitchSoundAct;
    // 有没有改变游戏行列数和游戏倒计时
    private boolean mChangeFlag1;
    public static final String EXTRA_CHANGE_FLAG_1 = "change_flag_1";
    // 有没有改变游戏背景音乐
    private boolean mChangeFlag2;
    public static final String EXTRA_CHANGE_FLAG_2 = "change_flag_2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        initViews();
        mBtnLines.setText("" + Config.gameLines);
        mBtnTime.setText("" + Config.gameTime);
        mBtnSoundBg.setText(Config.gameSoundBgName);
        mBtnSoundAct.setText(Config.gameSoundActName);
        mSwitchSoundBg.setChecked(Config.gameSoundBg);
        mSwitchSoundAct.setChecked(Config.gameSoundAct);
        mBtnLines.setOnClickListener(this);
        mBtnTime.setOnClickListener(this);
        mBtnSoundBg.setOnClickListener(this);
        mBtnSoundAct.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mBtnDone.setOnClickListener(this);
    }

    private void initViews() {
        mBtnLines = (Button) findViewById(R.id.id_btn_lines);
        mBtnTime = (Button) findViewById(R.id.id_btn_time);
        mBtnSoundBg = (Button) findViewById(R.id.id_btn_sound_bg);
        mBtnSoundAct = (Button) findViewById(R.id.id_btn_sound_act);
        mBtnBack = (Button) findViewById(R.id.id_btn_back);
        mBtnDone = (Button) findViewById(R.id.id_btn_done);
        mBuilder = new AlertDialog.Builder(this);
        mArrayLines = new String[] {"6", "8", "10"};
        mArrayTime = new String[] {"30", "45", "60"};
        mArraySoundBg = new String[] {"一人我饮酒醉", "你还要我怎样", "没有你陪伴真的好孤单", "演员",
                "走着走着就散了", "逆流成河"};
        mArraySoundAct = new String[] {"音效1", "音效2", "音效3"};
        mSwitchSoundBg = (Switch) findViewById(R.id.id_switch_sound_bg);
        mSwitchSoundAct = (Switch) findViewById(R.id.id_switch_sound_act);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.id_btn_lines:
                mBuilder.setItems(mArrayLines, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBtnLines.setText(mArrayLines[which]);
                    }
                }).create().show();
                break;
            case R.id.id_btn_time:
                mBuilder.setItems(mArrayTime, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBtnTime.setText(mArrayTime[which]);
                    }
                }).create().show();
                break;
            case R.id.id_btn_sound_bg:
                mBuilder.setItems(mArraySoundBg, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBtnSoundBg.setText(mArraySoundBg[which]);
                    }
                }).create().show();
                break;
            case R.id.id_btn_sound_act:
                mBuilder.setItems(mArraySoundAct, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBtnSoundAct.setText(mArraySoundAct[which]);
                    }
                }).create().show();
                break;
            case R.id.id_btn_back:
                finish();
                break;
            case R.id.id_btn_done:
                saveConfig();
                Intent data = new Intent();
                data.putExtra(EXTRA_CHANGE_FLAG_1, mChangeFlag1);
                data.putExtra(EXTRA_CHANGE_FLAG_2, mChangeFlag2);
                setResult(RESULT_OK, data);
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 保存配置信息
     */
    private void saveConfig() {
        // 设置mFlag
        int lines = Integer.parseInt(mBtnLines.getText().toString());
        int time = Integer.parseInt(mBtnTime.getText().toString());
        String soundBgName = mBtnSoundBg.getText().toString();
        if(lines != Config.gameLines || time != Config.gameTime) {
            mChangeFlag1 = true;
        }
        if(!soundBgName.equals(Config.gameSoundBgName)) {
            mChangeFlag2 = true;
        }

        SharedPreferences.Editor editor = Config.sp.edit();
        editor.putInt(Config.KEY_GAME_LINES, Integer.parseInt(mBtnLines.getText().toString()));
        editor.putInt(Config.KEY_GAME_TIME, Integer.parseInt(mBtnTime.getText().toString()));
        editor.putString(Config.KEY_GAME_SOUND_BG_NAME, mBtnSoundBg.getText().toString());
        editor.putString(Config.KEY_GAME_SOUND_ACT_NAME, mBtnSoundAct.getText().toString());
        editor.putBoolean(Config.KEY_GAME_SOUND_BG, mSwitchSoundBg.isChecked());
        editor.putBoolean(Config.KEY_GAME_SOUND_ACT, mSwitchSoundAct.isChecked());
        editor.commit();
        Config.gameLines = Config.sp.getInt(Config.KEY_GAME_LINES, 6);
        Config.gameTime = Config.sp.getInt(Config.KEY_GAME_TIME, 30);
        Config.gameSoundBgName = Config.sp.getString(Config.KEY_GAME_SOUND_BG_NAME, "一人我饮酒醉");
        Config.gameSoundActName = Config.sp.getString(Config.KEY_GAME_SOUND_ACT_NAME, "音效1");
        Config.gameSoundBg = Config.sp.getBoolean(Config.KEY_GAME_SOUND_BG, false);
        Config.gameSoundAct = Config.sp.getBoolean(Config.KEY_GAME_SOUND_ACT, false);
    }
}

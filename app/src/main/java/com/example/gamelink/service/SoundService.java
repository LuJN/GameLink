package com.example.gamelink.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class SoundService extends Service {
    private MediaPlayer mPlayer;
    public static final String EXTRA_OPERATE = "operate";
    public static final String EXTRA_SOUND_BG_RES_ID = "sound_bg_res_id";
    public static final int PLAY = 0;
    public static final int PAUSE = 1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int operate = intent.getIntExtra(EXTRA_OPERATE, PLAY);
        switch(operate) {
            case PLAY:
                int resId = intent.getIntExtra(EXTRA_SOUND_BG_RES_ID, -1);
                if(resId == -1) {
                    // 没有传入新歌曲参数
                    if(!mPlayer.isPlaying()) {
                        mPlayer.start();
                    }
                } else {
                    // 传入新歌曲参数
                    if(mPlayer != null) {
                        if(mPlayer.isPlaying()) {
                            mPlayer.stop();
                        }
                        mPlayer.release();
                        mPlayer = null;
                    }
                    mPlayer = MediaPlayer.create(this, resId);
                    mPlayer.setLooping(true);
                    mPlayer.start();
                }
                break;
            case PAUSE:
                if(mPlayer.isPlaying()) {
                    mPlayer.pause();
                }
                break;
            default:
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if(mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer.release();
        mPlayer = null;
        super.onDestroy();
    }
}

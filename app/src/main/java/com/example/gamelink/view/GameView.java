package com.example.gamelink.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;

import com.example.gamelink.R;
import com.example.gamelink.activity.MainActivity;
import com.example.gamelink.bean.GameItem;
import com.example.gamelink.bean.Route;
import com.example.gamelink.config.Config;
import com.example.gamelink.util.DisPlayUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/12/29.
 */
public class GameView extends GridLayout implements View.OnTouchListener {
    private int mGameLines;
    private int mScreenWidth;
    private int mItemSize;
    private GameItem[][] mGameMatrix;
    private int[] mBitmapResIds;
    // 本次点击项和上次点击项
    private GameItem mGameItem;
    private GameItem mLastGameItem;
    // Timer、TimerTask
    private Timer mTimer;
    private TimerTask mTimerTask;
    // 倒计时按钮是否变更数字
    public boolean mTimeChange = true;
    // 时间
    private int mTime;
    // Handler
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mTime--;
                    ((MainActivity) getContext()).setTime(mTime);
                    if (mTime == 0) {
                        mTimer.cancel();
                        mTimerTask.cancel();
                        showGameResult(GAME_OVER);
                    }
                    break;
                default:
                    break;
            }
        }
    };
    // 游戏状态
    private static final int GAME_OVER = 0;// 游戏结束
    private static final int GAME_NORMAL = 1;// 正常进行游戏中
    private static final int GAME_NEXT = 2;// 下一关
    private static final int GAME_SUCCESS = 3;// 所有关卡通过
    // 开始游戏参数
    public static final int FIRST_LEVEL = 0;
    public static final int NEXT_LEVEL = 1;
    // 音效
    private SoundPool mSoundPool;
    private Map<String, Integer> mSoundActMap;
    // 背景音乐
    private Map<String, Integer> mSoundBgMap;

    public GameView(Context context) {
        super(context);
        initSoundActMap();
        initBitmapResIds();
        initGameView();
    }

    /**
     * 初始化音效Map
     */
    private void initSoundActMap() {
        mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        int soundId1 = mSoundPool.load(getContext(), R.raw.sound_action_1, 1);
        int soundId2 = mSoundPool.load(getContext(), R.raw.sound_action_2, 1);
        int soundId3 = mSoundPool.load(getContext(), R.raw.sound_action_3, 1);
        mSoundActMap = new HashMap<>();
        mSoundActMap.put("音效1", soundId1);
        mSoundActMap.put("音效2", soundId2);
        mSoundActMap.put("音效3", soundId3);
    }

    private void initBitmapResIds() {
        mBitmapResIds = new int[]{R.drawable.blank, R.drawable.pic1, R.drawable.pic2, R.drawable.pic3,
                R.drawable.pic4, R.drawable.pic5, R.drawable.pic6};
    }

    private void initGameView() {
        // 移除所有的View，再添加View
        removeAllViews();
        // 初始化游戏面板相关参数
        mGameLines = Config.gameLines;
        mScreenWidth = DisPlayUtil.getScreenSize(getContext()).widthPixels;
        mItemSize = mScreenWidth / mGameLines;
        mGameMatrix = new GameItem[mGameLines][mGameLines];
        setColumnCount(mGameLines);
        setRowCount(mGameLines);
        // 初始化游戏矩阵
        initGameMatrix();
        // 检查游戏矩阵是否合理，不合理就重新初始化游戏矩阵
        if (!checkGameMatrix()) {
            initGameMatrix();
        }
        for (int row = 0; row < mGameLines; row++) {
            for (int column = 0; column < mGameLines; column++) {
                addView(mGameMatrix[row][column], mItemSize, mItemSize);
            }
        }
        setOnTouchListener(this);
        // 初始化mTime（因为后面计时器执行任务是0延迟的，所以要加1）
        mTime = Config.gameTime + 1;
        // 初始化mTimer和mTimerTask
        mTimer = new Timer(true);
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if(mTimeChange) {
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                }
            }
        };
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    /**
     * 检查是否有可连接项
     */
    private boolean checkGameMatrix() {
        List<GameItem> gameItemList = new ArrayList<>();
        for (int row = 0; row < mGameLines; row++) {
            for (int column = 0; column < mGameLines; column++) {
                if (mGameMatrix[row][column].getBitmapId() != mBitmapResIds[0]) {
                    gameItemList.add(mGameMatrix[row][column]);
                }
            }
        }
        for (int index1 = 0; index1 < gameItemList.size(); index1++) {
            GameItem gameItem1 = gameItemList.get(index1);
            for (int index2 = index1 + 1; index2 < gameItemList.size(); index2++) {
                GameItem gameItem2 = gameItemList.get(index2);
                if (link(gameItem1, gameItem2) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 初始化矩阵
     */
    private void initGameMatrix() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (i == 0 || i == mGameLines - 1 || j == 0 || j == mGameLines - 1) {
                    GameItem item = new GameItem(getContext(), i * mGameLines + j,
                            mBitmapResIds[0]);
                    mGameMatrix[i][j] = item;
                } else {
                    int indexRandom = (int) (Math.random() * mBitmapResIds.length);
                    GameItem item = new GameItem(getContext(), i * mGameLines + j,
                            mBitmapResIds[indexRandom]);
                    mGameMatrix[i][j] = item;
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mGameItem = mGameMatrix[y / mItemSize][x / mItemSize];
                if (mLastGameItem == null) {
                    mLastGameItem = mGameItem;
                } else {
                    if (mGameItem != mLastGameItem && link(mGameItem, mLastGameItem) != null) {
                        mGameItem.setBitmapId(mBitmapResIds[0]);
                        mLastGameItem.setBitmapId(mBitmapResIds[0]);
                        // 音效
                        if(Config.gameSoundAct) {
                            playSoundAction(Config.gameSoundActName);
                        }
                        // 更改当前得分
                        Config.gameScore = Config.gameScore + 10;
                        ((MainActivity) getContext()).setScore(Config.gameScore);
                        // 更改最高分
                        if (Config.gameScore > Config.gameRecord) {
                            SharedPreferences.Editor editor = Config.sp.edit();
                            editor.putInt(Config.KEY_GAME_RECORD, Config.gameScore);
                            editor.commit();
                            Config.gameRecord = Config.gameScore;
                            ((MainActivity) getContext()).setRecord(Config.gameRecord);
                        }
                        int gameState = checkGameState();
                        showGameResult(gameState);
                    }
                    // 重置mLastGameItem
                    mLastGameItem = null;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    /**
     * 播放音效
     */
    private void playSoundAction(String soundName) {
        int soundId = mSoundActMap.get(soundName);
        AudioManager manager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        float currentVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float leftVolume = currentVolume / maxVolume;
        float rightVolume = leftVolume;
        mSoundPool.play(soundId, leftVolume, rightVolume, 1, 0, 1);
    }

    /**
     * 返回连接消除的路线，没有路线则返回null
     */
    private Route link(GameItem gameItem, GameItem lastGameItem) {
        // 图片不相同
        if (gameItem.getBitmapId() != lastGameItem.getBitmapId()) {
            return null;
        }
        List<Route> routeListAll = null;
        if (gameItem.getItemId() / mGameLines == lastGameItem.getItemId() / mGameLines) {
            // 在同一行
            // 返回所有可能路线（包括包含障碍物的路线）
            routeListAll = routesOnRowEqual(gameItem, lastGameItem);
        } else if (gameItem.getItemId() % mGameLines == lastGameItem.getItemId() % mGameLines) {
            // 在同一列
            // 返回所有可能路线（包括包含障碍物的路线）
            routeListAll = routesOnColumnEqual(gameItem, lastGameItem);
        } else if (gameItem.getItemId() / mGameLines > lastGameItem.getItemId() / mGameLines &&
                gameItem.getItemId() % mGameLines > lastGameItem.getItemId() % mGameLines) {
            // 这次的在上次的右下方
            // 返回所有可能路线（包括包含障碍物的路线）
            routeListAll = routesOnSlash1(gameItem, lastGameItem);
        } else if (gameItem.getItemId() / mGameLines < lastGameItem.getItemId() / mGameLines &&
                gameItem.getItemId() % mGameLines < lastGameItem.getItemId() % mGameLines) {
            // 这次的在这次的左上方
            // 返回所有可能路线（包括包含障碍物的路线）
            routeListAll = routesOnSlash1(gameItem, lastGameItem);
        } else if (gameItem.getItemId() / mGameLines > lastGameItem.getItemId() / mGameLines &&
                gameItem.getItemId() % mGameLines < lastGameItem.getItemId() % mGameLines) {
            // 这次的在这次的左下方
            // 返回所有可能路线（包括包含障碍物的路线）
            routeListAll = routesOnSlash2(gameItem, lastGameItem);
        } else if (gameItem.getItemId() / mGameLines < lastGameItem.getItemId() / mGameLines &&
                gameItem.getItemId() % mGameLines > lastGameItem.getItemId() % mGameLines) {
            // 这次的在上次的右上方
            // 返回所有可能路线（包括包含障碍物的路线）
            routeListAll = routesOnSlash2(gameItem, lastGameItem);
        }
        // 不包含障碍物的路线
        List<Route> routeListSuccess = new ArrayList<>();
        for (Route route : routeListAll) {
            boolean flag = true;
            List<Integer> nodeList = route.getNodeList();
            for (int i = 1; i < nodeList.size() - 1; i++) {
                int node = nodeList.get(i);
                if (mGameMatrix[node / mGameLines][node % mGameLines].getBitmapId() !=
                        mBitmapResIds[0]) {
                    flag = false;
                }
            }
            if (flag) {
                routeListSuccess.add(route);
            }
        }
        // 没有不包含障碍物的路线
        if (routeListSuccess.size() == 0) {
            return null;
        }
        // 得到routeListSuccess（不包含障碍物的路线集合）中最短的路线
        Route routeShortest = routeListSuccess.get(0);
        int minSize = routeShortest.getNodeList().size();
        for (Route route : routeListSuccess) {
            int size = route.getNodeList().size();
            if (size < minSize) {
                routeShortest = route;
                minSize = size;
            }
        }
        return routeShortest;
    }

    /**
     * 相同行
     * 返回连接路线（不一定可用，中间可能有障碍物）
     */
    private List<Route> routesOnRowEqual(GameItem gameItem, GameItem lastGameItem) {
        // 如果gameItem在lastGameItem的左边，那就交换两者
        if (gameItem.getItemId() < lastGameItem.getItemId()) {
            GameItem tempGameItem = new GameItem(getContext());
            tempGameItem.setItemId(gameItem.getItemId());
            tempGameItem.setBitmapId(gameItem.getBitmapId());
            gameItem.setItemId(lastGameItem.getItemId());
            gameItem.setBitmapId(lastGameItem.getBitmapId());
            lastGameItem.setItemId(tempGameItem.getItemId());
            lastGameItem.setBitmapId(tempGameItem.getBitmapId());
        }
        // gameItem在lastGameItem的右边
        List<Route> routeList = new ArrayList<>();
        // 上——右——下（lastGameItem --> gameItem）
        for (int row = 0; row < lastGameItem.getItemId() / mGameLines; row++) {
            Route route1 = new Route();
            List<Integer> nodeList1 = route1.getNodeList();
            for (int node = lastGameItem.getItemId();
                 node >= lastGameItem.getItemId() % mGameLines + (row + 1) * mGameLines;
                 node = node - mGameLines) {
                nodeList1.add(node);
            }
            for (int node = lastGameItem.getItemId() % mGameLines + row * mGameLines;
                 node <= gameItem.getItemId() % mGameLines + row * mGameLines;
                 node++) {
                nodeList1.add(node);
            }
            for (int node = gameItem.getItemId() % mGameLines + (row + 1) * mGameLines;
                 node <= gameItem.getItemId();
                 node = node + mGameLines) {
                nodeList1.add(node);
            }
            routeList.add(route1);
        }
        // 右（lastGameItem --> gameItem）
        Route route2 = new Route();
        List<Integer> nodeList2 = route2.getNodeList();
        for (int node = lastGameItem.getItemId();
             node <= gameItem.getItemId();
             node++) {
            nodeList2.add(node);
        }
        routeList.add(route2);
        // 下——右——上（lastGameItem --> gameItem）
        for (int row = lastGameItem.getItemId() / mItemSize + 1;
             row < mGameLines; row++) {
            Route route3 = new Route();
            List<Integer> nodeList3 = route3.getNodeList();
            for (int node = lastGameItem.getItemId();
                 node <= lastGameItem.getItemId() % mGameLines + (row - 1) * mGameLines;
                 node = node + mGameLines) {
                nodeList3.add(node);
            }
            for (int node = lastGameItem.getItemId() % mGameLines + row * mGameLines;
                 node <= gameItem.getItemId() % mGameLines + row * mGameLines;
                 node++) {
                nodeList3.add(node);
            }
            for (int node = gameItem.getItemId() % mGameLines + (row - 1) * mGameLines;
                 node >= gameItem.getItemId();
                 node = node - mGameLines) {
                nodeList3.add(node);
            }
            routeList.add(route3);
        }
        return routeList;
    }

    /**
     * 相同列
     * 返回的连接路线（不一定可用，中间可能有障碍物）
     */
    private List<Route> routesOnColumnEqual(GameItem gameItem, GameItem lastGameItem) {
        // 如果gameItem在lastGameItem的上边，那就交换两者
        if (gameItem.getItemId() < lastGameItem.getItemId()) {
            GameItem tempGameItem = new GameItem(getContext());
            tempGameItem.setItemId(gameItem.getItemId());
            tempGameItem.setBitmapId(gameItem.getBitmapId());
            gameItem.setItemId(lastGameItem.getItemId());
            gameItem.setBitmapId(lastGameItem.getBitmapId());
            lastGameItem.setItemId(tempGameItem.getItemId());
            lastGameItem.setBitmapId(tempGameItem.getBitmapId());
        }
        // gameItem在lastGameItem的下边
        List<Route> routeList = new ArrayList<>();
        // 左——下——右（lastGameItem --> gameItem）
        for (int column = 0; column < lastGameItem.getItemId() % mGameLines; column++) {
            Route route1 = new Route();
            List<Integer> nodeList1 = route1.getNodeList();
            for (int node = lastGameItem.getItemId();
                 node >= lastGameItem.getItemId() / mGameLines * mGameLines + column + 1;
                 node--) {
                nodeList1.add(node);
            }
            for (int node = lastGameItem.getItemId() / mGameLines * mGameLines + column;
                 node <= gameItem.getItemId() / mGameLines * mGameLines + column;
                 node = node + mGameLines) {
                nodeList1.add(node);
            }
            for (int node = gameItem.getItemId() / mGameLines * mGameLines + column + 1;
                 node <= gameItem.getItemId();
                 node++) {
                nodeList1.add(node);
            }
            routeList.add(route1);
        }
        // 下（lastGameItem --> gameItem）
        Route route2 = new Route();
        List<Integer> nodeList2 = route2.getNodeList();
        for (int node = lastGameItem.getItemId();
             node <= gameItem.getItemId();
             node = node + mGameLines) {
            nodeList2.add(node);
        }
        routeList.add(route2);
        // 右——下——左（lastGameItem --> gameItem）
        for (int column = lastGameItem.getItemId() % mGameLines + 1;
             column < mGameLines; column++) {
            Route route3 = new Route();
            List<Integer> nodeList3 = route3.getNodeList();
            for (int node = lastGameItem.getItemId();
                 node <= lastGameItem.getItemId() / mGameLines * mGameLines + column - 1;
                 node++) {
                nodeList3.add(node);
            }
            for (int node = lastGameItem.getItemId() / mGameLines * mGameLines + column;
                 node <= gameItem.getItemId() / mGameLines * mGameLines + column;
                 node = node + mGameLines) {
                nodeList3.add(node);
            }
            for (int node = gameItem.getItemId() / mGameLines * mGameLines + column - 1;
                 node >= gameItem.getItemId();
                 node--) {
                nodeList3.add(node);
            }
            routeList.add(route3);
        }
        return routeList;
    }

    /**
     * 这次的在上次的左上方或右下方
     * 返回连接路线（不一定可用，中间可能有障碍物）
     * slash:斜线
     */
    private List<Route> routesOnSlash1(GameItem gameItem, GameItem lastGameItem) {
        // 如果gameItem在lastGameItem的左上方，那就交换两者
        if (gameItem.getItemId() < lastGameItem.getItemId()) {
            GameItem tempGameItem = new GameItem(getContext());
            tempGameItem.setItemId(gameItem.getItemId());
            tempGameItem.setBitmapId(gameItem.getBitmapId());
            gameItem.setItemId(lastGameItem.getItemId());
            gameItem.setBitmapId(lastGameItem.getBitmapId());
            lastGameItem.setItemId(tempGameItem.getItemId());
            lastGameItem.setBitmapId(tempGameItem.getBitmapId());
        }
        // gameItem在lastGameItem的右下方
        List<Route> routeList = new ArrayList<>();
        // 上——右——下（lastGameItem --> gameItem）
        for (int row = 0; row <= lastGameItem.getItemId() / mGameLines - 1; row++) {
            Route route1 = new Route();
            List<Integer> nodeList1 = route1.getNodeList();
            for (int node = lastGameItem.getItemId();
                 node >= (row + 1) * mGameLines + lastGameItem.getItemId() % mGameLines;
                 node = node - mGameLines) {
                nodeList1.add(node);
            }
            for (int node = row * mGameLines + lastGameItem.getItemId() % mGameLines;
                 node <= row * mGameLines + gameItem.getItemId() % mGameLines;
                 node++) {
                nodeList1.add(node);
            }
            for (int node = (row + 1) * mGameLines + gameItem.getItemId() % mGameLines;
                 node <= gameItem.getItemId();
                 node = node + mGameLines) {
                nodeList1.add(node);
            }
            routeList.add(route1);
        }
        // 下——右——下（lastGameItem --> gameItem）
        for (int row = lastGameItem.getItemId() / mGameLines + 1;
             row <= gameItem.getItemId() / mGameLines - 1;
             row++) {
            Route route2 = new Route();
            List<Integer> nodeList2 = route2.getNodeList();
            for (int node = lastGameItem.getItemId();
                 node <= (row - 1) * mGameLines + lastGameItem.getItemId() % mGameLines;
                 node = node + mGameLines) {
                nodeList2.add(node);
            }
            for (int node = row * mGameLines + lastGameItem.getItemId() % mGameLines;
                 node <= row * mGameLines + gameItem.getItemId() % mGameLines;
                 node++) {
                nodeList2.add(node);
            }
            for (int node = (row + 1) * mGameLines + gameItem.getItemId() % mGameLines;
                 node <= gameItem.getItemId();
                 node = node + mGameLines) {
                nodeList2.add(node);
            }
            routeList.add(route2);
        }
        // 下——右（lastGameItem --> gameItem）
        Route route3 = new Route();
        List<Integer> nodeList3 = route3.getNodeList();
        for (int row = lastGameItem.getItemId() / mGameLines;
             row <= gameItem.getItemId() / mGameLines - 1;
             row++) {
            int node = row * mGameLines + lastGameItem.getItemId() % mGameLines;
            nodeList3.add(node);
        }
        int nodeCorner = gameItem.getItemId() / mGameLines * mGameLines +
                lastGameItem.getItemId() % mGameLines;
        nodeList3.add(nodeCorner);
        for (int column = lastGameItem.getItemId() % mGameLines + 1;
             column <= gameItem.getItemId() % mGameLines;
             column++) {
            int node = gameItem.getItemId() / mGameLines * mGameLines + column;
            nodeList3.add(node);
        }
        routeList.add(route3);
        // 下——右——上（lastGameItem --> gameItem）
        for (int row = gameItem.getItemId() / mGameLines + 1; row < mGameLines; row++) {
            Route route4 = new Route();
            List<Integer> nodeList4 = route4.getNodeList();
            for (int node = lastGameItem.getItemId();
                 node <= (row - 1) * mGameLines + lastGameItem.getItemId() % mGameLines;
                 node = node + mGameLines) {
                nodeList4.add(node);
            }
            for (int node = row * mGameLines + lastGameItem.getItemId() % mGameLines;
                 node <= row * mGameLines + gameItem.getItemId() % mGameLines;
                 node++) {
                nodeList4.add(node);
            }
            for (int node = (row - 1) * mGameLines + gameItem.getItemId() % mGameLines;
                 node >= gameItem.getItemId();
                 node = node - mGameLines) {
                nodeList4.add(node);
            }
            routeList.add(route4);
        }
        // 左——下——右（lastGameItem --> gameItem）
        for (int column = 0; column <= lastGameItem.getItemId() % mGameLines - 1; column++) {
            Route route5 = new Route();
            List<Integer> nodeList5 = route5.getNodeList();
            for (int node = lastGameItem.getItemId();
                 node >= lastGameItem.getItemId() / mGameLines * mGameLines + column + 1;
                 node--) {
                nodeList5.add(node);
            }
            for (int node = lastGameItem.getItemId() / mGameLines * mGameLines + column;
                 node <= gameItem.getItemId() / mGameLines * mGameLines + column;
                 node = node + mGameLines) {
                nodeList5.add(node);
            }
            for (int node = gameItem.getItemId() / mGameLines * mGameLines + column + 1;
                 node <= gameItem.getItemId();
                 node++) {
                nodeList5.add(node);
            }
            routeList.add(route5);
        }
        // 右——下——右（lastGameItem --> gameItem）
        for (int column = lastGameItem.getItemId() % mGameLines + 1;
             column <= gameItem.getItemId() % mGameLines - 1;
             column++) {
            Route route6 = new Route();
            List<Integer> nodeList6 = route6.getNodeList();
            for (int node = lastGameItem.getItemId();
                 node <= lastGameItem.getItemId() / mGameLines * mGameLines + column - 1;
                 node++) {
                nodeList6.add(node);
            }
            for (int node = lastGameItem.getItemId() / mGameLines * mGameLines + column;
                 node <= gameItem.getItemId() / mGameLines * mGameLines + column;
                 node = node + mGameLines) {
                nodeList6.add(node);
            }
            for (int node = gameItem.getItemId() / mGameLines * mGameLines + column + 1;
                 node <= gameItem.getItemId();
                 node++) {
                nodeList6.add(node);
            }
            routeList.add(route6);
        }
        // 右——下（lastGameItem --> gameItem）
        Route route7 = new Route();
        List<Integer> nodeList7 = route7.getNodeList();
        for (int column = lastGameItem.getItemId() % mGameLines;
             column <= gameItem.getItemId() % mGameLines - 1;
             column++) {
            int node = lastGameItem.getItemId() / mGameLines * mGameLines + column;
            nodeList7.add(node);
        }
        int nodeCorner2 = lastGameItem.getItemId() / mGameLines * mGameLines +
                gameItem.getItemId() % mGameLines;
        nodeList7.add(nodeCorner2);
        for (int row = lastGameItem.getItemId() / mGameLines + 1;
             row <= gameItem.getItemId() / mGameLines;
             row++) {
            int node = row * mGameLines + gameItem.getItemId() % mGameLines;
            nodeList7.add(node);
        }
        routeList.add(route7);
        // 右——下——左（lastGameItem --> gameItem）
        for (int column = gameItem.getItemId() % mGameLines + 1; column < mGameLines; column++) {
            Route route8 = new Route();
            List<Integer> nodeList8 = route8.getNodeList();
            for (int node = lastGameItem.getItemId();
                 node <= lastGameItem.getItemId() / mGameLines * mGameLines + column - 1;
                 node++) {
                nodeList8.add(node);
            }
            for (int node = lastGameItem.getItemId() / mGameLines * mGameLines + column;
                 node <= gameItem.getItemId() / mGameLines * mGameLines + column;
                 node = node + mGameLines) {
                nodeList8.add(node);
            }
            for (int node = gameItem.getItemId() / mGameLines * mGameLines + column - 1;
                 node >= gameItem.getItemId();
                 node--) {
                nodeList8.add(node);
            }
            routeList.add(route8);
        }
        return routeList;
    }

    /**
     * 这次的在上次的右上方或左下方
     * 返回连接路线（不一定可用，中间可能有障碍物）
     * slash:斜线
     */
    private List<Route> routesOnSlash2(GameItem gameItem, GameItem lastGameItem) {
        // 如果gameItem在lastGameItem的右上方，那就交换两者
        if (gameItem.getItemId() < lastGameItem.getItemId()) {
            GameItem tempGameItem = new GameItem(getContext());
            tempGameItem.setItemId(gameItem.getItemId());
            tempGameItem.setBitmapId(gameItem.getBitmapId());
            gameItem.setItemId(lastGameItem.getItemId());
            gameItem.setBitmapId(lastGameItem.getBitmapId());
            lastGameItem.setItemId(tempGameItem.getItemId());
            lastGameItem.setBitmapId(tempGameItem.getBitmapId());
        }
        // gameItem在lastGameItem的左下方
        List<Route> routeList = new ArrayList<>();
        // 上——左——下（lastGameItem --> gameItem）
        for (int row = 0; row <= lastGameItem.getItemId() / mGameLines - 1; row++) {
            Route route1 = new Route();
            List<Integer> nodeList1 = route1.getNodeList();
            for (int node = lastGameItem.getItemId();
                 node >= (row + 1) * mGameLines + lastGameItem.getItemId() % mGameLines;
                 node = node - mGameLines) {
                nodeList1.add(node);
            }
            for (int node = row * mGameLines + lastGameItem.getItemId() % mGameLines;
                 node >= row * mGameLines + gameItem.getItemId() % mGameLines;
                 node--) {
                nodeList1.add(node);
            }
            for (int node = (row + 1) * mGameLines + gameItem.getItemId() % mGameLines;
                 node <= gameItem.getItemId();
                 node = node + mGameLines) {
                nodeList1.add(node);
            }
            routeList.add(route1);
        }
        // 下——左——下（lastGameItem --> gameItem）
        for (int row = lastGameItem.getItemId() / mGameLines + 1;
             row <= gameItem.getItemId() / mGameLines - 1; row++) {
            Route route2 = new Route();
            List<Integer> nodeList2 = route2.getNodeList();
            for (int node = lastGameItem.getItemId();
                 node <= (row - 1) * mGameLines + lastGameItem.getItemId() % mGameLines;
                 node = node + mGameLines) {
                nodeList2.add(node);
            }
            for (int node = row * mGameLines + lastGameItem.getItemId() % mGameLines;
                 node >= row * mGameLines + gameItem.getItemId() % mGameLines;
                 node--) {
                nodeList2.add(node);
            }
            for (int node = (row + 1) * mGameLines + gameItem.getItemId() % mGameLines;
                 node <= gameItem.getItemId();
                 node = node + mGameLines) {
                nodeList2.add(node);
            }
            routeList.add(route2);
        }
        // 下——左（lastGameItem --> gameItem）
        Route route3 = new Route();
        List<Integer> nodeList3 = route3.getNodeList();
        for (int row = lastGameItem.getItemId() / mGameLines;
             row <= gameItem.getItemId() / mGameLines - 1;
             row++) {
            int node = row * mGameLines + lastGameItem.getItemId() % mGameLines;
            nodeList3.add(node);
        }
        int nodeCorner = gameItem.getItemId() / mGameLines * mGameLines +
                lastGameItem.getItemId() % mGameLines;
        nodeList3.add(nodeCorner);
        for (int column = lastGameItem.getItemId() % mGameLines - 1;
             column >= gameItem.getItemId() % mGameLines;
             column--) {
            int node = gameItem.getItemId() / mGameLines * mGameLines + column;
            nodeList3.add(node);
        }
        routeList.add(route3);
        // 下——左——上（lastGameItem --> gameItem）
        for (int row = gameItem.getItemId() / mGameLines + 1; row < mGameLines; row++) {
            Route route4 = new Route();
            List<Integer> nodeList4 = route4.getNodeList();
            for (int node = lastGameItem.getItemId();
                 node <= (row - 1) * mGameLines + lastGameItem.getItemId() % mGameLines;
                 node = node + mGameLines) {
                nodeList4.add(node);
            }
            for (int node = row * mGameLines + lastGameItem.getItemId() % mGameLines;
                 node >= row * mGameLines + gameItem.getItemId() % mGameLines;
                 node--) {
                nodeList4.add(node);
            }
            for (int node = (row - 1) * mGameLines + gameItem.getItemId() % mGameLines;
                 node >= gameItem.getItemId();
                 node = node - mGameLines) {
                nodeList4.add(node);
            }
            routeList.add(route4);
        }
        // 右——下——左（lastGameItem --> gameItem）
        for (int column = lastGameItem.getItemId() % mGameLines + 1;
             column < mGameLines;
             column++) {
            Route route5 = new Route();
            List<Integer> nodeList5 = route5.getNodeList();
            for (int node = lastGameItem.getItemId();
                 node <= lastGameItem.getItemId() / mGameLines * mGameLines + column - 1;
                 node++) {
                nodeList5.add(node);
            }
            for (int node = lastGameItem.getItemId() / mGameLines * mGameLines + column;
                 node <= gameItem.getItemId() / mGameLines * mGameLines + column;
                 node = node + mGameLines) {
                nodeList5.add(node);
            }
            for (int node = gameItem.getItemId() / mGameLines * mGameLines + column - 1;
                 node >= gameItem.getItemId();
                 node--) {
                nodeList5.add(node);
            }
            routeList.add(route5);
        }
        // 左——下——左（lastGameItem --> gameItem）
        for (int column = lastGameItem.getItemId() % mGameLines - 1;
             column >= gameItem.getItemId() % mGameLines + 1;
             column--) {
            Route route6 = new Route();
            List<Integer> nodeList6 = route6.getNodeList();
            for (int node = lastGameItem.getItemId();
                 node >= lastGameItem.getItemId() / mGameLines * mGameLines + column + 1;
                 node--) {
                nodeList6.add(node);
            }
            for (int node = lastGameItem.getItemId() / mGameLines * mGameLines + column;
                 node <= gameItem.getItemId() / mGameLines * mGameLines + column;
                 node = node + mGameLines) {
                nodeList6.add(node);
            }
            for (int node = gameItem.getItemId() / mGameLines * mGameLines + column - 1;
                 node >= gameItem.getItemId();
                 node--) {
                nodeList6.add(node);
            }
            routeList.add(route6);
        }
        // 左——下（lastGameItem --> gameItem）
        Route route7 = new Route();
        List<Integer> nodeList7 = route7.getNodeList();
        for (int column = lastGameItem.getItemId() % mGameLines;
             column >= gameItem.getItemId() % mGameLines + 1;
             column--) {
            int node = lastGameItem.getItemId() / mGameLines * mGameLines + column;
            nodeList7.add(node);
        }
        int nodeCorner2 = lastGameItem.getItemId() / mGameLines * mGameLines +
                gameItem.getItemId() % mGameLines;
        nodeList7.add(nodeCorner2);
        for (int row = lastGameItem.getItemId() / mGameLines + 1;
             row <= gameItem.getItemId() / mGameLines;
             row++) {
            int node = row * mGameLines + gameItem.getItemId() % mGameLines;
            nodeList7.add(node);
        }
        routeList.add(route7);
        // 左——下——右（lastGameItem --> gameItem）
        for (int column = 0; column <= gameItem.getItemId() % mGameLines - 1; column++) {
            Route route8 = new Route();
            List<Integer> nodeList8 = route8.getNodeList();
            for (int node = lastGameItem.getItemId();
                 node >= lastGameItem.getItemId() / mGameLines * mGameLines + column + 1;
                 node--) {
                nodeList8.add(node);
            }
            for (int node = lastGameItem.getItemId() / mGameLines * mGameLines + column;
                 node <= gameItem.getItemId() / mGameLines * mGameLines + column;
                 node = node + mGameLines) {
                nodeList8.add(node);
            }
            for (int node = gameItem.getItemId() / mGameLines * mGameLines + column + 1;
                 node <= gameItem.getItemId();
                 node++) {
                nodeList8.add(node);
            }
            routeList.add(route8);
        }
        return routeList;
    }

    /**
     * 检查游戏状态
     * GAME_OVER = 0
     * GAME_NEXT = 1
     * GAME_SUCCESS = 2
     */
    private int checkGameState() {
        // 如果有可连接的 那就返回正常进行游戏中
        if (checkGameMatrix()) {
            return GAME_NORMAL;
        }
        // 如果没有可连接的
        if (Config.gameLevel == 10) {
            // 当前是最后一关，返回游戏成功
            return GAME_SUCCESS;
        } else {
            // 当前不是最后一关，返回下一关
            return GAME_NEXT;
        }
    }

    /**
     * 展示游戏结果
     */
    private void showGameResult(int gameState) {
        switch (gameState) {
            case GAME_OVER:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setTitle("游戏结束")
                        .setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 第一关开始游戏
                                startGame(FIRST_LEVEL);
                            }
                        }).create().show();
                break;
            case GAME_NORMAL:
                break;
            case GAME_NEXT:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                builder2.setTitle("下一关")
                        .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 下一关开始游戏
                                startGame(NEXT_LEVEL);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                break;
            case GAME_SUCCESS:
                AlertDialog.Builder builder3 = new AlertDialog.Builder(getContext());
                builder3.setTitle("通过所有关卡")
                        .setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 第一关开始游戏
                                startGame(FIRST_LEVEL);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                break;
            default:
                break;
        }
    }

    /**
     * 重新开始游戏
     * 第一关 FIRST_LEVEL
     * 下一关 NEXT_LEVEL
     */
    public void startGame(int flag) {
        mTimer.cancel();
        mTimerTask.cancel();
        if (flag == FIRST_LEVEL) {
            Config.gameLevel = 1;
            Config.gameScore = 0;
            ((MainActivity) getContext()).setLevel(Config.gameLevel);
            ((MainActivity) getContext()).setScore(Config.gameScore);
            initGameView();
        } else if (flag == NEXT_LEVEL) {
            Config.gameLevel++;
            ((MainActivity) getContext()).setLevel(Config.gameLevel);
            initGameView();
        }
    }
}
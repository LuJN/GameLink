package com.example.gamelink.bean;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/12/29.
 */
public class GameItem extends FrameLayout {
    private int itemId;
    private int bitmapId;
    private ImageView mImageView;

    public GameItem(Context context) {
        super(context);
    }

    public GameItem(Context context, int itemId, int bitmapId) {
        super(context);
        this.itemId = itemId;
        this.bitmapId = bitmapId;
        initItemView();
    }

    private void initItemView() {
        mImageView = new ImageView(getContext());
        mImageView.setBackgroundResource(bitmapId);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(5, 5, 5, 5);
        addView(mImageView, params);
    }

    private ImageView getItemView() {
        return mImageView;
    }

    public int getBitmapId() {
        return bitmapId;
    }

    public void setBitmapId(int bitmapId) {
        this.bitmapId = bitmapId;
        if(getItemView() != null) {
            getItemView().setBackgroundResource(bitmapId);
        }
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}

/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.samples.question;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.framework.audio.StatusCode;
import com.hyena.framework.audio.bean.Song;
import com.hyena.framework.servcie.audio.PlayerBusService;
import com.hyena.framework.servcie.audio.listener.PlayStatusChangeListener;
import com.hyena.framework.utils.UIUtils;

/**
 * Created by yangzc on 17/2/7.
 */
public class AudioBlock extends CYPlaceHolderBlock {

    private PlayerBusService mPlayBusService;
    private boolean mIsPlaying = false;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public AudioBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        mPlayBusService = (PlayerBusService) textEnv.getContext()
                .getSystemService(PlayerBusService.BUS_SERVICE_NAME);
        mPlayBusService.getPlayerBusServiceObserver().addPlayStatusChangeListener(mPlayStatusChangeListener);

        Paint paint = getTextEnv().getPaint();
        int height = (int) (Math.ceil(paint.descent() - paint.ascent()) + 0.5f);
        setWidth(UIUtils.dip2px(50));
        setHeight(height);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawRect(getContentRect(), mPaint);
    }

    @Override
    public boolean onTouchEvent(int action, float x, float y) {
        super.onTouchEvent(action, x, y);
        switch (action) {
            case MotionEvent.ACTION_UP: {
                //action click
                playOrPause();
                break;
            }
        }
        return super.onTouchEvent(action, x, y);
    }

    private void playOrPause() {
        if (mIsPlaying) {
            try {
                mPlayBusService.pause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                String url = "http://7xohdn.com2.z0.glb.qiniucdn.com/tingli/15594833.mp3";
                Song song = new Song(true, url, null);
                mPlayBusService.play(song);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void release() {
        super.release();
        if (mPlayBusService != null) {
            mPlayBusService.getPlayerBusServiceObserver().removemPlayStatusChangeListener(mPlayStatusChangeListener);
        }
    }

    private PlayStatusChangeListener mPlayStatusChangeListener = new PlayStatusChangeListener() {
        @Override
        public void onStatusChange(Song song, int status) {
            debug("AudioBlock status --->" + StatusCode.getStatusLabel(status));
            switch (status) {
                case StatusCode.STATUS_RELEASE:
                case StatusCode.STATUS_PREPARED:
                case StatusCode.STATUS_INITED:
                case StatusCode.STATUS_UNINITED:
                case StatusCode.STATUS_BUFFING: {
                    break;
                }
                case StatusCode.STATUS_PLAYING: {
                    if (mIsPlaying)
                        return;

                    mIsPlaying = true;
                    break;
                }
                case StatusCode.STATUS_ERROR:
                case StatusCode.STATUS_PAUSE:
                case StatusCode.STATUS_STOP:
                case StatusCode.STATUS_COMPLETED: {
                    if (!mIsPlaying)
                        return;

                    mIsPlaying = false;
                    break;
                }
            }
        }
    };

    @Override
    public boolean isDebug() {
        return true;
    }
}

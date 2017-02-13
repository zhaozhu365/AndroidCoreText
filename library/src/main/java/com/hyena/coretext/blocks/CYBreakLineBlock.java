package com.hyena.coretext.blocks;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.hyena.coretext.TextEnv;

/**
 * Created by yangzc on 16/4/9.
 */
public class CYBreakLineBlock extends CYBlock {

    public CYBreakLineBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
    }

    @Override
    public int getContentWidth() {
        return 0;
    }

    @Override
    public int getContentHeight() {
        return 0;
    }

    @Override
    public void draw(Canvas canvas) {

    }
}

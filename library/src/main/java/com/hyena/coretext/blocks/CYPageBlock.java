package com.hyena.coretext.blocks;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.hyena.coretext.TextEnv;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 16/4/8.
 */
public class CYPageBlock extends CYBlock<CYLineBlock> {

    public CYPageBlock() {
        super(null, "");
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
        if (getChildren() != null) {
            for (int i = 0; i < getChildren().size(); i++) {
                getChildren().get(i).draw(canvas);
            }
        }
    }
}

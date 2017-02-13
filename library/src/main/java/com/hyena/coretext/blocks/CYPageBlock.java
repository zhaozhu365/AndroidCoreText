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

    public CYPageBlock(TextEnv textEnv) {
        super(textEnv, "");
    }

    @Override
    public int getContentWidth() {
        List<CYBlock> blocks = getBlocks();
        int maxX = 0;
        if (blocks != null && !blocks.isEmpty()) {
            for (int i = 0; i < blocks.size(); i++) {
                CYBlock block = blocks.get(i);
                if ((block.getX() + block.getWidth()) > maxX) {
                    maxX = block.getX() + block.getWidth();
                }
            }
        }
        return maxX;
    }

    @Override
    public int getContentHeight() {
        List<CYBlock> blocks = getBlocks();
        int maxY = 0;
        if (blocks != null && !blocks.isEmpty()) {
            for (int i = 0; i < blocks.size(); i++) {
                CYBlock block = blocks.get(i);
                if ((block.getLineY() + block.getHeight()) > maxY) {
                    maxY = block.getLineY() + block.getHeight();
                }
            }
        }
        return maxY;
    }

    @Override
    public void draw(Canvas canvas) {
        if (getChildren() != null) {
            for (int i = 0; i < getChildren().size(); i++) {
                getChildren().get(i).draw(canvas);
            }
        }
    }

    public List<CYBlock> getBlocks() {
        List<CYBlock> blocks = new ArrayList<CYBlock>();
        List<CYLineBlock> lines = getChildren();
        if (lines != null) {
            for (int i = 0; i < lines.size(); i++) {
                CYLineBlock line = lines.get(i);
                blocks.addAll(line.getChildren());
            }
        }
        return blocks;
    }
}

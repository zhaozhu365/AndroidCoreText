package com.hyena.coretext.layout;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYLineBlock;
import com.hyena.coretext.blocks.CYPageBlock;

import java.util.List;

/**
 * Created by yangzc on 16/4/8.
 */
public abstract class CYLayout {

    private TextEnv mTextEnv;

    public CYLayout(TextEnv textEnv) {
        this.mTextEnv = textEnv;
    }

    public TextEnv getTextEnv() {
        return mTextEnv;
    }

    /**
     * parse block to page
     * @param blocks blocks
     * @return pages
     */
    public abstract List<CYPageBlock> parsePage(List<CYBlock> blocks);

}

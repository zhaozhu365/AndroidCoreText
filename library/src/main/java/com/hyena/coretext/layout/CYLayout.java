package com.hyena.coretext.layout;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYLineBlock;
import com.hyena.coretext.blocks.CYPageBlock;

import java.util.List;

/**
 * Created by yangzc on 16/4/8.
 */
public interface CYLayout {

    /**
     * parse block to page
     * @param textEnv environment
     * @param blocks blocks
     * @return pages
     */
    List<CYPageBlock> parsePage(TextEnv textEnv, List<CYBlock> blocks);

}

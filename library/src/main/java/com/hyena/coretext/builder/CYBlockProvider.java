/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.builder;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;

import java.util.List;

/**
 * Created by yangzc on 17/3/3.
 */
public class CYBlockProvider {

    private static CYBlockProvider mBlockProvider;
    private CYBlockBuilder mBlockBuilder;
    private CYBlockProvider() {}

    public static CYBlockProvider getBlockProvider() {
        if (mBlockProvider == null)
            mBlockProvider = new CYBlockProvider();
        return mBlockProvider;
    }

    public List<CYBlock> build(TextEnv textEnv, String content) {
        if (mBlockBuilder != null) {
            return mBlockBuilder.build(textEnv, content);
        }
        return null;
    }

    public void registerBlockBuilder(CYBlockBuilder builder) {
        this.mBlockBuilder = builder;
    }

    public static interface CYBlockBuilder {
        public List<CYBlock> build(TextEnv textEnv, String content);
    }
}

/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.blocks;

/**
 * Created by yangzc on 17/2/7.
 */
public interface ICYEditable extends ICYFocusable {

    public int getTabId();

    public String getText();

    public void setText(String text);
}

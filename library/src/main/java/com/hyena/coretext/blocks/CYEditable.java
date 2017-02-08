/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.blocks;

/**
 * Created by yangzc on 17/2/7.
 */
public interface CYEditable {

    public int getTabId();

    public void setTabId(int id);

    public String getText();

    public void setText(String text);

    public void setFocus(boolean focus);

    public boolean hasFocus();
}

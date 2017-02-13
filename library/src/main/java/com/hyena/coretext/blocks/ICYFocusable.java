/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.blocks;

/**
 * Created by yangzc on 17/2/10.
 */
public interface ICYFocusable {

    public void setFocus(boolean hasFocus);
    public boolean hasFocus();

    public void setFocusable(boolean focusable);
    public boolean isFocusable();
}

/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.blocks;

/**
 * Created by yangzc on 17/2/10.
 */
public interface ICYFocusable {

    void setFocus(boolean hasFocus);
    boolean hasFocus();

    void setFocusable(boolean focusable);
    boolean isFocusable();
}

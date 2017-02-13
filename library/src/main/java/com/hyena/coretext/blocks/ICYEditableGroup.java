/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.blocks;

/**
 * Created by yangzc on 17/2/8.
 */
public interface ICYEditableGroup {

    public ICYEditable findEditable(float x, float y);

    public ICYEditable getFocusEditable();
}

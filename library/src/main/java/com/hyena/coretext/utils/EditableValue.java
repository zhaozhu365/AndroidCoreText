/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.utils;

/**
 * Created by yangzc on 17/4/6.
 */
public class EditableValue {

    private int color = -1;
    private String value;

    public EditableValue() {
    }

    public EditableValue(int color, String value) {
        this.color = color;
        this.value = value;
    }

    public int getColor() {
        return color;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

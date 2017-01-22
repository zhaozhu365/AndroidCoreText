/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Created by yangzc on 17/1/20.
 */
public class TextEnv {

    private int mVerticalSpacing;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private TextEnv() {}

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Paint getPaint() {
        return paint;
    }

    public int getVerticalSpacing() {
        return mVerticalSpacing;
    }

    private void setVerticalSpacing(int spacing) {
        this.mVerticalSpacing = spacing;
    }

    public static class Builder {

        private int fontSize;
        private int textColor = Color.BLACK;
        private Typeface typeface;
        private int verticalSpacing = 0;

        public Builder() {}

        public Builder setFontSize(int fontSize) {
            this.fontSize = fontSize;
            return this;
        }

        public Builder setTextColor(int color) {
            this.textColor = color;
            return this;
        }

        public Builder setTypeFace(Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        public void setVerticalSpacing(int spacing) {
            this.verticalSpacing = spacing;
        }

        public TextEnv build() {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(textColor);
            if (fontSize <= 0) {
                fontSize = 60;
            }
            paint.setTextSize(fontSize);
            if (typeface != null)
                paint.setTypeface(typeface);

            TextEnv textEnv = new TextEnv();
            textEnv.setPaint(paint);
            textEnv.setVerticalSpacing(verticalSpacing);
            return textEnv;
        }
    }
}

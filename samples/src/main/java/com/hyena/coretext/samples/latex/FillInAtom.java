/*
 * Copyright (C) 2017 The AndroidLatexExt Project
 */

package com.hyena.coretext.samples.latex;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYEditFace;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.framework.utils.UIUtils;

import maximsblog.blogspot.com.jlatexmath.core.Atom;
import maximsblog.blogspot.com.jlatexmath.core.Box;
import maximsblog.blogspot.com.jlatexmath.core.ScaleBox;
import maximsblog.blogspot.com.jlatexmath.core.TeXEnvironment;
import maximsblog.blogspot.com.jlatexmath.core.TeXFont;
import maximsblog.blogspot.com.jlatexmath.core.TeXFormula;
import maximsblog.blogspot.com.jlatexmath.core.Text;

/**
 * Created by yangzc on 17/2/8.
 */
public class FillInAtom extends Atom {
    private String mIndex;
    private String mText;
    private String textStyle;

    public FillInAtom(String index, String text) {
        this.mIndex = index;
        this.mText = text;
    }

    @Override
    public Box createBox(TeXEnvironment env) {
        if (textStyle == null) {
            String ts = env.getTextStyle();
            if (ts != null) {
                textStyle = ts;
            }
        }
        boolean smallCap = env.getSmallCap();
        Text ch = getString(env.getTeXFont(), env.getStyle(), smallCap);
        Box box = new FillInBox((TextEnv) env.getTag(), Integer.valueOf(mIndex), ch);
        if (smallCap && Character.isLowerCase('0')) {
            // We have a small capital
            box = new ScaleBox(box, 0.8f, 0.8f);
        }
        return box;
    }

    private Text getString(TeXFont tf, int style, boolean smallCap) {
        if (textStyle == null) {
            return tf.getDefaultText(mText, style);
        } else {
            return tf.getText(mText, textStyle, style);
        }
    }

    public static class FillInBox extends Box implements ICYEditable {

        private TextEnv mTextEnv;
        private int mTabId;
        private Text mText;
        private CYEditFace mEditFace;
        private float mScale = 1.0f;

        public FillInBox(TextEnv textEnv, int tabId, Text text) {
            super();
            this.mTextEnv = textEnv;
            this.mTabId = Integer.valueOf(tabId);
            this.mText = text;
            mEditFace = new CYEditFace(textEnv, this);

            this.mScale = UIUtils.px2dip(textEnv.getContext(), textEnv.getPaint().getTextSize())
                    * textEnv.getContext().getResources().getDisplayMetrics()
                    .scaledDensity / TeXFormula.PIXELS_PER_POINT;
            double height = Math.ceil(mEditFace.getTextPaint().descent() - mEditFace.getTextPaint().ascent());

            setWidth(UIUtils.dip2px(50 + 6)/ mScale);
            setHeight((float) ((height + UIUtils.dip2px(4)) /mScale));
            mEditFace.setPadding(UIUtils.dip2px(3), UIUtils.dip2px(2), UIUtils.dip2px(3), UIUtils.dip2px(2));
            setDepth(text.getDepth());
            this.mTabId = tabId;
            mTextEnv.setEditableValue(tabId, text.getText());
        }

        private Rect mBlockRect = new Rect();
        private RectF mVisibleRect = new RectF();

        @Override
        public void draw(Canvas g2, float x, float y) {
            mVisibleRect.set(x, y - getHeight(), x + getWidth(), y);
            mBlockRect.set((int)(x * mScale + 0.5), (int)((y - getHeight()) * mScale + 0.5)
                    , (int)((x + getWidth()) * mScale + 0.5), (int)(y * mScale + 0.5));

            g2.save();
            g2.scale(mText.getMetrics().getSize() / mScale, mText.getMetrics().getSize() / mScale);
            mEditFace.onDraw(g2, mBlockRect);
            g2.restore();
        }

        @Override
        public int getLastFontId() {
            return mText.getTextFont().fontId;
        }


        public RectF getVisibleRect() {
            return mVisibleRect;
        }

        @Override
        public void setFocus(boolean focus) {
            if (mEditFace != null)
                mEditFace.setFocus(focus);
        }

        @Override
        public boolean hasFocus() {
            if (mEditFace != null)
                return mEditFace.hasFocus();
            return false;
        }

        @Override
        public void setFocusable(boolean focusable) {
            if (mEditFace != null)
                mEditFace.setEditable(focusable);
        }

        @Override
        public boolean isFocusable() {
            return true;
        }

        @Override
        public int getTabId() {
            return mTabId;
        }

        @Override
        public String getText() {
            if (mEditFace != null)
                return mEditFace.getText();
            return "";
        }

        @Override
        public void setText(String text) {
            if (mEditFace != null)
                mEditFace.setText(text);
        }

        public void release() {
            if (mEditFace != null)
                mEditFace.release();
        }

    }
}

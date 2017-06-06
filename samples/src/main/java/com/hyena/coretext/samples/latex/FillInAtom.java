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
import com.hyena.coretext.samples.question.EditFace;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.EditableValue;
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
    private String mClazz;
    private String textStyle;

    public FillInAtom(String index, String clazz, String text) {
        this.mIndex = index;
        this.mText = text;
        this.mClazz = clazz;
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
        Box box = new FillInBox((TextEnv) env.getTag(), Integer.valueOf(mIndex), mClazz, ch);
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

        private static final int DP_1 = Const.DP_1;
        private static final int DP_2 = Const.DP_1 * 2;
        private static final int DP_3 = Const.DP_1 * 3;
        private static final int DP_56 = Const.DP_1 * 56;

        public FillInBox(TextEnv textEnv, int tabId, String clazz, Text text) {
            super();
            this.mTextEnv = textEnv;
            this.mTabId = Integer.valueOf(tabId);
            this.mText = text;
            mEditFace = new EditFace(textEnv, this);

            this.mScale = UIUtils.px2dip(textEnv.getContext(), textEnv.getPaint().getTextSize())
                    * textEnv.getContext().getResources().getDisplayMetrics()
                    .scaledDensity / TeXFormula.PIXELS_PER_POINT;
//            double height = Math.ceil(mEditFace.getTextPaint().descent() - mEditFace.getTextPaint().ascent());
//            float finalHeight = (float) ((height + UIUtils.dip2px(4)) /mScale);
            setWidth(DP_56/ mScale);
            setHeight((-mEditFace.getTextPaint().ascent()  + DP_1)/mScale);
            setDepth((mEditFace.getTextPaint().descent() + DP_1)/mScale);
            mEditFace.setPadding(DP_3, DP_2, DP_3, DP_2);
            this.mTabId = tabId;
            mTextEnv.setEditableValue(tabId, text.getText());
        }

        private Rect mBlockRect = new Rect();
        private RectF mVisibleRect = new RectF();

        @Override
        public void draw(Canvas g2, float x, float y) {
            mVisibleRect.set(x, y - getHeight(), x + getWidth(), y);
            mBlockRect.set((int)(x * mScale + 0.5), (int)((y - getHeight()) * mScale + 0.5)
                    , (int)((x + getWidth()) * mScale + 0.5), (int)((y + getDepth()) * mScale + 0.5));
            g2.save();
            g2.scale(mText.getMetrics().getSize() / mScale, mText.getMetrics().getSize() / mScale);
            mEditFace.onDraw(g2, mBlockRect, mBlockRect);
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
            EditableValue value = mTextEnv.getEditableValue(getTabId());
            return value == null ? null : value.getValue();
        }

        @Override
        public void setText(String text) {
            mTextEnv.setEditableValue(getTabId(), text);
            mEditFace.setText(text);
        }

        @Override
        public float getHeight() {
            return super.getHeight();
        }

        @Override
        public void setTextColor(int color) {
            EditableValue value = mTextEnv.getEditableValue(getTabId());
            if (value == null) {
                value = new EditableValue();
                mTextEnv.setEditableValue(getTabId(), value);
            }
            value.setColor(color);
            mEditFace.setTextColor(color);
            mTextEnv.getEventDispatcher().postInvalidate(null);

        }

        public void release() {
            if (mEditFace != null)
                mEditFace.release();
        }

    }
}

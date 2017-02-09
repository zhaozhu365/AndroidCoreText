/*
 * Copyright (C) 2017 The AndroidLatexExt Project
 */

package com.hyena.coretext.samples.latex;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;

import com.hyena.coretext.CYPageView;
import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYEditable;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.event.CYEventDispatcher;
import com.hyena.framework.utils.UIUtils;

import maximsblog.blogspot.com.jlatexmath.core.AjLatexMath;
import maximsblog.blogspot.com.jlatexmath.core.Atom;
import maximsblog.blogspot.com.jlatexmath.core.Box;
import maximsblog.blogspot.com.jlatexmath.core.FontInfo;
import maximsblog.blogspot.com.jlatexmath.core.ScaleBox;
import maximsblog.blogspot.com.jlatexmath.core.TeXEnvironment;
import maximsblog.blogspot.com.jlatexmath.core.TeXFont;
import maximsblog.blogspot.com.jlatexmath.core.TeXFormula;
import maximsblog.blogspot.com.jlatexmath.core.Text;
import maximsblog.blogspot.com.jlatexmath.core.TextFont;

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
        Box box = new FillInBox((TextEnv) env.getTag(), mIndex, ch);
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

    public static class FillInBox extends Box implements CYEditable {

        private final TextFont cf;
        private final float size;
        private RectF mRect = new RectF();
        private int mIndex;
        private TextEnv mTextEnv;


        public FillInBox(TextEnv textEnv, String index, Text c) {
            super();
            this.mTextEnv = textEnv;
            this.mIndex = Integer.valueOf(index);
            mTextEnv.setEditableValue(mIndex, c.getText());
            cf = c.getTextFont();
            size = c.getMetrics().getSize();
            float scale = 20.0f * textEnv.getContext().getResources().getDisplayMetrics().scaledDensity / TeXFormula.PIXELS_PER_POINT;
            width = UIUtils.dip2px(50) / scale;
//            width = 50/TeXFormula.PIXELS_PER_POINT;
            height = c.getHeight();
            depth = c.getDepth();
        }

        private float mX, mY;

        @Override
        public void draw(Canvas g2, float x, float y) {
            this.mX = x;
            this.mY = y;

            drawDebug(g2, x, y);
            g2.save();
            g2.translate(x, y);
            Typeface font = FontInfo.getFont(cf.fontId);
            if (size != 1) {
                g2.scale(size, size);
            }
            Paint st = AjLatexMath.getPaint();
            st.setTextSize(TeXFormula.PIXELS_PER_POINT);
            st.setTypeface(font);
            st.setAntiAlias(true);
            st.setStrokeWidth(0);

            if (hasFocus()) {
                mRect.set(0, -(height) - 0.5f, (int) width, 0.5f);
                st.setStyle(Paint.Style.STROKE);
                g2.drawRect(mRect, st);
            }

            st.setStyle(Paint.Style.FILL);
            String text = getText();

            if (!TextUtils.isEmpty(text)) {
                float textX;
                float textWidth = st.measureText(text);
                if (textWidth > getWidth()) {
                    textX = getWidth() - textWidth;
                } else {
                    textX = (getWidth() - textWidth) / 2;
                }
                g2.drawText(text, textX, 0.0f, st);
            }
            g2.restore();
        }

        @Override
        public int getLastFontId() {
            return cf.fontId;
        }

        private RectF mRectF = new RectF();

        public RectF getVisibleRect() {
            mRectF.set(mX, mY - height - 0.5f, mX + width, mY + 0.5f);
            Log.v("yangzc", mRectF.toString());
            return mRectF;
        }

        @Override
        public void setFocus(boolean focus) {
            if (focus) {
                CYPageView.FOCUS_TAB_ID = mIndex;
                Log.v("yangzc", "setFocus: " + mIndex);
            }
            CYEventDispatcher.getEventDispatcher().postInvalidate();
        }

        @Override
        public float getWidth() {
            return super.getWidth();
        }

        @Override
        public boolean hasFocus() {
            return CYPageView.FOCUS_TAB_ID == mIndex;
        }

        @Override
        public int getTabId() {
            return mIndex;
        }

        @Override
        public void setTabId(int id) {
            this.mIndex = id;
        }

        @Override
        public String getText() {
            return mTextEnv.getEditableValue(mIndex);
        }

        @Override
        public void setText(String text) {
            mTextEnv.setEditableValue(mIndex, text);
            CYEventDispatcher.getEventDispatcher().postInvalidate();
        }

    }
}

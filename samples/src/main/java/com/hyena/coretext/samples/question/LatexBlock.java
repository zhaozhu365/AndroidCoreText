package com.hyena.coretext.samples.question;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYEditable;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.coretext.samples.latex.FillInAtom;

import maximsblog.blogspot.com.jlatexmath.ExampleFormula;
import maximsblog.blogspot.com.jlatexmath.core.AjLatexMath;
import maximsblog.blogspot.com.jlatexmath.core.Box;
import maximsblog.blogspot.com.jlatexmath.core.Insets;
import maximsblog.blogspot.com.jlatexmath.core.TeXConstants;
import maximsblog.blogspot.com.jlatexmath.core.TeXFormula;
import maximsblog.blogspot.com.jlatexmath.core.TeXIcon;

/**
 * Created by yangzc on 16/6/14.
 */
public class LatexBlock extends CYPlaceHolderBlock {

    private TeXFormula mTexFormula;
    private TeXIcon mTexIcon;
    private TeXFormula.TeXIconBuilder mBuilder;
    private String mLatex;

    public LatexBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init();
    }

    private void init() {
        mTexFormula = new TeXFormula();
        mBuilder = mTexFormula.new TeXIconBuilder()
                .setStyle(TeXConstants.STYLE_DISPLAY)
                .setSize(30)
                .setWidth(TeXConstants.UNIT_PIXEL, getTextEnv().getPageWidth(), TeXConstants.ALIGN_LEFT)
                .setIsMaxWidth(true)//非精准宽度
                .setInterLineSpacing(TeXConstants.UNIT_PIXEL,
                AjLatexMath.getLeading(30));
        setFormula(ExampleFormula.mExample8);
    }

    public void setFormula(String latex){
        this.mLatex = latex;
        mTexFormula.setLaTeX(latex);
        mTexIcon = mBuilder.build();
        mTexIcon.setInsets(new Insets(5, 5, 5, 5));

        requestLayout();
    }

    public String getLatex() {
        return mLatex;
    }

    @Override
    public int getContentWidth() {
        if (mTexIcon != null)
            return (int) mTexIcon.getTrueIconWidth();
        return super.getContentWidth();
    }

    @Override
    public int getContentHeight() {
        if (mTexIcon != null) {
            return (int) mTexIcon.getTrueIconHeight();
        }
        return super.getContentHeight();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mTexIcon != null) {
            canvas.save();
            Rect contentRect = getContentRect();
            canvas.translate(contentRect.left, contentRect.top);
            mTexIcon.paintIcon(canvas, 0, 0);
            canvas.restore();
        }
    }

    @Override
    public void setFocus(boolean focus) {
        super.setFocus(focus);
    }

    @Override
    public boolean onTouchEvent(int action, float x, float y) {
        if (mTexIcon == null && mTexIcon.getSize() != 0)
            return false;
        x = x / mTexIcon.getSize();
        y = y / mTexIcon.getSize();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            {
                Log.v("yangzc", "x: " + x + ", y: " + y);
                FillInAtom.FillInBox fillInBox = getFillInBox(mTexIcon.getBox(), x, y);
                if (fillInBox != null) {
                    fillInBox.setFocus(true);
                }
                postInvalidate();
                break;
            }
        }
        return true;
    }

    public FillInAtom.FillInBox getFocusFillIn(){
        return getFocusFillIn(mTexIcon.getBox());
    }

    private FillInAtom.FillInBox getFillInBox(Box box, float x, float y) {
        if (box != null) {
            if (box instanceof FillInAtom.FillInBox) {
                if (((FillInAtom.FillInBox) box).getVisibleRect().contains(x, y)) {
                    return (FillInAtom.FillInBox) box;
                }
            } else {
                if (box.getChildren() != null && !box.getChildren().isEmpty()) {
                    for (int i = 0; i < box.getChildren().size(); i++) {
                        Box child = box.getChildren().get(i);
                        FillInAtom.FillInBox result = getFillInBox(child, x, y);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        return null;
    }

    public FillInAtom.FillInBox getFocusFillIn(Box box) {
        if (box != null) {
            if (box instanceof FillInAtom.FillInBox) {
                if (((FillInAtom.FillInBox) box).hasFocus()) {
                    return (FillInAtom.FillInBox) box;
                }
            } else {
                if (box.getChildren() != null && !box.getChildren().isEmpty()) {
                    for (int i = 0; i < box.getChildren().size(); i++) {
                        Box child = box.getChildren().get(i);
                        FillInAtom.FillInBox result = getFocusFillIn(child);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean isDebug() {
        return true;
    }
}

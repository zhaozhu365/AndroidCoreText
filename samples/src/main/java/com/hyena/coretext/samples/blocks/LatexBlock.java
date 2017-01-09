//package com.hyena.coretext.samples.blocks;
//
//import android.content.Context;
//import android.graphics.Canvas;
//
//import com.himamis.retex.renderer.android.FactoryProviderAndroid;
//import com.himamis.retex.renderer.android.graphics.ColorA;
//import com.himamis.retex.renderer.android.graphics.Graphics2DA;
//import com.himamis.retex.renderer.share.TeXConstants;
//import com.himamis.retex.renderer.share.TeXFormula;
//import com.himamis.retex.renderer.share.TeXIcon;
//import com.himamis.retex.renderer.share.exception.ParseException;
//import com.himamis.retex.renderer.share.platform.FactoryProvider;
//import com.himamis.retex.renderer.share.platform.graphics.Color;
//import com.hyena.coretext.blocks.CYPlaceHolderBlock;
//import com.hyena.fillin.utils.AjLatexMath;
//
///**
// * Created by yangzc on 16/6/14.
// */
//public class LatexBlock extends CYPlaceHolderBlock {
//
//    private TeXFormula mFormula;
//    protected TeXIcon mTexIcon;
//    private TeXFormula.TeXIconBuilder mTexIconBuilder;
//
//    private Graphics2DA mGraphics;
//
//    private String mLatexText = "";
//    private float mSize = 20;
//    private Color mForegroundColor = new ColorA(android.graphics.Color.BLACK);
//    private int mBackgroundColor = android.graphics.Color.TRANSPARENT;
//    private int mType = TeXFormula.SERIF;
//
//    private float mSizeScale;
//
//    public LatexBlock(String content) {
//        super(content);
//        mSizeScale = AjLatexMath.getContext().getResources().getDisplayMetrics().scaledDensity;
//        ensureTeXIconExists();
//        setLatexText(content);
//    }
//
//    public void setLatexText(String latexText) {
//        mLatexText = latexText;
//        mFormula = null;
//        mTexIconBuilder = null;
//        mTexIcon = null;
//        ensureTeXIconExists();
//        requestLayout();
//    }
//
//    private void ensureTeXIconExists() {
//        if (mFormula == null) {
//            try {
//                mFormula = new TeXFormula(mLatexText);
//            } catch (ParseException exception) {
//                mFormula = TeXFormula.getPartialTeXFormula(mLatexText);
//            }
//        }
//        if (mTexIconBuilder == null) {
//            mTexIconBuilder = mFormula.new TeXIconBuilder();
//        }
//        if (mTexIcon == null) {
//            mTexIconBuilder.setSize(mSize * mSizeScale).setStyle(TeXConstants.STYLE_DISPLAY).setType(mType);
//            mTexIcon = mTexIconBuilder.build();
//        }
//    }
//
//    @Override
//    public int getWidth() {
//        return mTexIcon.getIconWidth();
//    }
//
//    @Override
//    public int getHeight() {
//        return mTexIcon.getIconHeight();
//    }
//
//    @Override
//    public void draw(Canvas canvas) {
//        if (mTexIcon == null) {
//            return;
//        }
//        canvas.save();
//        canvas.translate(x, lineY + (lineHeight - getHeight())/2);
//
//        if (mGraphics == null) {
//            mGraphics = new Graphics2DA();
//        }
//        // draw background
//        canvas.drawColor(mBackgroundColor);
//
//        // draw latex
//        mGraphics.setCanvas(canvas);
//        mTexIcon.setForeground(mForegroundColor);
//        mTexIcon.paintIcon(null, mGraphics, 0, 0);
//
//        canvas.restore();
//    }
//}

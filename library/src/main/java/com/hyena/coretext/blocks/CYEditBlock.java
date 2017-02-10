package com.hyena.coretext.blocks;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.hyena.coretext.TextEnv;
import com.hyena.framework.utils.UIUtils;

/**
 * Created by yangzc on 16/4/12.
 */
public class CYEditBlock extends CYPlaceHolderBlock implements ICYEditable {

    private int mTabId = 0;
    private CYEditFace mEditFace;

    public CYEditBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        init();
    }

    private void init(){
        Paint paint = getTextEnv().getPaint();
        int height = (int) (Math.ceil(paint.descent() - paint.ascent()) + 0.5f);
        setWidth(UIUtils.dip2px(80));
        setHeight(height);
        setFocusable(true);
        mEditFace = createEditFace(getTextEnv(), this);
        mEditFace.setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight()
                , getPaddingBottom());
    }

    protected CYEditFace createEditFace(TextEnv textEnv, ICYEditable editable) {
        return new CYEditFace(textEnv, editable);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        mEditFace.onDraw(canvas, getBlockRect());
    }

    @Override
    public int getTabId() {
        return mTabId;
    }

    public void setTabId(int tabId) {
        this.mTabId = tabId;
    }

    @Override
    public String getText() {
        return mEditFace.getText();
    }

    @Override
    public void setText(String text) {
        mEditFace.setText(text);
    }

    @Override
    public void setFocus(boolean focus) {
        super.setFocus(focus);
        if (isFocusable()) {
            mEditFace.setFocus(focus);
        }
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        if (mEditFace != null) {
            mEditFace.setPadding(left, top, right, bottom);
        }
    }

    @Override
    public boolean hasFocus() {
        return mEditFace.hasFocus();
    }

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        if (mEditFace != null) {
            mEditFace.setEditable(focusable);
        }
    }

    @Override
    public void release() {
        super.release();
        if (mEditFace != null)
            mEditFace.release();
    }
}

package com.hyena.coretext.blocks;

import android.graphics.Canvas;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.EditableValue;

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
        setWidth(Const.DP_1 * 80);
        setHeight(getTextHeight(getTextEnv().getPaint()));
        setFocusable(true);
        mEditFace = createEditFace(getTextEnv(), this);
    }

    protected CYEditFace createEditFace(TextEnv textEnv, ICYEditable editable) {
        return new CYEditFace(textEnv, editable);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mEditFace != null)
            mEditFace.onDraw(canvas, getBlockRect(), getContentRect());
    }

    public CYEditFace getEditFace() {
        return mEditFace;
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
        EditableValue value = getTextEnv().getEditableValue(getTabId());
        return value == null ? null : value.getValue();
    }

    @Override
    public void setText(String text) {
        getTextEnv().setEditableValue(getTabId(), text);
        requestLayout();
    }

    @Override
    public void setTextColor(int color) {
        EditableValue value = getTextEnv().getEditableValue(getTabId());
        if (value == null) {
            value = new EditableValue();
            getTextEnv().setEditableValue(getTabId(), value);
        }
        value.setColor(color);
        mEditFace.setTextColor(color);
        postInvalidateThis();
    }

    public void setDefaultText(String defaultText) {
        mEditFace.setDefaultText(defaultText);
    }

    @Override
    public void setFocus(boolean focus) {
        super.setFocus(focus);
        if (isFocusable()) {
            mEditFace.setFocus(focus);
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

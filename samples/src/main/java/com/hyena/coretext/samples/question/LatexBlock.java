package com.hyena.coretext.samples.question;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextUtils;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.ICYEditableGroup;
import com.hyena.coretext.samples.latex.FillInAtom;
import com.hyena.framework.utils.UIUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import maximsblog.blogspot.com.jlatexmath.core.AjLatexMath;
import maximsblog.blogspot.com.jlatexmath.core.Box;
import maximsblog.blogspot.com.jlatexmath.core.TeXConstants;
import maximsblog.blogspot.com.jlatexmath.core.TeXFormula;
import maximsblog.blogspot.com.jlatexmath.core.TeXIcon;

/**
 * Created by yangzc on 16/6/14.
 */
public class LatexBlock extends CYPlaceHolderBlock implements ICYEditableGroup {

    private TeXFormula mTexFormula;
    private TeXIcon mTexIcon;
    private TeXFormula.TeXIconBuilder mBuilder;
    private String mLatex;

    public LatexBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        try {
            JSONObject jsonObject = new JSONObject(content);
            init(jsonObject.optString("content"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init(String latex) {
        setFocusable(true);
        mTexFormula = new TeXFormula();
        mBuilder = mTexFormula.new TeXIconBuilder()
                .setStyle(TeXConstants.STYLE_DISPLAY)
                .setSize(UIUtils.px2dip(getTextEnv().getPaint().getTextSize()))
                .setWidth(TeXConstants.UNIT_PIXEL, getTextEnv().getPageWidth(), TeXConstants.ALIGN_LEFT)
                .setIsMaxWidth(true)//非精准宽度
                .setInterLineSpacing(TeXConstants.UNIT_PIXEL, AjLatexMath.getLeading(UIUtils
                        .px2dip(getTextEnv().getPaint().getTextSize())))
                .setTag(getTextEnv());

        latex = latex.replaceAll("labelsharp", "#");
        Pattern pattern = Pattern.compile("#\\{(.*?)\\}#");
        Matcher matcher = pattern.matcher(latex);
        while (matcher.find()) {
            String data = matcher.group(1);
            try {
                JSONObject jsonFillIn = new JSONObject("{" + data + "}");
                String type = jsonFillIn.optString("type");
                if (TextUtils.equals(type, "blank")) {
                    String id = jsonFillIn.optString("id");
//                String size = jsonFillIn.optString("size");//永远express
                    String clazz = jsonFillIn.optString("class");
                    String replaceStr = "\\\\fillin{" + id + "}{" + clazz + "}{}";
                    latex = matcher.replaceAll(replaceStr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        setFormula(latex);
    }

    public void setFormula(String latex){
        this.mLatex = latex;
        mTexFormula.setLaTeX(latex);
        mTexIcon = mBuilder.build();
        requestLayout();
    }

    public String getLatex() {
        return mLatex;
    }

    @Override
    public int getContentWidth() {
        if (mTexIcon != null)
            return Math.round(mTexIcon.getTrueIconWidth());
        return super.getContentWidth();
    }

    @Override
    public int getContentHeight() {
        if (mTexIcon != null) {
            return Math.round(mTexIcon.getTrueIconHeight());
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
    public ICYEditable findEditableByTabId(int tabId) {
        if (mTexIcon == null)
            return null;
        return findEditableByTabId(mTexIcon.getBox(), tabId);
    }

    @Override
    public ICYEditable getFocusEditable(){
        return getFocusEditable(mTexIcon.getBox());
    }

    @Override
    public ICYEditable findEditable(float x, float y) {
        if (mTexIcon == null || mTexIcon.getSize() == 0)
            return null;

        x = x / mTexIcon.getSize();
        y = y / mTexIcon.getSize();
        return findEditable(mTexIcon.getBox(), x, y);
    }

    private ICYEditable findEditableByTabId(Box box, int tabId) {
        if (box != null) {
            if (box instanceof ICYEditable) {
                if (((ICYEditable) box).getTabId() == tabId) {
                    return (ICYEditable) box;
                }
            } else {
                if (box.getChildren() != null && !box.getChildren().isEmpty()) {
                    for (int i = 0; i < box.getChildren().size(); i++) {
                        Box child = box.getChildren().get(i);
                        ICYEditable result = findEditableByTabId(child, tabId);
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
    public List<ICYEditable> findAllEditable() {
        List<ICYEditable> editable = new ArrayList<ICYEditable>();
        findAllEditable(mTexIcon.getBox(), editable);
        return editable;
    }

    private void findAllEditable(Box box, List<ICYEditable> editables) {
        if (box != null) {
            if (box instanceof ICYEditable) {
                editables.add((ICYEditable) box);
            } else {
                if (box.getChildren() != null && !box.getChildren().isEmpty()) {
                    for (int i = 0; i < box.getChildren().size(); i++) {
                        Box child = box.getChildren().get(i);
                        findAllEditable(child, editables);
                    }
                }
            }
        }
    }

    private ICYEditable findEditable(Box box, float x, float y) {
        if (box != null) {
            if (box instanceof FillInAtom.FillInBox) {
                if (((FillInAtom.FillInBox) box).getVisibleRect().contains(x, y)) {
                    return (ICYEditable) box;
                }
            } else {
                if (box.getChildren() != null && !box.getChildren().isEmpty()) {
                    for (int i = 0; i < box.getChildren().size(); i++) {
                        Box child = box.getChildren().get(i);
                        ICYEditable result = findEditable(child, x, y);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        return null;
    }

    public ICYEditable getFocusEditable(Box box) {
        if (box != null) {
            if (box instanceof FillInAtom.FillInBox) {
                if (((FillInAtom.FillInBox) box).hasFocus()) {
                    return (FillInAtom.FillInBox) box;
                }
            } else {
                if (box.getChildren() != null && !box.getChildren().isEmpty()) {
                    for (int i = 0; i < box.getChildren().size(); i++) {
                        Box child = box.getChildren().get(i);
                        ICYEditable result = getFocusEditable(child);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void releaseAll(Box box) {
        if (box != null) {
            if (box instanceof FillInAtom.FillInBox) {
                ((FillInAtom.FillInBox) box).release();
            } else {
                if (box.getChildren() != null && !box.getChildren().isEmpty()) {
                    for (int i = 0; i < box.getChildren().size(); i++) {
                        Box child = box.getChildren().get(i);
                        releaseAll(child);
                    }
                }
            }
        }
    }

    @Override
    public void release() {
        super.release();
        if (mTexIcon != null)
            releaseAll(mTexIcon.getBox());
    }
}

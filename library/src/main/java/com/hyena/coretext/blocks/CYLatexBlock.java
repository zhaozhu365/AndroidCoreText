package com.hyena.coretext.blocks;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.TextUtils;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.latex.FillInBox;
import com.hyena.framework.utils.UIUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import maximsblog.blogspot.com.jlatexmath.core.AjLatexMath;
import maximsblog.blogspot.com.jlatexmath.core.Atom;
import maximsblog.blogspot.com.jlatexmath.core.Box;
import maximsblog.blogspot.com.jlatexmath.core.MacroInfo;
import maximsblog.blogspot.com.jlatexmath.core.ParseException;
import maximsblog.blogspot.com.jlatexmath.core.TeXConstants;
import maximsblog.blogspot.com.jlatexmath.core.TeXFormula;
import maximsblog.blogspot.com.jlatexmath.core.TeXIcon;
import maximsblog.blogspot.com.jlatexmath.core.TeXParser;

/**
 * Created by yangzc on 17/6/27.
 */

public class CYLatexBlock extends CYPlaceHolderBlock implements ICYEditableGroup {

    private TeXFormula mTexFormula;
    private TeXIcon mTexIcon;
    private TeXFormula.TeXIconBuilder mBuilder;

    private HashMap<String, Integer> mCommandMap = new HashMap<>();
    public CYLatexBlock(TextEnv textEnv, String content) {
        super(textEnv, content);
        //注册自定义命令
        registerCommand();
        //初始化latex自定义命令
        initCustomCommand();
    }

    /**
     * 初始化Latex
     */
    private void initCustomCommand() {
        Iterator<String> iterator = mCommandMap.keySet().iterator();
        while (iterator.hasNext()) {
            final String command = iterator.next();
            Integer argCount = mCommandMap.get(command);
            MacroInfo.Commands.put(command, new MacroInfo(argCount) {
                @Override
                public Object invoke(TeXParser tp, String[] args) throws ParseException {
                    return createAtom(command, tp, args);
                }
            });
        }
    }

    /***
     * 注册命令
     */
    public void registerCommand() {
    }

    /**
     * 添加命令
     * @param command latex命令
     * @param count 参数个数
     */
    public void addCommand(String command, int count) {
        mCommandMap.put(command, count);
    }

    /**
     * 根据命令创建Atom
     * @param command 命令
     * @param tp 解析器
     * @param args 参数
     * @return
     */
    public Atom createAtom(String command, TeXParser tp, String[] args) {
        return null;
    }

    @Override
    public void setParagraphStyle(CYParagraphStyle style) {
        super.setParagraphStyle(style);
        String content = getContent();
        if (!TextUtils.isEmpty(content)) {
            update(content);
        }
    }

    /**
     * 样式更新
     * @param latex latex
     */
    protected void update(String latex) {
        setFocusable(true);
        mTexFormula = new TeXFormula();
        float fontSize = getTextEnv().getPaint().getTextSize();
        int color = getTextEnv().getPaint().getColor();
        if (getParagraphStyle() != null) {
            fontSize = getParagraphStyle().getTextSize();
            color = getParagraphStyle().getTextColor();
        }
        fontSize = UIUtils.px2dip(fontSize);

        AjLatexMath.getPaint().setColor(color);
        mBuilder = mTexFormula.new TeXIconBuilder()
                .setStyle(TeXConstants.STYLE_DISPLAY)
                .setSize(fontSize)
                .setFGColor(color)
                .setWidth(TeXConstants.UNIT_PIXEL, getTextEnv().getPageWidth(), TeXConstants.ALIGN_LEFT)
                .setIsMaxWidth(true)//非精准宽度
                .setInterLineSpacing(TeXConstants.UNIT_PIXEL, AjLatexMath.getLeading(fontSize))
                .setTag(getTextEnv());
        setFormula(latex);
    }

    /**
     * 刷新数据
     * @param latex
     */
    public void setFormula(String latex){
        if (mTexIcon != null && mTexIcon.getBox() != null) {
            releaseAll(mTexIcon.getBox());
        }
        mTexFormula.setLaTeX(latex);
        mTexIcon = mBuilder.build();
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
            if (box instanceof FillInBox) {
                if (((FillInBox) box).getVisibleRect().contains(x, y)) {
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
            if (box instanceof ICYEditable) {
                if (((ICYEditable) box).hasFocus()) {
                    return (ICYEditable) box;
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
            if (box instanceof FillInBox) {
                ((FillInBox) box).release();
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

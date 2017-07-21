package com.hyena.coretext.blocks.latex;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.IEditFace;
import com.hyena.framework.utils.MathUtils;

import maximsblog.blogspot.com.jlatexmath.core.Atom;
import maximsblog.blogspot.com.jlatexmath.core.Box;
import maximsblog.blogspot.com.jlatexmath.core.ScaleBox;
import maximsblog.blogspot.com.jlatexmath.core.TeXEnvironment;
import maximsblog.blogspot.com.jlatexmath.core.TeXFont;
import maximsblog.blogspot.com.jlatexmath.core.Text;

/**
 * Created by yangzc on 17/6/27.
 */
public abstract class FillInAtom extends Atom {

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
        Box box = createFillInBox(env, MathUtils.valueOfInt(mIndex), mClazz, ch);
        if (smallCap && Character.isLowerCase('0')) {
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

    /**
     * 获得Latex编辑框Box
     */
    public abstract Box createFillInBox(final TeXEnvironment env, int index, String clazz, Text ch);
}

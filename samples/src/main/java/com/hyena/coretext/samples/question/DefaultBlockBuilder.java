/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.samples.question;

import android.text.TextUtils;

import com.hyena.coretext.AttributedString;
import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYTextBlock;
import com.hyena.coretext.builder.CYBlockProvider;
import com.hyena.coretext.builder.IBlockMaker;
import com.hyena.coretext.samples.App;
import com.hyena.coretext.utils.Const;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import maximsblog.blogspot.com.jlatexmath.core.AjLatexMath;
import maximsblog.blogspot.com.jlatexmath.core.DefaultTeXFont;
import maximsblog.blogspot.com.jlatexmath.core.Glue;
import maximsblog.blogspot.com.jlatexmath.core.SymbolAtom;
import maximsblog.blogspot.com.jlatexmath.core.TeXFormula;

/**
 * Created by yangzc on 17/3/3.
 */
public class DefaultBlockBuilder extends BlockMaker implements CYBlockProvider.CYBlockBuilder {

    {
        //init latex
        AjLatexMath.init(App.getAppContext());
        try {
            SymbolAtom.get("");
        } catch (Throwable e) {}
        DefaultTeXFont.getSizeFactor(1);
        try {
            Glue.get(1, 1, null);
        } catch (Throwable e) {}
        try {
            TeXFormula.get("");
        } catch (Throwable e) {}
    }

    @Override
    public List<CYBlock> build(TextEnv textEnv, String content) {
        return analysisCommand(textEnv, content).build();
    }

    @Override
    public CYTextBlock buildTextBlock(TextEnv textEnv, String content) {
        CYTextBlock textBlock =  new CYTextBlock(textEnv, content) {
            @Override
            public int getMarginLeft() {
                if (word != null && !TextUtils.isEmpty(word.pinyin)) {
                    return Const.DP_1 * 5;
                }
                return super.getMarginLeft();
            }

            @Override
            public int getMarginRight() {
                if (word != null && !TextUtils.isEmpty(word.pinyin)) {
                    return Const.DP_1 * 5;
                }
                return super.getMarginRight();
            }
        };
        return textBlock;
    }

    private AttributedString analysisCommand(TextEnv textEnv, String content) {
        AttributedString attributedString = new AttributedString(textEnv, content);
        if (!TextUtils.isEmpty(content)) {
            Pattern pattern = Pattern.compile("#\\{(.*?)\\}#");
            Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                String data = matcher.group(1);
                IBlockMaker maker = textEnv.getBlockMaker();
                if (maker == null) {
                    maker = this;
                }
                CYBlock block = maker.getBlock(textEnv, "{" + data + "}");
                if (block != null) {
                    attributedString.replaceBlock(start, end, block);
                }
            }
        }
        return attributedString;
    }

}

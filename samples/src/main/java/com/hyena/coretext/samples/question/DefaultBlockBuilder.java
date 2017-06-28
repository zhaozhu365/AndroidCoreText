/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.samples.question;

import android.text.TextUtils;

import com.hyena.coretext.AttributedString;
import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYBreakLineBlock;
import com.hyena.coretext.blocks.CYEditFace;
import com.hyena.coretext.blocks.CYParagraphEndBlock;
import com.hyena.coretext.blocks.CYTextBlock;
import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.blocks.latex.FillInAtom;
import com.hyena.coretext.blocks.latex.LatexBlock;
import com.hyena.coretext.builder.CYBlockProvider;
import com.hyena.coretext.samples.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import maximsblog.blogspot.com.jlatexmath.core.AjLatexMath;
import maximsblog.blogspot.com.jlatexmath.core.Atom;
import maximsblog.blogspot.com.jlatexmath.core.Box;
import maximsblog.blogspot.com.jlatexmath.core.DefaultTeXFont;
import maximsblog.blogspot.com.jlatexmath.core.Glue;
import maximsblog.blogspot.com.jlatexmath.core.SymbolAtom;
import maximsblog.blogspot.com.jlatexmath.core.TeXEnvironment;
import maximsblog.blogspot.com.jlatexmath.core.TeXFormula;
import maximsblog.blogspot.com.jlatexmath.core.TeXParser;
import maximsblog.blogspot.com.jlatexmath.core.Text;

/**
 * Created by yangzc on 17/3/3.
 */
public class DefaultBlockBuilder implements CYBlockProvider.CYBlockBuilder {

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
        return new CYTextBlock(textEnv, content);
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
                CYBlock block = getBlock(textEnv, "{" + data + "}");
                if (block != null) {
                    attributedString.replaceBlock(start, end, block);
                }
            }
        }
        return attributedString;
    }

    protected <T extends CYBlock> T getBlock(TextEnv textEnv, String data) {
        try {
            JSONObject json = new JSONObject(data);
            String type = json.optString("type");
            String content = json.optString("content");
            if ("latex".equals(type)) {
                data = content;
            }
            return newBlock(textEnv, type, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected <T extends CYBlock> T newBlock(TextEnv textEnv, String type, String data) {
        if ("blank".equals(type)) {
            return (T) new BlankBlock(textEnv, data);
        } else if("img".equals(type)) {
            return (T) new ImageBlock(textEnv, data);
        } else if("P".equals(type)) {
            return (T) new CYBreakLineBlock(textEnv, data);
        } else if ("para_begin".equals(type)) {
            return (T) new ParagraphStartBlock(textEnv, data);
        } else if ("para_end".equals(type)) {
            return (T) new CYParagraphEndBlock(textEnv, data);
        } else if ("audio".equals(type)) {
            return (T) new AudioBlock(textEnv, data);
        } else if ("latex".equals(type)) {
            String latex = data.replaceAll("labelsharp", "#");
            Pattern pattern = Pattern.compile("#\\{(.*?)\\}#");
            Matcher matcher = pattern.matcher(latex);
            while (matcher.find()) {
                String group = matcher.group(1);
                try {
                    JSONObject jsonFillIn = new JSONObject("{" + group + "}");
                    String fillInType = jsonFillIn.optString("type");
                    if (TextUtils.equals(fillInType, "blank")) {
                        String id = jsonFillIn.optString("id");
    //                String size = jsonFillIn.optString("size");//永远express
                        String clazz = jsonFillIn.optString("class");
                        String replaceStr = "\\\\fillin{" + id + "}{" + clazz + "}{10}";
                        latex = matcher.replaceFirst(replaceStr);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return (T) new LatexBlock(textEnv, latex) {
                @Override
                public void registerCommand() {
                    super.registerCommand();
                    addCommand("fillin", 3);
                }

                @Override
                public Atom createAtom(String command, TeXParser tp, String[] args) {
                    if ("fillin".equals(command)) {
                        return new FillInAtom(args[1], args[2], args[3]) {
                            @Override
                            public CYEditFace getEditFace(TextEnv env, ICYEditable editable) {
                                return new EditFace(env, editable);
                            }

                            @Override
                            public Box getFillInBox(TeXEnvironment env, Text ch) {
                                //重新new一个box
                                return super.getFillInBox(env, ch);
                            }
                        };
                    }
                    return super.createAtom(command, tp, args);
                }

            };
        }
        return null;
    }
}

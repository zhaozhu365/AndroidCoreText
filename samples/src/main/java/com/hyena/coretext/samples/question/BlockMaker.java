package com.hyena.coretext.samples.question;

import android.text.TextUtils;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYBreakLineBlock;
import com.hyena.coretext.blocks.CYLatexBlock;
import com.hyena.coretext.blocks.CYStyleEndBlock;
import com.hyena.coretext.blocks.CYTableBlock;
import com.hyena.coretext.blocks.CYTextBlock;
import com.hyena.coretext.blocks.IEditFace;
import com.hyena.coretext.blocks.latex.FillInAtom;
import com.hyena.coretext.blocks.latex.FillInBox;
import com.hyena.coretext.builder.IBlockMaker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import maximsblog.blogspot.com.jlatexmath.core.Atom;
import maximsblog.blogspot.com.jlatexmath.core.Box;
import maximsblog.blogspot.com.jlatexmath.core.TeXEnvironment;
import maximsblog.blogspot.com.jlatexmath.core.TeXParser;
import maximsblog.blogspot.com.jlatexmath.core.Text;

/**
 * Created by yangzc on 17/7/25.
 */

public class BlockMaker implements IBlockMaker {

    @Override
    public <T extends CYBlock> T getBlock(TextEnv textEnv, String data) {
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

    @Override
    public CYTextBlock buildTextBlock(TextEnv textEnv, String content) {
        return new CYTextBlock(textEnv, content);
    }

    protected  <T extends CYBlock> T newBlock(TextEnv textEnv, String type, String data) {
        if ("blank".equals(type)) {
            return (T) new BlankBlock(textEnv, data);
        } else if("img".equals(type)) {
            return (T) new ImageBlock(textEnv, data);
        } else if("P".equals(type)) {
            return (T) new CYBreakLineBlock(textEnv, data);
        } else if ("para_begin".equals(type)) {
            return (T) new ParagraphStartBlock(textEnv, data);
        } else if ("para_end".equals(type)) {
            return (T) new CYStyleEndBlock(textEnv, data);
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
                        String replaceStr = "\\fillin{" + id + "}{" + clazz + "}{10}";
                        latex = latex.replace("#{" + group + "}#", replaceStr);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return (T) new CYLatexBlock(textEnv, latex) {
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
                            public Box createFillInBox(final TeXEnvironment env, int index, String clazz, Text ch) {
                                return new FillInBox((TextEnv) env.getTag(), index, clazz, ch) {
                                    @Override
                                    public IEditFace createEditFace() {
                                        return new EditFace((TextEnv) env.getTag(), this);
                                    }
                                };
                            }
                        };
                    }
                    return super.createAtom(command, tp, args);
                }

            };
        } else if ("table".equals(type)) {
            return (T) new CYTableBlock(textEnv, data);
        }
        return null;
    }
}

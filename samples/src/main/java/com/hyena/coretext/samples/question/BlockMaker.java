package com.hyena.coretext.samples.question;

import android.text.TextUtils;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYBreakLineBlock;
import com.hyena.coretext.blocks.CYHorizontalAlign;
import com.hyena.coretext.blocks.CYStyle;
import com.hyena.coretext.blocks.CYStyleEndBlock;
import com.hyena.coretext.blocks.CYTableBlock;
import com.hyena.coretext.blocks.CYTextBlock;
import com.hyena.coretext.builder.IBlockMaker;
import com.hyena.coretext.utils.Const;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            return (T) new BoxListParagraphBlock(textEnv, data);
        } else if ("para_end".equals(type)) {
            return (T) new CYStyleEndBlock(textEnv, data);
        } else if ("audio".equals(type)) {
            return (T) new AudioBlock(textEnv, data);
        } else if ("latex".equals(type)) {
            String latex = data/*.replaceAll("labelsharp", "#")*/;
            Pattern pattern = Pattern.compile("\\\\#\\{(.*?)\\}\\\\#");
            Matcher matcher = pattern.matcher(latex);
            while (matcher.find()) {
                String group = matcher.group(1);
                try {
                    JSONObject jsonFillIn = new JSONObject("{" + group + "}");
                    String fillInType = jsonFillIn.optString("type");
                    if (TextUtils.equals(fillInType, "blank")) {
                        int id = jsonFillIn.optInt("id");
                        //                String size = jsonFillIn.optString("size");//永远express
                        String clazz = jsonFillIn.optString("class");
                        String replaceStr = "\\fillin{" + id + "}{" + clazz + "}{}";
                        latex = latex.replace("\\#{" + group + "}\\#", replaceStr);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return (T) new LatexBlock(textEnv, latex);
        } else if ("table".equals(type)) {
            return (T) new CYTableBlock(textEnv, data);
        }
        return null;
    }

    public class BoxListParagraphBlock extends ParagraphStartBlock {

        public BoxListParagraphBlock(TextEnv textEnv, String content) {
            super(textEnv, content);
        }

        @Override
        public CYStyle getStyle() {
            CYStyle style = super.getStyle();
            String styleText = style.getStyle();
            //数学
            if ("chinese_guide".equals(styleText)) {
                style.setTextSize(Const.DP_1 * 40);
                style.setTextColor(0xff333333);
                style.setMarginBottom(Const.DP_1 *15);
                style.setHorizontalAlign(CYHorizontalAlign.LEFT);
            } else if ("math_picture".equals(styleText)) {
                style.setMarginBottom(Const.DP_1 *15);
                style.setHorizontalAlign(CYHorizontalAlign.LEFT);
            }
            return style;
        }
    }
}

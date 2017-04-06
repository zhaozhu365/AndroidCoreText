/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.samples.question;

import android.text.TextUtils;
import android.util.Log;

import com.hyena.coretext.AttributedString;
import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYBreakLineBlock;
import com.hyena.coretext.blocks.CYParagraphEndBlock;
import com.hyena.coretext.builder.CYBlockProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yangzc on 17/3/3.
 */
public class DefaultBlockBuilder implements CYBlockProvider.CYBlockBuilder {

    @Override
    public List<CYBlock> build(TextEnv textEnv, String content) {
        return analysisCommand(textEnv, content).buildBlocks();
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
            return newBlock(textEnv, type, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected <T extends CYBlock> T newBlock(TextEnv textEnv, String type, String data) {
        if ("blank".equals(type)) {
//            return (T) new BlankBlock(textEnv, data);
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
        }
        return null;
    }
}

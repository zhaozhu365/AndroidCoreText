package com.hyena.coretext.samples;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.himamis.retex.renderer.share.TeXIcon;
import com.hyena.coretext.AttributedString;
import com.hyena.coretext.CYView;
import com.hyena.coretext.blocks.CYBreakLineBlock;
import com.hyena.coretext.blocks.CYEditBlock;
import com.hyena.coretext.layout.CYHorizontalLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yangzc on 16/6/1.
 */
public class SampleFragment2 extends Fragment {

    private CYView mCyView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mCyView = new CYView(getActivity());
        mCyView.setLayout(new CYHorizontalLayout());
        mCyView.setPadding(20, 20, 20, 20);
        return mCyView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String question = "<p><span class=\"mathquill-embedded-latex\" style=\"width: " +
                "26px; height: 42px;\">\\frac{7}{5}</span><span style=\" white-space: normal;" +
                "\">×25=(<img src=\"/images/edu_fillin.png\" class=\"img_fillin\"/>)</span></p>";
        String result = trimString(replaceFillIn(question));
        Log.v("yangzc", result);
        AttributedString attString = new AttributedString(result);
//
//        char ch[] = result.toCharArray();
//        int startIndex = 0, endIndex = 0;
//        boolean isInCommand = false;
//        for (int i = 0; i < ch.length; i++) {
//            if (ch[i] == '\\') {//command
//                startIndex = i;
//                isInCommand = true;
//            }
//            if (ch[i] >= 'a' && ch[i] <= 'z') {
//                endIndex = i;
//                if (isInCommand) {
//                    attString.replaceBlock(startIndex, endIndex, CYEditBlock.class)
//                            .setWidth(200).setHeight(100);
//                }
//                isInCommand = false;
//            }
//        }
        //replace latex

        //replace fillInBlock
        Pattern patternFillIn = Pattern.compile("\\\\fillin\\{\\}");
        Matcher matcherFillIn = patternFillIn.matcher(result);
        while (matcherFillIn.find()) {
            attString.replaceBlock(matcherFillIn.start(), matcherFillIn.end(), CYEditBlock.class)
                    .setWidth(200).setHeight(100);
        }

        //replace p
        Pattern patternP = Pattern.compile("</p>");
        Matcher matcherP = patternP.matcher(result);
        while (matcherP.find()) {
            attString.replaceBlock(matcherP.start(), matcherP.end(), CYBreakLineBlock.class);
        }
        mCyView.setBlocks(attString.buildBlocks());
    }

    private String replaceFillIn(String html) {
        Pattern pattern = Pattern.compile("<img.*?edu_fillin.*?>|<div[^>]*?edu_fillin.*?>(</div>)");
        Matcher matcher = pattern.matcher(html);
        String result = html;
        while (matcher.find()) {
            String group = matcher.group();
            result = result.replace(group, "\\fillin{}");
        }

        return result;
    }

    private String trimString(String str) {
        Pattern pattern = Pattern.compile("<[\\s\\S]*?>");
        Matcher matcher = pattern.matcher(str);
        String result = str;
        while (matcher.find()) {
            String group = matcher.group();
            if (!"</p>".equals(group)) {
                result = result.replace(group, "");
            }
        }
        return result;
    }

}

/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.samples;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.event.CYFocusEventListener;
import com.hyena.coretext.samples.question.QuestionTextView;
import com.hyena.coretext.utils.Const;
import com.hyena.coretext.utils.EditableValue;
import com.hyena.framework.clientlog.LogUtil;

/**
 * Created by yangzc on 17/2/6.
 */
public class SampleQuestionFragment extends Fragment {

    private QuestionTextView mQtvQuestion;
    private int mFocusTabId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return View.inflate(getContext(), R.layout.layout_question, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.latex_keyboard_1).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_2).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_3).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_4).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_5).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_6).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_7).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_8).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_9).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_star).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_del).setOnClickListener(mClickListener);
        view.findViewById(R.id.latex_keyboard_w).setOnClickListener(mClickListener);

        mQtvQuestion = (QuestionTextView) view.findViewById(R.id.qtv_question);
        mQtvQuestion.setFocusEventListener(new CYFocusEventListener() {
            @Override
            public void onFocusChange(boolean focus, final int tabId) {
                if (focus) {
                    LogUtil.v("yangzc", "tabId: " + tabId);
                    mFocusTabId = tabId;

                    ICYEditable editable = mQtvQuestion.findEditableByTabId(tabId);
                    LogUtil.v("yangzc", editable.getBlockRect().toString());
                }
            }

            @Override
            public void onClick(int tabId) {
                LogUtil.v("yangzc", "onClick, tabId: " + tabId);
            }
        });
        mQtvQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mQtvQuestion.clearFocus();
//                List<ICYEditable> list = mQtvQuestion.getEditableList();
//                ToastUtils.showToast(getContext(), "onClick!!! , editable size: " + list.size());
//                mQtvQuestion.setText(2, "p");
//                mQtvQuestion.setFocus(2);
            }
        });
        String question = "" +
                "#{\"type\":\"para_begin\",\"style\":\"math_text\"}##{\"type\":\"latex\",\"content\":\"\\\\frac{2}{5}\"}#+(#{\"type\":\"latex\",\"content\":\"\\\\frac{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":1,\\\"size\\\":\\\"express\\\",\\\"class\\\":\\\"fillin\\\"}\\\\#}{\\\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":2,\\\"size\\\":\\\"express\\\",\\\"class\\\":\\\"fillin\\\"}\\\\#}\"}#)=#{\"type\":\"latex\",\"content\":\"\\\\frac{3}{5}\"}##{\"type\":\"para_end\"}#"
                +
//                "#{\"type\":\"para_begin\",\"style\":\"math_text\"}#以下各数中，循环小数有(　　)个．#{\"type\":\"P\"}#(1)0.484484…(2)0.38485…(3)0.44…(4)3.12121…#{\"type\":\"para_end\"}#" +
//                "#{\"type\":\"para_begin\",\"size\" : 100,\"align\": \"left\",\"color\":\"#D0D0D0\",\"margin\":8}#" +
//                "根据录音选择正确的翻译" +
//                "#{\"type\":\"para_end\"}#" +
//                "#{\"type\":\"latex\",\"content\":\"14+(0.6+2)^2+22\"}#" +
//                "#{\"type\":\"latex\",\"content\":\"\\\\frac{7}{5}\"}#" +
//                "#{\"type\":\"table\",\"content\":\"\\\\frac{7}{5}\"}#" +

//                "#{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"mid\",\"color\":\"#000000\",\"margin\":8}#" +
//                "#{\"type\":\"audio\",\"src\":\"http://7xohdn.com2.z0.glb.qiniucdn.com/tingli/15590285.mp3\"}#" +
//                "\r\n#{\"type\":\"blank\",\"id\": 1,\"size\":\"express\"}#\r\n" +
//                "#{\"type\":\"para_end\"}#" +
//
//                "#{\"type\":\"latex\",\"content\":\"\\\\frac{8}{3+\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":\\\"2\\\",\\\"size\\\":\\\"express\\\",\\\"class\\\":\\\"fillin\\\"}\\#}\"}#=2" +
//                "#{\\\"type\\\":\\\"latex\\\",\\\"content\\\":\\\"\\\\\\\\frac{7}{5}\\\"}#" +
//
//                "2" +
//                "#{\\\"type\\\":\\\"para_begin\\\",\\\"style\\\":\\\"chinese_guide\\\"}#选择性背诵题#{\\\"type\\\":\\\"para_end\\\"}#\\n#{\\\"type\\\":\\\"para_begin\\\",\\\"style\\\":\\\"chinese_guide\\\"}#20 我是什么-第4、5自然段#{\\\"type\\\":\\\"para_end\\\"}##{\\\"type\\\":\\\"para_begin\\\",\\\"style\\\":\\\"chinese_recite_pinyin\\\"}#有(!yǒu!)时(!shí!)候(!hòu!)我(!wǒ!)很(!hěn!)温(!wēn!)和(!hé!)，有(!yǒu!)时(!shí!)候(!hòu!)我(!wǒ!)很(!hěn!)暴(!bào!)躁(!zào!)。我(!wǒ!)做(!zuò!)过(!guò!)许(!xǔ!)多(!duō!)好(!hǎo!)事(!shì!)，灌(!guàn!)溉(!gài!)田(!tián!)地(!dì!)，发(!fā!)动(!dòng!)机(!jī!)器(!qì!)，帮(!bāng!)助(!zhù!)人(!rén!)们(!men!)工(!gōng!)作(!zuò!)。我(!wǒ!)也(!yě!)做(!zuò!)过(!guò!)许(!xǔ!)多(!duō!)坏(!huài!)事(!shì!)，淹(!yān!)没(!mò!)庄(!zhuāng!)稼(!jià!)，冲(!chōng!)毁(!huǐ!)房(!fáng!)屋(!wū!)，给(!gěi!)人(!rén!)们(!men!)带(!dài!)来(!lái!)灾(!zāi!)害(!hài!)。人(!rén!)们(!men!)想(!xiǎng!)出(!chū!)种(!zhǒng!)种(!zhǒng!)办(!bàn!)法(!fǎ!)管(!guǎn!)住(!zhù!)我(!wǒ!)，让(!ràng!)我(!wǒ!)光(!guāng!)做(!zuò!)好(!hǎo!)事(!shì!)，不(!bù!)做(!zuò!)坏(!huài!)事(!shì!)。\\n小(!xiǎo!)朋(!péng!)友(!yǒu!)，你(!nǐ!)们(!men!)猜(!cāi!)猜(!cāi!)我(!wǒ!)是(!shì!)什(!shén!)么(!me!)。#{\\\"type\\\":\\\"para_end\\\"}#" +
                "";
        mQtvQuestion.getBuilder(question).setEditable(true).setDebug(true).setEditableValue(1, new EditableValue(Color.RED, "12", true)).setFontSize(20 * Const.DP_1).build();//.replaceAll("\\\\", "")
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v != null && v instanceof TextView) {
                TextView textView = (TextView) v;
                if (mFocusTabId >= 0) {
                    ICYEditable editable = mQtvQuestion.findEditableByTabId(mFocusTabId);
                    if (editable != null) {
                        String currentText = mQtvQuestion.getText(mFocusTabId);
                        if (currentText == null)
                            currentText = "";
                        String text = textView.getText().toString();
                        if ("删除".equals(text)) {
                            if (TextUtils.isEmpty(currentText))
                                return;

                            editable.setText(currentText.substring(0, currentText.length() - 1));
                        } else {
                            editable.setText(currentText + text);
                        }
                    }
                }
            }
        }
    };
}

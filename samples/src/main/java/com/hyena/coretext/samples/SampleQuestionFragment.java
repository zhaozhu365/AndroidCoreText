/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.samples;

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
            public void onFocusChange(boolean focus, int tabId) {
                if (focus) {
                    LogUtil.v("yangzc", "tabId: " + tabId);
                    mFocusTabId = tabId;
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
                "#{\"type\":\"para_begin\",\"size\" : 100,\"align\": \"left\",\"color\":\"#D0D0D0\",\"margin\":8}#" +
                "根据录音选择正确的翻译" +
                "#{\"type\":\"para_end\"}#" +
                "#{\"type\":\"latex\",\"content\":\"14+(0.6+2)^2+2+22+22+2\"}#" +


//                "#{\"type\":\"latex\",\"content\":\"\\\\frac{7}{5}\"}#" +
//                "#{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"mid\",\"color\":\"#000000\",\"margin\":8}#" +
//                "#{\"type\":\"audio\",\"src\":\"http://7xohdn.com2.z0.glb.qiniucdn.com/tingli/15590285.mp3\"}#" +
//                "\r\n#{\"type\":\"blank\",\"id\": 1,\"size\":\"express\"}#\r\n#{\"type\":\"para_end\"}#" +
//
//                "#{\"type\":\"latex\",\"content\":\"\\\\frac{8}{3+\\#{\\\"type\\\":\\\"blank\\\",\\\"id\\\":\\\"2\\\",\\\"size\\\":\\\"express\\\",\\\"class\\\":\\\"fillin\\\"}\\#}\"}#=2" +
//                "#{\\\"type\\\":\\\"latex\\\",\\\"content\\\":\\\"\\\\\\\\frac{7}{5}\\\"}#" +
//
//                "2" +
//                "#{\"type\":\"table\",\"content\":\"\\\\frac{7}{5}\"}#" +
                "";
        mQtvQuestion.getBuilder(question).setDebug(true).setFontSize(38 * Const.DP_1).build();//.replaceAll("\\\\", "")
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

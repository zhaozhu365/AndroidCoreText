/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.samples;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyena.coretext.samples.question.QuestionTextView;

/**
 * Created by yangzc on 17/2/6.
 */
public class SampleQuestionFragment extends Fragment {

    private QuestionTextView mQtvQuestion;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return View.inflate(getContext(), R.layout.layout_question, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mQtvQuestion = (QuestionTextView) view.findViewById(R.id.qtv_question);
        mQtvQuestion.setTextSize(30);
        String question = "根据图片意思选择相符的句子#{\"type\":\"blank\",\"id\":1}##{\"type\":\"img\",\"id\"=1,\"size\":\"big_image\",\"src\":\"http://img1.3lian.com/2015/w8/28/d/66.jpg\"}#";
        mQtvQuestion.setText(question);
    }
}

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
 * Created by yangzc on 17/4/5.
 */
public class TestFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return View.inflate(getContext(), R.layout.sss, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        QuestionTextView qtv = (QuestionTextView) view.findViewById(R.id.question_content);
        qtv.getBuilder().setText("哈哈哈中国人哈哈哈中国人哈哈哈中国人哈哈哈中国人哈哈哈中国人").build();

        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.choice_content);
        View child = viewGroup.getChildAt(0);
        child.setVisibility(View.VISIBLE);
        QuestionTextView questionTextView = (QuestionTextView) child.findViewById(R.id.choice_detail);
        questionTextView.getBuilder().setText("空间或时间距离短的（跟“远”相对）：").build();
    }
}

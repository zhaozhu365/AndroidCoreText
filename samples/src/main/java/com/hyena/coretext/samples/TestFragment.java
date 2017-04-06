/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.samples;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyena.coretext.samples.question.QuestionTextView;
import com.hyena.coretext.utils.EditableValue;

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
        qtv.getTextEnv().setEditableValue(1, new EditableValue(Color.RED, "a"));
        qtv.getTextEnv().setEditableValue(2, new EditableValue(Color.BLUE, "a"));
        qtv.getTextEnv().setEditableValue(3, new EditableValue(Color.GRAY, "a"));
        qtv.getTextEnv().setEditableValue(4, new EditableValue(Color.GREEN, "a"));

        String question = "#{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"left\",\"color\":\"#D0D0D0\",\"margin\":8}#根据读音（音频）提示补全单词#{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"mid\",\"color\":\"#000000\",\"margin\":8}##{\"type\":\"audio\",\"src\":\"http://7xohdn.com2.z0.glb.qiniucdn.com/sseng/clean.mp3\"}##{\"type\":\"para_end\"}##{\"type\":\"para_begin\",\"size\" : 30,\"align\": \"left\",\"color\":\"#000000\",\"margin\":8}##{\"type\":\"blank\",\"id\": 1,\"size\":\"letter\"}##{\"type\":\"blank\",\"id\": 2,\"size\":\"letter\"}##{\"type\":\"blank\",\"id\": 3,\"size\":\"letter\"}#a#{\"type\":\"blank\",\"id\": 4,\"size\":\"letter\"}##{\"type\":\"para_end\"}#";
        qtv.getBuilder().setText(question).build();

//        ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.choice_content);
//        View child = viewGroup.getChildAt(0);
//        child.setVisibility(View.VISIBLE);
//        QuestionTextView questionTextView = (QuestionTextView) child.findViewById(R.id.choice_detail);
//        questionTextView.getBuilder().setText("空间或时间距离短的（跟“远”相对）：").build();
    }
}

/*
 * Copyright (C) 2017 The AndroidCoreText Project
 */

package com.hyena.coretext.samples;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hyena.coretext.blocks.ICYEditable;
import com.hyena.coretext.samples.question.QuestionTextView;
import com.hyena.framework.app.adapter.SingleTypeAdapter;

/**
 * Created by yangzc on 17/2/14.
 */
public class QuestionListFragment extends Fragment {

    private ListView mListView;
    private QuestionAdapter mQuestionAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return View.inflate(getContext(), R.layout.layout_question_list, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView) view.findViewById(R.id.lv_question_list);
        mQuestionAdapter = new QuestionAdapter(getContext());
        mListView.setAdapter(mQuestionAdapter);
    }



    class QuestionAdapter extends SingleTypeAdapter<String> {

        public QuestionAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.layout_question_list_item, null);
                viewHolder = new ViewHolder();
                convertView.setTag(viewHolder);

                viewHolder.mQtvQuestion = (QuestionTextView) convertView.findViewById(R.id.qtv_question);
                viewHolder.mQtvQuestion.setEditable(false);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String question = "第" + position + "题：根据图片意思选择相符的句子#{\"type\":\"blank\",\"id\":1}#啊啊啊啊啊";
            viewHolder.mQtvQuestion.setText(question);
            ICYEditable editable = viewHolder.mQtvQuestion.findEditableByTabId(1);
            if (editable != null) {
                editable.setText("第" + position + "题答案");
                editable.setTextColor(Color.RED);
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return 100;
        }

        class ViewHolder {
            QuestionTextView mQtvQuestion;
        }
    }
}

package com.hyena.coretext.samples;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.hyena.coretext.samples.question.QuestionTextView;
import com.hyena.framework.utils.ToastUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by yangzc on 17/5/15.
 */

public class PreviewQuestionFragment extends Fragment {

    private QuestionTextView mPreviewQuestion;
    private Button mBtnFetch;

    private boolean isRefreshing = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            doFetch();

            if (isRefreshing) {
                mHandler.sendEmptyMessageDelayed(0, 5000);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return View.inflate(getContext(), R.layout.layout_preview, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mPreviewQuestion = (QuestionTextView) view.findViewById(R.id.qtv_preview);
        this.mBtnFetch = (Button) view.findViewById(R.id.btn_fetch);
        mBtnFetch.setOnClickListener(mClickListener);

        startRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopRefresh();
    }

    private void startRefresh() {
        isRefreshing = true;
        mHandler.sendEmptyMessage(0);
    }

    private void stopRefresh() {
        isRefreshing = false;
        mHandler.removeMessages(0);
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            doFetch();
        }
    };

    private void doFetch() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mBtnFetch.setEnabled(false);
                mBtnFetch.setText("获取数据中...");
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    URL url = new URL("http://192.168.10.97:8080/testWeb/fetch-question?ts=" + System.currentTimeMillis());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    if (connection.getResponseCode() == 200) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        InputStream is = connection.getInputStream();
                        byte buf[] = new byte[1024];
                        int len;
                        while ((len = is.read(buf, 0, 1024)) != -1) {
                            baos.write(buf, 0, len);
                        }
                        String result = baos.toString("utf-8");
                        return result;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (TextUtils.isEmpty(s)) {
                    ToastUtils.showShortToast(getContext(), "获取数据失败!!!");
                } else {
                    setText(s);
                }
                mBtnFetch.setEnabled(true);
                mBtnFetch.setText("获取题目");
            }
        }.execute();
    }

    private String mLastText;
    private void setText(String text) {
        if (!TextUtils.isEmpty(text) && !text.equals(mLastText)) {
            this.mLastText = text;
            mPreviewQuestion.getBuilder().setTextSize(18)
                    .setText(text).build();
        }
    }
}

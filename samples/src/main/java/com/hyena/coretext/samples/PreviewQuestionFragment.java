package com.hyena.coretext.samples;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.hyena.coretext.samples.question.QuestionTextView;
import com.hyena.framework.utils.ToastUtils;
import com.hyena.framework.utils.UiThreadHandler;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by yangzc on 17/5/15.
 */

public class PreviewQuestionFragment extends Fragment {

    private QuestionTextView mPreviewQuestion;
    private Button mBtnFetch;
    private TextView mStatus;

    private WebSocketClient mWebSocket;

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
        this.mStatus = (TextView) view.findViewById(R.id.status);
        mBtnFetch.setOnClickListener(mClickListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            try {
                if (mWebSocket != null && !mWebSocket.isClosed()) {
                    close();
                } else {
                    mStatus.setText("连接中...");
                    mBtnFetch.setText("连接");
                    mBtnFetch.setEnabled(false);
                    connect();
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    };

    private void onConnected() {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                mStatus.setText("已连接");
                mBtnFetch.setText("断开连接");
                mBtnFetch.setEnabled(true);
            }
        });
    }

    private void onDisconnected() {
        UiThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                mStatus.setText("断开连接");
                mBtnFetch.setText("连接");
                mBtnFetch.setEnabled(true);
                ToastUtils.showShortToast(getContext(), "已经断开连接!!!");
            }
        });
    }

    private void connect() throws URISyntaxException {
        if (mWebSocket != null && !mWebSocket.isClosed()) {
            onConnected();
            return;
        }
        mWebSocket = new WebSocketClient(new URI("ws://192.168.30.78:8080/testWeb/fetch-question-socket")) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.v("WebSocket", "onOpen");
                onConnected();
            }

            @Override
            public void onMessage(final String s) {
                Log.v("WebSocket", "msg: " + s);
                UiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setText(s);
                    }
                });
            }

            @Override
            public void onClose(int status, String s, boolean b) {
                Log.v("WebSocket", "Connection closed by " + ( b ? "remote peer" : "us" ) + ", info=" + s);
                onDisconnected();
            }

            @Override
            public void onError(Exception e) {
                Log.v("WebSocket", "onError");
                e.printStackTrace();
                onDisconnected();
            }
        };

        new Thread(){
            @Override
            public void run() {
                mWebSocket.connect();
            }
        }.start();
    }

    public void close() {
        try {
            if (mWebSocket != null) {
                mWebSocket.close();
            }
        } catch (Exception e) {}
        finally {
            mWebSocket = null;
        }
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

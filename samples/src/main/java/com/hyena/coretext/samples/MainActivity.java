package com.hyena.coretext.samples;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

import com.hyena.framework.servcie.IServiceManager;
import com.hyena.framework.servcie.ServiceProvider;

/**
 * Created by yangzc on 16/3/19.
 */
public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Debug.startMethodTracing();
        setContentView(R.layout.activity_main);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.main_container, new QuestionListFragment1());
        transaction.replace(R.id.main_container, new SampleQuestionFragment());
//        transaction.replace(R.id.main_container, new TestFragment());
//        transaction.replace(R.id.main_container, new PreviewQuestionFragment());
        transaction.commit();
    }

    @Override
    public Object getSystemService(String name) {
        IServiceManager manager = ServiceProvider.getServiceProvider()
                .getServiceManager();
        if (manager != null) {
            Object service = manager.getService(name);
            if (service != null)
                return service;
        }
        return super.getSystemService(name);
    }
}

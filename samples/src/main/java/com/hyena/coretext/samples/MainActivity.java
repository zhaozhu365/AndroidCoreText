package com.hyena.coretext.samples;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.hyena.coretext.samples.latex.FillInAtom;
import com.hyena.framework.servcie.IServiceManager;
import com.hyena.framework.servcie.ServiceProvider;

import maximsblog.blogspot.com.jlatexmath.core.MacroInfo;
import maximsblog.blogspot.com.jlatexmath.core.ParseException;
import maximsblog.blogspot.com.jlatexmath.core.TeXParser;

/**
 * Created by yangzc on 16/3/19.
 */
public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MacroInfo.Commands.put("fillin", new MacroInfo(2) {
            @Override
            public Object invoke(TeXParser tp, String[] args) throws ParseException {
                //return custom atom
                return new FillInAtom(args[1], args[2]);
            }
        });
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, new SampleQuestionFragment());
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

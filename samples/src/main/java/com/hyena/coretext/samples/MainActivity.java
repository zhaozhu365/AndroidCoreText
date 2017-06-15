package com.hyena.coretext.samples;

import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.hyena.coretext.samples.latex.FillInAtom;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.servcie.IServiceManager;
import com.hyena.framework.servcie.ServiceProvider;

import org.json.JSONException;
import org.json.JSONObject;

import maximsblog.blogspot.com.jlatexmath.core.DefaultTeXFont;
import maximsblog.blogspot.com.jlatexmath.core.Glue;
import maximsblog.blogspot.com.jlatexmath.core.MacroInfo;
import maximsblog.blogspot.com.jlatexmath.core.ParseException;
import maximsblog.blogspot.com.jlatexmath.core.SymbolAtom;
import maximsblog.blogspot.com.jlatexmath.core.TeXFormula;
import maximsblog.blogspot.com.jlatexmath.core.TeXParser;

/**
 * Created by yangzc on 16/3/19.
 */
public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Debug.startMethodTracing();
        setContentView(R.layout.activity_main);
        MacroInfo.Commands.put("fillin", new MacroInfo(3) {
            @Override
            public Object invoke(TeXParser tp, String[] args) throws ParseException {
                //return custom atom
                return new FillInAtom(args[1], args[2], args[3]);
            }
        });
        try {
            SymbolAtom.get("");
        } catch (Throwable e) {}
        DefaultTeXFont.getSizeFactor(1);
        try {
            Glue.get(1, 1, null);
        } catch (Throwable e) {}
        try {
            TeXFormula.get("");
        } catch (Throwable e) {}


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.main_container, new QuestionListFragment());
        transaction.replace(R.id.main_container, new SampleQuestionFragment());
//        transaction.replace(R.id.main_container, new TestFragment());
//        transaction.replace(R.id.main_container, new PreviewQuestionFragment());
        transaction.commit();

        String value = "#{\"type\":\"para_begin\",\"style\":\"math_text\"}#" +
                "#{\"type\":\"latex\",\"content\":\"\\\\frac{8}{3+@@@\"}#=2#{\"type\":\"para_end\"}#";

        JSONObject json = new JSONObject();
        try {
            json.put("type", "blank");
            json.put("id", "1");
            json.put("size", "express");
            json.put("class", "fillin");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("yangzc", value.replace("@@@", json.toString().replace("\"", "\\\"")));
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

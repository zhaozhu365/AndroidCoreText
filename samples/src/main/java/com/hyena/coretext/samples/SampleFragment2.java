//package com.hyena.coretext.samples;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.himamis.retex.renderer.android.FactoryProviderAndroid;
//import com.himamis.retex.renderer.share.MacroInfo;
//import com.himamis.retex.renderer.share.TeXFormula;
//import com.himamis.retex.renderer.share.TeXIcon;
//import com.himamis.retex.renderer.share.platform.FactoryProvider;
//import com.hyena.coretext.AttributedString;
//import com.hyena.coretext.CYView;
//import com.hyena.coretext.blocks.CYBreakLineBlock;
//import com.hyena.coretext.blocks.CYEditBlock;
//import com.hyena.coretext.layout.CYHorizontalLayout;
//import com.hyena.coretext.samples.blocks.LatexBlock;
//import com.hyena.fillin.utils.PluginInstaller;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Stack;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * Created by yangzc on 16/6/1.
// */
//public class SampleFragment2 extends Fragment {
//
//    private CYView mCyView;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        PluginInstaller.install(getContext());
//        FactoryProvider.INSTANCE = new FactoryProviderAndroid(getActivity().getAssets());
//        new TeXFormula();
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        mCyView = new CYView(getActivity());
//        mCyView.setLayout(new CYHorizontalLayout());
//        mCyView.setPadding(20, 20, 20, 20);
//        return mCyView;
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        String question = "<p><span class=\"mathquill-embedded-latex\" style=\"width: " +
//                "26px; height: 42px;\">\\frac{7}{5}</span><span style=\" white-space: normal;" +
//                "\">×25=(<img src=\"/images/edu_fillin.png\" class=\"img_fillin\"/>)</span></p>";
//        String result = trimString(replaceFillIn(question));
//        Log.v("yangzc", result);
//        List<Integer[]> pointArray = parseFormula(result);
//        AttributedString attString = new AttributedString(result);
//        for (int i = 0; i < pointArray.size(); i++) {
//            System.out.println("formula:" + result.substring(pointArray.get(i)[0],
//                    pointArray.get(i)[1]));
//            attString.replaceBlock(pointArray.get(i)[0], pointArray.get(i)[1], LatexBlock.class);
//        }
//
////        //replace fillInBlock
////        Pattern patternFillIn = Pattern.compile("\\\\fillin\\{\\}");
////        Matcher matcherFillIn = patternFillIn.matcher(result);
////        while (matcherFillIn.find()) {
////            attString.replaceBlock(matcherFillIn.start(), matcherFillIn.end(), CYEditBlock.class)
////                    .setWidth(200).setHeight(100);
////        }
////
////        //replace p
////        Pattern patternP = Pattern.compile("</p>");
////        Matcher matcherP = patternP.matcher(result);
////        while (matcherP.find()) {
////            attString.replaceBlock(matcherP.start(), matcherP.end(), CYBreakLineBlock.class);
////        }
//        mCyView.setBlocks(attString.buildBlocks());
//    }
//
//    private String replaceFillIn(String html) {
//        Pattern pattern = Pattern.compile("<img.*?edu_fillin.*?>|<div[^>]*?edu_fillin.*?>(</div>)");
//        Matcher matcher = pattern.matcher(html);
//        String result = html;
//        while (matcher.find()) {
//            String group = matcher.group();
//            result = result.replace(group, "\\fillIn{0}{123}");
//        }
//
//        return result;
//    }
//
//    private String trimString(String str) {
//        Pattern pattern = Pattern.compile("<[\\s\\S]*?>");
//        Matcher matcher = pattern.matcher(str);
//        String result = str;
//        while (matcher.find()) {
//            String group = matcher.group();
//            if (!"</p>".equals(group)) {
//                result = result.replace(group, "");
//            }
//        }
//        return result;
//    }
//
//    private List<Integer[]> parseFormula(String data){
//        int startIndex = -1;
//        List<Integer[]> result = new ArrayList<Integer[]>();
//        for (int i = 0; i < data.length(); i++) {
//            char ch = data.charAt(i);
//            if (ch == '\\') {
//                startIndex = i;
//            }
//            if (ch == '{' && startIndex >= 0) {
//                String command = data.substring(startIndex + 1, i);
//                MacroInfo macroInfo = MacroInfo.Commands.get(command);
//                if (macroInfo != null && macroInfo.nbArgs > 0) {
//                    //公式
//                    for (int j = 0; j < macroInfo.nbArgs; j++) {
//                        int endIndex = getEndIndex(data, i);
//                        if (endIndex > 0) {
//                            i = endIndex;
//                        }
//                        i ++;
//                    }
//                    i--;
//                    result.add(new Integer[]{startIndex, i + 1});
//                }
//            }
//        }
//        return result;
//    }
//
//    private int getEndIndex(String data, int startIndex){
//        int endIndex = startIndex;
//        char first = data.charAt(startIndex);
//        if (first != '{') {
//            return -1;
//        }
//
//        Stack<Character> stack = new Stack<Character>();
//        while (endIndex < data.length()) {
//            char c = data.charAt(endIndex);
//            if (c == '}') {
//                while (!stack.isEmpty()) {
//                    c = stack.pop();
//                    if (c == '{') {
//                        break;
//                    }
//                }
//                if (stack.isEmpty()) {
//                    return endIndex;
//                }
//            } else {
//                stack.push(c);
//            }
//            endIndex ++;
//        }
//        return endIndex;
//    }
//}

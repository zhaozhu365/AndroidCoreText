package com.hyena.coretext.samples;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyena.coretext.AttributedString;
import com.hyena.coretext.CYPageView;
import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYImageBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.coretext.blocks.CYTextBlock;
import com.hyena.coretext.layout.CYHorizontalLayout;
import com.hyena.coretext.layout.CYLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 16/6/1.
 */
public class SampleRichTextFragment extends Fragment {

    private CYPageView mCyPvPageView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int width = getResources().getDisplayMetrics().widthPixels;
        TextEnv textEnv = new TextEnv.Builder(getContext()).setTextColor(Color.BLACK).setFontSize(60)
                .setPageWidth(width - 40).setPageHeight(Integer.MAX_VALUE).setEditable(true).build();

        List<CYBlock> blocks = new ArrayList<CYBlock>();
        //=================================================================================================================
//        blocks.add(new CYTextBlock(textEnv, "这").setTextSize(50).setTextColor(Color.RED));
//        blocks.add(new CYImageBlock(textEnv, "").setResId(getContext(), R.drawable.car).setAlignStyle(CYPlaceHolderBlock.AlignStyle.Style_Round));
//        blocks.add(new CYTextBlock(textEnv, "是一个最好的时代，这是一个最坏的时代；这是一个智慧的年代，这是一个愚蠢的年代；" +
//                "这是一个光明的季节，这是一个黑暗的季节；这是希望之春，这是失望之冬；人们面前应有尽有，人们面前一无所有；人们正踏向天堂之路，人们正走向地狱之门。").setTextSize(30));
//        blocks.add(new CYImageBlock(textEnv, "").setResId(getContext(), R.drawable.car).setAlignStyle(CYPlaceHolderBlock.AlignStyle.Style_MONOPOLY));
//        blocks.add(new CYTextBlock(textEnv, "这是一个最好的时代，这是一个最坏的时代；这是一个智慧的年代，这是一个愚蠢的年代；" +
//                "这是一个光明的季节，这是一个黑暗的季节；这是希望之春，这是失望之冬；人们面前应有尽有，人们面前一无所有；人们正踏向天堂之路，人们正走向地狱之门。").setTextSize(30));
//        blocks.add(new CYBreakLineBlock(textEnv, ""));
//        blocks.add(new CYImageBlock(textEnv, "").setResId(getContext(), R.drawable.baidu).setAlignStyle(CYPlaceHolderBlock.AlignStyle.Style_Round));
//        blocks.add(new CYBreakLineBlock(textEnv, ""));
//        blocks.add(new CYEditBlock(textEnv, "").setWidth(200).setHeight(60));
//        blocks.add(new CYTextBlock(textEnv, "是一个最好的时代，这是一个最坏的时代；这是一个智慧的年代，这是一个愚蠢的年代；" +
//                "这是一个光明的季节，这是一个黑暗的季节；这是希望之春，这是失望之冬；人们面前应有尽有，人们面前一无所有；人们正踏向天堂之路，人们正走向地狱之门。").setTextSize(30));
//
//        blocks.add(new CYTextBlock(textEnv, "是一个最好的时代，这是一个最坏的时代；这是一个智慧的年代，这是一个愚蠢的年代；" +
//                "这是一个光明的季节，这是一个黑暗的季节；这是希望之春，这是失望之冬；人们面前应有尽有，人们面前一无所有；人们正踏向天堂之路，人们正走向地狱之门。").setTextSize(40));
        //==================================================================================================================
        AttributedString string = new AttributedString(textEnv, "这是一个最好的时代，这是一个最坏的时代；这是一个智慧的年代，这是一个愚蠢的年代；" +
                "这是一个光明的季节，这是一个黑暗的季节；这是希望之春，这是失望之冬；人们面前应有尽有，人们面前一无所有；人们正踏向天堂之路，人们正走向地狱之门。");

        string.replaceBlock(0, 1, CYTextBlock.class).setTextSize(100).setTextColor(Color.RED)
                .setTypeFace(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC));

        string.replaceBlock(30, 31, CYImageBlock.class).setResId(getContext(), R.drawable.baidu)
                .setAlignStyle(CYPlaceHolderBlock.AlignStyle.Style_Round);

        blocks = string.buildBlocks();
        //=================================================================================================================

        View view = View.inflate(getActivity(), R.layout.content_main, null);
        mCyPvPageView = (CYPageView) view.findViewById(R.id.page_view);


        CYLayout layout = new CYHorizontalLayout();
        List<CYPageBlock> pages = layout.parsePage(textEnv, blocks);
        if (pages != null && pages.size() > 0) {
            CYPageBlock pageBlock = pages.get(0);
            pageBlock.setPadding(20, 20, 20, 20);
            mCyPvPageView.setPageBlock(pageBlock);
        }
        return view;
    }

}

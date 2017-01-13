package com.hyena.coretext.samples;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyena.coretext.CYView;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYBreakLineBlock;
import com.hyena.coretext.blocks.CYEditBlock;
import com.hyena.coretext.blocks.CYImageBlock;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.coretext.blocks.CYTextBlock;
import com.hyena.coretext.layout.CYHorizontalLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 16/6/1.
 */
public class SampleFragment1 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        CYView view = new CYView(getActivity());

        List<CYBlock> blocks = new ArrayList<CYBlock>();
        blocks.add(new CYImageBlock("").setResId(getContext(), R.drawable.car).setAlignStyle(CYPlaceHolderBlock.AlignStyle.Style_Round));
        blocks.add(new CYTextBlock("这").setTextSize(50).setTextColor(Color.RED));
        blocks.add(new CYImageBlock("").setResId(getContext(), R.drawable.car).setAlignStyle(CYPlaceHolderBlock.AlignStyle.Style_Round));
        blocks.add(new CYTextBlock("是一个最好的时代，这是一个最坏的时代；这是一个智慧的年代，这是一个愚蠢的年代；" +
                "这是一个光明的季节，这是一个黑暗的季节；这是希望之春，这是失望之冬；人们面前应有尽有，人们面前一无所有；人们正踏向天堂之路，人们正走向地狱之门。").setTextSize(30));
        blocks.add(new CYTextBlock("这是一个最好的时代，这是一个最坏的时代；这是一个智慧的年代，这是一个愚蠢的年代；" +
                "这是一个光明的季节，这是一个黑暗的季节；这是希望之春，这是失望之冬；人们面前应有尽有，人们面前一无所有；人们正踏向天堂之路，人们正走向地狱之门。").setTextSize(30));
        blocks.add(new CYBreakLineBlock(""));
        blocks.add(new CYImageBlock("").setResId(getContext(), R.drawable.baidu).setAlignStyle(CYPlaceHolderBlock.AlignStyle.Style_Round));
        blocks.add(new CYBreakLineBlock(""));
        blocks.add(new CYEditBlock("").setWidth(200).setHeight(60));

//        AttributedString string = new AttributedString("这是一个最好的时代，这是一个最坏的时代；这是一个智慧的年代，这是一个愚蠢的年代；" +
//                "这是一个光明的季节，这是一个黑暗的季节；这是希望之春，这是失望之冬；人们面前应有尽有，人们面前一无所有；人们正踏向天堂之路，人们正走向地狱之门。");
//        string.replaceBlock(0, 1, CYTextBlock.class).setTextSize(50).setTextColor(Color.RED)
//                .setTypeFace(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC));
//
//        string.replaceBlock(30, 31, CYImageBlock.class).setResId(this, R.drawable.baidu)
//                .setAlignStyle(CYPlaceHolderBlock.AlignStyle.Style_Round);

        view.setLayout(new CYHorizontalLayout());
        view.setBlocks(blocks);
//        view.setBlocks(string.buildBlocks());

        view.setPadding(20, 20, 20, 20);
        return view;
    }
}

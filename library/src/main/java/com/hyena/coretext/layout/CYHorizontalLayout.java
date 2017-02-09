package com.hyena.coretext.layout;

import android.graphics.Rect;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYBreakLineBlock;
import com.hyena.coretext.blocks.CYLineBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.coretext.blocks.CYTextBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 16/4/8.
 */
public class CYHorizontalLayout implements CYLayout {

    @Override
    public List<CYPageBlock> parsePage(TextEnv textEnv, List<CYBlock> blocks) {
        List<CYLineBlock> lines = parseLines(textEnv, resetBlocks(blocks), textEnv.getPageWidth());
        List<CYPageBlock> pages = new ArrayList<CYPageBlock>();
        CYPageBlock page = new CYPageBlock(textEnv);
        int y = 0;
        if (lines != null) {
            for (int i = 0; i < lines.size(); i++) {
                CYLineBlock line = lines.get(i);
                if (line.getChildren() == null || line.getChildren().isEmpty())
                    continue;

                int maxBlockHeight = line.getMaxBlockHeightInLine();
                if (y + maxBlockHeight > textEnv.getPageHeight()) {
                    page = new CYPageBlock(textEnv);
                    y = 0;
                } else {
                    line.updateLineY(y);
                    y += line.getLineHeight() + textEnv.getVerticalSpacing();
                }

                page.addChild(line);
            }
        }
        pages.add(page);
        return pages;
    }

    private List<CYBlock> resetBlocks(List<CYBlock> rawBlocks) {
        List<CYBlock> result = new ArrayList<CYBlock>();
        if (rawBlocks != null) {
            for (int i = 0; i < rawBlocks.size(); i++) {
                CYBlock block = rawBlocks.get(i);
                if (block instanceof CYTextBlock && block.getChildren() != null) {
                    result.addAll(block.getChildren());
                } else {
                    result.add(block);
                }
            }
        }
        return result;
    }

    private List<CYLineBlock> parseLines(TextEnv textEnv, List<CYBlock> blocks, int pageWidth) {
        List<CYLineBlock> lines = new ArrayList<CYLineBlock>();
        List<CYPlaceHolderBlock> placeHolderBlocks = new ArrayList<CYPlaceHolderBlock>();

        int leftWidth = pageWidth;
        CYLineBlock line = new CYLineBlock(textEnv);
        lines.add(line);
        List<CYPlaceHolderBlock> linePlaceHolderBlocks = new ArrayList<CYPlaceHolderBlock>();
        int y = 0;
        for (int i = 0; i < blocks.size(); i++) {
            CYBlock itemBlock = blocks.get(i);
            if (itemBlock instanceof CYBreakLineBlock) {
                //wrap
                y += line.getLineHeight() + textEnv.getVerticalSpacing();
                leftWidth = pageWidth;
                line = new CYLineBlock(textEnv);
                lines.add(line);
                linePlaceHolderBlocks = getLinePlaceHolderBlocks(placeHolderBlocks, y);
            } else {
                if (itemBlock instanceof CYPlaceHolderBlock) {
                    if (((CYPlaceHolderBlock) itemBlock).getAlignStyle() == CYPlaceHolderBlock.AlignStyle.Style_MONOPOLY) {
                        //add new line
                        y += line.getLineHeight() + textEnv.getVerticalSpacing();
                        line = new CYLineBlock(textEnv);
                        lines.add(line);

                        itemBlock.setX((pageWidth - itemBlock.getWidth()) / 2);
                        itemBlock.setLineY(y);
                        line.addChild(itemBlock);

                        //add new line
                        y += line.getLineHeight() + textEnv.getVerticalSpacing();
                        leftWidth = pageWidth;
                        line = new CYLineBlock(textEnv);
                        lines.add(line);
                        if (placeHolderBlocks != null) {
                            placeHolderBlocks.clear();
                        }
                        continue;
                    }
                    placeHolderBlocks.add((CYPlaceHolderBlock) itemBlock);
                }
                CYPlaceHolderBlock hitCell;
                if (itemBlock.getWidth() < leftWidth) {
                    while ((hitCell = getHitCell(linePlaceHolderBlocks, pageWidth - leftWidth, y,
                            itemBlock.getWidth(), itemBlock.getContentHeight())) != null) {
                        leftWidth = pageWidth - hitCell.getWidth() - hitCell.getX();
                    }

                    while (leftWidth != pageWidth && leftWidth < itemBlock.getWidth()) {
                        //wrap
                        y += line.getLineHeight() + textEnv.getVerticalSpacing();
                        leftWidth = pageWidth;
                        line = new CYLineBlock(textEnv);
                        lines.add(line);
                        linePlaceHolderBlocks = getLinePlaceHolderBlocks(placeHolderBlocks, y);

                        while ((hitCell = getHitCell(linePlaceHolderBlocks, pageWidth - leftWidth, y,
                                itemBlock.getWidth(), itemBlock.getContentHeight())) != null) {
                            leftWidth = pageWidth - hitCell.getWidth() - hitCell.getX();
                        }
                    }
                    itemBlock.setX(pageWidth - leftWidth);
                    itemBlock.setLineY(y);
                    leftWidth -= itemBlock.getWidth();
                    line.addChild(itemBlock);
                } else {
                    while (leftWidth != pageWidth && leftWidth < itemBlock.getWidth()) {
                        //wrap
                        y += line.getLineHeight() + textEnv.getVerticalSpacing();
                        leftWidth = pageWidth;
                        line = new CYLineBlock(textEnv);
                        lines.add(line);
                        linePlaceHolderBlocks = getLinePlaceHolderBlocks(placeHolderBlocks, y);

                        while ((hitCell = getHitCell(linePlaceHolderBlocks, pageWidth - leftWidth, y,
                                itemBlock.getWidth(), itemBlock.getContentHeight())) != null) {
                            leftWidth = pageWidth - hitCell.getWidth() - hitCell.getX();
                        }
                    }
                    itemBlock.setX(pageWidth - leftWidth);
                    itemBlock.setLineY(y);
                    leftWidth -= itemBlock.getWidth();
                    line.addChild(itemBlock);
                }
            }
        }


//        for (int i = 0; i < blocks.size(); i++) {
//            CYBlock itemBlock = blocks.get(i);
//            if (itemBlock instanceof CYPlaceHolderBlock) {
//                placeHolderBlocks.add((CYPlaceHolderBlock) itemBlock);
//
//                if (itemBlock.getWidth() < leftWidth) {
//                    itemBlock.x = pageWidth - leftWidth;
//                    itemBlock.lineY = y;
//                    leftWidth -= itemBlock.getWidth();
//                } else {
//                    y += line.getLineHeight();
//                    leftWidth = pageWidth - itemBlock.getWidth();
//                    line = new CYLineBlock();
//                    lines.add(line);
//                    //create new placeHolder when break-line
//                    linePlaceHolderBlocks = getLinePlaceHolderBlocks(placeHolderBlocks, y);
//                    itemBlock.lineY = y;
//                }
//                line.addBlock(itemBlock);
//            } else if(itemBlock instanceof CYBreakLineBlock){
//                y += line.getLineHeight();
//                leftWidth = pageWidth;
//                line = new CYLineBlock();
//                lines.add(line);
//                linePlaceHolderBlocks = getLinePlaceHolderBlocks(placeHolderBlocks, y);
//            } else {//must be textBlock
//                List<CYBlock> subBlocks = itemBlock.getChildren();
//                if (subBlocks != null) {
//                    for (int j = 0; j < subBlocks.size(); j++) {
//                        CYBlock block = subBlocks.get(j);
//                        if (block.getWidth() < leftWidth) {
//                            CYPlaceHolderBlock hitCell;
//                            while ((hitCell = getHitCell(linePlaceHolderBlocks, pageWidth - leftWidth, y,
//                                    block.getWidth(), block.getContentHeight())) != null) {
//                                leftWidth = leftWidth - hitCell.getWidth() - (hitCell.x - (pageWidth - leftWidth));
//                            }
//
//                            if (block.getWidth() < leftWidth) {
//                                block.x = pageWidth - leftWidth;
//                                block.lineY = y;
//                                leftWidth -= block.getWidth();
//                            } else {
//                                y += line.getLineHeight();
//                                linePlaceHolderBlocks = getLinePlaceHolderBlocks(placeHolderBlocks, y);
//                                leftWidth = pageWidth - block.getWidth();
//                                line = new CYLineBlock();
//                                lines.add(line);
//                                block.lineY = y;
//                            }
//                        } else {
//                            leftWidth = pageWidth;
//                            linePlaceHolderBlocks = getLinePlaceHolderBlocks(placeHolderBlocks, y);
//                            y += line.getLineHeight();
//                            CYBlock hitCell;
//                            while ((hitCell = getHitCell(linePlaceHolderBlocks, pageWidth - leftWidth, y,
//                                    block.getWidth(), block.getContentHeight())) != null) {
//                                leftWidth = leftWidth - hitCell.getWidth() - (hitCell.x - (pageWidth - leftWidth));
//                            }
//
//                            line = new CYLineBlock();
//                            lines.add(line);
//                            block.lineY = y;
//                            block.x = pageWidth - leftWidth;
//                            leftWidth -= block.getWidth();
//                        }
//
//                        line.addBlock(block);
//                    }
//                }
//            }
//        }
        if (line != null) {
            line.measure();
        }
        return lines;
    }

    private List<CYPlaceHolderBlock> getLinePlaceHolderBlocks(
            List<CYPlaceHolderBlock> placeHolderBlocks, int y) {

        if (placeHolderBlocks == null || placeHolderBlocks.isEmpty()) {
            return null;
        }
        List<CYPlaceHolderBlock> linePlaceHolderBlocks = new ArrayList<CYPlaceHolderBlock>();
        for (int i = 0; i < placeHolderBlocks.size(); i++) {
            CYPlaceHolderBlock block = placeHolderBlocks.get(i);
            if (y >= block.getLineY() && y <= block.getLineY() + block.getContentHeight()) {
                linePlaceHolderBlocks.add(block);
            }
        }
        return linePlaceHolderBlocks;
    }

    private CYPlaceHolderBlock getHitCell(List<CYPlaceHolderBlock> linePlaceHolderBlocks
            , int x, int y, int width, int height) {
        if (linePlaceHolderBlocks == null || linePlaceHolderBlocks.isEmpty())
            return null;
        for (int i = 0; i < linePlaceHolderBlocks.size(); i++) {
            CYPlaceHolderBlock cell = linePlaceHolderBlocks.get(i);

            if (new Rect(cell.getX(), cell.getLineY(), cell.getX() + cell.getWidth(),
                    cell.getLineY() + cell.getContentHeight()).intersect(new Rect(x, y, x + width, y + height))) {
                return cell;
            }

        }
        return null;
    }
}

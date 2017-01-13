package com.hyena.coretext.layout;

import android.graphics.Rect;

import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYBreakLineBlock;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.coretext.blocks.CYLineBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.CYTextBlock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzc on 16/4/8.
 */
public class CYHorizontalLayout implements CYLayout {

    @Override
    public List<CYPageBlock> parsePage(List<CYBlock> blocks, int pageWidth, int pageHeight) {
        List<CYLineBlock> lines = parseLines(resetBlocks(blocks), pageWidth);
        List<CYPageBlock> pages = new ArrayList<CYPageBlock>();
        CYPageBlock page = new CYPageBlock();
        int height = 0;
        int y = 0;
        if (lines != null) {
            for (int i = 0; i < lines.size(); i++) {
                CYLineBlock line = lines.get(i);
                int maxBlockHeight = line.getMaxBlockHeightInLine();
                if (height + maxBlockHeight > pageHeight) {
                    page = new CYPageBlock();
                    height = 0;
                    y = 0;
                } else {
                    line.updateLineY(y);
                    y += line.getLineHeight();
                }
                page.addLines(line);
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
                if (block instanceof CYTextBlock) {
                    result.addAll(block.getSubBlocks());
                } else {
                    result.add(block);
                }
            }
        }
        return result;
    }

    private List<CYLineBlock> parseLines(List<CYBlock> blocks, int pageWidth) {
        List<CYLineBlock> lines = new ArrayList<CYLineBlock>();
        List<CYPlaceHolderBlock> placeHolderBlocks = new ArrayList<CYPlaceHolderBlock>();

        int leftWidth = pageWidth;
        CYLineBlock line = new CYLineBlock();
        lines.add(line);
        List<CYPlaceHolderBlock> linePlaceHolderBlocks = new ArrayList<CYPlaceHolderBlock>();
        int y = 0;
        for (int i = 0; i < blocks.size(); i++) {
            CYBlock itemBlock = blocks.get(i);
            if (itemBlock instanceof CYBreakLineBlock) {
                //wrap
                y += line.getLineHeight();
                leftWidth = pageWidth;
                line = new CYLineBlock();
                lines.add(line);
                linePlaceHolderBlocks = getLinePlaceHolderBlocks(placeHolderBlocks, y);
            } else {
                if (itemBlock instanceof CYPlaceHolderBlock) {
                    placeHolderBlocks.add((CYPlaceHolderBlock) itemBlock);
                }
                CYPlaceHolderBlock hitCell;
                if (itemBlock.getWidth() < leftWidth) {
                    while ((hitCell = getHitCell(linePlaceHolderBlocks, pageWidth - leftWidth, y,
                            itemBlock.getWidth(), itemBlock.getHeight())) != null) {
                        leftWidth = pageWidth - hitCell.getWidth() - hitCell.x;
                    }

                    while (leftWidth < itemBlock.getWidth()) {
                        //wrap
                        y += line.getLineHeight();
                        leftWidth = pageWidth;
                        line = new CYLineBlock();
                        lines.add(line);
                        linePlaceHolderBlocks = getLinePlaceHolderBlocks(placeHolderBlocks, y);

                        while ((hitCell = getHitCell(linePlaceHolderBlocks, pageWidth - leftWidth, y,
                                itemBlock.getWidth(), itemBlock.getHeight())) != null) {
                            leftWidth = pageWidth - hitCell.getWidth() - hitCell.x;
                        }
                    }
                    itemBlock.x = pageWidth - leftWidth;
                    itemBlock.lineY = y;
                    leftWidth -= itemBlock.getWidth();
                    line.addBlock(itemBlock);
                } else {
                    while (leftWidth < itemBlock.getWidth()) {
                        //wrap
                        y += line.getLineHeight();
                        leftWidth = pageWidth;
                        line = new CYLineBlock();
                        lines.add(line);
                        linePlaceHolderBlocks = getLinePlaceHolderBlocks(placeHolderBlocks, y);

                        while ((hitCell = getHitCell(linePlaceHolderBlocks, pageWidth - leftWidth, y,
                                itemBlock.getWidth(), itemBlock.getHeight())) != null) {
                            leftWidth = pageWidth - hitCell.getWidth() - hitCell.x;
                        }
                    }
                    itemBlock.x = pageWidth - leftWidth;
                    itemBlock.lineY = y;
                    leftWidth -= itemBlock.getWidth();
                    line.addBlock(itemBlock);
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
//                List<CYBlock> subBlocks = itemBlock.getSubBlocks();
//                if (subBlocks != null) {
//                    for (int j = 0; j < subBlocks.size(); j++) {
//                        CYBlock block = subBlocks.get(j);
//                        if (block.getWidth() < leftWidth) {
//                            CYPlaceHolderBlock hitCell;
//                            while ((hitCell = getHitCell(linePlaceHolderBlocks, pageWidth - leftWidth, y,
//                                    block.getWidth(), block.getHeight())) != null) {
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
//                                    block.getWidth(), block.getHeight())) != null) {
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
            if (y >= block.lineY && y <= block.lineY + block.getHeight()) {
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

            if (new Rect(cell.x, cell.lineY, cell.x + cell.getWidth(),
                    cell.lineY + cell.getHeight()).intersect(new Rect(x, y, x + width, y + height))) {
                return cell;
            }

        }
        return null;
    }
}

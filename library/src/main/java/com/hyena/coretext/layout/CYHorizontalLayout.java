package com.hyena.coretext.layout;

import android.graphics.Rect;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYBreakLineBlock;
import com.hyena.coretext.blocks.CYLineBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;
import com.hyena.coretext.blocks.CYParagraphStyle;
import com.hyena.coretext.blocks.CYParagraphEndBlock;
import com.hyena.coretext.blocks.CYParagraphStartBlock;
import com.hyena.coretext.blocks.CYTextBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by yangzc on 16/4/8.
 */
public class CYHorizontalLayout extends CYLayout {

    private int leftWidth = 0;
    private int y = 0;
    private CYLineBlock line = null;
    private List<CYPlaceHolderBlock> placeHolderBlocks = new ArrayList<CYPlaceHolderBlock>();
    private List<CYPlaceHolderBlock> linePlaceHolderBlocks = new ArrayList<CYPlaceHolderBlock>();
    private Stack<CYParagraphStyle> styleParagraphStack = new Stack<CYParagraphStyle>();
    private List<CYLineBlock> lines = new ArrayList<CYLineBlock>();

    private List<CYBlock> mBlocks;
    private List<CYPageBlock> mPageBlocks = new ArrayList<CYPageBlock>();

    public CYHorizontalLayout(TextEnv textEnv, List<CYBlock> blocks) {
        super(textEnv);
        this.leftWidth = textEnv.getPageWidth();
        this.mBlocks = resetBlocks(blocks);
    }

    private void reset() {
        this.leftWidth = getTextEnv().getPageWidth();
        this.y = 0;
        line = null;
        if (placeHolderBlocks == null)
            placeHolderBlocks = new ArrayList<CYPlaceHolderBlock>();
        placeHolderBlocks.clear();

        if (linePlaceHolderBlocks == null)
            linePlaceHolderBlocks = new ArrayList<CYPlaceHolderBlock>();
        linePlaceHolderBlocks.clear();

        if (styleParagraphStack == null)
            styleParagraphStack = new Stack<CYParagraphStyle>();
        styleParagraphStack.clear();

        if (lines == null)
            lines = new ArrayList<CYLineBlock>();
        lines.clear();
    }

    @Override
    public List<CYPageBlock> parse() {
        reset();

        List<CYLineBlock> lines = parseLines(mBlocks);
        CYPageBlock page = new CYPageBlock(getTextEnv());
        int y = 0;
        if (lines != null) {
            for (int i = 0; i < lines.size(); i++) {
                CYLineBlock line = lines.get(i);
                int maxBlockHeight = line.getMaxBlockHeightInLine();
                if (y + maxBlockHeight > getTextEnv().getPageHeight()) {
                    page = new CYPageBlock(getTextEnv());
                    y = 0;
                } else {
                    line.updateLineY(y);
                    y += line.getHeight() + getTextEnv().getVerticalSpacing();
                }

                page.addChild(line);
            }
        }
        mPageBlocks.add(page);
        return mPageBlocks;
    }

    @Override
    public List<CYPageBlock> getPages() {
        return mPageBlocks;
    }

    @Override
    public List<CYBlock> getBlocks() {
        return mBlocks;
    }

    private List<CYLineBlock> parseLines(List<CYBlock> blocks) {
        int pageWidth = getTextEnv().getPageWidth();
        for (int i = 0; i < blocks.size(); i++) {
            CYBlock itemBlock = blocks.get(i);
            if (itemBlock instanceof CYParagraphStartBlock) {
                styleParagraphStack.push(((CYParagraphStartBlock) itemBlock).getStyle());
                //wrap line
                wrapLine();
                if (line != null)
                    line.setIsFirstLineInParagraph(true);
            } else if(itemBlock instanceof CYParagraphEndBlock) {
                if (!styleParagraphStack.isEmpty())
                    styleParagraphStack.pop();

                //auto break line
                if (line == null) {
                    line = new CYLineBlock(getTextEnv(), getParagraphStyle(styleParagraphStack));
                    lines.add(line);
                }
                line.setIsFinishingLineInParagraph(true);
                //wrap line
                wrapLine();
            } else if (itemBlock instanceof CYBreakLineBlock) {
                if (line == null) {
                    line = new CYLineBlock(getTextEnv(), getParagraphStyle(styleParagraphStack));
                    lines.add(line);
                }
                //wrap line
                wrapLine();
                continue;
            } else {
                if (line == null) {
                    line = new CYLineBlock(getTextEnv(), getParagraphStyle(styleParagraphStack));
                    lines.add(line);
                }

                if (itemBlock instanceof CYPlaceHolderBlock) {
                    if (((CYPlaceHolderBlock) itemBlock).getAlignStyle() == CYPlaceHolderBlock.AlignStyle.Style_MONOPOLY) {
                        //add line
                        wrapLine();
                        itemBlock.setX((getTextEnv().getPageWidth() - itemBlock.getWidth()) / 2);
                        itemBlock.setLineY(y);
                        line.addChild(itemBlock);
                        //add new line
                        wrapLine();
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
                            itemBlock)) != null) {
                        leftWidth = pageWidth - hitCell.getWidth() - hitCell.getX();
                    }

                    while (leftWidth != pageWidth && leftWidth < itemBlock.getWidth()) {
                        //wrap
                        wrapLine();
                        while ((hitCell = getHitCell(linePlaceHolderBlocks, pageWidth - leftWidth, y,
                                itemBlock)) != null) {
                            leftWidth = pageWidth - hitCell.getWidth() - hitCell.getX();
                        }
                    }
                    itemBlock.setX(getTextEnv().getPageWidth() - leftWidth);
                    itemBlock.setLineY(y);
                    leftWidth -= itemBlock.getWidth();
                    line.addChild(itemBlock);
                } else {
                    while (leftWidth != pageWidth && leftWidth < itemBlock.getWidth()) {
                        //wrap
                        wrapLine();
                        while ((hitCell = getHitCell(linePlaceHolderBlocks, pageWidth - leftWidth, y,
                                itemBlock)) != null) {
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
        if (line != null) {
            line.measure();
        }
        return lines;
    }

    private void wrapLine() {
        if (line == null || line.getChildren() == null || line.getChildren().isEmpty())
            return;

        y += line.getHeight() + getTextEnv().getVerticalSpacing();
        leftWidth = getTextEnv().getPageWidth();
        line = new CYLineBlock(getTextEnv(), getParagraphStyle(styleParagraphStack));
        lines.add(line);
        linePlaceHolderBlocks = getLinePlaceHolderBlocks(y);
    }

    private List<CYPlaceHolderBlock> getLinePlaceHolderBlocks(int y) {

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
            , int x, int y, CYBlock block) {
        if (linePlaceHolderBlocks == null || linePlaceHolderBlocks.isEmpty())
            return null;
        for (int i = 0; i < linePlaceHolderBlocks.size(); i++) {
            CYPlaceHolderBlock cell = linePlaceHolderBlocks.get(i);

            if (cell != block && new Rect(cell.getX(), cell.getLineY(), cell.getX() + cell.getWidth(),
                    cell.getLineY() + cell.getContentHeight()).intersect(new Rect(x, y,
                    x + block.getWidth(), y + block.getHeight()))) {
                return cell;
            }

        }
        return null;
    }

    private CYParagraphStyle getParagraphStyle(Stack<CYParagraphStyle> styleStack) {
        if (styleStack == null || styleStack.isEmpty())
            return null;
        return styleStack.peek();
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
}

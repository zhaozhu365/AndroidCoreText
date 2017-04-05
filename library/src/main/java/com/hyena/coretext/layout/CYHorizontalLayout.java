package com.hyena.coretext.layout;

import android.graphics.Rect;

import com.hyena.coretext.TextEnv;
import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYBreakLineBlock;
import com.hyena.coretext.blocks.CYLineBlock;
import com.hyena.coretext.blocks.CYPageBlock;
import com.hyena.coretext.blocks.CYParagraphEndBlock;
import com.hyena.coretext.blocks.CYParagraphStartBlock;
import com.hyena.coretext.blocks.CYParagraphStyle;
import com.hyena.coretext.blocks.CYPlaceHolderBlock;

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
        this.mBlocks = blocks;
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
                if (line.getChildren() == null || line.getChildren().isEmpty())
                    continue;

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
        int blockCount = blocks.size();
        for (int i = 0; i < blockCount; i++) {
            CYBlock itemBlock = blocks.get(i);
            if (itemBlock != null) {
                itemBlock.setParagraphStyle(getParagraphStyle(styleParagraphStack));
            }
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
                        itemBlock.setX(0);
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
                int blockWidth = itemBlock.getWidth();
                //修正位置
                if (blockWidth < leftWidth) {
                    hitCell = getHitCell(linePlaceHolderBlocks, pageWidth - leftWidth, y, itemBlock);
                    while (hitCell != null) {
                        leftWidth = pageWidth - hitCell.getWidth() - hitCell.getX();
                        hitCell = getHitCell(linePlaceHolderBlocks, pageWidth - leftWidth, y, itemBlock);
                    }
                }
                //如果剩余位置不充足 则换行
                while (leftWidth != pageWidth && leftWidth < blockWidth) {
                    //wrap
                    wrapLine();
                    hitCell = getHitCell(linePlaceHolderBlocks, pageWidth - leftWidth, y, itemBlock);
                    while (hitCell != null) {
                        leftWidth = pageWidth - hitCell.getWidth() - hitCell.getX();
                        hitCell = getHitCell(linePlaceHolderBlocks, pageWidth - leftWidth, y, itemBlock);
                    }
                }
                itemBlock.setX(pageWidth - leftWidth);
                itemBlock.setLineY(y);
                leftWidth -= blockWidth;
                line.addChild(itemBlock);
            }
        }
        return lines;
    }

//    private static int ts = 0;
    private void wrapLine() {
        if (line == null)
            return;

        int lineHeight = 0;
        if (line.getChildren() == null || line.getChildren().isEmpty()) {
            if (lines != null)
                lines.remove(line);
        } else {
//            long start = System.currentTimeMillis();
            lineHeight = line.getHeight();
//            ts += (System.currentTimeMillis() - start);
        }

        y += lineHeight + getTextEnv().getVerticalSpacing();
        leftWidth = getTextEnv().getPageWidth();
        line = new CYLineBlock(getTextEnv(), getParagraphStyle(styleParagraphStack));
        lines.add(line);
        linePlaceHolderBlocks = getLinePlaceHolderBlocks(y);
//        LogUtil.v("yangzc", "new line block cost: " + ts);
    }

    private List<CYPlaceHolderBlock> getLinePlaceHolderBlocks(int y) {
        if (placeHolderBlocks == null || placeHolderBlocks.isEmpty()) {
            return null;
        }
        List<CYPlaceHolderBlock> linePlaceHolderBlocks = new ArrayList<CYPlaceHolderBlock>();
        int count = placeHolderBlocks.size();
        for (int i = 0; i < count; i++) {
            CYPlaceHolderBlock block = placeHolderBlocks.get(i);
            int top = block.getLineY();
            int bottom = top + block.getHeight();
            if (y >= top && y <= bottom) {
                linePlaceHolderBlocks.add(block);
            }
        }
        return linePlaceHolderBlocks;
    }

    private Rect mTemp1Rect = new Rect();
    private Rect mTemp2Rect = new Rect();
    private CYPlaceHolderBlock getHitCell(List<CYPlaceHolderBlock> linePlaceHolderBlocks
            , int x, int y, CYBlock block) {
        if (linePlaceHolderBlocks == null || linePlaceHolderBlocks.isEmpty())
            return null;
        mTemp1Rect.set(x, y, x + block.getWidth(), y + block.getHeight());
        int count = linePlaceHolderBlocks.size();
        for (int i = 0; i < count; i++) {
            CYPlaceHolderBlock cell = linePlaceHolderBlocks.get(i);
            mTemp2Rect.set(cell.getX(), cell.getLineY(),
                    cell.getX() + cell.getWidth(), cell.getLineY() + cell.getHeight());
            if (cell != block && mTemp2Rect.intersect(mTemp1Rect)) {
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

}

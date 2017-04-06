package com.hyena.coretext;

import android.text.TextUtils;

import com.hyena.coretext.blocks.CYBlock;
import com.hyena.coretext.blocks.CYTextBlock;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yangzc on 16/4/9.
 */
public class AttributedString {

    private String mText;
    private TextEnv mTextEnv;
    private List<BlockSection> mBlockSections;

    public AttributedString(TextEnv textEnv, String text) {
        this.mTextEnv = textEnv;
        this.mText = text;
    }

    public <T extends CYBlock> T replaceBlock(int start, int end, Class<T> blockClz)
            throws IndexOutOfBoundsException {

        if (TextUtils.isEmpty(mText))
            return null;

        if (mBlockSections == null)
            mBlockSections = new ArrayList<BlockSection>();

        if (start >=0 && end >=0 && end <= mText.length() && start<= mText.length()
                && end >= start) {
            BlockSection section = new BlockSection(start, end, blockClz);
            T block = (T) section.getOrNewBlock();
            mBlockSections.add(section);
            return block;
        } else {
            throw new IndexOutOfBoundsException("IndexOutOfBoundsException");
        }
    }

    public void replaceBlock(int start, int end, CYBlock block) {
        if (TextUtils.isEmpty(mText) || block == null)
            return;

        if (mBlockSections == null)
            mBlockSections = new ArrayList<BlockSection>();

        if (start >=0 && end >=0 && end <= mText.length() && start<= mText.length()
                && end >= start) {
            BlockSection section = new BlockSection(start, end, block);
            mBlockSections.add(section);
        } else {
            throw new IndexOutOfBoundsException("IndexOutOfBoundsException");
        }
    }

    public List<CYBlock> buildBlocks() {
//        long ts = System.currentTimeMillis();
        List<CYBlock> blocks = new ArrayList<CYBlock>();
        if (mBlockSections == null) {
            BlockSection ghostSection = new BlockSection(0
                    , mText.length(), CYTextBlock.class);
            blocks.add(ghostSection.getOrNewBlock());
        } else {
            Collections.sort(mBlockSections, new Comparator<BlockSection>() {
                @Override
                public int compare(BlockSection t1, BlockSection t2) {
                    return t1.startIndex - t2.startIndex;
                }
            });

            int endIndex = 0;
            for (int i = 0; i < mBlockSections.size(); i++) {
                BlockSection blockSection = mBlockSections.get(i);
                if (blockSection.startIndex != endIndex) {
                    BlockSection ghostSection = new BlockSection(endIndex
                            , blockSection.startIndex, CYTextBlock.class);
                    blocks.add(ghostSection.getOrNewBlock());
                }
                blocks.add(blockSection.getOrNewBlock());

                endIndex = blockSection.endIndex;
            }

            if (endIndex < mText.length()) {
                BlockSection ghostSection = new BlockSection(endIndex
                        , mText.length(), CYTextBlock.class);
                blocks.add(ghostSection.getOrNewBlock());
            }
        }
//        Log.v("yangzc", "buildBlocks cost: " + (System.currentTimeMillis() - ts));
        return resetBlocks(blocks);
    }

    private class BlockSection {
        public int startIndex;
        public int endIndex;
        public Class<? extends CYBlock> blockClz;

        private CYBlock mBlock;

        public BlockSection(int startIndex, int endIndex, Class<? extends CYBlock> blockClz) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.blockClz = blockClz;
        }

        public BlockSection(int startIndex, int endIndex, CYBlock block) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            mBlock = block;
        }

        public CYBlock getOrNewBlock(){
            if (mBlock != null)
                return mBlock;

            if (blockClz != null) {
                try {
                    Constructor<? extends  CYBlock> constructor = blockClz.getConstructor(TextEnv.class, String.class);
                    String content = mText.substring(startIndex, endIndex);
                    content = content.replaceAll("labelsharp", "#");
                    mBlock = constructor.newInstance(mTextEnv, content);
                    return mBlock;
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    /**
     * 把所有TextBlock切成最小单元
     * @param rawBlocks
     * @return
     */
    private List<CYBlock> resetBlocks(List<CYBlock> rawBlocks) {
//        long ts = System.currentTimeMillis();
        List<CYBlock> result = new ArrayList<CYBlock>();
        if (rawBlocks != null) {
            int blockCount = rawBlocks.size();
            for (int i = 0; i < blockCount; i++) {
                CYBlock block = rawBlocks.get(i);
                if (block instanceof CYTextBlock && block.getChildren() != null) {
                    result.addAll(block.getChildren());
                } else {
                    result.add(block);
                }
            }
        }
//        Log.v("yangzc", "resetBlocks cost: " + (System.currentTimeMillis() - ts));
        return result;
    }
}

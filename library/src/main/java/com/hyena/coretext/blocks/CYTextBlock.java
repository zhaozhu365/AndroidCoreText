package com.hyena.coretext.blocks;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;

import com.hyena.coretext.TextEnv;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yangzc on 16/4/8.
 */
public class CYTextBlock extends CYBlock {

    private Paint paint;
    private int width, height;
    private Paint.FontMetrics fontMetrics;
    private Word word = null;

    /*
     * 构造方法
     */
    public CYTextBlock(TextEnv textEnv, String content){
        super(textEnv, content);
        if (TextUtils.isEmpty(content))
            content = "";
        //初始化画笔
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.set(textEnv.getPaint());
        //解析成单词
        List<Word> words = parseWords(content);
        //初始化子节点
        setChildren(new ArrayList(words.size()));
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        for (int i = 0; i < words.size(); i++) {
            Word word = words.get(i);
            int blockWidth = getTextWidth(paint, word.word);
            addChild(buildChildBlock(textEnv, paint, blockWidth, fontMetrics, word));
        }
    }

    /*
     * 构造子节点
     */
    protected CYTextBlock buildChildBlock(TextEnv textEnv, Paint paint
            , int width, Paint.FontMetrics fontMetrics, Word word) {
        try {
            CYTextBlock textBlock = (CYTextBlock) clone();
            textBlock.setTextEnv(textEnv);
            textBlock.paint = new Paint(paint);
            textBlock.width = width;
            textBlock.fontMetrics = fontMetrics;
            textBlock.word = word;
            return textBlock;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setStyle(CYStyle style) {
        super.setStyle(style);
        if (style != null) {
            paint.setColor(style.getTextColor());
            setTextSize(style.getTextSize());
        }
        updateSize();
    }

    public CYTextBlock setTextColor(int color) {
        if (paint != null && color > 0) {
            paint.setColor(color);
        }
        return this;
    }

    public CYTextBlock setTypeFace(Typeface typeface){
        if (paint != null && typeface != null) {
            paint.setTypeface(typeface);
        }
        return this;
    }

    @Override
    public void setTextHeightInLine(int textHeight) {
        super.setTextHeightInLine(textHeight);
        this.height = textHeight - getPaddingBottom() - getPaddingTop();
    }

    protected void updateSize() {
        float textWidth = getTextWidth(paint, word.word);
        float textHeight = getTextHeight(paint);
        if (!TextUtils.isEmpty(word.pinyin)) {
            float pinyinWidth = getTextWidth(paint, word.pinyin);
            float pinyinHeight = getTextHeight(paint);
            if (pinyinWidth > textWidth) {
                textWidth = pinyinWidth;
            }
            textHeight += pinyinHeight;
        }
        this.width = (int) textWidth;
        this.height = (int) textHeight;
    }

    public CYTextBlock setTextSize(int fontSize){
        if (paint != null && fontSize > 0
                && paint.getTextSize() != fontSize) {
            paint.setTextSize(fontSize);
        }
        return this;
    }

    @Override
    public List<CYBlock> getChildren() {
        if (word != null) {
            return null;
        }
        return super.getChildren();
    }

    /**
     * 解析单词
     * @param content 内容
     * @return 单词列表
     */
    protected List<Word> parseWords(String content) {
        List<Word> words = new ArrayList<>();
        Pattern pattern = Pattern.compile(".*?<.*?>");
        Matcher matcher = pattern.matcher(content);
        String text = content;
        if (content.contains("<") && content.contains(">")) {
            while (matcher.find()) {
                String value = matcher.group();
                String word = value.replaceFirst("<.*?>", "");
                String pinyin = value.replace(word, "").replaceAll("[<|>]", "");
                words.add(new Word(word, pinyin));
                text = text.replace(value, "");
            }
        }

        if (!TextUtils.isEmpty(text)) {
            char chs[] = text.toCharArray();
            for (int i = 0; i < chs.length; i++) {
                int wordStart = i, count = 1;
                while ((i + 1) < chs.length && isLetter(chs[i + 1])
                        && !Character.isSpace(chs[i + 1])) {
                    count ++;
                    i ++;
                }
                words.add(new Word(new String(chs, wordStart, count), ""));
            }
        }
        return words;
    }

    @Override
    public int getContentWidth() {
        return width;
    }

    @Override
    public int getContentHeight() {
        return height;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (fontMetrics != null && word != null) {
            Rect rect = getContentRect();
            float x = rect.left;
            float y = rect.bottom - fontMetrics.bottom;
            //绘制单词
            drawText(canvas, word.word, x, y, paint);
            //绘制拼音
            drawPinyin(canvas, word.pinyin, x, y - getTextHeight(paint), paint);
            //绘制下横线
            drawUnderLine(canvas, rect);
        }
    }

    /*
     * 绘制单词
     */
    protected void drawText(Canvas canvas, String text, float x, float y, Paint paint) {
        if (!TextUtils.isEmpty(text)) {
            canvas.drawText(text, x, y, paint);
        }
    }

    /*
     * 绘制拼音
     */
    protected void drawPinyin(Canvas canvas, String pinyin, float x, float y, Paint paint) {
        if (!TextUtils.isEmpty(pinyin)) {
            canvas.drawText(pinyin, x, y, paint);
        }
    }

    /**
     * 绘制下横线
     * @param canvas canvas
     * @param rect 文本范围
     */
    protected void drawUnderLine(Canvas canvas, Rect rect) {
        float x = rect.left;
        CYStyle paragraphStyle = getParagraphStyle();
        if (paragraphStyle != null) {
            String style = paragraphStyle.getStyle();
            if ("under_line".equals(style)) {//添加下横线
                canvas.drawLine(x, rect.bottom, x + rect.width(), rect.bottom, paint);
            }
        }
    }

    /**
     * 是否是字母
     * @param ch 内容
     * @return true|false
     */
    public boolean isLetter(char ch) {
        if (('A' <= ch && ch <= 'Z') || ('a' <= ch && ch <= 'z') || ch == '-') {
            return true;
        }
        return false;
    }

    public static class Word {
        public String word;
        public String pinyin;
        public Word(String word, String pinyin) {
            this.word = word;
            this.pinyin = pinyin;
        }
    }

    @Override
    public boolean isDebug() {
        return false;
    }
}

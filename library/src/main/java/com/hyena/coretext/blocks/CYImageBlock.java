package com.hyena.coretext.blocks;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.hyena.coretext.TextEnv;

/**
 * Created by yangzc on 16/4/8.
 */
public class CYImageBlock extends CYPlaceHolderBlock {

    private Bitmap mBitmap;

    public CYImageBlock(TextEnv textEnv, String content){
        super(textEnv, content);
    }

    public CYImageBlock setResId(Context context, int resId){
        mBitmap = BitmapFactory.decodeResource(context.getResources(), resId);
        return this;
    }

    @Override
    public CYImageBlock setAlignStyle(AlignStyle style) {
        return (CYImageBlock) super.setAlignStyle(style);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (mBitmap != null)
            canvas.drawBitmap(mBitmap, null, getContentRect(), null);
    }

    @Override
    public int getContentWidth() {
        if (mBitmap != null)
            return mBitmap.getWidth();
        return 100;
    }

    @Override
    public int getContentHeight() {
        if (mBitmap != null)
            return mBitmap.getHeight();
        return 100;
    }
}

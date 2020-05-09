package com.liboshuai.framework.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import plat.skytv.client.framework.R;

/**
 * Author:boshuai.li
 * Time:2020/5/9   16:53
 * Description: 拖拽验证码view
 */
public class TouchPictureV extends View {

    // 背景
    private Bitmap bgBitmap;
    // 背景画笔
    private Paint mPaintbg;

    // 空白块
    private Bitmap mNullBitmap;
    // 空白块画笔
    private Paint mPaintNull;

    // 移动方块
    private Bitmap mMoveBitmap;
    // 移动画笔
    private Paint mPaintMove;

    // view的宽高
    private int mWidth;
    private int mHeight;

    private int CARD_SIZE = 200;

    // 空白块的 坐标
    private int LINE_W, LINE_H;

    public TouchPictureV(Context context) {
        super(context);
        init();
    }

    public TouchPictureV(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchPictureV(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        mPaintbg = new Paint();
        mPaintMove = new Paint();
        mPaintNull = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 1. 绘制背景
        drawBg(canvas);
        // 2. 绘制一个空白块
        drawNullCard(canvas);
        // 3. 绘制一个移动的方块
        drawMoveCard(canvas);
    }

    /**
     * 绘制移动方块
     * @param canvas
     */
    private void drawMoveCard(Canvas canvas) {
        // 1. 对bgBitmap进行裁剪 从（LINE_W， LINE_H）坐标开始截取 宽高为CARD_SIZE的图片
        Bitmap bitmap = Bitmap.createBitmap(bgBitmap, LINE_W, LINE_H, CARD_SIZE, CARD_SIZE);
        // 2.绘制到view上
        canvas.drawBitmap(bitmap, 0, LINE_H, mPaintMove);
    }

    /**
     * 绘制一个空白块
     *
     * @param canvas
     */
    private void drawNullCard(Canvas canvas) {
        mNullBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_null_card);

        CARD_SIZE = mNullBitmap.getWidth();

        LINE_W = mWidth / 3 * 2;
        LINE_H = mHeight / 2 - (CARD_SIZE / 2);

        canvas.drawBitmap(mNullBitmap, LINE_W, LINE_H, mPaintNull);
    }

    /**
     * 绘制背景
     *
     * @param canvas
     */
    private void drawBg(Canvas canvas) {
        // 1.获取图片
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_bg);
        // 2. 创建一个跟view 登高等宽的bitmap
        bgBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        // 3. 将图片绘制到空的bitmap
        Canvas bgCanvas = new Canvas(bgBitmap);

        bgCanvas.drawBitmap(bitmap, null, new Rect(0, 0, mWidth, mHeight), mPaintbg);

        // 4. 将bgBitmap 绘制到view上
        canvas.drawBitmap(bgBitmap, null, new Rect(0, 0, mWidth, mHeight), mPaintbg);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }
}

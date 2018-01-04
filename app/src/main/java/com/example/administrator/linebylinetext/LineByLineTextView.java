package com.example.administrator.linebylinetext;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;

/**
 * 逐行显示的textView
 * 通过每一行的遮罩渐变实现逐行显示
 * Created by lishaojie on 2017/12/28.
 */

public class LineByLineTextView extends android.support.v7.widget.AppCompatTextView implements ValueAnimator.AnimatorUpdateListener {
    private final int LINE_ANIMATOR_DURATION = 1500;
    /**
     * 遮罩画笔
     */
    private Paint mMaskPaint;

    private Paint mLinePaint;

    /**
     * 遮罩颜色
     */
    private @ColorInt
    int mMaskColor = 0xFFffffff;
    private int mLineHeight;
    private ValueAnimator mLineValueAnimator;
    private int mLineCount;
    private int mCurrentHeight;
    private int mCurrentLine = -1;
    //换行时间
    private long mCurrentTime;
    // 大遮罩矩形
    private Rect mMaskRect;
    private boolean mIsRunning = false;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mLineCount = getLineCount();
            if (mLineCount > 0) {
                if (mLineValueAnimator.isRunning()) {
                    mLineValueAnimator.cancel();
                }
                mLineValueAnimator.setIntValues(0, getHeight());
                mLineValueAnimator.setDuration(LINE_ANIMATOR_DURATION * mLineCount);
                mLineValueAnimator.setInterpolator(new LinearInterpolator());
                mLineValueAnimator.addUpdateListener(LineByLineTextView.this);
                mMaskRect.left = 0;
                mMaskRect.top = 0;
                mMaskRect.right = getWidth();
                mMaskRect.bottom = getHeight();
                mLineValueAnimator.start();
                mIsRunning = true;

            }
        }
    };


    public LineByLineTextView(Context context) {
        this(context, null);
    }

    public LineByLineTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineByLineTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mMaskPaint = new Paint();
        mMaskPaint.setAntiAlias(true);
        mMaskPaint.setColor(mMaskColor);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(mMaskColor);

        mLineHeight = getLineHeight();
        mLineValueAnimator = new ValueAnimator();
        mMaskRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mLineCount = getLineCount();
        if (mLineCount > 0) {
            if (mLineValueAnimator.isRunning()) {
                mLineValueAnimator.cancel();
            }
            mLineValueAnimator.setIntValues(0, getHeight());
            mLineValueAnimator.setDuration(LINE_ANIMATOR_DURATION * mLineCount);
            mLineValueAnimator.setInterpolator(new LinearInterpolator());
            mLineValueAnimator.addUpdateListener(this);
            mLineValueAnimator.start();
            mMaskRect.left = 0;
            mMaskRect.top = 0;
            mMaskRect.right = getWidth();
            mMaskRect.bottom = getHeight();
        }


    }

    public void bindText(String text) {
        setText(text);
        post(mRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mRunnable);
        if (mLineValueAnimator != null && mLineValueAnimator.isRunning()) {
            mLineValueAnimator.removeAllUpdateListeners();
            mLineValueAnimator.cancel();
            mLineValueAnimator = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mIsRunning) return;
        super.onDraw(canvas);
        canvas.drawRect(mMaskRect, mMaskPaint);
        for (int i = mCurrentLine; i < mLineCount; i++) {
            if (i == mCurrentLine) {
                mLinePaint.setColor(getAlphaColor());
            }
            canvas.drawRect(new Rect(0, i * mLineHeight, getWidth(), (i + 1) * mLineHeight), mLinePaint);
        }
        mMaskRect.top = mLineHeight * (mCurrentLine + 1);

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        mCurrentHeight = (int) animation.getAnimatedValue();
        int line = mCurrentHeight / mLineHeight;
        Log.e("mCurrentLine", "line" + line + "mCurrentHeight" + mCurrentHeight + "mLineHeight" + mLineHeight);
        if (mCurrentLine != line) {
            mCurrentTime = System.nanoTime();
            mCurrentLine = line;
        }

        invalidate();
    }

    private @ColorInt
    int getAlphaColor() {
        int alpha = 255 - (int) ((System.nanoTime() - mCurrentTime) / 1000000f / LINE_ANIMATOR_DURATION * 255);
        Log.e("getAlphaColor", alpha + "");
        return ColorUtils.setAlphaComponent(mMaskColor, alpha < 0 ? 0 : alpha);
    }
}

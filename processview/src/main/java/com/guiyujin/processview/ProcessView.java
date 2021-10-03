package com.guiyujin.processview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ProcessView extends View {
    private Context context;

    //外弧颜色
    private int progressOuterColor;
    //内弧颜色
    private int progressInnerColor;
    //圆弧宽度
    private int progressBorderWidth = 10;
    //字体颜色
    private int progressTextColor;
    //字体大小
    private int progressTextSize = 20;

    private int processAddColor;

    private Paint progressOuterPaint, progressInnerPaint, processAddPaint, progressTextPaint;

    //最大进度
    private int progressMax = 100;
    //当前进度
    private int progressCurrent = 0;

    // 起始进度
    private int progressStart = 0;

    private int processEnd = 0;

    private int circleHeight;

    private boolean isComplete;

    private onCompleteListener onCompleteListener;

    @ProcessType
    private int type = AUTO_INCREASE;
    public static final int AUTO_INCREASE = 0;
    public static final int MANUAL_INCREASE = 1;

    private float percent;
    private String text;
    private Rect textBounds;
    private int bottom_height;
    private int duration;

    public int getProgressOuterColor() {
        return progressOuterColor;
    }

    public void setProgressOuterColor(int progressOuterColor) {
        this.progressOuterColor = progressOuterColor;
    }

    public int getProgressInnerColor() {
        return progressInnerColor;
    }

    public void setProgressInnerColor(int progressInnerColor) {
        this.progressInnerColor = progressInnerColor;
    }

    public int getProgressBorderWidth() {
        return progressBorderWidth;
    }

    public void setProgressBorderWidth(int progressBorderWidth) {
        this.progressBorderWidth = progressBorderWidth;
    }

    public int getProgressTextColor() {
        return progressTextColor;
    }

    public void setProgressTextColor(int progressTextColor) {
        this.progressTextColor = progressTextColor;
    }

    public int getProgressTextSize() {
        return progressTextSize;
    }

    public void setProgressTextSize(int progressTextSize) {
        this.progressTextSize = progressTextSize;
    }

    public int getProcessAddColor() {
        return processAddColor;
    }

    public void setProcessAddColor(int processAddColor) {
        this.processAddColor = processAddColor;
    }

    public int getProgressMax() {
        return progressMax;
    }

    public int getProgressCurrent() {
        return progressCurrent;
    }

    public int getProgressStart() {
        return progressStart;
    }

    public int getProcessEnd() {
        return processEnd;
    }

    public void setProcessEnd(int processEnd) {
        setProgressStart(this.processEnd);
        this.processEnd = processEnd;
    }

    public int getCircleHeight() {
        return circleHeight;
    }

    public void setCircleHeight(int circleHeight) {
        this.circleHeight = circleHeight;
    }

    public ProcessView.onCompleteListener getOnCompleteListener() {
        return onCompleteListener;
    }

    public void setOnCompleteListener(ProcessView.onCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    public int getType() {
        return type;
    }

    public void setType(@ProcessType int type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ProcessView(Context context) {
        this(context, null);
    }

    public ProcessView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProcessView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        progressOuterColor = ContextCompat.getColor(context, R.color.processOut);
        progressInnerColor = ContextCompat.getColor(context, R.color.processIn);
        progressTextColor = ContextCompat.getColor(context, R.color.processIn);
        processAddColor = ContextCompat.getColor(context, R.color.processIn);

        //获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressView);
        progressOuterColor = typedArray.getColor(R.styleable.ProgressView_progressOuterColor, progressOuterColor);
        progressInnerColor = typedArray.getColor(R.styleable.ProgressView_progressInnerColor, progressInnerColor);
        progressBorderWidth = typedArray.getDimensionPixelOffset(R.styleable.ProgressView_progressBorderWidth, progressBorderWidth);
        progressTextColor = typedArray.getColor(R.styleable.ProgressView_progressTextColor, progressTextColor);
        progressTextSize = typedArray.getDimensionPixelSize(R.styleable.ProgressView_progressTextSize, progressTextSize);
        typedArray.recycle();

        //初始化画笔
        progressOuterPaint = new Paint();
        progressOuterPaint.setAntiAlias(true);
        progressOuterPaint.setColor(progressOuterColor);
        progressOuterPaint.setStrokeWidth(progressBorderWidth);
        progressOuterPaint.setStyle(Paint.Style.STROKE);
        progressOuterPaint.setStrokeCap(Paint.Cap.ROUND);

        progressInnerPaint = new Paint();
        progressInnerPaint.setAntiAlias(true);
        progressInnerPaint.setColor(progressInnerColor);
        progressInnerPaint.setStrokeWidth(progressBorderWidth);
        progressInnerPaint.setStyle(Paint.Style.STROKE);
        progressInnerPaint.setStrokeCap(Paint.Cap.ROUND);

        processAddPaint = new Paint();
        processAddPaint.setAntiAlias(true);
        processAddPaint.setStrokeWidth(progressBorderWidth);
        processAddPaint.setStrokeCap(Paint.Cap.ROUND);
        processAddPaint.setColor(processAddColor);

        progressTextPaint = new Paint();
        progressTextPaint.setAntiAlias(true);
        progressTextPaint.setTextSize(progressTextSize);
        progressTextPaint.setColor(progressTextColor);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        checkType();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (MeasureSpec.AT_MOST == widthMode) {
            width = 40;
        }

        if (MeasureSpec.AT_MOST == heightMode) {
            height = 40;
        }

        text = "%";
        textBounds = new Rect();
        progressTextPaint.getTextBounds(text, 0, text.length(), textBounds);
        width = Math.min(height, width);
        if (type == 0) {
            height = Math.min(height, width);
        } else {
            circleHeight = Math.min(height, width);
            bottom_height = textBounds.height() + (textBounds.bottom - textBounds.top) / 2 - textBounds.bottom + 10;
            height = circleHeight + bottom_height;
        }

        //设置宽高
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        checkType();
        percent = (float) progressCurrent / progressMax;

        // 画内圆弧
        canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2 - progressBorderWidth / 2, progressOuterPaint);

        // 画外圆弧
        RectF rectF;
        if (type == 0) {
            rectF = new RectF(0 + progressBorderWidth / 2, 0 + progressBorderWidth / 2,
                    getHeight() - progressBorderWidth / 2, getHeight() - progressBorderWidth / 2);
        } else {
            rectF = new RectF(0 + progressBorderWidth / 2, 0 + progressBorderWidth / 2,
                    circleHeight - progressBorderWidth / 2, circleHeight - progressBorderWidth / 2);
        }
        canvas.drawArc(rectF, -90, percent * 360, false, progressInnerPaint);


        drawText(canvas, percent);
    }

    private void drawText(Canvas canvas, float percent) {
        percent = (float) progressCurrent / progressMax;
        // 画进度文字
        text = (int) (percent * 100) + "%";

        progressTextPaint.getTextBounds(text, 0, text.length(), textBounds);
        int dx = getWidth() / 2 - textBounds.width() / 2;
        if (type == AUTO_INCREASE) {
            int dy = (textBounds.bottom - textBounds.top) / 2 - textBounds.bottom;
            int baseLine = getHeight() / 2 + dy;
            canvas.drawText(text, dx, baseLine, progressTextPaint);
        } else {
            drawAdd(canvas);
            int baseLine = circleHeight + bottom_height;
            canvas.drawText(text, dx, baseLine, progressTextPaint);
        }

    }

    private void drawAdd(Canvas canvas) {
        int dx = circleHeight / 2;
        int dy = circleHeight / 2;
        int addWidth = 20;
        canvas.drawLine(dx, dy, dx - addWidth, dy, processAddPaint);
        canvas.drawLine(dx, dy, dx + addWidth, dy, processAddPaint);
        canvas.drawLine(dx, dy, dx, dy - addWidth, processAddPaint);
        canvas.drawLine(dx, dy, dx, dy + addWidth, processAddPaint);
    }

    public synchronized void setProgressMax(int max) {
        if (0 > max) {
            throw new IllegalArgumentException("最大值需大于0");
        }
        this.progressMax = max;
    }

    public synchronized void setProgressCurrent(int current) {
        if (progressMax < current) {
            throw new IllegalArgumentException("当前进度需小于最大值");
        }
        this.progressCurrent = current;
        invalidate();
    }

    public synchronized void setProgressStart(int progressStart) {
        if (0 > progressStart) {
            throw new IllegalArgumentException("起始值需大于0");
        }
        if (progressMax < progressStart) {
            throw new IllegalArgumentException("起始值需小于最大值");
        }
        this.progressStart = progressStart;
    }


    public void start() {
        checkType();
        ValueAnimator valueAnimator = ObjectAnimator.ofFloat(progressStart, processEnd);
        duration = 2000;
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                if (animatedValue == progressMax && !isComplete) {
                    isComplete = true;
                    onCompleteListener.onComplete();
                    processAddPaint.setColor(getProgressOuterColor());
                    invalidate();
                }
                setProgressCurrent((int) animatedValue);
            }
        });
        valueAnimator.start();
    }

    @IntDef({AUTO_INCREASE, MANUAL_INCREASE})
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.SOURCE)
    @interface ProcessType {
    }


    private void checkType() {
        if (type != AUTO_INCREASE && type != MANUAL_INCREASE) {
            throw new IllegalArgumentException("进度错误");
        }
    }

    public interface onCompleteListener {
        void onComplete();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (type != MANUAL_INCREASE || isComplete) {
            return false;
        }
        return super.onTouchEvent(event);
    }
}

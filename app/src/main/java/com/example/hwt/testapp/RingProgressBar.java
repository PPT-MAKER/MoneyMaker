package com.example.hwt.testapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class RingProgressBar extends View {
    /**
     * 画笔对象的引用
     */
    private Paint paint;

    /**
     * 圆环的颜色
     */
    private int ringColor;
    /**
     * 中间圆的颜色
     */
    private int circleColor;
    /**
     * 圆环进度的颜色
     */
    private int progressColor;

    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor;

    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize;

    /**
     * 最大进度
     */
    private int max;

    /**
     * 当前进度
     */
    private int progress;

    /**
     * 进度的风格，实心或者空心
     */
    private int style;

    /**
     *百分号& 下载速度的字体大小
     */
    private int detailTextColor;

    /**
     *百分号& 下载速度的字体颜色
     */
    private float detailTextSize;

    public static final int STROKE = 0;
    public static final int FILL = 1;

    public RingProgressBar(Context context) {
        this(context, null);
    }

    public RingProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();
        paint.setAntiAlias(true);//消除锯齿
        //获取自定义属性和默认值
        max = 100;
        textColor = context.getResources().getColor(R.color.black);//字体颜色
        textSize = 110.0f;//字体大小
        circleColor = Color.TRANSPARENT;//中间圆的颜色,默认透明
        ringColor = context.getResources().getColor(R.color.color_FFF2F3F7);//外层初始圆环的颜色，默认透明
        style = STROKE;
        progressColor = context.getResources().getColor(R.color.black);//外层进度环的颜色，默认蓝色
        detailTextColor = context.getResources().getColor(R.color.color_FF999999);
        detailTextSize = 30.0f;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int ringWidth = getWidth() / 40;
        //1.画最外层的圆
        int center = getWidth() / 2; //获取圆心的x坐标
        int radius = (int) (center - ringWidth / 2); //圆环的半径
        paint.setStrokeWidth(ringWidth); //设置圆环的宽度
        paint.setColor(ringColor); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        canvas.drawCircle(center, center, radius, paint); //画出圆环
        //2.画中间圆环
        paint.setColor(circleColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(center, center, center - ringWidth, paint); //画出圆环
        //3.画进度圆环
        paint.setStrokeWidth(ringWidth); //设置圆环的宽度
        paint.setColor(progressColor); //设置进度的颜色
        RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius); //用于定义的圆弧的形状和大小的界限
        switch (style) {
            case STROKE: {//空心进度环
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, -90, 360 * progress / max, false, paint);  //根据进度画圆弧
                break;
            }
            case FILL: {//实心进度环
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (progress != 0) {
                    canvas.drawArc(oval, -90, 360 * progress / max, true, paint);  //根据进度画圆弧
                }
                break;
            }
        }
        //4.画进度百分比
        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        int percent = (int) (((float) progress / (float) max) * 100); //中间的进度百分比，先转换成float在进行除法运算，不然都为0
        float textWidth = paint.measureText("" + percent);   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        canvas.drawText(percent + "", center - textWidth / 2, center + textSize / 2 - 40, paint); //画出进度百分比
        paint.setColor(detailTextColor);
        paint.setTextSize(detailTextSize);
        canvas.drawText("%", center + textWidth / 2, center - 40, paint); //画出百分号
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }
    }

    public int getProgress() {
        return progress;
    }
}

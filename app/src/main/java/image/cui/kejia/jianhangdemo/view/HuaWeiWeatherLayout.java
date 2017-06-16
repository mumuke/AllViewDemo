package image.cui.kejia.jianhangdemo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import image.cui.kejia.jianhangdemo.R;

/**
 * Created by ckj on 2017/6/14.
 */

public class HuaWeiWeatherLayout extends View {
    //圆弧半径
    private float mRadius;
    //圆弧paint
    private Paint mArcPaint;
    //圆弧的角度
    private int mSweepAngle = 300;
    //开始角度
    private int mStartAngle = 120;

    //小短线paint
    private Paint mLinePaint;
    //刻度分数
    private int count = 60;
    //最小温度
    private int minTemp = 20;
    //最大温度
    private int maxTemp = 27;
    //0度初始角
    private int ocAngle = 230;
    //总覆盖角度
    private int fgAngle = 90;
    //现在的温度
    private int currentTemp = 26;

    //文字和图片paint
    private Paint mTextPaint;
    private Bitmap mBitmap;

    //动态温度Paint
    private Paint mPointPaint;
    private int offset = 22;

    public HuaWeiWeatherLayout(Context context) {
        this(context, null);
    }

    public HuaWeiWeatherLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HuaWeiWeatherLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mArcPaint = new Paint();
        mArcPaint.setColor(Color.WHITE);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(3f);
        mArcPaint.setAntiAlias(true);

        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeWidth(4);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(144);
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.cloudy);

//        mPointPaint = new Paint();
//        mPointPaint.setColor(Color.WHITE);
//        mPointPaint.setAntiAlias(true);
//        mPointPaint.setStrokeWidth(4);
//        mPointPaint.setTextAlign(Paint.Align.CENTER);
//        mPointPaint.setTextSize(24);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wrap_len = 600;
        int width = measureDimension(wrap_len, widthMeasureSpec);
        int height = measureDimension(wrap_len, heightMeasureSpec);
        int len = Math.min(width, height);
        //确定是一个正方形
        setMeasuredDimension(len, len);
    }

    /**
     * 看模式：是EXACTLY模式则就是这个尺寸，UNSPECIFIED是设置一个默认值，AT_MOST是默认值和测量值的最小值
     *
     * @param defaultSize
     * @param measureSpec
     * @return
     */
    private int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int mWidth = getWidth();
        int mHeight = getHeight();
        mRadius = (mWidth - getPaddingLeft() - getPaddingRight()) / 2;
        canvas.translate(mWidth / 2, mHeight / 2);
        //画圆弧(1)
//        drawArcView(canvas);
        //画小短线(2)
        drawLineView(canvas);
        //画中间的温度和底下的图标(3)
        drawTextBitmapView(canvas);
        //画动态温度(4)
        drawTempLineView(canvas);
    }

    /**
     * 画圆弧
     *
     * @param canvas
     */
    private void drawArcView(Canvas canvas) {
        RectF mRectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);
        canvas.drawRect(mRectF, mArcPaint);
        canvas.drawArc(mRectF, mStartAngle, mSweepAngle, false, mArcPaint);
    }

    /**
     * 画小短线
     *
     * @param canvas
     */
    private void drawLineView(Canvas canvas) {
        canvas.save();
        float angle = (float) (mSweepAngle / count);
        //旋转到正上方
        canvas.rotate(-270 + mStartAngle);
        for (int i = 0; i <= count; i++) {
            if (i == 0 || i == count) {
                mLinePaint.setStrokeWidth(1);
                mLinePaint.setColor(Color.WHITE);
                canvas.drawLine(0, -mRadius, 0, -mRadius + 40, mLinePaint);
            } else if (i >= getStartLineIndex(minTemp, maxTemp) && i <= getEndLineIndex(minTemp, maxTemp)) {
                mLinePaint.setStrokeWidth(3);
                mLinePaint.setColor(getRealColor(minTemp, maxTemp));
                canvas.drawLine(0, -mRadius, 0, -mRadius + 30, mLinePaint);
            } else {
                mLinePaint.setStrokeWidth(2);
                mLinePaint.setColor(Color.WHITE);
                canvas.drawLine(0, -mRadius, 0, -mRadius + 30, mLinePaint);
            }
            canvas.rotate(angle);
        }
        canvas.restore();
    }

    /**
     * 画文字和下面的图片
     *
     * @param canvas
     */
    private void drawTextBitmapView(Canvas canvas) {
        mTextPaint.setTextSize(144);
        canvas.drawText(currentTemp + "°", 0, getTextPaintOffset(mTextPaint), mTextPaint);
        canvas.drawBitmap(mBitmap, 0 - mBitmap.getWidth() / 2, mRadius - mBitmap.getHeight() / 2 - 100, null);
    }

    /**
     * 画动态温度
     *
     * @param canvas
     */
    private void drawTempLineView(Canvas canvas) {
        mTextPaint.setTextSize(24);
        int startTempAngle = getStartAngle(minTemp, maxTemp);
        canvas.drawText(minTemp + "°", getRealCosX(startTempAngle, offset, true), getRealSinY(startTempAngle, offset, true), mTextPaint);
        canvas.drawText(maxTemp + "°", getRealCosX(startTempAngle + fgAngle, offset, true), getRealSinY(startTempAngle + fgAngle, offset, true), mTextPaint);
    }

    /**
     * 得到开始短线索引(2)
     *
     * @param minTemp
     * @param maxTemp
     * @return
     */
    private int getStartLineIndex(int minTemp, int maxTemp) {
        return (getStartAngle(minTemp, maxTemp) - mStartAngle) / (mSweepAngle / count);
    }

    /**
     * 得到结束短线索引(2)
     *
     * @param minTemp
     * @param maxTemp
     * @return
     */
    private int getEndLineIndex(int minTemp, int maxTemp) {
        return (getStartAngle(minTemp, maxTemp) - mStartAngle) / (mSweepAngle / count) + fgAngle / (mSweepAngle / count);
    }

    /**
     * 根据当天温度得到起始角度(2)
     *
     * @param minTemp
     * @param maxTemp
     * @return
     */
    private int getStartAngle(int minTemp, int maxTemp) {
        int startFgAngle = 0;
        if (minTemp >= maxTemp) {
            return startFgAngle;
        }
        if (minTemp <= 0) {
            startFgAngle = ocAngle - (0 - minTemp) * fgAngle / (maxTemp - minTemp);
        } else {
            startFgAngle = ocAngle + (minTemp - 0) * fgAngle / (maxTemp - minTemp);
        }
        //边界开始
        if (startFgAngle <= mStartAngle) {//如果开始界小于startAngle，防止过边界
            startFgAngle = mStartAngle + 10;
        } else if ((startFgAngle + fgAngle) > (mStartAngle + mSweepAngle)) {//结束角大于最后的边界
            startFgAngle = mStartAngle + mSweepAngle - 20 - fgAngle;
        }
        //边界结束
        return startFgAngle;
    }

    /**
     * 得到温度范围的真是颜色(2)
     *
     * @param minTemp
     * @param maxTemp
     * @return
     */
    private int getRealColor(int minTemp, int maxTemp) {
        if (maxTemp <= 0) {
            //深海蓝
            return Color.parseColor("#00008B");
        } else if (minTemp <= 0 && maxTemp > 0) {
            //黄君兰
            return Color.parseColor("#4169E1");
        } else if (minTemp > 0 && minTemp < 15) {
            //宝石绿
            return Color.parseColor("#40E0D0");
        } else if (minTemp >= 15 && minTemp < 25) {
            //酸橙绿
            return Color.parseColor("#00FF00");
        } else if (minTemp >= 25 && minTemp < 30) {
            //金色
            return Color.parseColor("#FFD700");
        } else if (minTemp >= 30) {
            //印度红
            return Color.parseColor("#CD5C5C");
        }
        //酸橙绿
        return Color.parseColor("#00FF00");
    }

    /**
     * 得到中间文字在Y轴的偏移量(3)
     *
     * @param mTextPaint
     * @return
     */
    private float getTextPaintOffset(Paint mTextPaint) {
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        return -fontMetrics.descent + (fontMetrics.bottom - fontMetrics.top) / 2;
    }

    /**
     * 得到动态温度的X轴(4)
     *
     * @param startTempAngle
     * @param off
     * @param outoff
     * @return
     */
    private float getRealCosX(int startTempAngle, int off, boolean outoff) {
        if (!outoff) {
            off = -off;
        }
        if (getCosX(startTempAngle) < 0) {
            return getCosX(startTempAngle) - off;
        } else {
            return getCosX(startTempAngle) + off;
        }
    }

    /**
     * 得到动态温度的Y轴(4)
     *
     * @param startTempAngle
     * @param off
     * @param outoff
     * @return
     */
    private float getRealSinY(int startTempAngle, int off, boolean outoff) {
        if (!outoff) {
            off = -off;
        }
        if (getSinY(startTempAngle) < 0) {
            return getSinY(startTempAngle) - off;
        } else {
            return getSinY(startTempAngle) + off;
        }
    }

    /**
     * 获得角度的cosX(4)
     *
     * @param angle
     * @return
     */
    private float getCosX(int angle) {
        return (float) (mRadius * Math.cos(angle * Math.PI / 180)) + getTextPaintOffset(mTextPaint);
    }

    /**
     * 获得角度的SinY(4)
     *
     * @param angle
     * @return
     */
    private float getSinY(int angle) {
        return (float) (mRadius * Math.sin(angle * Math.PI / 180)) + getTextPaintOffset(mTextPaint);
    }
}

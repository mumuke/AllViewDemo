package image.cui.kejia.jianhangdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import image.cui.kejia.jianhangdemo.R;

/**
 * Created by ckj on 2017/6/12.
 * 中间的整体圆（实现建行的转盘效果）View里没有dispatchTouchEnvent()这个方法，不会再分发事件，就是自己处理
 * dispatchTouchEvent()和onInterceptTouchEvent()和OnTouchEvent()返回true都是不向下执行，返回false就是继续做事
 */

public class CircleMenuLayout extends ViewGroup {
    //菜单项的图标
    private int[] mItemImgs;
    //菜单项的文本
    private String[] mItemTexts;
    //菜单项的个数
    private int mMenuCount;
    //半径
    private int mRadius;
    //该容器内child item的默认尺寸
    private static final float RADIO_DEFAULT_CHILD_DIMENSION = 1 / 4f;
    //菜单的中心child的默认尺寸
    private static final float RADIO_DEFAULT_CENTERITEM_DIMENSION = 1 / 3f;
    //无视padding，如需要padding则使用这个常量
    private static final float RADIO_PADDING_LAYOUT = 1 / 12f;

    //当每秒移动角度超多这个值时则定义为快速旋转
    private static final int FLINGABLE_VALUE = 300;
    //当移动角度达到该值，则屏蔽点击
    private static final int NOCLICK_VALUE = 3;
    //当每秒转动角度超过这个即为快速旋转
    private int mFlingable = FLINGABLE_VALUE;
    private float mPadding;
    //布局开始时的角度
    private int mStartAngle = 0;
    //计算从按下到抬起旋转的角度
    private float mTemAngle;
    //计算从按下到抬起的时间
    private float mDownTime;
    //判断是否在自动旋转
    private boolean mIsFling;


    private onMenuItemClickListener mOnMenuItemClickListener;

    public interface onMenuItemClickListener {
        void itemClick(View view, int pos);

        void itemCenterClick(View view);
    }

    public void setOnMenuItemClickListener(onMenuItemClickListener mOnMenuItemClickListener) {
        this.mOnMenuItemClickListener = mOnMenuItemClickListener;
    }

    public CircleMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //无视Padding
        setPadding(0, 0, 0, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int resWidth = 0;
        int resHeight = 0;

        //根据传入参数，分别得到测量值和测量模式
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {
            //如果宽或高的测量模式为非精准模式

            //设置背景图的宽
            resWidth = getSuggestedMinimumWidth();
            //如果未设置背景图，则设置屏幕的默认宽度
            resWidth = resWidth == 0 ? getDefaultWidth() : resWidth;

            resHeight = getSuggestedMinimumHeight();
            resHeight = resHeight == 0 ? getDefaultWidth() : resHeight;
        } else {
            //如果为精准值，则取最小值,
            resWidth = resHeight = Math.min(width, height);
        }

        setMeasuredDimension(resWidth, resHeight);

        //获取半径
        mRadius = Math.max(getMeasuredWidth(), getMeasuredHeight());

        //获取menu item 的数量
        int count = getChildCount();
        //获取menu item的尺寸
        int childSize = (int) (mRadius * RADIO_DEFAULT_CHILD_DIMENSION);
        //设置menu item的模式
        int childMode = MeasureSpec.EXACTLY;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            int makeMeasureSpec = -1;
            if (child.getId() == R.id.id_circle_menu_item_center) {
                makeMeasureSpec = MeasureSpec.makeMeasureSpec((int) (mRadius * RADIO_DEFAULT_CHILD_DIMENSION), childMode);
            } else {
                makeMeasureSpec = MeasureSpec.makeMeasureSpec(childSize, childMode);
            }
            child.measure(makeMeasureSpec, makeMeasureSpec);
        }

        mPadding = RADIO_PADDING_LAYOUT * mRadius;

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int layoutRadius = mRadius;

        // Laying out the child views
        final int childCount = getChildCount();

        int left, top;
        // menu item 的尺寸
        int cWidth = (int) (layoutRadius * RADIO_DEFAULT_CHILD_DIMENSION);

        // 根据menu item的个数，计算角度
        double angleDelay = (double) 360 / (getChildCount() - 1);

        // 遍历去设置menuitem的位置
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);

            if (child.getId() == R.id.id_circle_menu_item_center)
                continue;

            if (child.getVisibility() == GONE) {
                continue;
            }

            mStartAngle %= 360;

            // 计算，中心点到menu item中心的距离
            float tmp = layoutRadius / 2f - cWidth / 2 - mPadding;

            // tmp cosa 即menu item中心点的横坐标
            left = layoutRadius
                    / 2
                    + (int) Math.round(tmp
                    * Math.cos(Math.toRadians(mStartAngle)) - 1 / 2f
                    * cWidth);
            // tmp sina 即menu item的纵坐标
            top = layoutRadius
                    / 2
                    + (int) Math.round(tmp
                    * Math.sin(Math.toRadians(mStartAngle)) - 1 / 2f
                    * cWidth);

            child.layout(left, top, left + cWidth, top + cWidth);
            // 叠加尺寸
            mStartAngle += angleDelay;
        }

        // 找到中心的view，如果存在设置onclick事件
        View cView = findViewById(R.id.id_circle_menu_item_center);
        if (cView != null) {
            cView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mOnMenuItemClickListener != null) {
                        mOnMenuItemClickListener.itemCenterClick(v);
                    }
                }
            });
            // 设置center item位置
            int cl = layoutRadius / 2 - cView.getMeasuredWidth() / 2;
            int cr = cl + cView.getMeasuredWidth();
            cView.layout(cl, cl, cr, cr);
        }
    }

    /**
     * 记录上一次的x，y
     */
    private float mLastX;
    private float mLastY;
    private AutoFlingRunnable mFlingRunnable;

    //事件传递
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        Log.i("TAG", "x:" + x + "+++++++y:" + y);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e("TAG", "ACTION_DOWN");
                mLastX = x;
                mLastY = y;
                mDownTime = System.currentTimeMillis();
                mTemAngle = 0;
                if (mIsFling) {
                    removeCallbacks(mFlingRunnable);
                    mIsFling = false;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("TAG", "ACTION_MOVE");
                //获取开始角度
                float start = getAngle(mLastX, mLastY);
                //获取当前角度
                float end = getAngle(x, y);

                if (getQuadrant(x, y) == 1 || getQuadrant(x, y) == 4) {
                    //第一，四象限，直接end-start，角度值是正直
                    mStartAngle += end - start;
                    mTemAngle += end - start;
                } else {
                    //第二，三象限，角度值是负值
                    mStartAngle += start - end;
                    mStartAngle += start - end;
                }
                //重新布局
                requestLayout();

                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                Log.e("TAG", "ACTION_UP");
                float anglePerSecond = mTemAngle * 1000 / (System.currentTimeMillis() - mDownTime);
                //达到该值则认为是快速移动
                if (Math.abs(anglePerSecond) > mFlingable && !mIsFling) {
                    //post一个任务，自动滚动
                    post(mFlingRunnable = new AutoFlingRunnable(anglePerSecond));
                    return true;
                }

                //如果旋转角度超过NOLICK_VALUE则屏蔽点击
                if (Math.abs(mTemAngle) > NOCLICK_VALUE) {
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 主要为了action_down时，返回true
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /**
     * 根据触摸位置，计算结果
     *
     * @param xTouch
     * @param yTouch
     * @return
     */
    private float getAngle(float xTouch, float yTouch) {
        double x = xTouch - mRadius / 2d;
        double y = yTouch - mRadius / 2d;
        return (float) (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }

    /**
     * 根据当前位置判断是第几象限
     *
     * @param x
     * @param y
     * @return
     */
    private int getQuadrant(float x, float y) {
        int tmpX = (int) (x - mRadius / 2);
        int tmpY = (int) (y - mRadius / 2);
        if (tmpX > 0) {
            return tmpY >= 0 ? 4 : 1;
        } else {
            return tmpY >= 0 ? 3 : 2;
        }
    }

    /**
     * 自动滚动任务
     */
    private class AutoFlingRunnable implements Runnable {
        private float anglePerSecond;

        public AutoFlingRunnable(float velocity) {
            this.anglePerSecond = velocity;
        }

        @Override
        public void run() {
            if ((int) Math.abs(anglePerSecond) < 20) {
                mIsFling = false;
                return;
            }
            mIsFling = true;
            mStartAngle += (anglePerSecond / 30);
            anglePerSecond /= 1.0666F;
            postDelayed(this, 30);
            requestLayout();
        }
    }


    /**
     * 获取默认的布局的尺寸
     *
     * @return
     */
    private int getDefaultWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return Math.min(dm.widthPixels, dm.heightPixels);
    }

    /**
     * 设置菜单项的图标和文本
     *
     * @param resIds
     * @param texts
     */
    public void setMenuItemIconsAndTexts(int[] resIds, String[] texts) {
        this.mItemImgs = resIds;
        this.mItemTexts = texts;

        //参数检查
        if (resIds == null && texts == null) {
            throw new IllegalArgumentException("菜单项文本和图片至少设置一项");
        }

        //初始化mMenuCount
        mMenuCount = resIds == null ? texts.length : resIds.length;

        if (resIds != null && texts != null) {
            mMenuCount = Math.min(resIds.length, texts.length);
        }

        addMenuItems();
    }

    /**
     * 添加菜单项
     */
    private void addMenuItems() {
        LayoutInflater mInflater = LayoutInflater.from(getContext());

        for (int i = 0; i < mMenuCount; i++) {
            final int j = i;
            View view = mInflater.inflate(R.layout.circle_menu_item, this, false);
            ImageView iv = (ImageView) view.findViewById(R.id.id_circle_menu_item_image);
            TextView tv = (TextView) view.findViewById(R.id.id_circle_menu_item_text);

            if (iv != null) {
                iv.setVisibility(View.VISIBLE);
                iv.setImageResource(mItemImgs[i]);
                iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnMenuItemClickListener != null) {
                            mOnMenuItemClickListener.itemClick(v, j);
                        }
                    }
                });
            }

            if (tv != null) {
                tv.setVisibility(View.VISIBLE);
                tv.setText(mItemTexts[i]);
            }

            addView(view);
        }
    }
}

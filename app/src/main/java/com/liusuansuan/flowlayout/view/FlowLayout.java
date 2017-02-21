package com.liusuansuan.flowlayout.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 流式布局主要就是测量和摆放的位置
 * Created by suansuan on 2017/2/21.
 */
@SuppressWarnings("all")
public class FlowLayout extends ViewGroup {

    private static final String Tag = "lala" ;

    private List<View> mLineViews ;
    private List<Integer> mLineHeights, mLineWidth ;
    private List<List<View>> mAllViews ;

    private int mWidth, mHeight, mCount;

    public FlowLayout(Context context) {
        this(context, null);
    }
    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initFlowLayout() ;
    }

    /** 初始化FlowLayout */
    private void initFlowLayout() {
        mLineViews = new ArrayList<>();
        mLineHeights = new ArrayList<>();
        mLineWidth = new ArrayList<>() ;
        mAllViews = new ArrayList<>() ;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        mCount = getChildCount() ;
        int lineWidth = 0, lineHeight = 0;

        for (int i = 0; i < mCount; i++) {
            View childView = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            //考虑View为Gone的时候情况。
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            int childViewWidth = childView.getWidth() + lp.leftMargin + lp.rightMargin ;
            int childViewHeight = childView.getHeight() + lp.topMargin + lp.bottomMargin ;
            if (lineWidth + childViewWidth < widthSize - getPaddingRight() - getPaddingLeft()) {
                /* 不换行的情况 */
                lineWidth += childViewWidth;
                lineHeight = Math.max(childViewHeight, lineHeight) ;
            } else {
                mWidth = Math.max(mWidth, lineWidth) ;
                mHeight = mHeight + childViewHeight ;
                Log.i(Tag, ">>>>>>>------mHeight " + mHeight) ;
                lineHeight = childViewHeight ;
                lineWidth = childViewWidth ;
            }
            if (i == mCount - 1) {
                mWidth = Math.max(mWidth, lineWidth) ;
            }
        } // for end
        setMeasuredDimension(
                widthMode == MeasureSpec.EXACTLY ? widthSize : mWidth + getPaddingLeft() + getPaddingRight(),
                heightMode == MeasureSpec.EXACTLY ? heightSize : mHeight + getPaddingTop() + getPaddingRight());
        Log.i(Tag, ">>>>>>>------Totle Height = " + getMeasuredHeight()) ;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        clearAllList();
        int width = getMeasuredWidth() ;
        int lineWidth = 0, lineHeight = 0;
        mCount = getChildCount() ;
        for (int i = 0; i < mCount; i++) {
            View childAt = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) childAt.getLayoutParams();
            int childViewWidth = childAt.getMeasuredWidth() + lp.leftMargin + lp.rightMargin ;
            int childViewHeight = childAt.getMeasuredHeight() + lp.topMargin + lp.bottomMargin ;
            if (lineWidth + childViewWidth > getMeasuredWidth()- getPaddingLeft() - getPaddingRight()) {
                /* 换行 */
                mAllViews.add(mLineViews);
                mLineHeights.add(lineHeight);
                mLineWidth.add(lineWidth) ;

                lineWidth = 0 ;
                lineHeight = childViewHeight ;
                mLineViews = new ArrayList<>();
            }
            lineWidth += childViewWidth ;
            lineHeight = Math.max(lineHeight, childViewHeight) ;
            mLineViews.add(childAt) ;
        }
        mAllViews.add(mLineViews);
        mLineHeights.add(lineHeight);
        mLineWidth.add(lineWidth) ;

        int left = getPaddingLeft(), top = getPaddingTop() ;
        int mLines = mAllViews.size();

        for(int i = 0; i < mLines; i++){
            List<View> views = mAllViews.get(i);
            int height = mLineHeights.get(i);

            for(int j = 0; j < views.size(); j++){
                View view = views.get(j);
                if(view.VISIBLE == View.GONE){ continue; }
                MarginLayoutParams lp = (MarginLayoutParams) view.getLayoutParams();
                int viewWidth = view.getMeasuredWidth() + lp.leftMargin + lp.leftMargin ;
                int leftChlid = left + lp.leftMargin ;
                int topChlid = top + lp.topMargin ;
                int rightChlid = leftChlid + view.getMeasuredWidth() ;
                int buttomChlid = topChlid + view.getMeasuredHeight() ;
                view.layout(leftChlid, topChlid, rightChlid, buttomChlid);

                left += viewWidth ;
            }
            top += lineHeight;
            left = getPaddingLeft() ;
        }
    }

    /** 由于这个方法有可能调用多遍 */
    protected void clearAllList() {
        mLineViews.clear();
        mLineHeights.clear();
        mLineWidth.clear();
        mAllViews.clear();
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(
                MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }
}

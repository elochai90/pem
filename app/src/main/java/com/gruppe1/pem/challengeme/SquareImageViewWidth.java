package com.gruppe1.pem.challengeme;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Square Image View
 */
public class SquareImageViewWidth extends ImageView {
    public SquareImageViewWidth(Context context) {
        super(context);
    }

    public SquareImageViewWidth(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageViewWidth(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredHeight()); //Snap to width
    }
}

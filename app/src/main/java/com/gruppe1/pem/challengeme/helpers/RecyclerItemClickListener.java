package com.gruppe1.pem.challengeme.helpers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by bianka on 07.11.2015.
 */

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    protected OnItemClickListener listener;

    protected GestureDetector gestureDetector;
    private int childViewPosition;
    @Nullable
    private View childView;
    private RecyclerView parentView;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onItemLongClick(RecyclerView parentView, View view, int position);
    }

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        this.listener = listener;
        this.gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        parentView = view;
        childView = view.findChildViewUnder(e.getX(), e.getY());
        childViewPosition = view.getChildPosition(childView);

        return childView != null && gestureDetector.onTouchEvent(e);
    }

    @Override public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) { }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    protected class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            if (childView != null) {
                listener.onItemClick(childView, childViewPosition);
            }

            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            if (childView != null) {
                listener.onItemLongClick(parentView,childView, childViewPosition);
            }
        }

        @Override
        public boolean onDown(MotionEvent event) {
            // Best practice to always return true here.
            // http://developer.android.com/training/gestures/detector.html#detect
            return true;
        }

    }
}
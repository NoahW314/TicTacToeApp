package com.example.tictactoe.Views;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tictactoe.Activities.BoardActivity;
import com.example.tictactoe.R;

public class BoardLayout extends ViewGroup {

    private Paint linePaint;
    private int lineColor = Color.BLACK;

    public BoardLayout(Context context) {
        super(context);

        initDrawing();

        addViews(context);
    }

    public BoardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BoardLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initDrawing();

        addViews(context);
    }

    public void redraw(){
        for(int i = 0; i < getChildCount(); i++){
            getChildAt(i).invalidate();
        }
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public void addViews(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        for(int i = 0; i < 9; i++){
            SectionButton button = (SectionButton) ((BoardLayout)inflater.inflate(R.layout.section_button, this)).getChildAt(i);
            button.setStateListAnimator(null);
            button.setOnClickListener(BoardActivity.boxListener);
            button.setId(i);
            button.sectionNum = i;
        }

        EndMarker endMarker = new EndMarker(context);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(endMarker, layoutParams);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int minDimension = Math.min(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(minDimension, minDimension);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        for (int i = 0; i < 9; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                // Place the child.
                child.layout(left+(i%3)*getMeasuredWidth()/3+5, top+(i-i%3)/3*getMeasuredHeight()/3+5,
                        left+(i%3+1)*getMeasuredWidth()/3-5, top+((i-i%3)/3+1)*getMeasuredHeight()/3-5);
            }
        }

        final View marker = getChildAt(9);
        if(marker.getVisibility() != GONE){
            marker.layout(left, top, right, bottom);
        }
    }

    public void onCatsGame(){
        redrawWinOverlay();
    }
    public void onWinner(int[] rowLocations){
        System.arraycopy(rowLocations, 0, ((EndMarker)getChildAt(9)).winningThree, 0, 3);
        redrawWinOverlay();
    }
    public void redrawWinOverlay(){
        this.post(new Runnable() {
            public void run() { getChildAt(9).invalidate(); }
        });
    }

    public void initDrawing(){
        setWillNotDraw(false);
        linePaint = new Paint();
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(10);
        linePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void onDraw(Canvas canvas){
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        for(int i = 1; i < 3; i++) {
            int x = i*width/3;
            canvas.drawLine(x, 0, x, height, linePaint);
        }
        for(int i = 1; i < 3; i++){
            int y = i*height/3;
            canvas.drawLine(0, y, width, y, linePaint);
        }
        getChildAt(9).draw(canvas);
    }
}
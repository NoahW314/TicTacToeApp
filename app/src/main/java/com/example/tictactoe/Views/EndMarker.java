package com.example.tictactoe.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.example.tictactoe.Activities.BoardActivity;

public class EndMarker extends AppCompatTextView {

    Paint paint = new Paint();
    int[] winningThree = new int[]{-1, -1, -1};
    int[] resetThree = new int[]{-1, -1, -1};

    public EndMarker(Context context) {
        super(context);

        paint.setStrokeWidth(20);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
    }
    public EndMarker(Context context, AttributeSet attributeSet){
        super(context, attributeSet);

        paint.setStrokeWidth(20);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void onDraw(Canvas canvas){
        int minDim = Math.min(getHeight(), getWidth());
        switch(BoardActivity.manager.gameStatus){
            case IN_PROGRESS:
                System.arraycopy(resetThree, 0, winningThree, 0, 3);
                break;
            case CATS_GAME:
                canvas.drawArc(getLeft()+0.1f*minDim, getTop()+0.1f*minDim, getLeft()+0.9f*minDim, getTop()+0.9f*minDim, 45, 270, false, paint);
                break;
            case WON_1:
            case WON_2:
                if(winningThree[0] != -1) {
                    int start = winningThree[0];
                    int stop = winningThree[2];
                    if(start%3 == stop%3){
                        //vertical line
                        canvas.drawLine((start%3+0.5f)*minDim/3, ((start-start%3)/3+0.1f)*minDim/3,
                                (stop%3+0.5f)*minDim/3, ((stop-stop%3)/3+0.9f)*minDim/3, paint);
                    }
                    else if(start-start%3 == stop-stop%3){
                        //horizontal line
                        canvas.drawLine((start%3+0.1f)*minDim/3, ((start-start%3)/3+0.5f)*minDim/3,
                                (stop%3+0.9f)*minDim/3, ((stop-stop%3)/3+0.5f)*minDim/3, paint);
                    }
                    else if(start == 0){
                        //diagonal line (starting in top left corner)
                        canvas.drawLine((start%3+0.1f)*minDim/3, ((start-start%3)/3+0.1f)*minDim/3,
                                (stop%3+0.9f)*minDim/3, ((stop-stop%3)/3+0.9f)*minDim/3, paint);
                    }
                    else{
                        //other diagonal line
                        canvas.drawLine((start%3+0.9f)*minDim/3, ((start-start%3)/3+0.1f)*minDim/3,
                                (stop%3+0.1f)*minDim/3, ((stop-stop%3)/3+0.9f)*minDim/3, paint);
                    }
                }
                break;
        }
    }
}

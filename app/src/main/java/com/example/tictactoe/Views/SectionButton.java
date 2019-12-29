package com.example.tictactoe.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.example.tictactoe.Activities.BoardActivity;

public class SectionButton extends AppCompatButton {

    public int sectionNum;
    public static Paint OPaint;
    public static int OColor = Color.RED;
    public static Paint XPaint;
    public static int XColor = Color.BLUE;
    public static Paint TPaint;

    public SectionButton(Context context){
        super(context);

        initDrawing();
    }
    public SectionButton (Context context, AttributeSet attributeSet){
        super(context, attributeSet);

        initDrawing();
    }

    public void initDrawing(){
        OPaint = new Paint();
        OPaint.setColor(OColor);
        OPaint.setStyle(Paint.Style.STROKE);
        OPaint.setStrokeWidth(10);

        XPaint = new Paint();
        XPaint.setColor(XColor);
        XPaint.setStyle(Paint.Style.STROKE);
        XPaint.setStrokeWidth(10);

        TPaint = new Paint();
        TPaint.setColor(Color.BLACK);
        TPaint.setStyle(Paint.Style.STROKE);
        TPaint.setStrokeWidth(3);
        TPaint.setTextSize(50);
    }

    @Override
    public void onDraw(Canvas canvas){
        if(BoardActivity.manager != null && !BoardActivity.manager.inStatMode()) {
            switch (BoardActivity.manager.getBoardSectionMarker(sectionNum)) {
                case O:
                    canvas.drawCircle(getWidth() / 2, getHeight() / 2, Math.min(getWidth(), getHeight()) / 3, OPaint);
                    break;
                case X:
                    canvas.drawLine(getWidth() * 0.2f, getHeight() * 0.2f,
                            getHeight() * 0.8f, getWidth() * 0.8f, XPaint);
                    canvas.drawLine(getWidth() * 0.2f, getHeight() * 0.8f,
                            getHeight() * 0.8f, getHeight() * 0.2f, XPaint);
                    break;
                default:
                    //canvas.drawText(getId()+"", getHeight()*0.5f, getWidth()*0.5f, TPaint);
                    break;
            }
        }
    }

    public enum Marker{
        X(2), O(1), NONE(0);
        Marker(int id){
            this.id = id;
        }
        public int id;

        public static Marker fromId(int id){
            for(Marker m : Marker.values()){
                if(m.id == id){
                    return m;
                }
            }
            throw new IllegalArgumentException("Invalid ID for a Marker");
        }

        public static Marker getOtherMarker(Marker marker){
            switch(marker){
                case X:
                    return O;
                case O:
                    return X;
                default:
                    return NONE;
            }
        }
    }
}

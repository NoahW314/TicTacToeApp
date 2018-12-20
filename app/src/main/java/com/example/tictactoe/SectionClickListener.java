package com.example.tictactoe;

import android.app.Activity;
import android.view.View;

import com.example.tictactoe.Activities.BoardActivity;
import com.example.tictactoe.Views.SectionButton;

public class SectionClickListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        int index = ((SectionButton)v).sectionNum;
        if(BoardActivity.manager != null) {
            if(BoardActivity.manager.isHumanTurn()) {
                BoardActivity.manager.buttonPressed(index, (Activity)v.getContext());
            }
        }
    }
}

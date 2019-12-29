package com.example.tictactoe;

import android.view.View;

import com.example.tictactoe.Activities.BoardActivity;
import com.example.tictactoe.Views.SectionButton;

public class SectionClickListener implements View.OnClickListener {

    @Override
    public void onClick(final View v) {
        final int index = ((SectionButton)v).sectionNum;
        if(BoardActivity.manager != null && BoardActivity.manager.isHumanTurn()) {
            BoardActivity.manager.humanPlay(index);
        }
    }
}

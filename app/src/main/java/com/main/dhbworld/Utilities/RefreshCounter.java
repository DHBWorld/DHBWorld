package com.main.dhbworld.Utilities;

import android.os.CountDownTimer;

import com.main.dhbworld.Utilities.BoardLogic;

public class RefreshCounter extends CountDownTimer {
    BoardLogic boardLogic;
    public RefreshCounter(BoardLogic boardLogic) {
        super(10000, 1000);
        this.boardLogic=boardLogic;
        boardLogic.setRefreshStatus(false);
    }

    @Override
    public void onTick(long l) {

    }

    @Override
    public void onFinish() {
        boardLogic.setRefreshStatus(true);
    }
}
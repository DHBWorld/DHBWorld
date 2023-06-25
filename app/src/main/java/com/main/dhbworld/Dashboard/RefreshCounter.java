package com.main.dhbworld.Dashboard;

import android.os.CountDownTimer;

public class RefreshCounter extends CountDownTimer {
    Dashboard dashboard;
    public RefreshCounter(Dashboard dashboard) {
        super(10000, 1000);
        this.dashboard=dashboard;
        dashboard.setRefreshStatus(false);
    }

    @Override
    public void onTick(long l) {

    }

    @Override
    public void onFinish() {
        dashboard.setRefreshStatus(true);
    }
}

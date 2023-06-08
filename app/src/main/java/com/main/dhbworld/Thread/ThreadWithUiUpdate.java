package com.main.dhbworld.Thread;

import android.app.Activity;

public class ThreadWithUiUpdate extends java.lang.Thread {

    java.lang.Thread after;

    public ThreadWithUiUpdate(Runnable runnable) {
        super(runnable);
    }

    public ThreadWithUiUpdate afterOnUiThread(Activity activity, Runnable runnable) {
        after = new java.lang.Thread(() -> {
            try {
                ThreadWithUiUpdate.this.join();
                activity.runOnUiThread(runnable);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        return this;
    }

    @Override
    public synchronized void start() {
        super.start();
        if (after != null) {
            after.start();
        }
    }
}

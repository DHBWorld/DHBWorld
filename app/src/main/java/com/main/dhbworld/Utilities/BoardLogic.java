package com.main.dhbworld.Utilities;

public class BoardLogic {
    private boolean refreshIsEnable;
    public BoardLogic(){
        refreshIsEnable=true;
    }

    public boolean getRefreshStatus() {
        return refreshIsEnable;
    }

    public void setRefreshStatus(boolean refreshIsEnable) {
        this.refreshIsEnable = refreshIsEnable;
    }
}

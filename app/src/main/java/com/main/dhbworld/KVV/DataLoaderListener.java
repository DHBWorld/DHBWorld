package com.main.dhbworld.KVV;

import java.util.ArrayList;

public interface DataLoaderListener {
    public void onDataLoaded(boolean success, ArrayList<Departure> departures);
}

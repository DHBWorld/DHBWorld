package com.main.dhbworld;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;

class CalendarFilterAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> whiteList = new ArrayList<>();
    private ArrayList<String> titleList = new ArrayList<>();
    private Context context;

    public CalendarFilterAdapter(ArrayList<String> whiteList, ArrayList<String> titleList ,Context context) {
        this.whiteList = whiteList;
        this.titleList = titleList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return whiteList.size();
    }

    @Override
    public Object getItem(int pos) {
        return whiteList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return whiteList.get(pos).getBytes().length;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.calendarfilter, null);
        }

        //Handle TextView and display string from your list
        TextView calendarFilterText= (TextView)view.findViewById(R.id.calendarFilterText);
        calendarFilterText.setText(whiteList.get(position));

        //Handle buttons and add onClickListeners
        SwitchCompat switchCompat= view.findViewById(R.id.calendarFilterSwitch);

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(CompoundButton switchBtn, boolean isChecked) {
                                                        if(!isChecked){
                                                            whiteList.remove(1);
                                                        }

                                                    }
                                                }
        );


        return view;
    }
}

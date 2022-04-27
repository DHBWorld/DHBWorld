package com.main.dhbworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalendarFilter {
    static String[] listItems = arrayConvertor(EventCreator.getAllTitleList());
    final boolean[] checkedItems = new boolean[listItems.length];
    final List<String> selectedItems = Arrays.asList(listItems);
    static List<String> blackList = new ArrayList<>();


    public static String[] getListItems() {
        return listItems;
    }

    public boolean[] getCheckedItems() {
        return checkedItems;
    }

    public List<String> getSelectedItems(){
        return selectedItems;
    }

    public static List<String> getBlackList() {
        return blackList;
    }

    public static void setUpLists(){
        final String[] listItems = arrayConvertor(EventCreator.getAllTitleList());
        final boolean[] checkedItems = new boolean[listItems.length];
        final List<String> selectedItems = Arrays.asList(listItems);

        for(int i = 0; i < listItems.length; i++){
            if(blackList.contains(listItems[i])){
                checkedItems[i] = false;
            }
            else{
                checkedItems[i] = true;
            }
        }

    }

    public static String[] arrayConvertor(List<String> titleList){
        String[] titleArray = titleList.toArray(new String[0]);
        return  titleArray;
    }

}

package com.main.dhbworld.CantineClasses;

import java.util.ArrayList;
import java.util.List;

public class Meal {
    private String name;
    private String category;
    private String price;
    private List<String> notes;


    public Meal (){
        name="";
        price="-";
        notes=new ArrayList<>();
        category="No category";
    }

    public void addNote(String s){
        notes.add(s);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        String result=price;
        if ((price!=null) && (!price.equals("null")) && (!price.equals("-"))){
            if (price.substring(price.indexOf('.')+1).length()>1){
                result=price;
            }else{
                result=price+"0";
            }
        }
        return  result;

    }

    public void setPrice(String price) {
        if ((price!=null) && (!price.equals("null"))){
            this.price = price;
        }
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public String toString(){
        String m=name+" "+price+" "+notes;
        return m;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

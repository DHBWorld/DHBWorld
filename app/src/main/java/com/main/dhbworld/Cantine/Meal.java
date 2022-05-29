package com.main.dhbworld.Cantine;

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
        //Standardisierung von dem Price-Format zur X.XX
        if ((price!=null) && (!price.equals("null")) && (!price.equals("-"))){
            if (price.indexOf('.')==-1){ // falls Preice ist  z.B. 2 Euro --> 2.00
                result=price+".00";
            }else if (price.substring(price.indexOf('.')+1).length()==1) { // falls Preice ist z.B. 2.1 Euro --> 2.10
                result = price + "0";
            }else{ // falls Preice ist bereits im richtigen Format z.B. 2.15
                result=price;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

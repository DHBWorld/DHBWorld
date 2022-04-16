package com.main.dhbworld.CantineClasses;





import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Meal {
    private String name;
    private String category;
    private double price;
    private List<String> notes;


    public Meal (){
        name="";
        price=0.00;
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
        String result;
        if ((price*100)%10==0){
            result=Double.toString(price)+0;
        }else{
            result=Double.toString(price);
        }
        return  result;
    }

    public void setPrice(double price) {

        this.price = price;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public String toString(){
        String m=name+" "+Double.toString(price)+notes;
        return m;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

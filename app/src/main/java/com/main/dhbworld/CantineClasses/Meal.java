package com.main.dhbworld.CantineClasses;





import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Meal {
    private String name;
    private double price;
    private List<String> notes;


    public Meal (){
        name="";
        price=0.0;
        notes=new ArrayList<>();


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

    public double getPrice() {
        return price;
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
}

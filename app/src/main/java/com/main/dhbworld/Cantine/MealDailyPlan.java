package com.main.dhbworld.Cantine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MealDailyPlan {
    private Meal[] meals;
    List<Meal> mainCourses;

    public MealDailyPlan(String inputFromApi) throws JSONException {
        //create JSONArray from input of OpennMensa
        JSONArray jsonArrayMeal = new JSONArray(inputFromApi);
        //Translate json-objects into meals
        meals= new Meal[jsonArrayMeal.length()];
        for (int i=0; i<jsonArrayMeal.length();i++){
            JSONObject jsonMeal = jsonArrayMeal.getJSONObject(i);
            meals[i]= new Meal();
            meals[i].setName(jsonMeal.getString("name"));
            meals[i].setCategory(jsonMeal.getString("category"));
            JSONObject jsonPrices = new JSONObject(jsonMeal.getString("prices"));
            meals[i].setPrice(jsonPrices.getString("students"));
            JSONArray jsonNotes = new JSONArray(jsonMeal.getString("notes"));
            for (int j=0; j<jsonNotes.length();j++){
                meals[i].addNote(jsonNotes.getString(j));
            }
        }
        findMainCourses();
    }

    public Meal[] getMeal() { return meals; }

    public  void  findMainCourses() {
        mainCourses= new ArrayList<>();
        boolean basicMealOne=true;
        boolean basicMealTwo=true;
        boolean basicMealThree=true;

        for (int i=0;i<getMeal().length; i++){
            // "Künstliche Intelligenz" zur Erkennunung von Haupgerichten
            if((getMeal()[i].getCategory().equals("Wahlessen 1")) && basicMealOne){
                basicMealOne=false;
                mainCourses.add(getMeal()[i]);
            }else if((getMeal()[i].getCategory().equals("Wahlessen 2")) && basicMealTwo){
                basicMealTwo=false;
                mainCourses.add(getMeal()[i]);
            }else if((getMeal()[i].getCategory().equals("Wahlessen 3")) && basicMealThree  ){
                try {
                    double price= Double.parseDouble(getMeal()[i].getPrice());
                    if (price>1.80){
                        basicMealThree=false;
                        mainCourses.add(getMeal()[i]);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    mainCourses.add(getMeal()[i]);
                    basicMealThree=false;
                } }}
    }

    public  List <String> getMainCourseNames(){
        List <String> names= new ArrayList<>();
        for (Meal m:mainCourses){
            names.add(m.getName());
        }
        return names;
    }

    public  List <String> getMainCourseNamesWithSalat(){
        List <String> names= new ArrayList<>();
        for (Meal m:mainCourses){
            if (m.getNotes().contains("Mit Salat oder Obst")) {
                names.add(m.getName() + " - Mit Salat oder Obst");
            } else {
                names.add(m.getName());
            }
        }
        return names;
    }

    public  List <Meal> getMainCourses(){
        return mainCourses;
    }
}

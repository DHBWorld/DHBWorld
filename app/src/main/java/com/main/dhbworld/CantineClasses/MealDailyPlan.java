package com.main.dhbworld.CantineClasses;

import com.main.dhbworld.CantineClasses.Meal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MealDailyPlan {
    private Meal[] meals;

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
            meals[i].setPrice(jsonPrices.getDouble("students"));

            JSONArray jsonNotes = new JSONArray(jsonMeal.getString("notes"));
            for (int j=0; j<jsonNotes.length();j++){
                meals[i].addNote(jsonNotes.getString(j));
            }


        }

    }

    public Meal[] getMeal() {
        return meals;
    }

    public void setMeal(Meal[] meal) {
        this.meals = meal;
    }
}

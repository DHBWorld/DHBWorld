package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONException;

import android.os.Bundle;
import android.widget.TextView;

import com.main.dhbworld.CantineClasses.Meal;
import com.main.dhbworld.CantineClasses.MealDailyPlan;


import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class CantineActivity extends AppCompatActivity {

    private TextView meal;
    private TextView preis;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cantine);

        meal= findViewById(R.id.meal);
        preis= findViewById(R.id.preis);

        Date date= new Date();


        String input ="[{\"id\":8495433,\"name\":\"Börek mit Rindfleischfüllung, Gemüse-Couscous, Bolognesesoße\",\"category\":\"Wahlessen 1\",\"prices\":{\"students\":2.6,\"employees\":3.5,\"pupils\":2.95,\"others\":4.0},\"notes\":[\"mit Farbstoff \",\"Eier\",\"Haselnüsse\",\"Milch/Laktose\",\"Sellerie\",\"Senf\",\"Soja\",\"Weizen\",\"enthält Rindfleisch\"]},{\"id\":8495434,\"name\":\"Börek mit Spinatfüllung, Gemüse-Couscous, pikanter Soja Dip\",\"category\":\"Wahlessen 2\",\"prices\":{\"students\":2.6,\"employees\":3.5,\"pupils\":2.95,\"others\":4.0},\"notes\":[\"mit Farbstoff \",\"mit Konservierungsstoff \",\"mit Antioxidationsmittel\",\"mit Geschmacksverstärker \",\"Sellerie\",\"Soja\",\"Weizen\",\"veganes Gericht\"]},{\"id\":8441249,\"name\":\"Verschiedene Dessert\",\"category\":\"Wahlessen 3\",\"prices\":{\"students\":0.95,\"employees\":0.95,\"pupils\":0.95,\"others\":1.15},\"notes\":[\"mit Farbstoff \",\"Milch/Laktose\",\"vegetarisches Gericht\"]},{\"id\":8441251,\"name\":\"Grüner Mischsalat\",\"category\":\"Wahlessen 3\",\"prices\":{\"students\":0.8,\"employees\":0.8,\"pupils\":0.8,\"others\":1.0},\"notes\":[\"mit Antioxidationsmittel\",\"Sellerie\",\"veganes Gericht\"]},{\"id\":8441250,\"name\":\"Blumenkohlgemüse\",\"category\":\"Wahlessen 3\",\"prices\":{\"students\":0.8,\"employees\":0.8,\"pupils\":0.8,\"others\":1.0},\"notes\":[\"mit Farbstoff \",\"Sellerie\",\"veganes Gericht\"]},{\"id\":8444721,\"name\":\"Gemüse-Couscous\",\"category\":\"Wahlessen 3\",\"prices\":{\"students\":0.75,\"employees\":0.75,\"pupils\":0.75,\"others\":0.95},\"notes\":[\"mit Farbstoff \",\"Sellerie\",\"Weizen\",\"veganes Gericht\"]}]";

        try {
            MealDailyPlan mealDailyPlan= new MealDailyPlan(input);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }



    private void mealPlanFromOpenMensa(Date date) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd");
                URL urlOpenMensa= null;
                try {
                    //urlOpenMensa = new URL("https://openmensa.org/api/v2/canteens/33/days/"+format.format(date)+"/meals");
                    urlOpenMensa = new URL("https://openmensa.org/api/v2/canteens/33/days/2021-10-25/meals");
                    HttpsURLConnection connection=(HttpsURLConnection) urlOpenMensa.openConnection();


                    if (connection.getResponseCode() ==200){ // something wrong
                        InputStream responseBody = connection.getInputStream();
                        InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

                        meal.setText(format.format(date));
                        meal.setText(responseBody.toString());

                       // TranslatorCantine t = new TranslatorCantine( responseBodyReader);
                      //  Meal testMeal= t.getMeal();
                        //meal.setText(testMeal.getName());

                        //JsonReader jsonReader = new JsonReader(responseBodyReader);
                        //jsonReader.beginArray();
                      /*  while (jsonReader.hasNext()){
                            String key=jsonReader.nextName();
                            if (key.equals("id")){
                                String value=jsonReader.nextString();

                                meal.setText(value);
                                break;
                            }else{
                                jsonReader.skipValue();
                            }
                        }*/


                    }else{
                        meal.setText("error with connection");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        //return "mealPlan";
    }


}
package com.main.dhbworld;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.gson.Gson;

import org.json.JSONException;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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


    private LinearLayout layoutMealCardsBasic;
    private LinearLayout layoutMealCardsExtra;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cantine);
        layoutMealCardsBasic= findViewById(R.id.layoutMealCardsBasic);
        layoutMealCardsExtra= findViewById(R.id.layoutMealCardsExtra);



        Date date= new Date();


        String input ="[{\"id\":8495433,\"name\":\"Börek mit Rindfleischfüllung, Gemüse-Couscous, Bolognesesoße\",\"category\":\"Wahlessen 1\",\"prices\":{\"students\":2.6,\"employees\":3.5,\"pupils\":2.95,\"others\":4.0},\"notes\":[\"mit Farbstoff \",\"Eier\",\"Haselnüsse\",\"Milch/Laktose\",\"Sellerie\",\"Senf\",\"Soja\",\"Weizen\",\"enthält Rindfleisch\"]},{\"id\":8495434,\"name\":\"Börek mit Spinatfüllung, Gemüse-Couscous, pikanter Soja Dip\",\"category\":\"Wahlessen 2\",\"prices\":{\"students\":2.6,\"employees\":3.5,\"pupils\":2.95,\"others\":4.0},\"notes\":[\"mit Farbstoff \",\"mit Konservierungsstoff \",\"mit Antioxidationsmittel\",\"mit Geschmacksverstärker \",\"Sellerie\",\"Soja\",\"Weizen\",\"veganes Gericht\"]},{\"id\":8441249,\"name\":\"Verschiedene Dessert\",\"category\":\"Wahlessen 3\",\"prices\":{\"students\":0.95,\"employees\":0.95,\"pupils\":0.95,\"others\":1.15},\"notes\":[\"mit Farbstoff \",\"Milch/Laktose\",\"vegetarisches Gericht\"]},{\"id\":8441251,\"name\":\"Grüner Mischsalat\",\"category\":\"Wahlessen 3\",\"prices\":{\"students\":0.8,\"employees\":0.8,\"pupils\":0.8,\"others\":1.0},\"notes\":[\"mit Antioxidationsmittel\",\"Sellerie\",\"veganes Gericht\"]},{\"id\":8441250,\"name\":\"Blumenkohlgemüse\",\"category\":\"Wahlessen 3\",\"prices\":{\"students\":0.8,\"employees\":0.8,\"pupils\":0.8,\"others\":1.0},\"notes\":[\"mit Farbstoff \",\"Sellerie\",\"veganes Gericht\"]},{\"id\":8444721,\"name\":\"Gemüse-Couscous\",\"category\":\"Wahlessen 3\",\"prices\":{\"students\":0.75,\"employees\":0.75,\"pupils\":0.75,\"others\":0.95},\"notes\":[\"mit Farbstoff \",\"Sellerie\",\"Weizen\",\"veganes Gericht\"]}]";

        try {
            MealDailyPlan mealDailyPlan= new MealDailyPlan(input);
            loadLayout(mealDailyPlan, date);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void loadLayout (MealDailyPlan mealDailyPlan, Date today){
        LinearLayout titleLayout = new LinearLayout(CantineActivity.this);
        titleLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        layoutMealCardsBasic.addView(titleLayout);

        TextView pageTitleBasic = new TextView(CantineActivity.this);
        pageTitleBasic.setTextSize(25);
        pageTitleBasic.setTextColor(getResources().getColor(R.color.black));
        pageTitleBasic.setText("Hauptgerichte");
        pageTitleBasic.setPadding(0,0,0,15);
        //mealView.setGravity(Gravity.CENTER);
        pageTitleBasic.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleLayout.addView(pageTitleBasic);

        TextView todayIs = new TextView(CantineActivity.this);
        todayIs.setTextSize(18);
        todayIs.setTextColor(getResources().getColor(R.color.grey_light));
        SimpleDateFormat format =new SimpleDateFormat("dd.MM.yyyy");
        todayIs.setText(format.format(today));
        todayIs.setPadding(250,0,0,15);
        todayIs.setGravity(Gravity.RIGHT);
        todayIs.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        titleLayout.addView(todayIs);

        TextView pageTitleExtra = new TextView(CantineActivity.this);
        pageTitleExtra.setTextSize(25);
        pageTitleExtra.setTextColor(getResources().getColor(R.color.black));
        pageTitleExtra.setText("Sonstiges");
        pageTitleExtra.setPadding(0,0,0,15);

        pageTitleExtra.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layoutMealCardsExtra.addView(pageTitleExtra);

        for (int i=0;i<mealDailyPlan.getMeal().length; i++){




        MaterialCardView mealCard= new MaterialCardView(CantineActivity.this);
        mealCard.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mealCard.setStrokeColor(getResources().getColor(R.color.grey_dark));

        if(mealDailyPlan.getMeal()[i].getCategory().equals("Wahlessen 3")){
            layoutMealCardsExtra.addView(mealCard);
        }else{
            layoutMealCardsBasic.addView(mealCard);
        }


        LinearLayout cardLayout = new LinearLayout(CantineActivity.this);
        cardLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        cardLayout.setOrientation(LinearLayout.HORIZONTAL);
        cardLayout.setPadding(0,20,25,20);
        mealCard.addView(cardLayout);

        LinearLayout mealLayout = new LinearLayout(CantineActivity.this);
        mealLayout.setLayoutParams(new ViewGroup.LayoutParams(780, ViewGroup.LayoutParams.WRAP_CONTENT));
        mealLayout.setOrientation(LinearLayout.VERTICAL);
        mealLayout.setPadding(0,0,20,0);
        cardLayout.addView(mealLayout);

        TextView mealView = new TextView(CantineActivity.this);
        mealView.setTextSize(15);
        mealView.setTextColor(getResources().getColor(R.color.black));
        mealView.setText(mealDailyPlan.getMeal()[i].getName());
        //mealView.setGravity(Gravity.CENTER);
        mealView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mealLayout.addView(mealView);


        HorizontalScrollView chipScroll = new HorizontalScrollView(CantineActivity.this);
        chipScroll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        chipScroll.setHorizontalScrollBarEnabled(false);
        mealLayout.addView(chipScroll);

        LinearLayout chipLayout = new LinearLayout(CantineActivity.this);
        chipLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        chipLayout.setOrientation(LinearLayout.HORIZONTAL);
        chipScroll.addView(chipLayout);

        for (int j=0; j<mealDailyPlan.getMeal()[i].getNotes().size(); j++){
        Chip chip = new Chip(CantineActivity.this);
        chip.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        chip.setText(mealDailyPlan.getMeal()[i].getNotes().get(j));
        chip.setTextSize(12);
        chip.setGravity(Gravity.CENTER);
        chip.setCheckable(false);
        chip.setClickable(false);
        chipLayout.addView(chip);
        }

        LinearLayout preisLayout = new LinearLayout(CantineActivity.this);
        preisLayout.setLayoutParams(new ViewGroup.LayoutParams(150, ViewGroup.LayoutParams.MATCH_PARENT));
        preisLayout.setOrientation(LinearLayout.VERTICAL);
        preisLayout.setBackgroundColor(getResources().getColor(R.color.grey_dark));
        preisLayout.setGravity(Gravity.CENTER);

        cardLayout.addView(preisLayout);

        TextView preisView = new TextView(CantineActivity.this);
        preisView.setTextSize(18);
        preisView.setTextColor(getResources().getColor(R.color.white));
        preisView.setText(mealDailyPlan.getMeal()[i].getPrice());
        preisView.setGravity(Gravity.CENTER);
        preisView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        preisLayout.addView(preisView);
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

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        //return "mealPlan";
    }


}
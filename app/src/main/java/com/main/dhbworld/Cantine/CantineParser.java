package com.main.dhbworld.Cantine;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.net.ssl.HttpsURLConnection;

public class CantineParser {

   private ResultListener resultListener;
   private Calendar calendar;

   public static final int NO_DATA_AVAILABLE = 404;
   public static final int ERROR_PARSING_RESULT = -1;
   public static final int SUCCESS = 200;

   Map<String, String> legend = Stream.of(new String[][]{
           {"1", "mit Farbstoff"},
           {"2", "mit Konservierungsstoff"},
           {"3", "mit Antioxidationsmittel"},
           {"4", "mit Geschmacksverstärker"},
           {"5", "mit Phosphat"},
           {"6", "Oberfläche gewachst"},
           {"7", "geschwefelt"},
           {"8", "Oliven geschwärzt"},
           {"9", "mit Süßungsmitteln"},
           {"10", "kann bei übermäßigem Verzehr abführend wirken"},
           {"11", "enthält eine Phenylalaninquelle"},
           {"12", "kann Restalkohol enthalten"},
           {"14", "aus Fleischstücken zusammengefügt"},
           {"15", "mit kakaohaltiger Fettglasur"},
           {"27", "aus Fischstücken zusammengefügt"},
           {"Ca", "Cashewnüsse"},
           {"Di", "Dinkel"},
           {"Ei", "Eier"},
           {"Er", "Erdnüsse"},
           {"Fi", "Fisch"},
           {"Ge", "Gerste"},
           {"Gl", "Glutenhaltiges Getreide"},
           {"Hf", "Hafer"},
           {"Ha", "Haselnüsse"},
           {"Ka", "Kamut"},
           {"Kr", "Krebstiere"},
           {"Lu", "Lupine"},
           {"Ma", "Mandeln"},
           {"ML", "Milch/Laktose"},
           {"Nu", "Schalenfrüchte/Nüsse"},
           {"Pa", "Paranüsse"},
           {"Pe", "Pekannüsse"},
           {"Pi", "Pistazie"},
           {"Qu", "Queenslandnüsse/Macadamianüsse"},
           {"Ro", "Roggen"},
           {"Sa", "Sesam"},
           {"Se", "Sellerie"},
           {"Sf", "Schwefeldioxid/Sulfit"},
           {"Sn", "Senf"},
           {"So", "Soja"},
           {"Wa", "Walnüsse"},
           {"We", "Weizen"},
           {"Wt", "Weichtiere"},
           {"LAB", "mit tierischem Lab"},
           {"GEL", "mit Gelatine"}
   }).collect(Collectors.toMap(p -> p[0], p -> p[1]));

   public CantineParser() {

   }

   public CantineParser requestDay(Calendar date) {
      this.calendar = date;
      this.calendar.setFirstDayOfWeek(Calendar.MONDAY);
      return this;
   }

   public CantineParser setResultListener(ResultListener resultListener) {
      this.resultListener = resultListener;
      return this;
   }

   public String start() {
      String result = "";
      try {
         result = compute();
      } catch (Exception e) {
         resultListener.onFailure(NO_DATA_AVAILABLE, e);
      }
      return result;
   }

   public void startAsync() {
      new Thread(() -> {
         String result = "";
         try {
            result = compute();
         } catch (Exception e) {
            resultListener.onFailure(NO_DATA_AVAILABLE, e);
         }
         if (!result.isEmpty()) {
            resultListener.onSuccess(result);
         }
      }).start();
   }

   private String compute() throws Exception {
      int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);

      String response = "";

      if (CantineParserCache.getFromCache(weekOfYear) == null) {
         try {
            URL urlMensa = new URL("https://www.sw-ka.de/de/hochschulgastronomie/speiseplan/mensa_erzberger/?kw=" + weekOfYear);
            HttpsURLConnection connection = (HttpsURLConnection) urlMensa.openConnection();
            if (connection.getResponseCode() != SUCCESS) {
               resultListener.onFailure(connection.getResponseCode(), null);
            }
            InputStream responseBody = connection.getInputStream();
            InputStreamReader responseBodyReader = new InputStreamReader(responseBody, StandardCharsets.UTF_8);
            response = new BufferedReader(responseBodyReader).lines().collect(Collectors.joining("\n"));

         } catch (IOException e) {
            e.printStackTrace();
         }
      } else {
         response = CantineParserCache.getFromCache(weekOfYear);
      }


      if (response.isEmpty()) {
         resultListener.onFailure(ERROR_PARSING_RESULT, new Exception("Failed reading Input to String"));
      }

      Document document = Jsoup.parse(response);

      Elements canteenDayNavElements = document.getElementsByClass("canteen-day-nav");
      if (canteenDayNavElements.size() < 1) {
         resultListener.onFailure(NO_DATA_AVAILABLE, null);
      }
      if (canteenDayNavElements.size() < 1) {
         resultListener.onFailure(NO_DATA_AVAILABLE, null);
         return "";
      }
      Element canteenDayNav = canteenDayNavElements.get(0);
      if (canteenDayNav.childrenSize() < 1) {
         resultListener.onFailure(NO_DATA_AVAILABLE, null);
      }

      Elements canteenDays = new Elements();
      canteenDays.add(document.getElementById("canteen_day_1"));
      canteenDays.add(document.getElementById("canteen_day_2"));
      canteenDays.add(document.getElementById("canteen_day_3"));
      canteenDays.add(document.getElementById("canteen_day_4"));
      canteenDays.add(document.getElementById("canteen_day_5"));

      Elements canteenDayDates = new Elements();
      canteenDayDates.add(document.getElementById("canteen_day_nav_1"));
      canteenDayDates.add(document.getElementById("canteen_day_nav_2"));
      canteenDayDates.add(document.getElementById("canteen_day_nav_3"));
      canteenDayDates.add(document.getElementById("canteen_day_nav_4"));
      canteenDayDates.add(document.getElementById("canteen_day_nav_5"));

      int requestedDay = -1;

      for (int i = 0; i < canteenDayDates.size(); i++) {
         Element canteenDayDate = canteenDayDates.get(i);
         if (canteenDayDate == null) {
            continue;
         }
         String dateStr = canteenDayDate.attr("rel");

         SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
         String dateStrRequest = formatter.format(calendar.getTime());

         if (dateStr.equals(dateStrRequest)) {
            requestedDay = i;
         }
      }

      System.out.println("requestDay:" + requestedDay);

      if (requestedDay == -1) {
         resultListener.onFailure(NO_DATA_AVAILABLE, null);
         return "";
      }

      JSONArray resultArray = new JSONArray();

      for (int i=0; i<canteenDays.size(); i++) {
         Element canteenDay = canteenDays.get(i);
         if (canteenDay == null || i != requestedDay) {
            continue;
         }

         Elements mensatypeRows = canteenDay.getElementsByClass("mensatype_rows");
         for (Element mensaType : mensatypeRows) {

            String type = mensaType.getElementsByClass("mensatype").text();

            Elements mealsBodys = mensaType.select(".meal-detail-table > tbody");
            if (mealsBodys.size() < 1) {
               continue;
            }

            Element mealsBody = mealsBodys.get(0);
            for (int j=0; j<mealsBody.children().size(); j++) {
               Element meal = mealsBody.children().get(j);
               ArrayList<String> extras = new ArrayList<>();
               if (meal.childrenSize() != 3) {
                  continue;
               }
               Elements icons = meal.getElementsByTag("img");
               if (icons.size() > 0) {
                  extras.add(icons.get(0).attr("title"));
               }
               Elements titles = meal.getElementsByClass("menu-title");
               if (titles.size() < 1) {
                  continue;
               }

               System.out.println("SIZE: " + mealsBody.children().size());

               String titleWithExtras = titles.get(0).text();
               String extrasStr;
               String title;

               if (!titleWithExtras.contains("[")) {
                  title = titleWithExtras;
               } else {
                  extrasStr = titleWithExtras.substring(titleWithExtras.indexOf("["));
                  title = titleWithExtras.replace(extrasStr, "");

                  extrasStr = extrasStr.replace("[", "").replace("]", "");
                  String[] extrasArray = extrasStr.split(",");
                  for (String extra : extrasArray) {
                     extras.add(legend.get(extra));
                  }
               }

               if (mealsBody.children().size() > j+1) {
                  for (int k=j+1; k<mealsBody.children().size(); k++) {
                     Element nextMeal = mealsBody.children().get(k);
                     Elements titlesNext = nextMeal.getElementsByClass("menu-title");
                     if (titlesNext.size() >= 1) {
                        String extra = titlesNext.get(0).text();
                        if (extra.contains("zu diesem Gericht reichen wir frisches Obst oder Salat")) {
                           extras.add(0, "Mit Salat oder Obst");
                        }
                        break;
                     }
                  }
               }

               String priceStr = meal.getElementsByClass("price_1").get(0).text();
               priceStr = priceStr.replaceAll("[^0-9,]", "");
               priceStr = priceStr.replace(",", ".");
               if (priceStr.isEmpty()) {
                  continue;
               }
               double price = Double.parseDouble(priceStr);

               JSONObject jsonObject = new JSONObject();
               try {
                  jsonObject.put("id", System.currentTimeMillis());
                  jsonObject.put("name", title);
                  jsonObject.put("category", type);
                  JSONObject prices = new JSONObject();
                  prices.put("students", price);
                  jsonObject.put("prices", prices);
                  JSONArray jsonArray = new JSONArray(extras.toArray());
                  jsonObject.put("notes", jsonArray);

                  resultArray.put(jsonObject);
               } catch (JSONException e) {
                  e.printStackTrace();
               }

            }
         }
      }

      CantineParserCache.addToCache(weekOfYear, response);

      if (resultArray.length() == 0) {
         resultListener.onFailure(NO_DATA_AVAILABLE, null);
         return "";
      }
      return resultArray.toString();
   }

   public interface ResultListener {
      void onSuccess(String response);
      void onFailure(int resultCode, Exception e);
   }
}

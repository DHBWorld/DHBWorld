package com.main.dhbworld.Dualis.view.tabs.documents;

public class DualisDocument {
   private final String name;
   private final String date;
   private final String url;

   public DualisDocument(String name, String date, String url) {
      this.name = name;
      this.date = date;
      this.url = url;
   }

   public String getName() {
      return name;
   }

   public String getDate() {
      return date;
   }

   public String getUrl() {
      return url;
   }
}

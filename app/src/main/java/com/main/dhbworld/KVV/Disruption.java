package com.main.dhbworld.KVV;

public class Disruption {
   private final String summary;
   private final String details;
   private final String lines;

   Disruption(String summary, String details, String lines) {
      this.summary = summary;
      this.details = details;
      this.lines = lines;
   }

   public String getSummary() {
      return summary;
   }

   public String getDetails() {
      return details;
   }

   public String getLines() {
      return lines;
   }

   @Override
   public String toString() {
      return "Disruption{" +
              "summary='" + summary + '\'' +
              ", details='" + details + '\'' +
              ", lines='" + lines + '\'' +
              '}';
   }
}

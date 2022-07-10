package com.main.dhbworld.Cantine;

import java.util.HashMap;
import java.util.Map;

class CantineParserCache {
   private static Map<Integer, String> cache = new HashMap<>();

   static void addToCache(int id, String data) {
      cache.put(id, data);
   }

   static String getFromCache(int id) {
      return cache.get(id);
   }
}

package com.main.dhbworld.Dualis;

class OverallCourseModel {
   private final String moduleID;
   private final String moduleName;
   private final String credits;
   private final String grade;
   private final boolean passed;

   OverallCourseModel(String moduleID, String moduleName, String credits, String grade, boolean passed) {
      this.moduleID = moduleID;
      this.moduleName = moduleName;
      this.credits = credits;
      this.grade = grade;
      this.passed = passed;
   }


   public String getModuleID() {
      return moduleID;
   }

   public String getModuleName() {
      return moduleName;
   }

   public String getCredits() {
      return credits;
   }

   public String getGrade() {
      return grade;
   }

   public boolean isPassed() {
      return passed;
   }
}

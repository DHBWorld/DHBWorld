#noinspectionLocalDateTime start=LocalDateTime.parse(nextClass.getStartTime()); CucumberPlusUndefinedStep
Feature: showDailyMealPlan

  as a User
  I want to see the meal plan for the current day

  Scenario: User has no internet
    Given The app is opened
    When The user opens the meal plan page
    And The user has no connection to the internet
    Then The meal plan page will show an error message

  Scenario: The meal plan server does not respond
    Given The app is opened
    When The user opens the meal plan page
    And The meal plan server does not respond
    Then The meal plan page will show an error message

  Scenario: The canteen is closed
    Given The app is opened
    When The user opens the meal plan page
    And There is no meal plan for current day
    Then The meal plan page will show warning message

  Scenario: Meal plan for current day is displayed to the user
    Given The app is opened
    When The user opens the meal plan page
    And The meal plan server responds
    Then The meal plan for the current day week will be displayed
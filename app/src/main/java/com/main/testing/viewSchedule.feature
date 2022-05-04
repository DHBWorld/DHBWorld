Feature: view schedule

  as a User
  I want to view schedule in form of calendar

  Scenario: Problems with internet connection
    Given The app is opened
    When I open schedule page
    Then I get an error message

  Scenario: Problems with schedule server
    Given The app is opened
    When I open schedule page
    Then I get an error message

  Scenario: Schedule is displayed to user
    Given The app is opened
    When I open schedule page
    Then I get an schedule in form of calendar
    And I can see all my classes until the end of semester there


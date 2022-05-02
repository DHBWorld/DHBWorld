Feature: view departures

  as a User
  I want to view tram departuren from the tram station near DHBW

  Scenario: Next tram
    Given The app is opened
    And I would like to see next tram
    When I open page of tram departures
    Then I see departure time of next tram

  Scenario: Tram in defined time
    Given The app is opened
    And I have an internet connection
    When I would like tram in defined time
    And I open page of tram departures
    And I choose the time
    And click button "find"
    Then I see tram departure in defined time


  Scenario: Problems with internet connection
    Given The page of tram departures is opened
    And I haven't an internet connection
    Then I get warning message


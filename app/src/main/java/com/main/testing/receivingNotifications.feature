Feature: receiving notification

  as a User
  I want to receive notification about problems with printer, canteen or coffeemaker

  Scenario: Long queue in the canteen
    Given The app is closed
    And I have an internet connection
    And I somebody reported long queue in the canteen
    Then I get a notification about ong queue in the canteen


  Scenario: Problems with internet connection
    Given The app is closed
    And I haven't an internet connection
    And I somebody reported long queue in the canteen
    Then I get nothing


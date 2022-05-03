Feature: reporting events

  as a User
  I want to report events or problems with printer, canteen or coffeemaker

  Scenario: Long queue in the canteen
    Given The Report Events page is opened
    And I see long queue in the canteen
    When I click "report problem in canteen"
    And I choose "long queue"
    Then I get an success message
    And I see the new status of canteen "long queue"

  Scenario: Printer is defect
    Given The Report Events page is opened
    And I see printer is defect
    When I click "report problem with printer"
    And I choose "defect"
    Then I get an success message
    And I see the new status of printer "defect"

  Scenario: Problem with internet connection
    Given The Report Events page is opened
    And I see printer is defect
    When I click "report problem with printer"
    And I choose "defect"
    Then I get an error message


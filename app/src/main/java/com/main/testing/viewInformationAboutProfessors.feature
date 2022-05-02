Feature: view information about professors

  as a User
  I want to view information about professors

  Scenario: View information
    Given The app is opened
    When I open page "Organizer"
    And I click tab "Professors"
    Then I can see all of the information about professors from DHBW-karlsruhe web-site




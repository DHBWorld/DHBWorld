Feature: view organizer

  as a User
  I want to view information about professors, room and courses

  Scenario: View information about professors
    Given The app is opened
    When I open page "Organizer"
    And I click tab "Professors"
    Then I can see all of the information about professors from DHBW-karlsruhe web-site

  Scenario: View information about rooms
    Given The app is opened
    When I open page "Organizer"
    And I click tab "Rooms"
    Then I can see all of the information about rooms from DHBW-karlsruhe web-site

  Scenario: View information about courses
    Given The app is opened
    When I open page "Organizer"
    And I click tab "courses"
    Then I can see all of the information about courses from DHBW-karlsruhe web-site

  Scenario: Search information
    Given The page "Organizer" is opened
    When I put a search word in seach bar
    And I click search button
    Then I can see information about professors, room and courses which contains my search word





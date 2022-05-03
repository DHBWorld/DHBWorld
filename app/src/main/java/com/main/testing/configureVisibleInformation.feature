Feature: configure visible information (on Dashboard)

  as a User
  I want to view on dashboard brief information from all parts of the app and to configure it

  Scenario: There is no internet connection
    Given The Dashboard page is opened
    When I see all dashboard-blocks empty
    Then I get an warning message

  Scenario: Make some dashboard-blocks invisible
    Given The Dashboard page is opened
    And All of the dashboard-blocks are visible
    When I click the button "configure"
    And I click the dashboard-blocks, which schould be invisible
    And I click the "save" button
    Then I can't see dashboard-blocks which I have clicked, but another dashboard-blocks are visible

  Scenario: Make some dashboard-blocks visible
    Given The Dashboard page is opened
    And Some of the dashboard-blocks are invisible
    When I click the button "configure"
    And I click the invisible dashboard-blocks, which schould be visible
    And I click the "save" button
    Then I can see the dashboard-blocks which I have clicked


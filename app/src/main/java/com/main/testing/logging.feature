Feature: logging in dualis
  as a User
  I want to login in dualis

  Scenario: Successful login
    Given The page "Log in" is opend
    When I give my student email
    And I give my dualis password
    And this data are right
    And I click login-button
    Then I am logged in
    And I can see the grades

  Scenario: Wrong password
    Given The page "Log in" is opend
    When I give my student email
    And I give wrong password
    And I click login-button
    Then I am not logged in
    And I can't see the grades
    And I can see warning message

  Scenario: Wrong student email
    Given The page "Log in" is opend
    When I give wrong student email
    And I give my dualis password
    And I click login-button
    Then I am not logged in
    And I can't see the grades
    And I can see warning message










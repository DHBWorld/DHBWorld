#noinspection CucumberPlusUndefinedStep
Feature: view personal information

  as a User
  I want to view saved personal information and to view it

  Scenario: Personal information is already saved
    Given The app is opened
    And Personal information is already saved
    When I open personal information page
    Then I can see my personal Information


  Scenario: Personal information isn't saved
    Given The app is opened
    And Personal information is isn't saved
    When I open personal information page
    Then I can see only enpty lines

  Scenario: Invalid E-Mail
    Given The UserInformation page is opened
    When I click the edit button
    And I enter "notAnEMail" in the e-mail field
    And I click the save button
    Then the e-mail field will show an error

  Scenario: Invalid matriculation number
    Given The UserInformation page is opened
    When I click the edit button
    And I enter a matriculation number which has not the length of 7 digits
    And I click the save button
    Then the matriculation number field will show an error

  Scenario: User information is correct
    Given The UserInformation page is opened
    When I click the edit button
    And I enter a name in the name field
    And I enter an valid e-mail in the e-mail field
    And I enter a correct matriculation number containing 7 digits in the matriculation number field
    And I enter a correct library number in the library number field
    And I click on the save button
    Then the information gets saved and shown in the UserInformation page
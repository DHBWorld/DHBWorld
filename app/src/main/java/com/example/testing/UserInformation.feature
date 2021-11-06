#noinspection CucumberPlusUndefinedStep
Feature: UserInformation

  as a User
  I want to save personal Information

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
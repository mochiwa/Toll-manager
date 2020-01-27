Feature: create employee
  
  Background:
	Given I have these users
	  | employeeId | login    | password   |
	  | 01         | aManager | Secret123@ |
	  | 02         | aDepHead | Secret123@ |
	
	Given I have these groups
	  | name            | roles           | groupToDo | members      |
	  | manager         | create_employee | *         | 01->aManager |
	  | head department |                 |           | 02->aDepHead |
  
  Scenario Outline: create employee
	Given I'm connected like "<user>"
	When I fill the employee form with these requirements:
	  | niss   | name | forename | birthday   | address                         | mobile        | email                 |
	  | <niss> | eric | brown    | 1995/12/17 | 20,28 street,4000,liege,belgium | 0476.11.02.02 | ericbrown@hotmail.fr |
	Then the employee repository should contain employee "<nissExpected>"
	And the error message should be "<errorMessage>"
	
	Examples: success
	  | user     | niss            | nissExpected    | errorMessage |
	  | aManager | 92.12.16-333.22 | 92.12.16-333.22 |              |
	Examples: failure
	  | user     | niss            | nissExpected | errorMessage                                          |
	  | aDepHead | 92.12.16-333.22 |              | 'aDepHead' is not authorized to 'create an employee'. |
  
 
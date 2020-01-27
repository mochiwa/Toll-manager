Feature: launch application
  
  Scenario: first launch
	Given I launch the application for the first time
	When I fill the admin form with these requirements:
	  | niss            | name | forename | birthday   | address                         | mobile        | email                 |
	  | 95.12.17-888.19 | doe  | john     | 1995/12/17 | 20,28 street,4000,liege,belgium | 0476.11.02.02 | eric_brown@hotmail.fr |
	Then I should have these groups
	  | name          | roles                                                                                       | groupToDo                                         | members |
	  | administrator | append_member,remove_member,manage_team,create_employee,create_user,delete_user,create_team | *,*,*,*,*,*,*                                     | admin   |
	  | manager       | append_member,remove_member,manage_team,create_employee,create_user,delete_user,create_team | teamLeader,teamLeader,*,*,teamLeader,teamLeader,* |         |
	  | teamLeader    | manage_team                                                                                 | *                                                 |         |
	And the user connected should be "admin"
	
  Scenario: normal launch
	  Given I have these users
		| employeeId | login    | password   |
		| 01         | admin    | Secret123@ |
	  When I'm connected with "admin" "Secret123@"
	  Then the application is initialized for the user "admin"
	  And the user connected should be "admin"
	  
  Scenario: logout user
	Given I have these users
	  | employeeId | login    | password   |
	  | 01         | admin    | Secret123@ |
	And I'm connected with "admin" "Secret123@"
	And the application is initialized for the user "admin"
	When I'm logout
	Then the user connected should be " "
Feature: create users
  
  Background:
	Given I have these users
	  | employeeId | login    | password   |
	  | 01         | admin    | Secret123@ |
	  | 02         | aManager | Secret123@ |
	  | 03         | aDepHead | Secret123@ |
	
	Given I have these groups
	  | name            | roles       | groupToDo       | members      |
	  | administrator   | create_user | *               | 01->admin    |
	  | manager         | create_user | head department | 02->aManager |
	  | head department |             |                 | 03->aDepHead |
  
  Scenario Outline: create a user
	Given I'm connected like "<user>"
	When I create an account with "<login>" "<password>" "<confirmedPassword>" for the employee with "<id>" in "<group>"
	Then the "<group>" should have these members: [ <membersExcepted> ]
	And the error message should be "<errorMessage>"
	
	Examples: Success
	  | user     | group           | login     | password   | confirmedPassword | id  | membersExcepted                | errorMessage |
	  | admin    | administrator   | jordanBel | Secret123@ | Secret123@        | bbb | 01->admin && bbb->jordanBel    |              |
	  | admin    | manager         | jordanBel | Secret123@ | Secret123@        | bbb | 02->aManager && bbb->jordanBel |              |
	  | aManager | head department | jordanBel | Secret123@ | Secret123@        | bbb | 03->aDepHead && bbb->jordanBel |              |
	
	Examples: failure due to access rules
	  | user     | group           | login     | password   | confirmedPassword | id  | membersExcepted | errorMessage                                                                 |
	  | aManager | administrator   | jordanBel | Secret123@ | Secret123@        | bbb | 01->admin       | 'aManager' has not the role to 'create_user' to the group 'administrator'.   |
	  | aDepHead | head department | jordanBel | Secret123@ | Secret123@        | bbb | 03->aDepHead    | 'aDepHead' has not the role to 'create_user' to the group 'head department'. |
	
	Examples: failure due to dataInformation
	  | user     | group             | login     | password      | confirmedPassword | id  | membersExcepted | errorMessage                                                                                                               |
	  | aManager | head department   | jordanBel | Secret123@    | Wrong27@          | bbb | 03->aDepHead    | Password does not match.                                                                                                   |
	  | aManager | head department   | jordanBel | notSecurePass | notSecurePass     | bbb | 03->aDepHead    | The password must contain 5 to 55 characters , one or more Uppercase , one ore more numbers and one ore more of #?!@$%^&*- |
	  | aManager | head department   | aManager  | Secret123@    | Secret123@        | bbb | 03->aDepHead    | This login is already used.                                                                                                |
	  | aManager | head department   | jordanBel | Secret123@    | Secret123@        | 03  | 03->aDepHead    | This employee already has an account.                                                                                      |
	  | aManager | aNotExistingGroup | jordanBel | Secret123@    | Secret123@        | bbb |                 | 'aManager' has not the role to 'create_user' to the group 'anotexistinggroup'.                                             |
	
	
	
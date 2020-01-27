Feature: delete user
  
  Background:
	Given I have these users
	  | employeeId | login    | password   |
	  | 01         | admin    | Secret123@ |
	  | 02         | aManager | Secret123@ |
	  | 03         | aDepHead | Secret123@ |
	
	Given I have these groups
	  | name            | roles       | groupToDo       | members      |
	  | administrator   | delete_user | *               | 01->admin    |
	  | manager         | delete_user | head department | 02->aManager |
	  | head department |             |                 | 03->aDepHead |
  
  
  Scenario Outline: delete a user
	Given I'm connected like "<user>"
	When I delete the user "<login>"
	Then the "<group>" should have these members: [ <membersExcepted> ]
	And the error message should be "<errorMessage>"
	
	Examples: Success
	  | user     | login    | group           | membersExcepted | errorMessage |
	  | admin    | aManager | manager         |                 |              |
	  | aManager | aDepHead | head department |                 |              |
	
	Examples: failure
	  | user     | login    | group         | membersExcepted | errorMessage                                                           |
	  | aDepHead | aManager | manager       | 02->aManager    | 'aDepHead' has not the role to 'remove_member' to the group 'manager'. |
	  | aManager | admin    | administrator | 01->admin       | 'aManager' is not authorized to 'delete an administrator'.             |
	  | aManager | aManager | manager       | 02->aManager    | 'aManager' cannot be deleted by himself.                               |
	  | admin    | admin    | administrator | 01->admin       | 'admin' cannot be deleted by himself.                                  |
  
  
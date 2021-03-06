Feature: append an employee to an existing team
  
  Background:
	Given I have these users
	  | employeeId | login             | password   |
	  | 01         | manager-A2016     | Secret123@ |
	  | 02         | leader-it01       | Secret123@ |
	  | 03         | leader-customer01 | Secret123@ |
	Given I have these groups
	  | name    | roles                   | groupToDo | members                                  |
	  | manager | manage_team,create_team | *,*       | 01->manager-A2016                        |
	  | leader  | manage_team             | *         | 02->leader-it01 && 03->leader-customer01 |
	Given I have these employees
	  | employeeId | niss            | forename | name    | birthday   | address                                      | mobile        | email                     |
	  | 01         | 56.08.10-384.11 | david    | born    | 1956/08/10 | 345,31st street,11535,new-york,united states | 0478.72.02.02 | david_born@hotmail.com    |
	  | 02         | 91.12.21-999.21 | john     | doe     | 1990/12/21 | 12,10st street,11535,new-york,united states  | 0476.11.02.02 | john_doe@hotmail.com      |
	  | 03         | 45.01.13-999.21 | johny    | orlan   | 1945/01/13 | 101,99st street,11535,new-york,united states | 0479.13.02.02 | johny_orlan@hotmail.com   |
	  | 04         | 91.10.21-111.50 | eric     | brown   | 1991/10/21 | 72,21st street,11535,new-york,united states  | 0470.99.02.03 | eric_brown@hotmail.com    |
	  | 05         | 46.11.11-118.10 | david    | biggest | 1946/11/11 | 477,72st street,11535,new-york,united states | 0479.92.06.78 | david_biggest@hotmail.com |
	Given  I have these teams
	  | teamId | teamName               | teamLeaderId | employeesId | description | subTeam | parentTeam    |
	  | aaa    | it-department          | 02           | 02,04       |             |         |               |
	  | aaab   | it-department-security | 02           |             |             |         | it-department |
  
  Scenario Outline: Manager append an employee to a root team
	Given I'm connected with "<login>" "<password>"
	When I append the employee "<employeeId>" to team "<teamName>"
	Then the team "<teamName>" should have these employees [ <employeeIdExpected> ]
	And the error message should be "<errorMessage>"
	Examples: Success
	  | login         | password   | employeeId | employeeIdExpected | teamName      | errorMessage |
	  | manager-A2016 | Secret123@ | 05         | 02, 04,05          | it-department |              |
	
	Examples: Failure
	  | login         | password   | employeeId | employeeIdExpected | teamName          | errorMessage                                    |
	  | manager-A2016 | Secret123@ | 05         |                    | not-existing-team | The team 'not-existing-team' not found.         |
	  | leader-it01   | Secret123@ | 05         | 02, 04             | it-department     | Only a manager can append member to a root team |
  
  Scenario Outline: Team leader append an employee to a sub team
	Given I'm connected with "<login>" "<password>"
	When I append the employee "<employeeId>" to team "<teamName>"
	Then the team "<teamName>" should have these employees [ <employeeIdExpected> ]
	And the error message should be "<errorMessage>"
	Examples: Success
	  | login       | password   | employeeId | employeeIdExpected | teamName               | errorMessage |
	  | leader-it01 | Secret123@ | 04         | 04                 | it-department-security |              |
	Examples: Failure
	  | login             | password   | employeeId | employeeIdExpected | teamName               | errorMessage                                                                     |
	  | leader-customer01 | Secret123@ | 05         |                    | it-department-security | 'leader-customer01' is not authorized to 'append employee to this sub team'.     |
	  | leader-it01       | Secret123@ | 05         |                    | it-department-security | 'leader-it01' is not authorized to 'append a foreign employee to this sub team'. |
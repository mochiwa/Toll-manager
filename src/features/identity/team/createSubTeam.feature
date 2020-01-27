Feature: create a team
  
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
  
  
  Scenario Outline: Success : Create a sub Team
	Given I'm connected with "<login>" "<password>"
	When I create a sub team named "<subTeamName>" for the team "<parentTeamName>"
	Then the team "<parentTeamName>" has sub team "<subTeamName>"
	Examples:
	  | login         | password   | subTeamName     | parentTeamName |
	  | manager-A2016 | Secret123@ | it-department-A | it-department  |
	  | leader-it01   | Secret123@ | it-department-A | it-department  |
  
  Scenario Outline: Failure : Create a sub Team
	Given I'm connected with "<login>" "<password>"
	When I create a sub team named "<subTeamName>" for the team "<parentTeamName>"
	Then the error message should be "<errorMessage>"
	Examples:
	  | login             | password   | subTeamName            | parentTeamName | errorMessage                                                                           |
	  | leader-it01       | Secret123@ | it-department          | it-department  | The sub team cannot have the same name as its parent.                                  |
	  | leader-it01       | Secret123@ | it-department-security | it-department  | A sub team 'it-department-security' already exist in this team.                        |
	  | leader-it01       | Secret123@ | it-department          | UnknownTeam    | The parent team 'unknownteam' not found.                                               |
	  | leader-customer01 | Secret123@ | it_department          | it-department  | 'leader-customer01' is not authorized to 'create sub team where he is not the leader'. |
  
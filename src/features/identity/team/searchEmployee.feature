Feature: search employee from a team
  
  Background:
	Given I have these users
	  | employeeId | login         | password   |
	  | 01         | manager-A2016 | Secret123@ |
	Given I have these groups
	  | name    | roles       | groupToDo | members           |
	  | manager | manage_team | *         | 01->manager-A2016 |
	Given I have these employees
	  | employeeId | niss            | forename | name    | birthday   | address                                      | mobile        | email                     |
	  | 01         | 56.08.10-384.11 | david    | born    | 1956/08/10 | 345,31st street,11535,new-york,united states | 0478.72.02.02 | david_born@hotmail.com    |
	  | 02         | 91.12.21-999.21 | john     | doe     | 1990/12/21 | 12,10st street,11535,new-york,united states  | 0476.11.02.02 | john_doe@hotmail.com      |
	  | 03         | 45.10.56-118.21 | johny    | biggest | 1945/01/13 | 101,99st street,11535,new-york,united states | 0479.13.02.02 | johny_orlan@hotmail.com   |
	  | 04         | 91.10.21-111.50 | eric     | robwn   | 1991/10/21 | 72,21st street,11535,new-york,united states  | 0470.99.02.03 | eric_brown@hotmail.com    |
	  | 05         | 46.11.56-118.10 | david    | biggest | 1946/11/11 | 477,72st street,11535,new-york,united states | 0479.92.06.78 | david_biggest@hotmail.com |
	Given  I have these teams
	  | teamId | teamName               | teamLeaderId | employeesId    | description | subTeam | parentTeam    |
	  | aaa    | it-department          | 01           | 02,01,03,04,05 |             |         |               |
	  | aaab   | it-department-security | 01           | 04,05          |             |         | it-department |
  
  
  Scenario Outline: Search employee by name
	Given I input "<name>" in field
	When I valid the search by name for team "<teamName>"
	Then I should have these employees [ <employeeIdExpected> ]
	Examples:
	  | name    | teamName               | employeeIdExpected |
	  | born    | it-department          | 01                 |
	  | biggest | it-department          | 03,05              |
	  | biggest | it-department-security | 05                 |
	  | b       | it-department          | 01,03,04,05        |
	  |         | it-department          | 02,01,03,04,05     |
  
  Scenario Outline: Search employee by forename
	Given I input "<forename>" in field
	When I valid the search by forename for team "<teamName>"
	Then I should have these employees [ <employeeIdExpected> ]
	Examples:
	  | forename | teamName               | employeeIdExpected |
	  | david    | it-department          | 01 ,05             |
	  | john     | it-department          | 02,03              |
	  | i        | it-department-security | 04,05              |
	  | w        | it-department          |                    |
	  |          | it-department          | 02,01,03,04,05     |
  
  Scenario Outline: Search employee by niss
	Given I input "<niss>" in field
	When I valid the search by niss for team "<teamName>"
	Then I should have these employees [ <employeeIdExpected> ]
	Examples:
	  | niss            | teamName      | employeeIdExpected |
	  | 56.08.10-384.11 | it-department | 01                 |
	  | 5               | it-department |                    |
	  | 56              | it-department | 01,03,05           |
	  | qsd@(           | it-department |                    |
	  |                 | it-department |                    |
  
  Scenario Outline: Search employee by identities information
	Given I input "<information>" in field
	When I valid the search identities information  for team "<teamName>"
	Then I should have these employees [ <employeeIdExpected> ]
	Examples:
	  | information     | teamName      | employeeIdExpected |
	  | 56.08.10-384.11 | it-department | 01                 |
	  | john            | it-department | 02,03              |
	  | john doe        | it-department | 02                 |
	  | doe  999        | it-department | 02                 |
	  | i  118          | it-department | 05,03              |
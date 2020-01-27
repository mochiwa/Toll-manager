Feature: authentication
  
  Background:
	Given I have these users
	  | employeeId | login | password   |
	  | 01         | admin | Secret123@ |
  
  Scenario Outline: sign in
	Given I'm connected with "<myLogin>" "<myPassword>"
	Then the error message should be "<errorMessage>"
	Examples: Success
	  | myLogin | myPassword | errorMessage |
	  | admin   | Secret123@ |              |
	Examples: Failure
	  | myLogin       | myPassword  | errorMessage                 |
	  | admin         | badpassword | Login or password incorrect. |
	  | badLogin      | Secret123@  | Login or password incorrect. |
	  | badLoginadmin | badpassword | Login or password incorrect. |
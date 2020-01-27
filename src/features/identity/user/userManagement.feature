Feature: user management
  
  Background:
	Given I have these users
	  | employeeId | login | password   |
	  | 01         | admin | Secret123@ |
  
  Scenario Outline: change password
	Given I'm connected like "<user>"
	When I change my password by "<newPassword>" "<newPasswordConfirmation>"
	Then my password should by "<password expected>"
	And the error message should be "<errorMessage>"
	And  I'm connected with "<user>" "<password expected>"
	
	Examples: success
	  | user  | newPassword | newPasswordConfirmation | password expected | errorMessage |
	  | admin | Admin123@    | Admin123@               | Admin123@         |     |
	Examples: failure
	  | user  | newPassword | newPasswordConfirmation | password expected | errorMessage                                                                                                               |
	  | admin | notSecure   | notSecure               | Secret123@        | The password must contain 5 to 55 characters , one or more Uppercase , one ore more numbers and one ore more of #?!@$%^&*- |
	  | admin | Admin123@   | AnotherPass456@         | Secret123@        | Password does not match.                                                                                                   |

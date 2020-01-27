Feature: access and role feature
  
  Scenario Outline: append a member
	Given I have these groups
	  | name             | roles         | groupToDo                 | members  |
	  | manager          | append_member | <groupWhereCallerHasRole> | <caller> |
	  | department heads |               |                           |          |
	When "<caller>" wants to append "<targetMember>" to "<groupToAppend>"
	Then the "<groupToAppend>" should have these members: [ <membersExcepted> ]
	And the error message should be "<errorMessage>"
	Examples: success
	  | caller     | targetMember | groupWhereCallerHasRole | groupToAppend    | membersExcepted         | errorMessage |
	  | aaa->admin | bbb->eric    | *                       | department heads | bbb->eric               |              |
	  | aaa->admin | bbb->eric    | manager                 | manager          | aaa->admin && bbb->eric |              |
	Examples: failure
	  | caller     | targetMember | groupWhereCallerHasRole | groupToAppend | membersExcepted | errorMessage                                                        |
	  | aaa->admin | bbb->eric    | department heads        | manager       | aaa->admin      | 'admin' has not the role to 'append_member' to the group 'manager'. |
	  | aaa->admin |              | manager                 | manager       | aaa->admin      | The member to append to group is required.                          |
	  | aaa->admin | aaa->admin   | manager                 | manager       | aaa->admin      | 'admin' is already present in group 'manager'.                      |
  
  
  Scenario Outline: remove a member
	Given I have these groups
	  | name             | roles         | groupToDo                 | members        |
	  | manager          | remove_member | <groupWhereCallerHasRole> | <caller>       |
	  | department heads |               |                           | <targetMember> |
	When "<caller>" wants to remove "<targetMember>" to "<groupToRemove>"
	Then the "<groupToRemove>" should have these members: [ <membersExcepted> ]
	And the error message should be "<errorMessage>"
	Examples: success
	  | caller     | targetMember | groupWhereCallerHasRole | groupToRemove    | membersExcepted | errorMessage |
	  | aaa->admin | bbb->eric    | department heads        | department heads |                 |              |
	  | aaa->admin | bbb->eric    | *                       | department heads |                 |              |
	Examples: failure
	  | caller     | targetMember | groupWhereCallerHasRole | groupToRemove    | membersExcepted | errorMessage                                                                 |
	  | aaa->admin | bbb->eric    | manager                 | department heads | bbb->eric       | 'admin' has not the role to 'remove_member' to the group 'department heads'. |
	  | aaa->admin |              | *                       | department heads |                 | The member to remove from group is required.                                 |

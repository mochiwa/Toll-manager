package tollmanager.model.access;


import tollmanager.model.access.authorization.AuthorizationService;
import tollmanager.model.identity.EmployeeId;
import tollmanager.infrastructure.persistance.inMemory.InMemoryGroupRepository;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AuthorizationServiceTest {
    private AuthorizationService service;
    private GroupMember caller;
    private Group adminGroup;


    @Before
    public void setUp()
    {
        GroupRepository repository=new InMemoryGroupRepository();
        service=new AuthorizationService(repository);

        caller=GroupMember.of(EmployeeId.of("aaa"),"admin");
        adminGroup=GroupBuilder.of().setName("admin").create();
        repository.add(adminGroup);
    }

    @Test
    public void isAuthorizedToCreateEmployee_shouldReturnTrue_whenCallerHasRole() {
        adminGroup.appendMember(caller);
        adminGroup.appendRole(Role.createEmployee());

        assertTrue(service.isAuthorizedToCreateEmployee(caller));
    }
    @Test
    public void isAuthorizedToCreateEmployee_shouldReturnFalse_whenCallerHasNotRole() {
        adminGroup.appendMember(caller);

        assertFalse(adminGroup.hasRole(Role.createEmployee()));
        assertFalse(service.isAuthorizedToCreateEmployee(caller));
    }

    @Test
    public void isAuthorizedToAppendMember_shouldReturnTrue_whenCallerHasRole(){
        adminGroup.appendMember(caller);
        GroupName groupNameTarget=GroupName.of("target");
        adminGroup.appendRole(Role.append_member(groupNameTarget));

        assertTrue(service.isAuthorizedToAppendMember(caller,groupNameTarget));
    }
    @Test
    public void isAuthorizedToAppendMember_shouldReturnTrue_whenCallerHasNotRole_But_HasRoleToCreateUserToTheGroup(){
        adminGroup.appendMember(caller);
        GroupName groupNameTarget=GroupName.of("target");
        adminGroup.appendRole(Role.createUser(groupNameTarget));

        assertTrue(service.isAuthorizedToAppendMember(caller,groupNameTarget));
    }
    @Test
    public void isAuthorizedToAppendMember_shouldReturnFalse_whenCallerHasNotRole(){
        adminGroup.appendMember(caller);
        GroupName groupNameTarget=GroupName.of("target");

        assertFalse(service.isAuthorizedToAppendMember(caller,groupNameTarget));
    }

    @Test
    public void isAuthorizedToRemoveMember_shouldReturnTrue_whenCallerHasRole() {
        adminGroup.appendMember(caller);
        GroupName groupNameTarget=GroupName.of("target");
        adminGroup.appendRole(Role.remove_member(groupNameTarget));

        assertTrue(service.isAuthorizedToRemoveMember(caller,groupNameTarget));
    }
    @Test
    public void isAuthorizedToRemoveMember_shouldReturnFalse_whenCallerHasNotRole() {
        adminGroup.appendMember(caller);
        GroupName groupNameTarget=GroupName.of("target");

        assertFalse(service.isAuthorizedToRemoveMember(caller,groupNameTarget));
    }
    @Test
    public void isAuthorizedToRemoveMember_shouldReturnTrue_whenCallerHasNotRole_But_HasRoleToDeleteUserToTheGroup(){
        adminGroup.appendMember(caller);
        GroupName groupNameTarget=GroupName.of("target");
        adminGroup.appendRole(Role.deleteUser(groupNameTarget));

        assertTrue(service.isAuthorizedToRemoveMember(caller,groupNameTarget));
    }

    @Test
    public void isAuthorizedToCreateUser_shouldReturnTrue_whenCallerHasRole() {
        adminGroup.appendMember(caller);
        adminGroup.appendRole(Role.createUser(adminGroup.name()));

        assertTrue(service.isAuthorizedToCreateUser(caller,adminGroup.name()));
    }
    @Test
    public void isAuthorizedToCreateUser_shouldReturnFalse_whenCallerHasNotRole() {
        adminGroup.appendMember(caller);

        assertFalse(service.isAuthorizedToCreateUser(caller,adminGroup.name()));
    }

    @Test
    public void isAuthorizedToDeleteUser_shouldReturnTrue_whenCallerHasRole() {
        adminGroup.appendMember(caller);
        adminGroup.appendRole(Role.deleteUser(adminGroup.name()));

        assertTrue(service.isAuthorizedToDeleteUser(caller,adminGroup.name()));
    }
    @Test
    public void isAuthorizedToDeleteUser_shouldReturnFalse_whenCallerHasNotRole() {
        adminGroup.appendMember(caller);

        assertFalse(service.isAuthorizedToDeleteUser(caller,adminGroup.name()));
    }


    @Test
    public void isAuthorizedToCreateTeam_shouldReturnTrue_whenCallerHasRole()
    {
        adminGroup.appendRole(Role.create_team());
        adminGroup.appendMember(caller);

        assertTrue(service.isAuthorizedToCreateTeam(caller));
    }

    @Test
    public void isAuthorizedToCreateTeam_shouldReturnFalse_whenCallerHasNotRole()
    {
        adminGroup.appendMember(caller);

        assertFalse(service.isAuthorizedToCreateTeam(caller));
    }

    @Test
    public void isAuthorizedToManageTeam_shouldReturnTrue_whenCallerHasRole()
    {
        adminGroup.appendRole(Role.manage_team(GroupName.wildCard()));
        adminGroup.appendMember(caller);

        assertTrue(service.isAuthorizedToManageTeam(caller));
    }

    @Test
    public void isAuthorizedToManageTeam_shouldReturnFalse_whenCallerHasNotRole()
    {
        adminGroup.appendMember(caller);

        assertFalse(service.isAuthorizedToManageTeam(caller));
    }

    @Test
    public void isAuthorizedToAppendEmployeeToATeam_shouldReturnTrue_WhenCallerHasRoleToCreateTeam() {
        adminGroup.appendRole(Role.create_team());
        adminGroup.appendMember(caller);

        assertTrue(service.isAuthorizedToAppendEmployeeToATeam(caller));
    }

    @Test
    public void isAuthorizedToAppendEmployeeToATeam_shouldReturnTrue_WhenCallerHasRoleToManage() {
        adminGroup.appendRole(Role.manage_team(GroupName.wildCard()));
        adminGroup.appendMember(caller);

        assertTrue(service.isAuthorizedToAppendEmployeeToATeam(caller));
    }
    @Test
    public void isAuthorizedToAppendEmployeeToATeam_shouldReturnFalse_WhenCallerHasNotRoleToManageAndCreateTeam() {
        adminGroup.appendMember(caller);

        assertFalse(service.isAuthorizedToAppendEmployeeToATeam(caller));
    }


    @Test
    public void isAuthorizedToCreatePlanning_shouldReturnTrue_whenUserHasRoleToManagePlanning(){
        adminGroup.appendRole(Role.manage_planning());
        adminGroup.appendMember(caller);

        assertTrue(service.isAuthorizedToManagePlanning(caller));
    }

}

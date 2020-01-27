package tollmanager.model.identity.user;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import tollmanager.TestHelper;
import tollmanager.infrastructure.persistance.inMemory.InMemoryGroupRepository;
import tollmanager.infrastructure.persistance.inMemory.InMemoryUserRepository;
import tollmanager.infrastructure.service.MD5PasswordEncryptionService;
import tollmanager.model.access.*;
import tollmanager.model.access.authorization.AuthorizationService;
import tollmanager.model.access.authorization.IAuthorizationService;
import tollmanager.model.access.authorization.IllegalRightException;

import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.user.password.Password;

import java.util.LinkedHashSet;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

public class UserServiceTest {
    private UserRepository userRepository;
    private GroupRepository groupRepository;
    private IGroupService groupService;
    private IUserService service;
    private User caller;
    private User toDelete;


    private IAuthorizationService authorizationService;

    @Before
    public void setUp() {
        userRepository=new InMemoryUserRepository();
        groupRepository=Mockito.spy(new InMemoryGroupRepository());
        authorizationService= Mockito.mock(IAuthorizationService.class);
        groupService=Mockito.spy(new GroupService(groupRepository, authorizationService));

        service=new UserService(userRepository,groupService, authorizationService);


        caller=User.of(EmployeeId.of("aaa"),Login.of("admin"), Password.of("Secret123@"));
        toDelete=User.of(EmployeeId.of("bbb"),Login.of("aLogin"), Password.of("Secret123@"));

        userRepository.add(caller);
        userRepository.add(toDelete);
        groupRepository.add(GroupBuilder
                .of()
                .addRole(Role.deleteUser(GroupName.wildCard()))
                .addRole(Role.remove_member(GroupName.wildCard()))
                .addMember(caller.toGroupMember())
                .addMember(toDelete.toGroupMember())
                .setName("manager")
                .create()
        );
    }


    @Test(expected = IllegalRightException.class)
    public void deleteUser_shouldThrow_whenCaller_IsNotAuthorized() {
        User notAuthorizedUser=User.of(EmployeeId.of("xxx"),Login.of("aLogin"),Password.of("aPassword"));

        Mockito.doReturn(false).when(authorizationService).isAuthorizedToCreateUser(any(),any());
        service.deleteUser(notAuthorizedUser,toDelete);
    }
    @Test(expected = IllegalArgumentException.class)
    public void deleteUser_shouldThrow_whenCallerTryToDeleteHimself() {
        service.deleteUser(caller,caller);
    }
    @Test(expected = IllegalRightException.class)
    public void deleteUser_shouldThrow_whenCallerTryToDeleteAdmin() {
        User user= TestHelper.of().getUser("01","aUser");
        User toDelete= TestHelper.of().getUser("01","admin");

        service.deleteUser(user,toDelete);
    }
    @Test
    public void deleteUser_shouldRemoveGroupMembersWhereHeBelong() {
        User user= TestHelper.of().getUser("01","aUser");
        User toDelete= TestHelper.of().getUser("01","anotherUser");
        Group group=TestHelper.of().getGroup("aGroup");
        group.appendMember(toDelete.toGroupMember());
        groupRepository.add(group);

        Mockito.doReturn(true).when(authorizationService).isAuthorizedToRemoveMember(any(),any());

        assertFalse(groupRepository.groupsWhereBelong(toDelete.toGroupMember()).isEmpty());
        service.deleteUser(user,toDelete);
        assertTrue(groupRepository.groupsWhereBelong(toDelete.toGroupMember()).isEmpty());
    }
   /* @Test
    public void deleteUser_shouldDeleteTheUser_whenCaller_IsAuthorized() {
        service.deleteUser(caller,toDelete);
        assertNull(userRepository.findByLogin(toDelete.login()));
    }*/

    @Test
    public void isAdministrator_shouldReturnTrue_WhenLoginIs_admin() {
        User user= TestHelper.of().getUser("01","admin");
        assertTrue(service.isAdministrator(user));
    }
    @Test
    public void isAdministrator_shouldReturnFalse_WhenLoginIsNot_admin() {
        User user= TestHelper.of().getUser("01","user");

        assertFalse(service.isAdministrator(user));
    }

    @Test
    public void changePassword_shouldChangeThePasswordOfTheUser() {
        MD5PasswordEncryptionService encryption=new MD5PasswordEncryptionService();
        User before=User.of(EmployeeId.of("aze"),Login.of("aLogina"),Password.of("aPassword"));
        Password newPassWord=Password.of("Secret123@");
        User expected=User.of(EmployeeId.of("aze"),Login.of("aLogina"),encryption.encryption(newPassWord));
        userRepository.add(before);
        service.changePassword(before,newPassWord,newPassWord,encryption);
        assertEquals(expected,userRepository.findByLogin(before.login()));
    }
    @Test(expected = IllegalArgumentException.class)
    public void changePassword_shouldThrow_whenNewPasswordAndConfirmationAreNotEquals() {
        User user=User.of(EmployeeId.of("aze"),Login.of("aLogina"),Password.of("aPassword"));
        service.changePassword(user,Password.of("aPassword"),Password.of("AnotherPassword"),new MD5PasswordEncryptionService());
    }
    @Test(expected = IllegalArgumentException.class)
    public void changePassword_shouldThrow_whenNewPasswordIsNotSecure() {
        User user=User.of(EmployeeId.of("aze"),Login.of("aLogina"),Password.of("aPassword"));
        service.changePassword(user,Password.of("aPassword"),Password.of("aPassword"),new MD5PasswordEncryptionService());
    }


    @Test
    public void getAllTypeOfUser_shouldReturn_AdminAndManager_whenThereAreAdminAndManagerInGroupRepository() {
        LinkedHashSet<Group> typeOFUserExpected=new LinkedHashSet<>();
        typeOFUserExpected.add(GroupBuilder.of().setName("admin").create());
        typeOFUserExpected.add(GroupBuilder.of().setName("Manager").create());

        Mockito.doReturn(typeOFUserExpected).when(groupService).getAllGroupInRepository();

        LinkedHashSet<GroupName> types=service.getAllTypeOfUser();

        typeOFUserExpected.forEach(g->assertTrue(types.contains(g.name())));
    }
}
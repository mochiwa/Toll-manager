package tollmanager.model.identity.user;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import tollmanager.infrastructure.persistance.inMemory.InMemoryGroupRepository;
import tollmanager.infrastructure.persistance.inMemory.InMemoryUserRepository;
import tollmanager.infrastructure.service.MD5PasswordEncryptionService;
import tollmanager.model.access.*;
import tollmanager.model.access.authorization.AuthorizationService;
import tollmanager.model.access.authorization.IllegalRightException;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.user.password.IPasswordEncryptionService;
import tollmanager.model.identity.user.password.Password;
import tollmanager.model.identity.user.password.PasswordException;


public class UserProviderServiceTest {
    private UserRepository userRepository;
    private IPasswordEncryptionService encryptionService;
    private UserProviderService service;
    private GroupRepository groupRepository;
    private User caller;

    @Before
    public void setUp() {
        groupRepository = new InMemoryGroupRepository();
        userRepository=new InMemoryUserRepository();
        encryptionService=new MD5PasswordEncryptionService();

        AuthorizationService authorizationService=new AuthorizationService(groupRepository);
        GroupService groupService=new GroupService(groupRepository,authorizationService);

        service=new UserProviderService(userRepository, groupService,authorizationService,encryptionService);

        caller=User.of(EmployeeId.of("aaa"),Login.of("admin"),Password.of("Secret123@"));
        groupRepository.add(GroupBuilder
                .of()
                .addRole(Role.createUser(GroupName.of("manager")))
                .addRole(Role.append_member(GroupName.of("manager")))
                .addMember(caller.toGroupMember())
                .setName("manager")
                .create()
        );
    }


    @Test(expected = IllegalArgumentException.class)
    public void registerUser_shouldThrow_whenTheEmployeeId_is_AlreadyLinkedToAnAccount() {
        EmployeeId alreadyUsedId=EmployeeId.of("aaa");
        userRepository.add(User.of(alreadyUsedId,Login.of("aLogin"),Password.of("aPassword")));

        service.registerUser(
                caller,
                GroupName.of("manager"),
                Login.of("AnotherLogin"),
                Password.of("Secret123@"),
                Password.of("Secret123@"),
                alreadyUsedId
        );
    }
    @Test(expected = LoginException.class)
    public void registerUser_shouldThrow_whenLogin_is_alreadyUsed() {
        Login alreadyUsedLogin=Login.of("alreadyUsedLogin");
        userRepository.add(User.of(EmployeeId.of("bbb"),alreadyUsedLogin,Password.of("Secret123@")));

        service.registerUser(
                caller,
                GroupName.of("manager"),
                alreadyUsedLogin,
                Password.of("Secret123@"),
                Password.of("Secret123@"),
                EmployeeId.of("aaa")
        );
    }

    @Test(expected = PasswordException.class)
    public void registerUser_shouldThrow_whenThePassword_IsNotSecure() {
        Password password=Mockito.spy(Password.of("aPassword"));
        Mockito.when(password.isSecure()).thenReturn(false);
        service.registerUser(
                caller,
                GroupName.of("manager"),
                Login.of("aLogin"),
                password,
                password,
                EmployeeId.of("aaa")
        );
    }
    @Test(expected = IllegalArgumentException.class)
    public void registerUser_shouldThrow_WhenTheConfirmedPassword_NotMatch_ToThePassword() {
        service.registerUser(
                caller,
                GroupName.of("manager"),
                Login.of("aLogin"),
                Password.of("Secret123@"),
                Password.of("Wrong123@"),
                EmployeeId.of("aaa")
        );
    }

    @Test(expected = IllegalRightException.class)
    public void registerUser_shouldThrow_whenTheCaller_isNot_authorizedToAppendMemberToGroup() {
        Group anotherGroup= GroupBuilder.of().setName("anotherGroup").create();
        groupRepository.add(anotherGroup);

        service.registerUser(
                caller,
                anotherGroup.name(),
                Login.of("aLogin"),
                Password.of("Secret123@"),
                Password.of("Secret123@"),
                EmployeeId.of("aaa")
        );
    }
    @Test(expected = IllegalRightException.class)
    public void registerUser_shouldThrow_whenTheCaller_isNot_authorizedToCreateUser() {
        service.registerUser(
                User.of(EmployeeId.of("xxx"),Login.of("NotAuthorizedUser"),Password.of("Secret123")),
                GroupName.of("manager"),
                Login.of("aLogin"),
                Password.of("Secret123@"),
                Password.of("Secret123@"),
                EmployeeId.of("aaa")
        );
    }

    @Test
    public void registerUser_shouldReturnUser() {
        User user=service.registerUser(
                caller,
                GroupName.of("manager"),
                Login.of("aLogin"),
                Password.of("Secret123@"),
                Password.of("Secret123@"),
                EmployeeId.of("aaa")
        );

        Assert.assertEquals(
                User.of(EmployeeId.of("aaa"), Login.of("aLogin"), encryptionService.encryption(Password.of("Secret123@"))),
                user
        );
    }
}

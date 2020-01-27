package tollmanager.model.identity.user.authentication;

import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.user.*;
import tollmanager.model.identity.user.password.IPasswordEncryptionService;
import tollmanager.infrastructure.service.MD5PasswordEncryptionService;
import tollmanager.model.identity.user.password.Password;
import tollmanager.infrastructure.persistance.inMemory.InMemoryUserRepository;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AuthenticationServiceTest {
    private UserRepository repository;
    private IAuthenticationService service;
    private IPasswordEncryptionService encryptionService;

    @Before
    public void setUp() {
        repository=new InMemoryUserRepository();
        encryptionService=new MD5PasswordEncryptionService();
        service=new AuthenticationService(repository,encryptionService);
    }

    @Test
    public void signIn_shouldReturnTheUser_whenLoginAndPasswordMatch() {
        User expectedUser=User.of(EmployeeId.of("aaa"), Login.of("aLogin"), encryptionService.encryption(Password.of("Secret123@")));
        repository.add(expectedUser);

        User user=service.signIn(Login.of("aLogin"),Password.of("Secret123@"));
        assertEquals(expectedUser,user);
    }

    @Test(expected = AuthenticationException.class)
    public void signIn_shouldThrowAuthenticationException_whenPasswordNotMatch() {
        repository.add(User.of(EmployeeId.of("aaa"),Login.of("aLogin"),Password.of("Secret123@")));
        service.signIn(Login.of("aLogin"),Password.of("BadPassword123@"));
    }
    @Test(expected = AuthenticationException.class)
    public void signIn_shouldThrowAuthenticationException_whenLoginNotMatch() {
        repository.add(User.of(EmployeeId.of("aaa"),Login.of("aLogin"),Password.of("Secret123@")));
        service.signIn(Login.of("badLogin"),Password.of("Secret123@"));
    }

    @Test
    public void signIn_shouldThrowExceptionWith_loginOrPasswordIncorrectMessage_WhenErrorOccur() {
        String errorMessageExpected="Login or password incorrect.";
        Login correctLogin=Login.of("aLogin");
        Login badLogin=Login.of("wrong");
        Password correctPassword=Password.of("Secret123@");
        Password badPassword=Password.of("Wrong123@");

        repository.add(User.of(EmployeeId.of("aaa"),correctLogin,correctPassword));

        try
        {
            service.signIn(correctLogin,badPassword);
        }catch(Throwable th)
        {
            assertEquals(errorMessageExpected,th.getMessage());
        }
        try
        {
            service.signIn(badLogin,correctPassword);
        }catch(Throwable th)
        {
            assertEquals(errorMessageExpected,th.getMessage());
        }
        try
        {
            service.signIn(badLogin,badPassword);
        }catch(Throwable th)
        {
            assertEquals(errorMessageExpected,th.getMessage());
        }
    }
}

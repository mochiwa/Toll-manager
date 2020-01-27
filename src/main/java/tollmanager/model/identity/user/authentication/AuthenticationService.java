package tollmanager.model.identity.user.authentication;

import tollmanager.model.identity.user.Login;
import tollmanager.model.identity.user.password.IPasswordEncryptionService;
import tollmanager.model.identity.user.password.Password;
import tollmanager.model.identity.user.User;
import tollmanager.model.identity.user.UserRepository;

import java.util.Objects;

public class AuthenticationService implements IAuthenticationService {
    private UserRepository userRepository;
    private IPasswordEncryptionService encryptionService;

    public AuthenticationService(UserRepository userRepository,IPasswordEncryptionService encryptionService) {
        Objects.requireNonNull(userRepository,"The user repository cannot be null.");
        Objects.requireNonNull(encryptionService,"The encryption service cannot be null");

        this.userRepository = userRepository;
        this.encryptionService=encryptionService;
    }

    /**
     * @exception if the login and password don't match
     * @param login the login of the user
     * @param password the password of the user
     * @return
     */
    @Override
    public User signIn(Login login, Password password) {
        Objects.requireNonNull(login,"The login is required.");
        Objects.requireNonNull(password,"The password is required");

        User user=userRepository.findByLogin(login);
        if(user==null || !user.isPasswordMatch(encryptionService.encryption(password)))
            throw new AuthenticationException("Login or password incorrect.");
        return user;
    }
}

package tollmanager.model.identity.user.authentication;

import tollmanager.model.identity.user.Login;
import tollmanager.model.identity.user.password.Password;
import tollmanager.model.identity.user.User;

public interface IAuthenticationService {
    /**
     * @param login the login of the user
     * @param password the password of the user
     * @return User linked to login and password
     */
    User signIn(Login login, Password password);
}

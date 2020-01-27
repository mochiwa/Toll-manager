package features.identity;

import features.helper.KnowTheDomain;
import io.cucumber.java.en.Given;
import tollmanager.model.identity.user.Login;
import tollmanager.model.identity.user.User;
import tollmanager.model.identity.user.authentication.AuthenticationException;
import tollmanager.model.identity.user.authentication.AuthenticationService;
import tollmanager.model.identity.user.authentication.IAuthenticationService;
import tollmanager.model.identity.user.password.Password;

import static org.junit.Assert.assertNotNull;

public class AuthenticationSteps {
    private KnowTheDomain helper;
    private IAuthenticationService service;

    public AuthenticationSteps(KnowTheDomain toInject) {
        helper=toInject;
        service=new AuthenticationService(helper.userRepository(),helper.encryptionService());
    }

    @Given("I'm connected with {login} {password}")
    public void i_m_connected_with_login_password(Login login, Password password) {
        try{
            User connectedUser=service.signIn(login, password);
            assertNotNull(connectedUser);
            helper.setConnectedUser(connectedUser);
        }catch (AuthenticationException e)
        {
            helper.setErrorMessage(e.getMessage());
        }
    }


    @Given("I'm connected like {login}")
    public void i_m_connected_like(Login login) {
        try{
            helper.setConnectedUser(helper.userRepository().findByLogin(login));
        }catch (AuthenticationException e)
        {
            helper.setErrorMessage(e.getMessage());
        }
    }
}

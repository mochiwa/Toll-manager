package features.identity;

import features.helper.KnowTheDomain;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import tollmanager.infrastructure.service.MD5PasswordEncryptionService;
import tollmanager.model.access.GroupName;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.user.IUserProviderService;
import tollmanager.model.identity.user.IUserService;
import tollmanager.model.identity.user.Login;
import tollmanager.model.identity.user.User;
import tollmanager.model.identity.user.password.Password;

import java.util.List;

import static org.junit.Assert.*;

public class UserSteps {
    private KnowTheDomain helper;
    private IUserProviderService userProviderService;
    private IUserService userService;

    public UserSteps(KnowTheDomain toInject) {
        helper = toInject;
        userProviderService = helper.userProviderService();
        userService = helper.userService();
    }

    @Given("I have these users")
    public void i_have_these_users(List<User> users) {
        users.forEach(u -> helper.userRepository().add(u));
    }

    @When("I create an account with {login} {password} {password} for the employee with {id} in {groupName}")
    public void i_create_an_account_with_for_the_employee_with_in(Login login,Password password,Password confirmedPassword,EmployeeId id,GroupName groupName) {
        try {
            assertNotNull(userProviderService.registerUser(helper.connectedUser(), groupName, login, password, confirmedPassword, id));
        } catch (Exception exception) {
            helper.setErrorMessage(exception.getMessage());
        }
    }

    @When("I delete the user {login}")
    public void i_delete_the_user(Login login) {
        try {
            userService.deleteUser(helper.connectedUser(),  helper.userRepository().findByLogin(login));
            assertNull(helper.userRepository().findByLogin(login));
        } catch (Exception e) {
            helper.setErrorMessage(e.getMessage());
        }
    }

    @When("I change my password by {password} {password}")
    public void i_change_my_password_by(Password password, Password confirmation) {
        try {
            userService.changePassword(helper.connectedUser(),password,confirmation,helper.encryptionService());
        }catch (Exception e)
        {
            helper.setErrorMessage(e.getMessage());
        }
    }

    @Then("my password should by {password}")
    public void my_password_should_by(Password password) {
        Assert.assertTrue(helper.connectedUser().isPasswordMatch(helper.encryptionService().encryption(password)));
    }


}

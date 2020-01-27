package tollmanager.model.identity.user;

import tollmanager.model.access.GroupName;
import tollmanager.model.access.GroupService;
import tollmanager.model.access.IGroupService;
import tollmanager.model.access.Role;
import tollmanager.model.access.authorization.AuthorizationService;
import tollmanager.model.access.authorization.IAuthorizationService;
import tollmanager.model.access.authorization.IllegalRightException;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.user.password.IPasswordEncryptionService;
import tollmanager.model.identity.user.password.Password;
import tollmanager.model.identity.user.password.PasswordException;

import java.util.Objects;


/**
 * service that provide user
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class UserProviderService implements IUserProviderService {
    private UserRepository userRepository;
    private IPasswordEncryptionService encryptionService;
    private IAuthorizationService authorizationService;
    private IGroupService groupService;

    public UserProviderService(UserRepository userRepository, IGroupService groupService, IAuthorizationService authorizationService, IPasswordEncryptionService encryptionService) {
        Objects.requireNonNull(userRepository,"The user repository cannot be null.");
        Objects.requireNonNull(groupService,"The group service cannot be null.");
        Objects.requireNonNull(authorizationService,"The authorization service cannot be null");
        Objects.requireNonNull(encryptionService,"The encryption service cannot be null.");

        this.userRepository=userRepository;
        this.groupService=groupService;
        this.authorizationService=authorizationService;
        this.encryptionService=encryptionService;
    }

    /**
     *
     *
     * @exception IllegalRightException if caller not authorized to create user
     * @exception IllegalArgumentException if the employee has already an account
     * @exception LoginException if the login already exist
     * @exception IllegalArgumentException if passwords don't match
     * @exception IllegalArgumentException if password not secure
     *
     * @param caller who's asks the service
     * @param groupName the group where append the user
     * @param login the login of the user
     * @param password the password of the user
     * @param confirmedPassword the confirmed password of the user
     * @param employeeId The id of the employee linked to the user
     * @return
     */
    @Override
    public User registerUser(User caller, GroupName groupName,Login login, Password password, Password confirmedPassword, EmployeeId employeeId) {
        Objects.requireNonNull(caller,"the user who calls this service is required");
        Objects.requireNonNull(login,"The login is required.");
        Objects.requireNonNull(password,"The password is required.");
        Objects.requireNonNull(employeeId,"The employee id is required.");
        Objects.requireNonNull(groupName,"The group name is required.");

        if(!authorizationService.isAuthorizedToCreateUser(caller.toGroupMember(),groupName))
            throw new IllegalRightException(caller.toGroupMember(), Role.createUser(groupName));

        if(isEmployeeHasAlreadyAccount(employeeId)) {
            throw new IllegalArgumentException("This employee already has an account.");
        }
        if(isLoginAlreadyUser(login)) {
            throw new LoginException("This login is already used.");
        }
        if(!isSamePassword(password,confirmedPassword)) {
            throw new IllegalArgumentException("Password does not match.");
        }
        if(!password.isSecure()) {
            throw new PasswordException(PasswordException.CODE.NOTSECURE);
        }

        User user=User.of(employeeId,login,encryptionService.encryption(password));
        userRepository.add(user);
        groupService.appendMemberToGroup(caller.toGroupMember(),groupName,user.toGroupMember());
        return user;
    }


    private boolean isSamePassword(Password password,Password confirmPassword) {
        return password.equals(confirmPassword);
    }
    private boolean isEmployeeHasAlreadyAccount(EmployeeId employeeId) {
        return userRepository.findById(employeeId)!=null;
    }
    private boolean isLoginAlreadyUser(Login login) {
        return userRepository.findByLogin(login)!=null;
    }
}

package tollmanager.model.identity.user;

import tollmanager.model.access.Group;
import tollmanager.model.access.GroupName;
import tollmanager.model.access.IGroupService;
import tollmanager.model.access.authorization.IAuthorizationService;
import tollmanager.model.access.authorization.IllegalRightException;
import tollmanager.model.identity.user.password.IPasswordEncryptionService;
import tollmanager.model.identity.user.password.Password;
import tollmanager.model.identity.user.password.PasswordException;

import java.util.LinkedHashSet;
import java.util.Objects;
/**
 * service that deals with user
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class UserService implements IUserService {
    private UserRepository userRepository;
    private IAuthorizationService authorizationService;
    private IGroupService groupService;

    public UserService(UserRepository userRepository,IGroupService groupService,IAuthorizationService authorizationService)
    {
        Objects.requireNonNull(userRepository,"The user repository cannot be null.");
        Objects.requireNonNull(groupService,"The group service cannot be null.");
        Objects.requireNonNull(authorizationService,"The authorization service cannot be null.");

        this.userRepository=userRepository;
        this.groupService=groupService;
        this.authorizationService=authorizationService;
    }

    /**
     * @exception  IllegalRightException if the caller try to delete administrator
     * @exception IllegalArgumentException if the caller try to delete himself
     * @param caller who's asked the service
     * @param userToDelete user to delete
     */
    @Override
    public void deleteUser(User caller, User userToDelete) {
        Objects.requireNonNull(caller,"The caller is required");
        Objects.requireNonNull(userToDelete,"the user to delete is required");

        if(caller.equals(userToDelete))
            throw new IllegalArgumentException("'"+caller.login().value()+"' cannot be deleted by himself.");
        if(isAdministrator(userToDelete))
            throw new IllegalRightException(caller.toGroupMember(),"delete an administrator");

        groupService.removeMemberFromAllGroups(caller.toGroupMember(),userToDelete.toGroupMember());
        userRepository.remove(userToDelete);
    }

    /**
     * @param user the user
     * @return true if is admin , false else
     */
    @Override
    public boolean isAdministrator(User user) {
        return user.login().equals(Login.of("admin"));
    }

    /**
     * change the password if it's correct
     *
     * @exception PasswordException if password not secure
     * @exception PasswordException if password and confirmation not match
     *
     * @param caller who's asked the service
     * @param newPassWord the new password
     * @param confirmation confirmation of the new password
     * @param service kind of encryption
     */
    @Override
    public void changePassword(User caller, Password newPassWord, Password confirmation, IPasswordEncryptionService service) {
        if(!newPassWord.isSecure())
            throw new PasswordException(PasswordException.CODE.NOTSECURE);
        if(!newPassWord.equals(confirmation))
            throw new PasswordException(PasswordException.CODE.NOTMATCH);
        caller.changePassword(service.encryption(newPassWord));
        userRepository.updateUser(caller);
    }

    /**
     * @return list of all kind of user
     */
    @Override
    public LinkedHashSet<GroupName> getAllTypeOfUser() {
        LinkedHashSet<GroupName> typeOfUsers=new LinkedHashSet<>();
        groupService.getAllGroupInRepository().forEach(g->typeOfUsers.add(g.name()));
        return typeOfUsers;
    }
}

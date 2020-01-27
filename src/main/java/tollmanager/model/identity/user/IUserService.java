package tollmanager.model.identity.user;

import tollmanager.model.access.GroupName;
import tollmanager.model.identity.user.password.IPasswordEncryptionService;
import tollmanager.model.identity.user.password.Password;

import java.util.LinkedHashSet;

public interface IUserService {
    /**
     * @param caller who's asked the service
     * @param userToDelete user to delete
     */
    void deleteUser(User caller, User userToDelete);

    /**
     * @param user the user
     * @return true if admin, false else
     */
    boolean isAdministrator(User user);

    /**
     * @param caller who's asked the service
     * @param newPassWord the new password
     * @param confirmation confirmation of the new password
     * @param service kind of encryption
     */
    void changePassword(User caller, Password newPassWord, Password confirmation, IPasswordEncryptionService service);

    /**
     * @return set of groupname
     */
    LinkedHashSet<GroupName> getAllTypeOfUser();
}

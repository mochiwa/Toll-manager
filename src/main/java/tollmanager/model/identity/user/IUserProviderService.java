package tollmanager.model.identity.user;

import tollmanager.model.access.GroupName;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.user.password.Password;

public interface IUserProviderService {
    /**
     * @param caller who's asks the service
     * @param groupName the group where append the user
     * @param login the login of the user
     * @param password the password of the user
     * @param confirmedPassword the confirmed password of the user
     * @param employeeId The id of the employee linked to the user
     * @return a user
     */
    User registerUser(User caller, GroupName groupName,Login login, Password password, Password confirmedPassword, EmployeeId employeeId);


}

package tollmanager.model.identity;

import tollmanager.model.access.GroupName;
import tollmanager.model.identity.person.Person;
import tollmanager.model.identity.user.User;

public interface IEmployeeService {

    /**
     * @param employee The employee to find type
     * @see GroupName
     * @return name of type
     */
    GroupName getEmployeeType(Employee employee);

    /**
     * @param employee the employee to find account
     * @return the account of an user
     */
    User findUserLinkedToEmployee(Employee employee);

    /**
     * @param caller User who asks the service
     * @param employee Employee to edit
     * @param person Information to update to the employee
     * @return the employee updated or not
     */
    Employee updateEmployee(User caller, Employee employee, Person person);

    /**
     * @param caller User who's asked the service
     * @param employee the employee to delete
     */
    void deleteEmployee(User caller, Employee employee);
}

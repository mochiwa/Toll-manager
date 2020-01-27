package tollmanager.model.identity;

import tollmanager.model.access.Group;
import tollmanager.model.access.GroupName;
import tollmanager.model.access.GroupService;
import tollmanager.model.access.IGroupService;
import tollmanager.model.access.authorization.IAuthorizationService;
import tollmanager.model.access.authorization.IllegalRightException;
import tollmanager.model.identity.person.Person;
import tollmanager.model.identity.user.User;
import tollmanager.model.identity.user.UserRepository;

import java.util.Objects;
/**
 * Service that deals with employee
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class EmployeeService implements IEmployeeService {
    private EmployeeRepository employeeRepository;
    private UserRepository userRepository;
    private IGroupService groupService;
    private IAuthorizationService authorizationService;


    public EmployeeService(EmployeeRepository employeeRepository, UserRepository userRepository, IGroupService groupService, IAuthorizationService authorizationService) {
        Objects.requireNonNull(employeeRepository,"The employee repository cannot be null.");
        Objects.requireNonNull(userRepository,"The user repository  repository cannot be null.");
        Objects.requireNonNull(groupService,"The group service cannot be null.");
        Objects.requireNonNull(authorizationService,"The authorization service cannot be null.");

        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.groupService = groupService;
        this.authorizationService = authorizationService;
    }

    /**
     * Find the type of an employee
     * @param employee The employee to find type
     * @return groupname of the employee or null group
     */
    @Override
    public GroupName getEmployeeType(Employee employee) {
        Objects.requireNonNull(employee,"The employee is required.");

        User user=userRepository.findById(employee.employeeId());
        if(user==null)
            return GroupName.Null();

        Group group= groupService.getAllGroupInRepository().stream()
                .filter(g-> groupService.isBelongTo(user.toGroupMember(),g.name()))
                .findFirst()
                .orElse(null);
        return group==null ? GroupName.Null() : group.name();
    }

    /**
     * Return the user linked to an employee
     * @param employee the employee to find account
     * @return
     */
    @Override
    public User findUserLinkedToEmployee(Employee employee) {
        User user=userRepository.findById(employee.employeeId());
        return user;
    }


    /**
     * try to update,check data integrity , if success then the employee is returned
     *
     * @exception IllegalRightException if the caller is not authorized to create an employee
     * @exception NissException if the niss is modified and is already used
     *
     * @param caller User who asks the service
     * @param employee Employee to edit
     * @param person Information to update to the employee
     * @return employee edited
     */
    @Override
    public Employee updateEmployee(User caller, Employee employee, Person person) {
        Objects.requireNonNull(caller,"The caller is required.");
        Objects.requireNonNull(employee,"The employee is required.");
        Objects.requireNonNull(person,"The person is required");

        if(!authorizationService.isAuthorizedToCreateEmployee(caller.toGroupMember()))
            throw new IllegalRightException(caller.toGroupMember(),"update an employee");

        if(!employee.niss().equals(person.niss())  && employeeRepository.findByNiss(person.niss())!=null)
            throw new NissException("The niss is already used");


        employee.changeInformation(person);
        employeeRepository.update(employee);
        return employeeRepository.findById(employee.employeeId());
    }

    /**
     * @exception IllegalRightException if the caller is no authorized to create employee
     * @param caller User who's asked the service
     * @param employee the employee to delete
     */
    @Override
    public void deleteEmployee(User caller, Employee employee) {
        if(!authorizationService.isAuthorizedToCreateEmployee(caller.toGroupMember()))
            throw new IllegalRightException(caller.toGroupMember(),"delete an employee");
        employeeRepository.removeEmployee(employee.employeeId());
    }


}

package tollmanager.model.identity;

import tollmanager.model.access.authorization.IAuthorizationService;
import tollmanager.model.access.authorization.IllegalRightException;
import tollmanager.model.identity.contact.ContactInformation;
import tollmanager.model.identity.person.Birthday;
import tollmanager.model.identity.person.FullName;
import tollmanager.model.identity.person.Niss;
import tollmanager.model.identity.person.Person;
import tollmanager.model.identity.user.User;

import java.util.Objects;
/**
 * Service that deal with employee
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class EmployeeProviderService implements IEmployeeProviderService {
    private EmployeeRepository employeeRepository;
    private IAuthorizationService authorizationService;

    public EmployeeProviderService(EmployeeRepository employeeRepository,IAuthorizationService authorizationService) {
        Objects.requireNonNull(employeeRepository,"The employee repository cannot be null.");
        Objects.requireNonNull(authorizationService,"The authorization service cannot be null.");

        this.employeeRepository=employeeRepository;
        this.authorizationService=authorizationService;
    }

    /**
     * provide an uniquer employee with a unique Id given by repository.
     * @exception IllegalRightException if the caller is not authorized to create employee
     * @exception NissException if an employee with same niss already exist
     * @param caller who's asked the service
     * @param niss the niss of the employee to register
     * @param fullName the full name of the employee to register
     * @param birthday the birthday of the employee to register
     * @param contactInformation contact information of the employee to register
     * @return
     */
    @Override
    public Employee registerEmployee(User caller, Niss niss, FullName fullName, Birthday birthday, ContactInformation contactInformation) {
        Objects.requireNonNull(caller,"The user is required.");
        Objects.requireNonNull(niss,"The niss is required.");
        Objects.requireNonNull(fullName,"The full name is required.");
        Objects.requireNonNull(birthday,"The birthday is required.");
        Objects.requireNonNull(contactInformation,"The contact information is required.");

        if(!authorizationService.isAuthorizedToCreateEmployee(caller.toGroupMember()))
            throw new IllegalRightException(caller.toGroupMember(),"create an employee");

        if (employeeRepository.findByNiss(niss)!=null)
            throw new NissException("This Niss is already used.");

        Employee employee=Employee.of(
                employeeRepository.nextId(),
                Person.of(niss,fullName,birthday,contactInformation)
        );
        employeeRepository.add(employee);
        return employee;
    }
}

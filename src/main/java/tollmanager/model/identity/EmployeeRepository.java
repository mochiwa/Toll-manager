package tollmanager.model.identity;

import tollmanager.model.identity.contact.Email;
import tollmanager.model.identity.contact.Phone;
import tollmanager.model.identity.person.Niss;

public interface EmployeeRepository {
    void add(Employee employee);

    Employee findById(EmployeeId employeeId);

    Employee findByNiss(Niss of);

    EmployeeId nextId();

    Employee findByEmail(Email email);

    Employee findByPhone(Phone phone);

    void update(Employee employee);

    void removeEmployee(EmployeeId employeeId);

}

package tollmanager.infrastructure.persistance.inMemory;

import tollmanager.model.identity.Employee;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.EmployeeRepository;
import tollmanager.model.identity.contact.Email;
import tollmanager.model.identity.contact.Phone;
import tollmanager.model.identity.person.Niss;

import java.util.LinkedHashSet;
import java.util.UUID;

public class InMemoryEmployeeRepository implements EmployeeRepository {
    private LinkedHashSet<Employee> employees;

    public InMemoryEmployeeRepository()
    {
        employees=new LinkedHashSet<>();
    }

    @Override
    public void add(Employee employee) {
        employees.add(employee);
    }

    @Override
    public Employee findById(EmployeeId employeeId) {
        return employees.stream()
                .filter(e->e.employeeId().equals(employeeId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Employee findByNiss(Niss niss) {
        return employees.stream()
                .filter(e->e.niss().equals(niss))
                .findFirst()
                .orElse(null);
    }

    @Override
    public EmployeeId nextId() {
        return EmployeeId.of(UUID.randomUUID().toString());
    }

    @Override
    public void update(Employee employee) {
        //nothing
    }

    @Override
    public void removeEmployee(EmployeeId employeeId) {
        employees.removeIf(e->e.employeeId().equals(employeeId));
    }

    @Override
    public Employee findByEmail(Email email) {
        return employees.stream()
                .filter(e->e.person().contactInformation().emails().contains(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Employee findByPhone(Phone phone) {
        return employees.stream()
                .filter(e->e.person().contactInformation().phones().contains(phone))
                .findFirst()
                .orElse(null);
    }
}

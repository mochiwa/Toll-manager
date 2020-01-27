package tollmanager.application.command;

import tollmanager.model.identity.Employee;
import tollmanager.model.identity.person.Person;
/**
 * Represents the use case : edit information about an employee
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class EditEmployeeCommand {
    private Employee employee;
    private Person person;

    public EditEmployeeCommand(Employee employee, Person person) {
        this.employee = employee;
        this.person = person;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}

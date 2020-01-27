package tollmanager.application.command;

import tollmanager.model.identity.Employee;
/**
 * Represents the use case : delete an employee
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class DeleteEmployeeCommand {
    private Employee employee;

    public DeleteEmployeeCommand(Employee employee) {
        this.employee=employee;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}

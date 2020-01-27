package tollmanager.model.identity;

import tollmanager.model.identity.person.Niss;
import tollmanager.model.identity.person.Person;

import java.io.Serializable;
import java.util.Objects;
/**
 * Represente an employee
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class Employee {
    private EmployeeId employeeId;
    private Person person;

    private Employee(EmployeeId employeeId, Person person) {
        this.employeeId = employeeId;
        this.person = person;
    }

    public static Employee of(EmployeeId employeeId, Person person) {
        Objects.requireNonNull(employeeId,"The employee id is required.");
        Objects.requireNonNull(person,"The person is required.");
        
        return new Employee(employeeId, person);
    }


    public EmployeeId employeeId() {
        return employeeId;
    }
    public Niss niss() {
        return person.niss();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee other = (Employee) o;
        return Objects.equals(employeeId, other.employeeId) &&
                Objects.equals(person, other.person);
    }
    @Override
    public int hashCode() {
        return Objects.hash(employeeId, person);
    }
    @Override
    public String toString() {
        return "Employee{" +
                "employeeId='" + employeeId + '\'' +
                ", person=" + person +
                '}';
    }
    public Person person() {
        return person;
    }

    /**
     * Change the personal information about an employee
     * @param person personal information
     */
    public void changeInformation(Person person) {
        this.person=person;
    }
}

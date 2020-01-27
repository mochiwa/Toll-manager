package tollmanager.model.access;

import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.user.User;

import java.util.Objects;

/**
 * Represents a group member from user
 * @see User#toGroupMember()
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class GroupMember {
    private final EmployeeId employeeId;
    private final String name;

    private GroupMember(EmployeeId employeeId, String name) {
        this.employeeId = employeeId;
        this.name = name;
    }

    public static GroupMember of(EmployeeId employeeId, String name) {
        Objects.requireNonNull(employeeId,"The employee id is required.");
        Objects.requireNonNull(name,"The name is required.");

        if(name.trim().length()<3 || name.trim().length()>55)
            throw new IllegalArgumentException("The name's length must be between 3 and 55.");

        return new GroupMember(employeeId, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupMember)) return false;
        GroupMember member = (GroupMember) o;
        return Objects.equals(employeeId, member.employeeId) &&
                Objects.equals(name, member.name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(employeeId, name);
    }
    @Override
    public String toString() {
        return "GroupMember{" +
                "idEmployee='" + employeeId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String name() {
        return name;
    }

    public EmployeeId getEmployeeId() {
        return employeeId;
    }
}

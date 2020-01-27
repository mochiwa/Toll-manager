package tollmanager.model.identity.user;

import tollmanager.model.access.GroupMember;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.user.password.Password;

import java.util.Objects;
/**
 * Represents an user
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class User {
    private EmployeeId employeeId;
    private Login login;
    private Password password;

    private User(EmployeeId employeeId, Login login, Password password) {
        this.employeeId = employeeId;
        this.login = login;
        this.password = password;
    }

    public static User of(EmployeeId employeeId, Login login, Password password) {
        Objects.requireNonNull(employeeId,"The employee id is required.");
        Objects.requireNonNull(login,"The login is required.");
        Objects.requireNonNull(password,"The password is required.");

        return new User(employeeId, login, password);
    }

    public EmployeeId employeeId() {
        return employeeId;
    }

    public Login  login() {
        return login;
    }

    public boolean isPasswordMatch(Password password) {
        return this.password.equals(password);
    }

    /**
     * transform a user to a group member
     * @return a GroupMember from the user
     */
    public GroupMember toGroupMember() {
        String groupMemberName=login.value();
        return GroupMember.of(employeeId,groupMemberName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(employeeId, user.employeeId) &&
                Objects.equals(login, user.login) &&
                Objects.equals(password, user.password);
    }
    @Override
    public int hashCode() {
        return Objects.hash(employeeId, login, password);
    }
    @Override
    public String toString() {
        return "User{" +
                "employeeId='" + employeeId + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public void changePassword(Password newPassWord) {
        this.password=newPassWord;
    }

    public Password password() {
        return password;
    }
}


package tollmanager.model.identity.user;

import tollmanager.model.identity.EmployeeId;

public interface UserRepository {

    void add(User user);

    User findById(EmployeeId employeeId);

    User findByLogin(Login login);

    void remove(User userToDelete);

    void updateUser(User user);
}

package tollmanager.infrastructure.persistance.inMemory;

import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.user.Login;
import tollmanager.model.identity.user.User;
import tollmanager.model.identity.user.UserRepository;

import java.util.LinkedHashSet;

public class InMemoryUserRepository implements UserRepository {
    private LinkedHashSet<User> users;

    public InMemoryUserRepository() {
        this.users = new LinkedHashSet<User>();
    }

    @Override
    public void add(User user) {
        users.add(user);
    }

    @Override
    public User findById(EmployeeId employeeId) {
        return users.stream()
                .filter(u->u.employeeId().equals(employeeId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User findByLogin(Login login) {
        return users.stream()
                .filter(u->u.login().equals(login))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void remove(User user) {
        users.remove(user);
    }

    @Override
    public void updateUser(User user) {

    }
}

package tollmanager.infrastructure.persistance.postgres;

import org.junit.Before;
import org.junit.Test;
import tollmanager.TestHelper;
import tollmanager.infrastructure.persistance.DatabaseConnection;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.EmployeeRepository;
import tollmanager.model.identity.user.Login;
import tollmanager.model.identity.user.User;
import tollmanager.model.identity.user.UserRepository;
import tollmanager.model.identity.user.password.Password;

import java.sql.SQLException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PostgresUserRepositoryTest {
    UserRepository repository;
    EmployeeRepository employeeRepository;

    private Employee employeeA;
    private Employee employeeB;


    @Before
    public void setUp() throws SQLException {
        DatabaseConnection.instance().setAutoCommit(false);
        repository=new PostgresUserRepository();
        employeeRepository=new PostgresEmployeeRepository();

        employeeA= TestHelper.of().getEmployee("a","john","doe","11.11.11-111.11");
        employeeRepository.add(employeeA);

        employeeB= TestHelper.of().getEmployee("b","thusand","eric","11.11.11-111.22");
        employeeRepository.add(employeeB);
    }

    @Test
    public void add_shouldAppendAnUserAccountToAnEmployee(){
        User user = User.of(employeeA.employeeId(), Login.of("Alogin"), Password.of("aPassword"));

        repository.add(user);

        assertEquals(user,repository.findById(employeeA.employeeId()));
    }

    @Test
    public void findById_shouldReturnUserA_whenPassIdOfEmployeeA(){
        User user = User.of(employeeA.employeeId(), Login.of("Alogin"), Password.of("aPassword"));

        repository.add(user);

        assertEquals(user,repository.findById(employeeA.employeeId()));
    }

    @Test
    public void findById_shouldReturnUserA_whenPassLoginOfEmployeeA(){
        User user = User.of(employeeA.employeeId(), Login.of("Alogin"), Password.of("aPassword"));

        repository.add(user);

        assertEquals(user,repository.findByLogin(user.login()));
    }

    @Test
    public void updateUser_shouldUpdateThePassword(){
        User user = User.of(employeeA.employeeId(), Login.of("Alogin"), Password.of("aPassword"));
        repository.add(user);
        user.changePassword(Password.of("Secret123@"));
        repository.updateUser(user);

        assertTrue(user.isPasswordMatch(repository.findByLogin(user.login()).password()));
    }
}

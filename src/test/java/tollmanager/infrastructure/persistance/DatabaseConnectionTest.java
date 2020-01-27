package tollmanager.infrastructure.persistance;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import tollmanager.TestHelper;
import tollmanager.infrastructure.persistance.DatabaseConnection;
import tollmanager.infrastructure.persistance.postgres.PostgresEmployeeRepository;
import tollmanager.infrastructure.persistance.postgres.PostgresUserRepository;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.user.Login;
import tollmanager.model.identity.user.User;
import tollmanager.model.identity.user.password.Password;

import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.any;

public class DatabaseConnectionTest {

    @Test
    public void instance_shouldConnectToDbWhenCalled() throws SQLException {
        Assert.assertNotNull(DatabaseConnection.instance().connection().getClientInfo());
    }

    @Test
    public void instance_shouldAppendEmployee() throws SQLException {
        PostgresEmployeeRepository repository=new PostgresEmployeeRepository();

        repository.add(TestHelper.of().getEmployee("aaa","john","doe"));
        DatabaseConnection.instance().connection().commit();
    }

    @Test
    public void instance_shouldCreateAccountForEmployee() throws SQLException {
        PostgresEmployeeRepository repository=new PostgresEmployeeRepository();
        PostgresUserRepository userRepository=new PostgresUserRepository();

        Employee employee=TestHelper.of().getEmployee("aaa","john","doe");
        repository.add(employee);

        User user=User.of(employee.employeeId(), Login.of("admin"), Password.of("Secret123@"));
        userRepository.add(user);

        DatabaseConnection.instance().connection().commit();
    }

}

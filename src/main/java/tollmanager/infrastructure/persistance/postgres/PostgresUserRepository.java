package tollmanager.infrastructure.persistance.postgres;

import tollmanager.infrastructure.persistance.DatabaseConnection;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.user.Login;
import tollmanager.model.identity.user.User;
import tollmanager.model.identity.user.UserRepository;
import tollmanager.model.identity.user.password.Password;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Implementation of repository for user with Postgres
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class PostgresUserRepository implements UserRepository {
    private DatabaseConnection db;
    public PostgresUserRepository() throws SQLException {
        db= DatabaseConnection.instance();
    }

    /**
     * Link an user account to an employee into the database
     * @param user
     */
    @Override
    public void add(User user) {
        PreparedStatement statement= null;
        try {
            statement = db.connection().prepareStatement("CALL p_append_account_for_employee(?,?,?)");
            statement.setString(1,user.employeeId().value());
            statement.setString(2,user.login().value());
            statement.setString(3,user.password().value());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * find an user by his employee id
     * @param employeeId
     * @return User or null
     */
    @Override
    public User findById(EmployeeId employeeId) {
        User user=null;

        PreparedStatement statement= null;
        try {
            statement = db.connection().prepareStatement("SELECT * FROM find_user_by_id(?)");
            statement.setString(1,employeeId.value());
            ResultSet r=statement.executeQuery();

            if(!r.next() || r.getString("login")==null)
                return null;

            user=User.of(
                    EmployeeId.of(r.getString("employee_id")),
                    Login.of(r.getString("login")),
                    Password.of(r.getString("password"))
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Find an user by his login
     * @param login
     * @return User or null
     */
    @Override
    public User findByLogin(Login login) {
        User user=null;

        PreparedStatement statement= null;
        try {
            statement = db.connection().prepareStatement("SELECT * FROM find_user_by_login(?)");
            statement.setString(1,login.value());
            ResultSet r=statement.executeQuery();

            if(!r.next() || r.getString("login")==null)
                return null;

            user=User.of(
                    EmployeeId.of(r.getString("employee_id")),
                    Login.of(r.getString("login")),
                    Password.of(r.getString("password"))
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public void remove(User userToDelete) {
        //todo : remove user
    }

    /**
     * Update user from the database
     * @param user to update
     */
    @Override
    public void updateUser(User user) {
        PreparedStatement statement= null;
        try {
            statement = db.connection().prepareStatement("CALL p_update_password(?,?)");
            statement.setString(1,user.login().value());
            statement.setString(2,user.password().value());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

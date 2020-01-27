package tollmanager.infrastructure.persistance;

import com.googlecode.dummyjdbc.DummyJdbcDriver;
import com.googlecode.dummyjdbc.connection.impl.DummyConnection;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Represent the database manager for the whole application
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private boolean isValid;
    private String url = "jdbc:postgresql://localhost:5432/tollManagerV2";
    private String username = "postgres";
    private String password = "postgres";


    private DatabaseConnection() throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        try {
            isValid=true;
            connection= DriverManager.getConnection(url,username,password);
        } catch (SQLException e) {
            isValid=false;
            DummyJdbcDriver.addTableResource("users", new File("users.csv"));
            try {
                connection = DriverManager.getConnection("any");

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Enable or disable the autocommit
     * @param value
     */
    public void setAutoCommit(boolean value){
        try {
            connection.setAutoCommit(value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cancel the database action
     */
    public void rollback(){
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection connection() {
        return connection;
    }

    /**
     * Assure that Database connection is a singleton with management  of multi thread
     * @return
     * @throws SQLException
     */
    public static DatabaseConnection instance() throws SQLException {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null)
                    try{
                        instance = new DatabaseConnection();
                    }catch (ClassNotFoundException e){
                        System.err.println("The POSTGRES SQL DRIVER Not FOund");
                        e.printStackTrace();
                    }
            }
        }
        return instance;
    }

    /**
     * Close the database connection
     */
    public void close(){
        try {
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * get if the database connection if success or not
     * @return true if connected , false else
     */
    public boolean isValid() {
        return isValid;
    }
}

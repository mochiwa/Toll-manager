package tollmanager.infrastructure.persistance.postgres;

import tollmanager.infrastructure.persistance.DatabaseConnection;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.EmployeeRepository;
import tollmanager.model.identity.contact.*;
import tollmanager.model.identity.person.Birthday;
import tollmanager.model.identity.person.FullName;
import tollmanager.model.identity.person.Niss;
import tollmanager.model.identity.person.Person;

import java.sql.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Implementation of repository for employee with Postgres
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class PostgresEmployeeRepository implements EmployeeRepository {

    private DatabaseConnection db;

    public PostgresEmployeeRepository() throws SQLException {
        db = DatabaseConnection.instance();
    }


    /**
     * Append an employee to the database
     * @param employee to append
     */
    @Override
    public void add(Employee employee) {
        try {
            PreparedStatement statement = db.connection().prepareStatement("SELECT * FROM f_create_employee(?,?,?,?,?)");
            statement.setString(1, employee.employeeId().value());
            statement.setString(2, employee.niss().value());
            statement.setString(3, employee.person().fullName().name());
            statement.setString(4, employee.person().fullName().forename());
            statement.setDate(5, Date.valueOf(employee.person().birthday().valueToLocalDate()));
            ResultSet resultSet = statement.executeQuery();
            addContactInformation(employee);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addContactInformation(Employee employee) {
        ContactInformation contacts = employee.person().contactInformation();

        contacts.addresses().forEach(a -> {
            try {
                PreparedStatement statement = db.connection().prepareStatement("SELECT * FROM f_create_postalAddress(?,?,?,?,?)");
                statement.setString(1, a.street());
                statement.setString(2, a.number());
                statement.setString(3, a.city());
                statement.setString(4, a.zipCode());
                statement.setString(5, a.country());

                ResultSet r=statement.executeQuery();
                if(!r.next())
                    return;

                statement = db.connection().prepareStatement("CALL link_employee_address(?,?)");
                statement.setString(1, employee.employeeId().value());
                statement.setInt(2, r.getInt(1));
                statement.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        contacts.emails().forEach(e->{
            try {
                PreparedStatement statement = db.connection().prepareStatement("SELECT * FROM f_append_email(?,?)");
                statement.setString(1,employee.employeeId().value());
                statement.setString(2, e.value());
                statement.execute();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });

        contacts.phones().forEach(p->{
            try {
                PreparedStatement statement = db.connection().prepareStatement("SELECT * FROM f_append_phoneNumber(?,?,?)");
                statement.setString(1,employee.employeeId().value());
                statement.setString(2, p.value());
                statement.setString(3, "BE"); //TODO : implent recongnize type of phone
                statement.execute();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Find an employee by his id
     * @param employeeId
     * @return Employee find or null
     */
    @Override
    public Employee findById(EmployeeId employeeId) {
        Employee employee = null;
        try {
            PreparedStatement statement = db.connection().prepareStatement("SELECT * FROM find_employee_by_id(?)");
            statement.setString(1, employeeId.value());
            ResultSet r = statement.executeQuery();
            if (r.next())
                employee = Employee.of(employeeId, buildPerson(r));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employee;
    }

    private Person buildPerson(ResultSet resultSet) throws SQLException {
        EmployeeId employeeId = EmployeeId.of(resultSet.getString("employee_id"));


        return Person.of(
                Niss.of(resultSet.getString("employee_niss")),
                FullName.of(resultSet.getString("employee_name"), resultSet.getString("employee_forename")),
                Birthday.of(resultSet.getDate("employee_birthday").toLocalDate()),
                ContactInformationBuilder.of()
                        .setAddresses(findAddress(employeeId))
                        .setPhones(findPhones(employeeId))
                        .setEmails(findEmails(employeeId))
                        .create()
        );
    }

    private Set<Phone> findPhones(EmployeeId employeeId) throws SQLException {
        LinkedHashSet<Phone> phones = new LinkedHashSet<>();
        PreparedStatement statement = db.connection().prepareStatement("SELECT * FROM find_phones(?)");
        statement.setString(1, employeeId.value());
        ResultSet r = statement.executeQuery();
        while (r.next()) {
            Phone phone = Phone.of(r.getString("phone_number"));
            phones.add(phone);
        }
        return phones;
    }

    private Set<Email> findEmails(EmployeeId employeeId) throws SQLException {
        LinkedHashSet<Email> emails = new LinkedHashSet<>();
        PreparedStatement statement = db.connection().prepareStatement("SELECT * FROM find_emails(?)");
        statement.setString(1, employeeId.value());
        ResultSet r = statement.executeQuery();
        while (r.next()) {
            Email email = Email.of(r.getString("email_value"));
            emails.add(email);
        }
        return emails;
    }

    private Set<PostalAddress> findAddress(EmployeeId employeeId) throws SQLException {
        LinkedHashSet<PostalAddress> addresses = new LinkedHashSet<>();
        PreparedStatement statement = db.connection().prepareStatement("SELECT * FROM find_postal_addresses(?)");
        statement.setString(1, employeeId.value());
        ResultSet r = statement.executeQuery();
        while (r.next()) {
            PostalAddress postalAddress = PostalAddress.of(
                    r.getString("house_number"),
                    r.getString("street"),
                    r.getString("city"),
                    r.getString("zip"),
                    r.getString("country"));
            addresses.add(postalAddress);
        }
        return addresses;
    }

    /**
     * Find an employee by his niss
     * @param niss the niss linked to the employee
     * @return Employee or null
     */
    @Override
    public Employee findByNiss(Niss niss) {
        Employee employee = null;

        try {
            PreparedStatement statement = db.connection().prepareStatement("SELECT * FROM find_employee_by_niss(?)");
            statement.setString(1, niss.value());
            ResultSet r = statement.executeQuery();
            if (r.next() && r.getString("employee_id")!=null)
                employee = Employee.of(EmployeeId.of(r.getString("employee_id")), buildPerson(r));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employee;
    }

    /**
     * Provide an id from the UUID
     * @return next available EmployeeId
     */
    @Override
    public EmployeeId nextId() {
        return EmployeeId.of(UUID.randomUUID().toString());
    }

    /**
     * update data from employee in database
     * @param employee
     */
    @Override
    public void update(Employee employee) {
        Employee saved=findById(employee.employeeId());

        ContactInformation contactSaved=saved.person().contactInformation();
        contactSaved.addresses().forEach(a->{
            if(!employee.person().contactInformation().addresses().contains(a)){
                try {
                    PreparedStatement statement = db.connection().prepareStatement("Call p_dislink_employee_address(?,?,?)");
                    statement.setString(1,employee.employeeId().value());
                    statement.setString(2,a.street());
                    statement.setString(3,a.number());
                    statement.execute();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        });
        contactSaved.phones().forEach(p->{
            if(!employee.person().contactInformation().phones().contains(p)){
                try {
                    PreparedStatement statement = db.connection().prepareStatement("Call p_dislink_employee_phone(?,?)");
                    statement.setString(1,employee.employeeId().value());
                    statement.setString(2,p.value());
                    statement.execute();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        });
        contactSaved.emails().forEach(e->{
            if(!employee.person().contactInformation().emails().contains(e)){
                try {
                    PreparedStatement statement = db.connection().prepareStatement("Call p_dislink_employee_email(?)");
                    statement.setString(1,e.value());
                    statement.execute();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        });
        addContactInformation(employee);

        try {
            PreparedStatement statement = db.connection().prepareStatement("Call p_update_employee(?,?,?,?,?)");
            statement.setString(1,employee.employeeId().value());
            statement.setString(2,employee.niss().value());
            statement.setString(3,employee.person().fullName().name());
            statement.setString(4,employee.person().fullName().forename());
            statement.setDate(5,Date.valueOf(employee.person().birthday().valueToLocalDate()));
            statement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Remove the employee in database
     * @param employeeId
     */
    @Override
    public void removeEmployee(EmployeeId employeeId) {
        try {
            PreparedStatement statement = db.connection().prepareStatement("Call p_delete_employee(?)");
            statement.setString(1,employeeId.value());
            statement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Find the employee by email
     * @param email
     * @return employee or null
     */
    @Override
    public Employee findByEmail(Email email) {
        Objects.requireNonNull(email,"The email is required");
        Employee employee = null;
        try {
            PreparedStatement statement = db.connection().prepareStatement("SELECT * FROM find_employee_by_email(?)");
            statement.setString(1, email.value());
            ResultSet r = statement.executeQuery();
            if (r.next() && r.getString("employee_id")!=null)
                employee = Employee.of(EmployeeId.of(r.getString("employee_id")), buildPerson(r));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employee;
    }

    /**
     * Find the employee linked to the phone
     * @param phone
     * @return employee or null
     */
    @Override
    public Employee findByPhone(Phone phone) {
        Objects.requireNonNull(phone,"The email is required");
        Employee employee = null;
        try {
            PreparedStatement statement = db.connection().prepareStatement("SELECT * FROM find_employee_by_phone(?)");
            statement.setString(1, phone.value());
            ResultSet r = statement.executeQuery();
            if (r.next() && r.getString("employee_id")!=null)
                employee = Employee.of(EmployeeId.of(r.getString("employee_id")), buildPerson(r));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employee;
    }
}

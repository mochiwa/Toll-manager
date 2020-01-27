package tollmanager.infrastructure.persistance.postgres;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import tollmanager.TestHelper;
import tollmanager.infrastructure.persistance.DatabaseConnection;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.contact.Email;
import tollmanager.model.identity.contact.Phone;
import tollmanager.model.identity.contact.PostalAddress;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class PostgresEmployeeRepositoryTest {
    PostgresEmployeeRepository repository;

    @Before
    public void setUp() throws SQLException {
        DatabaseConnection.instance().setAutoCommit(false);
        repository=new PostgresEmployeeRepository();
    }

    @Test
    public void add_shouldAppendEmployeeToDB() throws SQLException {
        Employee employee=TestHelper.of().getEmployee("aaa","doe","john","11.11.11-111.11");
        repository.add(employee);
        //DatabaseConnection.instance().connection().commit();

        assertEquals(employee,repository.findById(employee.employeeId()));
        assertEquals(employee,repository.findByNiss(employee.niss()));
    }

    @Test
    public void update_shouldNotRemove_address_whenLocalEmployeeHadRemoved_ButDBHasNoMoreAddressForEmployee(){
        Employee employee=TestHelper.of().getEmployee("aaa","doe","john","11.11.11-111.11");
        repository.add(employee);

        assertEquals(employee,repository.findById(employee.employeeId()));

        employee.person().contactInformation().removeAddress(employee.person().contactInformation().getAddress(0));
        repository.update(employee);

        assertNotEquals(employee,repository.findById(employee.employeeId()));
        assertEquals(1,repository.findById(employee.employeeId()).person().contactInformation().addresses().size());
    }

    @Test
    public void update_shouldRemove_address_whenLocalEmployeeHadRemoved(){
        Employee employee=TestHelper.of().getEmployee("aaa","doe","john","11.11.11-111.11");
        employee.person().contactInformation().addAddress(PostalAddress.of("111","testing street","new york","44444","usa"));
        repository.add(employee);

        assertEquals(employee,repository.findById(employee.employeeId()));
        assertEquals(2,repository.findById(employee.employeeId()).person().contactInformation().addresses().size());


        employee.person().contactInformation().removeAddress(employee.person().contactInformation().getAddress(0));
        repository.update(employee);

        assertEquals(employee,repository.findById(employee.employeeId()));
        assertEquals(1,repository.findById(employee.employeeId()).person().contactInformation().addresses().size());
    }

    @Test
    public void update_shouldNotRemove_phone_whenLocalEmployeeHadRemoved_ButDBHasNoMorePhoneForEmployee(){
        Employee employee=TestHelper.of().getEmployee("aaa","doe","john","11.11.11-111.11");
        repository.add(employee);

        assertEquals(employee,repository.findById(employee.employeeId()));

        employee.person().contactInformation().removePhone(employee.person().contactInformation().getPhone(0));
        repository.update(employee);

        assertNotEquals(employee,repository.findById(employee.employeeId()));
        assertEquals(1,repository.findById(employee.employeeId()).person().contactInformation().phones().size());
    }

    @Test
    public void update_shouldRemove_phone_whenLocalEmployeeHadRemoved(){
        Employee employee=TestHelper.of().getEmployee("aaa","doe","john","11.11.11-111.11");
        employee.person().contactInformation().addPhone(Phone.of("0478.62.12.12"));
        repository.add(employee);

        assertEquals(employee,repository.findById(employee.employeeId()));
        assertEquals(2,repository.findById(employee.employeeId()).person().contactInformation().phones().size());


        employee.person().contactInformation().removePhone(employee.person().contactInformation().getPhone(0));
        repository.update(employee);

        assertEquals(employee,repository.findById(employee.employeeId()));
        assertEquals(1,repository.findById(employee.employeeId()).person().contactInformation().phones().size());
    }


    @Test
    public void update_shouldNotRemove_email_whenLocalEmployeeHadRemoved_ButDBHasNoMoreEmailForEmployee(){
        Employee employee=TestHelper.of().getEmployee("aaa","doe","john","11.11.11-111.11");
        repository.add(employee);

        assertEquals(employee,repository.findById(employee.employeeId()));

        employee.person().contactInformation().removeEmail(employee.person().contactInformation().getEmail(0));
        repository.update(employee);

        assertNotEquals(employee,repository.findById(employee.employeeId()));
        assertEquals(1,repository.findById(employee.employeeId()).person().contactInformation().emails().size());
    }

    @Test
    public void update_shouldRemove_email_whenLocalEmployeeHadRemoved(){
        Employee employee=TestHelper.of().getEmployee("aaa","doe","john","11.11.11-111.11");
        employee.person().contactInformation().addEmail(Email.of("teste@gmail.com"));
        repository.add(employee);

        assertEquals(employee,repository.findById(employee.employeeId()));
        assertEquals(2,repository.findById(employee.employeeId()).person().contactInformation().emails().size());


        employee.person().contactInformation().removeEmail(employee.person().contactInformation().getEmail(0));
        repository.update(employee);

        assertEquals(employee,repository.findById(employee.employeeId()));
        assertEquals(1,repository.findById(employee.employeeId()).person().contactInformation().emails().size());
    }

}

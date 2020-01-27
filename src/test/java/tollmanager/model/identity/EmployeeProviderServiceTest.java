package tollmanager.model.identity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import tollmanager.TestHelper;
import tollmanager.infrastructure.persistance.inMemory.InMemoryEmployeeRepository;
import tollmanager.infrastructure.persistance.inMemory.InMemoryGroupRepository;
import tollmanager.model.access.*;
import tollmanager.model.access.authorization.AuthorizationService;
import tollmanager.model.access.authorization.IAuthorizationService;
import tollmanager.model.access.authorization.IllegalRightException;
import tollmanager.model.identity.contact.ContactInformation;
import tollmanager.model.identity.contact.ContactInformationBuilder;
import tollmanager.model.identity.person.Birthday;
import tollmanager.model.identity.person.FullName;
import tollmanager.model.identity.person.Niss;
import tollmanager.model.identity.user.Login;
import tollmanager.model.identity.user.User;
import tollmanager.model.identity.user.password.Password;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

public class EmployeeProviderServiceTest {

    private TestHelper helper;
    private EmployeeRepository employeeRepository;
    private IEmployeeProviderService employeeProviderService;
    private IAuthorizationService authorizationService;


    @Before
    public void setUp() {
        helper=TestHelper.of();
        employeeRepository = Mockito.spy(new InMemoryEmployeeRepository());
        authorizationService=Mockito.mock(IAuthorizationService.class);
        employeeProviderService = new EmployeeProviderService(employeeRepository,authorizationService);

    }

    @Test
    public void registerEmployee_shouldReturnNewEmployee_whenTheUser_Is_Authorized() {
        User user=helper.getUser("01","aUser");

        Mockito.doReturn(EmployeeId.of("xxx")).when(employeeRepository).nextId();
        Mockito.doReturn(true).when(authorizationService).isAuthorizedToCreateEmployee(user.toGroupMember());

        Employee employee=employeeProviderService.registerEmployee(user,
                Niss.of("12.12.12-333.12"),
                FullName.of("anEmployee","enEmployee"),
                Birthday.of("1993/12/12"),
                TestHelper.of().getContactInformation()
        );
        assertNotNull(employee);
    }

    @Test(expected = IllegalRightException.class)
    public void registerEmployee_shouldThrow_whenTheUser_IsNot_Authorized() {
        User user=helper.getUser("01","aUser");

        Mockito.doReturn(false).when(authorizationService).isAuthorizedToCreateEmployee(user.toGroupMember());

        Employee employee=employeeProviderService.registerEmployee(user,
                Niss.of("12.12.12-333.12"),
                FullName.of("anEmployee","enEmployee"),
                Birthday.of("1993/12/12"),
                TestHelper.of().getContactInformation()
        );
    }

    @Test(expected = NissException.class)
    public void registerEmployee_shouldThrow_whenAnEmployeeWithNissAlreadyExist() {
        Employee existingEmployee=helper.getEmployee("02","anEmployee","anEmployee");
        User user=helper.getUser("01","aUser");
        Mockito.doReturn(true).when(authorizationService).isAuthorizedToCreateEmployee(user.toGroupMember());
        Mockito.doReturn(existingEmployee).when(employeeRepository).findByNiss(any());


        Employee employee=employeeProviderService.registerEmployee(user,
                Niss.of("12.12.12-333.12"),
                FullName.of("anEmployee","enEmployee"),
                Birthday.of("1993/12/12"),
                TestHelper.of().getContactInformation()
        );
    }
}
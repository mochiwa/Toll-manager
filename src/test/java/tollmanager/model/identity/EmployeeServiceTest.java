package tollmanager.model.identity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import tollmanager.TestHelper;
import tollmanager.infrastructure.persistance.inMemory.InMemoryEmployeeRepository;
import tollmanager.infrastructure.persistance.inMemory.InMemoryUserRepository;
import tollmanager.model.access.Group;
import tollmanager.model.access.GroupBuilder;
import tollmanager.model.access.GroupName;
import tollmanager.model.access.IGroupService;
import tollmanager.model.access.authorization.IAuthorizationService;
import tollmanager.model.access.authorization.IllegalRightException;
import tollmanager.model.identity.person.Niss;
import tollmanager.model.identity.person.Person;
import tollmanager.model.identity.user.User;
import tollmanager.model.identity.user.UserRepository;

import java.util.LinkedHashSet;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

public class EmployeeServiceTest{
    private TestHelper helper=TestHelper.of();
    private UserRepository userRepository;
    private IGroupService groupService;
    private EmployeeRepository employeeRepository;
    private IAuthorizationService authorizationService;

    private IEmployeeService service;


    @Before
    public void setUp() {
        employeeRepository=Mockito.spy(new InMemoryEmployeeRepository());
        userRepository= Mockito.spy(new InMemoryUserRepository());
        groupService=Mockito.mock(IGroupService.class);
        authorizationService=Mockito.mock(IAuthorizationService.class);

        service=new EmployeeService(employeeRepository,userRepository,groupService,authorizationService);
    }

    @Test
    public void getEmployeeType_shouldReturn_NullGroupName_whenTheEmployeeIsNotLinkedToAnUser() {
        Employee employee=helper.getEmployee("01","doe","john");
        GroupName type=service.getEmployeeType(employee);

        assertEquals(GroupName.Null(),type);
    }

    @Test
    public void getEmployeeType_shouldReturn_NullGroupName_whenEmployeeIsLinkedToAnUserAccountButNotGroupFound() {
        Employee employee=helper.getEmployee("01","doe","john");
        User user=helper.getUser("01","aManager");

        Mockito.doReturn(user).when(userRepository).findById(any());


        GroupName type=service.getEmployeeType(employee);
        assertEquals(GroupName.Null(),type);
    }

    @Test
    public void getEmployeeType_shouldReturn_manager_whenTheEmployeeIsAManager() {
        Employee employee=helper.getEmployee("01","doe","john");
        User user=helper.getUser("01","aManager");

        LinkedHashSet<Group> groups=new LinkedHashSet<>();
        groups.add(GroupBuilder.of().setName("admin").create());
        groups.add(GroupBuilder.of().setName("manager").create());
        groups.add(GroupBuilder.of().setName("team leader").create());



        Mockito.doReturn(user).when(userRepository).findById(any());
        Mockito.doReturn(groups).when(groupService).getAllGroupInRepository();
        Mockito.doReturn(true).when(groupService).isBelongTo(user.toGroupMember(),GroupName.of("manager"));


        GroupName type=service.getEmployeeType(employee);
        assertEquals(GroupName.of("manager"),type);
    }


    @Test
    public void findUserLinkedToEmployee_shouldReturnNull_whenEmployeeHasNotUserAccount() {
        Employee employee=helper.getEmployee("01","doe","john");

        assertNull(service.findUserLinkedToEmployee(employee));
    }
    @Test
    public void findUserLinkedToEmployee_shouldReturnTheUser_whenEmployeeHasUserAccount() {
        Employee employee=helper.getEmployee("01","doe","john");
        User user=helper.getUser("01","aLogin");
        Mockito.doReturn(user).when(userRepository).findById(any());

        assertEquals(user,service.findUserLinkedToEmployee(employee));
    }


    @Test(expected = IllegalRightException.class)
    public void updateEmployee_shouldThrow_whenCallerIsNotAuthorizedToCreateEmployee() {
        Employee employee=helper.getEmployee("01","doe","john");
        User user=helper.getUser("01","aLogin");

        Mockito.doReturn(false).when(authorizationService).isAuthorizedToCreateEmployee(any());
        service.updateEmployee(user,employee,employee.person());
    }

}

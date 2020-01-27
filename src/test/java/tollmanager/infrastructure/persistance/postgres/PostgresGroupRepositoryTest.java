package tollmanager.infrastructure.persistance.postgres;

import org.junit.Before;
import org.junit.Test;
import tollmanager.TestHelper;
import tollmanager.infrastructure.persistance.DatabaseConnection;
import tollmanager.model.access.*;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.EmployeeRepository;
import tollmanager.model.identity.user.User;

import javax.xml.crypto.Data;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class PostgresGroupRepositoryTest {
    GroupRepository repository;

    private Employee employeeA;
    private User userA;
    private Employee employeeB;
    private User userB;

    @Before
    public void setUp() throws SQLException {
        DatabaseConnection.instance().setAutoCommit(false);
        PostgresEmployeeRepository employeeRepository=new PostgresEmployeeRepository();
        PostgresUserRepository userRepository=new PostgresUserRepository();

        repository =new PostgresGroupRepository();
        employeeA=TestHelper.of().getEmployee("a","doe","john","11.11.11-111.11");
        userA=TestHelper.of().getUser(employeeA.employeeId().value(),"aLogin");
        employeeRepository.add(employeeA);
        userRepository.add(userA);

        employeeB=TestHelper.of().getEmployee("b","eric","trash","11.11.11-111.22");
        userB=TestHelper.of().getUser(employeeB.employeeId().value(),"anotherLogin");
        employeeRepository.add(employeeB);
        userRepository.add(userB);
    }

    @Test
    public void findByName_shouldReturnTheGroupWithName(){
        Group g= TestHelper.of().getGroup("aGroup");
        repository.add(g);
        Group groupInRepo=repository.findByName(g.name());

        assertEquals(g.name(),groupInRepo.name());
    }

    @Test
    public void findByName_shouldReturnTheGroupWithRole(){
        Group g= GroupBuilder.of()
                .setName("aGroup")
                .addRole(Role.createUser(GroupName.wildCard()))
                .addRole(Role.create_team())
                .create();
        repository.add(g);
        Group groupInRepo=repository.findByName(g.name());

        assertEquals(g.roles(),groupInRepo.roles());
    }

    @Test
    public void findByName_shouldReturnTheGroupWithMember() throws SQLException {
        Group g= GroupBuilder.of()
                .setName("aGroup")
                .addMember(userA.toGroupMember())
                .addMember(userB.toGroupMember())
                .create();
        repository.add(g);
        Group groupInRepo=repository.findByName(g.name());

        assertEquals(g.members(),groupInRepo.members());
    }

    @Test
    public void add_shouldAppendGroupInRepository(){
        Group g= TestHelper.of().getGroup("aGroup");
        repository.add(g);

        assertEquals(g,repository.findByName(g.name()));
    }



    @Test
    public void update_shouldRemoveDeleteMemberFromRepository() throws SQLException {
        Group g= GroupBuilder.of()
                .setName("aGroup")
                .addMember(userA.toGroupMember())
                .addMember(userB.toGroupMember())
                .create();
        repository.add(g);

        g.removeMember(userA.toGroupMember());
        repository.update(g);

        Group groupInRepo=repository.findByName(g.name());
        assertFalse(groupInRepo.hasMember(userA.toGroupMember()));
    }

    @Test
    public void update_shouldRemoveDeleteRoleFromRepository() throws SQLException {
        Group g= GroupBuilder.of()
                .setName("aGroup")
                .addRole(Role.createUser(GroupName.wildCard()))
                .addRole(Role.create_team())
                .create();

        repository.add(g);


        g.removeRole(Role.create_team());
        repository.update(g);

        Group groupInRepo=repository.findByName(g.name());
        assertFalse(groupInRepo.hasRole(Role.create_team()));
    }

    @Test
    public void groupsWhereBelong_shouldReturn_groupA_And_groupA2_whenUserIsUserA(){
        Group groupA=GroupBuilder.of().setName("groupA").addMember(userA.toGroupMember()).create();
        Group groupA2=GroupBuilder.of().setName("groupA2").addMember(userA.toGroupMember()).create();
        repository.add(groupA);
        repository.add(groupA2);

        assertTrue(repository.groupsWhereBelong(userA.toGroupMember()).contains(groupA));
        assertTrue(repository.groupsWhereBelong(userA.toGroupMember()).contains(groupA2));
        assertEquals(2,repository.groupsWhereBelong(userA.toGroupMember()).size());

    }

    @Test
    public void findAll_shouldReturn_groupA_And_groupA2(){
        Group groupA=GroupBuilder.of().setName("groupA").addMember(userA.toGroupMember()).create();
        Group groupA2=GroupBuilder.of().setName("groupA2").addMember(userA.toGroupMember()).create();
        repository.add(groupA);
        repository.add(groupA2);

        assertTrue(repository.findAll().contains(groupA));
        assertTrue(repository.findAll().contains(groupA2));
        System.err.println(repository.findAll());
        assertEquals(2,repository.findAll().size());

    }
}

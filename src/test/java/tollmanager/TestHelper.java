package tollmanager;

import tollmanager.model.access.Group;
import tollmanager.model.access.GroupBuilder;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.contact.*;
import tollmanager.model.identity.person.Birthday;
import tollmanager.model.identity.person.FullName;
import tollmanager.model.identity.person.Niss;
import tollmanager.model.identity.person.Person;
import tollmanager.model.identity.team.Team;
import tollmanager.model.identity.team.TeamBuilder;
import tollmanager.model.identity.team.TeamId;
import tollmanager.model.identity.team.TeamName;
import tollmanager.model.identity.user.Login;
import tollmanager.model.identity.user.User;
import tollmanager.model.identity.user.password.Password;
import tollmanager.model.planning.Planning;
import tollmanager.model.planning.PlanningId;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;

public class TestHelper {
    private static TestHelper helper;

    public static TestHelper of() {
        if (helper == null)
            helper = new TestHelper();
        return helper;
    }

    public User getUser(String id, String login, String password) {
        return User.of(EmployeeId.of(id), Login.of(login), Password.of(password));
    }

    public User getUser(String id, String login) {
        return getUser(id, login, "aPassword");
    }

    public Team getTeam(String id, String name, User leader) {
        return TeamBuilder.of()
                .setTeamId(TeamId.of(id))
                .setName(TeamName.of(name))
                .setLeader(leader)
                .setDescription("aDescription")
                .createTeam();
    }

    public Employee getEmployee(String id, String name, String forename) {

        return Employee.of(EmployeeId.of(id), getPerson(name, forename));
    }
    public Employee getEmployee(String id, String name, String forename,String niss) {

        return Employee.of(EmployeeId.of(id), getPerson(name, forename,niss));
    }

    public Person getPerson(String name, String forename) {
        return Person.of(
                Niss.of("11.11.11-333.11"),
                FullName.of(name, forename),
                Birthday.of("1999/12/12"),
                getContactInformation()
        );
    }
    public Person getPerson(String name, String forename,String niss) {
        return Person.of(
                Niss.of(niss),
                FullName.of(name, forename),
                Birthday.of("1999/12/12"),
                getContactInformation()
        );
    }

    public ContactInformation getContactInformation() {
        LinkedHashSet<Email> emails = new LinkedHashSet<>();
        LinkedHashSet<Phone> phones = new LinkedHashSet<>();
        LinkedHashSet<PostalAddress> addresses = new LinkedHashSet<>();

        emails.add(Email.of("fakeemail@gmail.com"));
        phones.add(Phone.of("0478.62.60.60"));
        addresses.add(PostalAddress.of("12", "street 01", "liege", "4000", "belgium"));
        return ContactInformationBuilder.of()
                .setEmails(emails)
                .setAddresses(addresses)
                .setPhones(phones)
                .create();
    }


    public Group getGroup(String name) {
        return GroupBuilder.of()
                .setName(name)
                .create();
    }

    public Planning getPlanning(String id, int hourBegin, int hourEnd, String employeeID, String teamId) {
        return Planning.of(PlanningId.of(id),
                LocalDateTime.of(2019,10,10,hourBegin,0),
                LocalDateTime.of(2019,10,10,hourEnd,0),
                "A comment",
                EmployeeId.of(employeeID),
                TeamId.of(teamId)
                );
    }
}

package tollmanager.application.command;

import tollmanager.model.identity.person.Person;
import tollmanager.model.identity.team.Team;
/**
 * Represents the use case : create a new employee
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class CreateEmployeeCommand {
    private Person person;
    private Team team;


    public CreateEmployeeCommand(Person person, Team team) {
        this.person = person;
        this.team = team;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}

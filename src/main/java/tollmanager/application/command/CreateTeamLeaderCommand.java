package tollmanager.application.command;

import tollmanager.model.identity.person.Person;
import tollmanager.model.identity.team.Team;
import tollmanager.model.identity.user.Login;
import tollmanager.model.identity.user.password.Password;
/**
 * Represents the use case : create a new team leader
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class CreateTeamLeaderCommand {
    private Person person;
    private Login login;
    private Password password;
    private Team team;

    public CreateTeamLeaderCommand(Person person, Login login, Password password, Team team) {
        this.person = person;
        this.login = login;
        this.password = password;
        this.team = team;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Login getLogin() {
        return login;
    }

    public void setLogin(Login login) {
        this.login = login;
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}

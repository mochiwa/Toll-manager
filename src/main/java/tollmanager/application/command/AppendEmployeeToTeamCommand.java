package tollmanager.application.command;

import tollmanager.model.identity.Employee;
import tollmanager.model.identity.team.Team;

/**
 * Represents the use case : append an employee to a team
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class AppendEmployeeToTeamCommand {
    private Employee employee;
    private Team team;


    public AppendEmployeeToTeamCommand(Employee employee, Team team) {
        this.employee = employee;
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}

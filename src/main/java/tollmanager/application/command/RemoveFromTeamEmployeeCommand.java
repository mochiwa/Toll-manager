package tollmanager.application.command;

import tollmanager.model.identity.Employee;
import tollmanager.model.identity.team.Team;
import tollmanager.model.identity.team.TeamName;
/**
 * Represents the use case : remove an employee from a team
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class RemoveFromTeamEmployeeCommand {
    private Team team;
    private Employee employee;


    public RemoveFromTeamEmployeeCommand(Team team, Employee employee) {
        this.team = team;
        this.employee = employee;
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

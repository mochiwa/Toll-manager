package tollmanager.model.identity.team;

import tollmanager.model.identity.Employee;
import tollmanager.model.identity.user.User;

import java.util.ArrayList;
import java.util.LinkedHashSet;
/**
 * The builder pattern for team
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class TeamBuilder {
    private TeamId teamId;
    private TeamName teamName;
    private User leader;
    private String description;
    private LinkedHashSet<Employee> employees;
    private Team parentTeam;
    private LinkedHashSet<Team> subTeams;

    public TeamBuilder() {
        description="";
        employees=new LinkedHashSet<>();
        subTeams=new LinkedHashSet<>();
        parentTeam=null;
    }

    public static TeamBuilder of() {
        return new TeamBuilder();
    }

    public TeamBuilder setTeamId(TeamId teamId) {
        this.teamId = teamId;
        return this;
    }

    public TeamBuilder setName(TeamName teamName) {
        this.teamName = teamName;
        return this;
    }

    public TeamBuilder setLeader(User leader) {
        this.leader = leader;
        return this;
    }

    public TeamBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public TeamBuilder setEmployees(LinkedHashSet<Employee> employees) {
        this.employees = employees;
        return this;
    }

    public TeamBuilder setParent(Team parentTeam) {
        this.parentTeam = parentTeam;
        return this;
    }



    public TeamBuilder setSubTeams(LinkedHashSet<Team> subTeams) {
        this.subTeams = subTeams;
        return this;
    }

    public Team createTeam() {
        return Team.of(teamId,teamName, leader, description, employees, parentTeam, subTeams);
    }
}
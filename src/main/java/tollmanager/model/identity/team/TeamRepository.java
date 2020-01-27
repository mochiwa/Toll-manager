package tollmanager.model.identity.team;

import tollmanager.model.identity.Employee;
import tollmanager.model.identity.user.User;

import java.util.LinkedHashSet;

public interface TeamRepository {
    TeamId nextId();

    void add(Team team);

    Team findById(TeamId teamId);

    Team findByName(TeamName teamName);

    LinkedHashSet<Team> findAllTeamWhereLeaderIs(User teamLeader);

    LinkedHashSet<Team> findAllRootTeam();

    void update(Team team);

    void remove(Team team);

    void appendEmployee(Team team, Employee employee);

    void removeEmployeeFromTeam(Team team, Employee employee);
}

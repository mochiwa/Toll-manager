package tollmanager.infrastructure.persistance.inMemory;

import tollmanager.model.identity.Employee;
import tollmanager.model.identity.team.Team;
import tollmanager.model.identity.team.TeamId;
import tollmanager.model.identity.team.TeamName;
import tollmanager.model.identity.team.TeamRepository;
import tollmanager.model.identity.user.User;

import java.util.LinkedHashSet;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;

public class InMemoryTeamRepository implements TeamRepository {
    LinkedHashSet<Team> teams;

    public InMemoryTeamRepository()
    {
        teams=new LinkedHashSet<>();
    }
    @Override
    public TeamId nextId() {
        return TeamId.of(UUID.randomUUID().toString());
    }

    @Override
    public void add(Team team) {
        if(team!=null)
            teams.add(team);
    }

    @Override
    public Team findById(TeamId teamId) {
        return teams.stream()
                .filter(t->t.teamId().equals(teamId))
                .findFirst().orElse(null);
    }

    @Override
    public Team findByName(TeamName teamName) {
        return teams.stream()
                .filter(t -> t.name().equals(teamName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public LinkedHashSet<Team> findAllTeamWhereLeaderIs(User teamLeader) {
        return findAllRootTeam()
                .stream()
                .filter(team -> team.leader().equals(teamLeader))
                .collect(toCollection(LinkedHashSet::new));
    }

    @Override
    public LinkedHashSet<Team> findAllRootTeam() {
        return teams
                .stream()
                .filter(Team::isRootTeam)
                .collect(toCollection(LinkedHashSet::new));
    }

    @Override
    public void update(Team team) {

    }

    @Override
    public void remove(Team team) {
        teams.remove(team);
    }

    @Override
    public void appendEmployee(Team team, Employee employee) {

    }

    @Override
    public void removeEmployeeFromTeam(Team team, Employee employee) {

    }


}

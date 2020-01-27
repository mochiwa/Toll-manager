package tollmanager.infrastructure.persistance.postgres;

import tollmanager.infrastructure.persistance.DatabaseConnection;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.EmployeeRepository;
import tollmanager.model.identity.team.*;
import tollmanager.model.identity.user.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.UUID;
/**
 * Implementation of repository for team with Postgres
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class PostgresTeamRepository implements TeamRepository {

    private DatabaseConnection db;

    public PostgresTeamRepository() throws SQLException {
        db = DatabaseConnection.instance();
    }

    /**
     * Provide a new unique id from UUID
     * @return TeamId
     */
    @Override
    public TeamId nextId() {
        return TeamId.of(UUID.randomUUID().toString());
    }

    /**
     * Append a team into the database
     * @param team
     */
    @Override
    public void add(Team team) {
        Objects.requireNonNull(team, "The team is required");
        try {
            PreparedStatement statement = db.connection().prepareStatement("SELECT * FROM f_create_team(?,?,?,?,?)");
            statement.setString(1, team.teamId().value());
            statement.setString(2, team.name().value());
            statement.setString(3, team.leader().employeeId().value());
            statement.setString(4, team.description());
            statement.setString(5, team.parent().teamId().value());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Find a team by its id
     * @param teamId
     * @return
     */
    @Override
    public Team findById(TeamId teamId) {
       Team rootTeam=findAllRootTeam().stream().filter(t->t.hasTeam(teamId)).findFirst().orElse(null);
       return rootTeam==null ? null : rootTeam.getSubTeam(teamId);
    }

    /**
     * Find a team by its name
     * @param teamName
     * @return
     */
    @Override
    public Team findByName(TeamName teamName) {
        Team rootTeam = findAllRootTeam().stream().filter(t -> t.hasTeam(teamName)).findFirst().orElse(null);
        return rootTeam==null ? null : rootTeam.getSubTeam(teamName);
    }

    /**
     * Find all team where the leader is the user from argument
     * @param teamLeader
     * @return list of team
     */
    @Override
    public LinkedHashSet<Team> findAllTeamWhereLeaderIs(User teamLeader) {
        LinkedHashSet<Team> teams = new LinkedHashSet<>();
        try {
            PreparedStatement statement = db.connection().prepareStatement("SELECT * FROM find_teamName_where_leaderIs(?)");
            statement.setString(1, teamLeader.employeeId().value());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
                teams.add(findByName(TeamName.of(resultSet.getString(1))));
        } catch (SQLException er) {
            er.printStackTrace();
        }
        return teams;
    }

    /**
     * Find all team that haven't parent
     * @return
     */
    @Override
    public LinkedHashSet<Team> findAllRootTeam() {
        LinkedHashSet<Team> rootTeams = new LinkedHashSet<>();
        try {
            PostgresUserRepository userRepository = new PostgresUserRepository();
            PreparedStatement statement = db.connection().prepareStatement("SELECT * FROM find_AllTeam_root_Id()");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                PreparedStatement teamStatement = db.connection().prepareStatement("select * from find_team_by_id(?)");
                teamStatement.setString(1, resultSet.getString(1));
                ResultSet r = teamStatement.executeQuery();
                if (r.next()) {
                    Team team = TeamBuilder.of()
                            .setTeamId(TeamId.of(r.getString("team_id")))
                            .setName(TeamName.of(r.getString("team_name")))
                            .setDescription(r.getString("team_description"))
                            .setLeader(userRepository.findById(EmployeeId.of(r.getString("employee_id"))))
                            .setEmployees(findEmployee(TeamId.of(r.getString("team_id"))))
                            .setParent(null)
                            .createTeam();
                    team.setSubTeams(findSubTeam(team));
                    rootTeams.add(team);
                }
            }
        } catch (SQLException er) {
            er.printStackTrace();
        }
        return rootTeams;
    }

    private LinkedHashSet<Team> findSubTeam(Team parent) {
        LinkedHashSet<Team> teams = new LinkedHashSet<>();
        try {
            PostgresUserRepository userRepository = new PostgresUserRepository();
            PreparedStatement statement = db.connection().prepareStatement("SELECT * FROM find_subTeamId_of_team(?)");
            statement.setString(1, parent.teamId().value());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                PreparedStatement teamStatement = db.connection().prepareStatement("select * from find_team_by_id(?)");
                teamStatement.setString(1, resultSet.getString(1));
                ResultSet r = teamStatement.executeQuery();
                if (r.next()) {
                    Team team = TeamBuilder.of()
                            .setTeamId(TeamId.of(r.getString("team_id")))
                            .setName(TeamName.of(r.getString("team_name")))
                            .setDescription(r.getString("team_description"))
                            .setLeader(userRepository.findById(EmployeeId.of(r.getString("employee_id"))))
                            .setEmployees(findEmployee(TeamId.of(r.getString("team_id"))))
                            .setParent(parent)
                            .createTeam();
                    team.setSubTeams(findSubTeam(team));
                    teams.add(team);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return teams;
    }
    private LinkedHashSet<Employee> findEmployee(TeamId id) {
        LinkedHashSet<Employee> employees = new LinkedHashSet<>();
        try {
            PreparedStatement statement = db.connection().prepareStatement("SELECT * FROM find_employeeIds_of_team(?)");
            statement.setString(1, id.value());
            ResultSet resultSet = statement.executeQuery();
            EmployeeRepository employeeRepository = new PostgresEmployeeRepository();
            while (resultSet.next())
                employees.add(employeeRepository.findById(EmployeeId.of(resultSet.getString("employee_id"))));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    /**
     * Update the team in database
     * @param team
     */
    @Override
    public void update(Team team) {
        Objects.requireNonNull(team, "The team is required");

        try {
            PreparedStatement statement = db.connection().prepareStatement("CALL p_update_team(?,?,?)");
            statement.setString(1, team.teamId().value());
            statement.setString(2, team.description());
            statement.setString(3, team.leader().employeeId().value());
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    /**
     * Remove the team in database
     * @param team
     */
    @Override
    public void remove(Team team) {
        try {
            PreparedStatement statement = db.connection().prepareStatement("CALL p_remove_team(?)");
            statement.setString(1, team.teamId().value());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Append an employee to the team
     * @param team
     * @param employee
     */
    @Override
    public void appendEmployee(Team team, Employee employee) {
        try {
            PreparedStatement statement = db.connection().prepareStatement("CALL p_append_employee_to_team(?,?)");
            statement.setString(1, team.teamId().value());
            statement.setString(2, employee.employeeId().value());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove an employee from the team
     * @param team
     * @param employee
     */
    @Override
    public void removeEmployeeFromTeam(Team team, Employee employee) {
        try {
            PreparedStatement statement = db.connection().prepareStatement("CALL p_remove_employee_to_team(?,?)");
            statement.setString(1, team.teamId().value());
            statement.setString(2, employee.employeeId().value());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
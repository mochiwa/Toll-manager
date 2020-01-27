package tollmanager.infrastructure.persistance.postgres;

import org.junit.Before;
import org.junit.Test;
import tollmanager.TestHelper;
import tollmanager.infrastructure.persistance.DatabaseConnection;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.team.*;
import tollmanager.model.identity.user.User;

import javax.xml.crypto.Data;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PostgresTeamRepositoryTest {
    TeamRepository repository;

    private Employee employeeA;
    private User userA;
    private Employee employeeB;
    private User userB;

    @Before
    public void setUp() throws SQLException {
        DatabaseConnection.instance().connection().setAutoCommit(false);

        PostgresEmployeeRepository employeeRepository=new PostgresEmployeeRepository();
        PostgresUserRepository userRepository=new PostgresUserRepository();

        repository =new PostgresTeamRepository();
        employeeA= TestHelper.of().getEmployee("a","doe","john","11.11.11-111.11");
        userA=TestHelper.of().getUser(employeeA.employeeId().value(),"aLogin");
        employeeRepository.add(employeeA);
        userRepository.add(userA);

        employeeB=TestHelper.of().getEmployee("b","eric","trash","11.11.11-111.22");
        userB=TestHelper.of().getUser(employeeB.employeeId().value(),"anotherLogin");
        employeeRepository.add(employeeB);
        userRepository.add(userB);
    }


    @Test
    public void findByName_shouldReturnTheTeamWithName(){
        Team team= TeamBuilder.of().setName(TeamName.of("aTeam")).setDescription("a team").setLeader(userA).setTeamId(TeamId.of("a")).createTeam();

        repository.add(team);
        assertEquals(team.name(),repository.findByName(team.name()).name());
    }
    @Test
    public void findByName_shouldLinkParentToASubTeamSearched(){
        Team parent= TeamBuilder.of().setName(TeamName.of("aTeam")).setDescription("a team").setLeader(userA).setTeamId(TeamId.of("a")).createTeam();
        Team sub= parent.appendSubTeam(TeamId.of("aa"),TeamName.of("subTeam"));

        repository.add(parent);
        assertEquals(parent.name(),repository.findByName(parent.name()).name());
        assertEquals(sub.name(),repository.findByName(sub.name()).name());

        assertEquals(parent,repository.findByName(sub.name()).parent());
    }

    @Test
    public void add_shouldAppendTeamInRepository(){
        Team team= TeamBuilder.of().setName(TeamName.of("aTeam")).setDescription("a team").setLeader(userA).setTeamId(TeamId.of("a")).createTeam();

        repository.add(team);

        assertEquals(team,repository.findByName(team.name()));
      //  assertEquals(team,repository.findById(team.teamId()));
    }
    @Test
    public void add_shouldMakeTheLinkForEmployeeInRepository(){
        Team team= TeamBuilder.of().setName(TeamName.of("aTeam")).setDescription("a team").setLeader(userA).setTeamId(TeamId.of("a")).createTeam();
        team.appendEmployee(employeeA);
        repository.add(team);

        assertEquals(team.employees(),repository.findByName(team.name()).employees());
    }
    @Test
    public void add_shouldAppendSubTeamInRepository(){
        Team team= TeamBuilder.of().setName(TeamName.of("aTeam")).setDescription("a team").setLeader(userA).setTeamId(TeamId.of("a")).createTeam();
        Team sub=team.appendSubTeam(TeamId.of("aa"),TeamName.of("aSubTeam"));
        repository.add(team);

        assertEquals(team.subTeams(),repository.findByName(team.name()).subTeams());
    }

    @Test
    public void update_shouldAppendNewEmployeeFromRepository() {
        Team team= TeamBuilder.of().setName(TeamName.of("aTeam")).setDescription("a team").setLeader(userA).setTeamId(TeamId.of("a")).createTeam();
        repository.add(team);
        team.appendEmployee(employeeA);
        repository.update(team);

        assertEquals(team.employees().size(),repository.findByName(team.name()).employees().size());
    }
    @Test
    public void update_shouldRemoveDeleteEmployeeFromRepository(){
        Team team= TeamBuilder.of().setName(TeamName.of("aTeam")).setDescription("a team").setLeader(userA).setTeamId(TeamId.of("a")).createTeam();
        team.appendEmployee(employeeA);
        repository.add(team);
        team.removeEmployee(employeeA);
        repository.update(team);

        assertEquals(team.employees().size(),repository.findByName(team.name()).employees().size());
    }
/*
    @Test
    public void update_shouldAppendNewSubTeamFromRepository() {
        Team team= TeamBuilder.of().setName(TeamName.of("aTeam")).setDescription("a team").setLeader(userA).setTeamId(TeamId.of("a")).createTeam();
        repository.add(team);
        team.appendSubTeam(TeamId.of("ab"),TeamName.of("subTeam"));
        repository.update(team);

        assertEquals(team.subTeams().size(),repository.findByName(team.name()).subTeams().size());

    }
    @Test
    public void update_shouldRemoveDeleteSubteamFromRepository(){
        Team team= TeamBuilder.of().setName(TeamName.of("aTeam")).setDescription("a team").setLeader(userA).setTeamId(TeamId.of("a")).createTeam();
        Team subTeam=team.appendSubTeam(TeamId.of("ab"),TeamName.of("subTeam"));
        repository.add(team);
        assertEquals(1,repository.findByName(team.name()).subTeams().size());

        team.removeTeam(subTeam);
        repository.update(team);

        assertEquals(team.subTeams().size(),repository.findByName(team.name()).subTeams().size());
    }

    @Test
    public void findAllTeamWhereLeaderIs_shouldReturnTeamA_and_teamAA_whenUserIserUserA() {
        Team teamA= TeamBuilder.of().setName(TeamName.of("aTeam")).setDescription("a team").setLeader(userA).setTeamId(TeamId.of("a")).createTeam();
        Team teamAA= TeamBuilder.of().setName(TeamName.of("aTeama")).setDescription("a team").setLeader(userA).setTeamId(TeamId.of("aa")).createTeam();
        repository.add(teamA);
        repository.add(teamAA);

        assertEquals(2,repository.findAllTeamWhereLeaderIs(userA).size());

    }

    @Test
    public void findAllRootTeam_shouldReturnTeamA_and_teamB() {
        Team teamA= TeamBuilder.of().setName(TeamName.of("aTeam")).setDescription("a team").setLeader(userA).setTeamId(TeamId.of("a")).createTeam();
        Team teamB= TeamBuilder.of().setName(TeamName.of("aTeama")).setDescription("a team").setLeader(userB).setTeamId(TeamId.of("aa")).createTeam();
        Team sub=teamB.appendSubTeam(TeamId.of("xxx"),TeamName.of("xxsub"));

        repository.add(teamA);
        repository.add(teamB);


        assertEquals(2,repository.findAllRootTeam().size());
    }
*/
    @Test
    public void findAllRootTeam_shouldReturnTeamA_and_teamB_andTeamBShouldHaveOneSubTeam() {
        Team teamA= TeamBuilder.of().setName(TeamName.of("aTeam")).setDescription("a team").setLeader(userA).setTeamId(TeamId.of("a")).createTeam();
        Team teamB= TeamBuilder.of().setName(TeamName.of("aTeama")).setDescription("a team").setLeader(userB).setTeamId(TeamId.of("aa")).createTeam();
        Team sub=teamB.appendSubTeam(TeamId.of("xxx"),TeamName.of("xxsub"));

        repository.add(teamA);
        repository.add(teamB);


        assertEquals(2,repository.findAllRootTeam().size());
        repository.findAllRootTeam().forEach(t->{
            if(t.name().equals(teamB.name()))
                assertEquals(1,t.subTeams().size());
        });
    }







}

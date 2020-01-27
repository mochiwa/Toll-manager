package features.identity;

import features.helper.KnowTheDomain;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.team.*;
import tollmanager.model.identity.user.Login;
import tollmanager.model.identity.user.User;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class TeamManagementSteps {
    private KnowTheDomain helper;
    private TeamRepository teamRepository;
    private TeamService service;

    public TeamManagementSteps(KnowTheDomain toInject) {
        helper=toInject;
        teamRepository=helper.teamRepository();
        service=new TeamService(teamRepository, helper.employeeRepository(), helper.authorizationService());
    }

    @Given("I have these teams")
    public void i_have_these_teams(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String,String>> data=dataTable.asMaps(String.class,String.class);
        data.forEach(m->{
            User teamLeader= helper.userRepository().findById(EmployeeId.of(m.get("teamLeaderId")));
            LinkedHashSet<Employee> membersOfTeam=new LinkedHashSet<>();
            String[] membersId=m.get("employeesId").split(",");
            for(String memberId : membersId) {
                if(!memberId.trim().isEmpty())
                    membersOfTeam.add(helper.employeeRepository().findById(EmployeeId.of(memberId.trim())));

            }
            Team rootTeam;
            if(m.get("parentTeam").isEmpty())
            {
               rootTeam=TeamBuilder.of()
                        .setTeamId(TeamId.of(m.get("teamId")))
                        .setName(TeamName.of(m.get("teamName")))
                        .setLeader(teamLeader)
                        .setDescription(m.get("description"))
                        .setEmployees(membersOfTeam)
                        .createTeam();
            }
            else
            {
                Team team=teamRepository.findByName(TeamName.of(m.get("parentTeam")));
                rootTeam=team.appendSubTeam(TeamId.of(m.get("teamId")),TeamName.of(m.get("teamName")));
                membersOfTeam.forEach(e->rootTeam.appendEmployee(e));
            }
            teamRepository.add(rootTeam);
        });
    }

    @When("I create a team of work named {teamName} for {login}")
    public void i_create_a_team_of_work_named_for(TeamName teamName, Login login) {
        User teamLeader=helper.userRepository().findByLogin(login);
        try {
            Team teamOfWork = service.provideTeam(helper.connectedUser(), teamName, teamLeader,"");
            assertNotNull(teamOfWork);
        }catch (Exception e) {
            helper.setErrorMessage(e.getMessage());
        }

    }

    @When("I append the employee {id} to team {teamName}")
    public void i_append_the_employee_to_team(EmployeeId employeeId, TeamName teamName) {
        try {
            Employee employee = helper.employeeRepository().findById(employeeId);
            Team team=teamRepository.findByName(teamName);
            service.appendEmployee(helper.connectedUser(), team, employee);
        }catch (Exception e)
        {
            helper.setErrorMessage(e.getMessage());
        }
    }

    @When("I remove the employee {id} to team {teamName}")
    public void i_remove_the_employee_to_team(EmployeeId employeeId, TeamName teamName) {
        Employee employee = helper.employeeRepository().findById(employeeId);
        Team t=teamRepository.findByName(teamName);
        try {
            service.removeEmployee(helper.connectedUser(), t, employee);

        }catch (Exception e)
        {
            helper.setErrorMessage(e.getMessage());
        }
    }

    @Then("the user {login} is the team leader of {teamName}")
    public void the_user_is_the_team_leader_of(Login login, TeamName teamName) {
        User user=helper.userRepository().findByLogin(login);
        Team team=teamRepository.findByName(teamName);
        Assert.assertTrue(team.hasTeamLeader(user));
    }

    @Then("the team {teamName} should have these employees {ids}")
    public void the_team_should_have_these_employees(TeamName teamName, LinkedHashSet<EmployeeId> ids) {
        Team team=teamRepository.findByName(teamName);
        ids.forEach( id -> {
            Employee employee=helper.employeeRepository().findById(id);
            Assert.assertTrue(team.hasEmployee(employee));
        });
        if(team!=null)
            Assert.assertEquals(ids.size(),team.employeeCount());
    }

    @When("I create a sub team named {teamName} for the team {teamName}")
    public void i_create_a_sub_team_named_for_the_team(TeamName subTeamName, TeamName teamName) {
        try {
            Team team=teamRepository.findByName(teamName);
            Team subTeam=service.createSubTeam(helper.connectedUser(),team,subTeamName);
            Assert.assertNotNull(subTeam);
        }catch (Exception e)
        {
            helper.setErrorMessage(e.getMessage());
        }
    }

    @Then("the team {teamName} has sub team {teamName}")
    public void the_team_has_sub_team(TeamName rootTeamName, TeamName subTeamName) {
        Team rootTeam=teamRepository.findByName(rootTeamName);
        Assert.assertTrue(rootTeam.hasTeam(subTeamName));
    }
}

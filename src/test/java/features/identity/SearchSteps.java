package features.identity;

import features.helper.KnowTheDomain;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.person.Niss;
import tollmanager.model.identity.team.Team;
import tollmanager.model.identity.team.TeamId;
import tollmanager.model.identity.team.TeamName;
import tollmanager.model.identity.team.search.SearchByForename;
import tollmanager.model.identity.team.search.SearchByIdentityInformation;
import tollmanager.model.identity.team.search.SearchByName;
import tollmanager.model.identity.team.search.SearchByNiss;
import tollmanager.model.shared.ISearch;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SearchSteps {
    private String userInput="";
    private LinkedHashSet<Employee> employeesFound;
    KnowTheDomain helper;

    public SearchSteps(KnowTheDomain toInject) {
        helper=toInject;
    }

    @Given("I input {string} in field")
    public void i_input_in_field(String input) {
        userInput=input;
    }

    @When("I valid the search by name for team {teamName}")
    public void i_valid_the_search_by_name_for_team(TeamName teamName) {
        employeesFound=new LinkedHashSet<>();
        Team team=helper.teamRepository().findByName(teamName);
        ISearch<Employee> search= new SearchByName();
        employeesFound.addAll(search.search(team.employees(),userInput));
    }
    @When("I valid the search by forename for team {teamName}")
    public void i_valid_the_search_by_forename_for_team(TeamName teamName) {
        employeesFound=new LinkedHashSet<>();
        Team team=helper.teamRepository().findByName(teamName);
        ISearch<Employee> search=new SearchByForename();

        employeesFound.addAll(search.search(team.employees(),userInput));
    }

    @When("I valid the search by niss for team {teamName}")
    public void i_valid_the_search_by_niss_for_team(TeamName teamName) {
        employeesFound=new LinkedHashSet<>();
        Team team=helper.teamRepository().findByName(teamName);
        ISearch<Employee> search=new SearchByNiss();

        employeesFound.addAll(search.search(team.employees(),userInput));
    }

    @When("I valid the search identities information  for team {teamName}")
    public void i_valid_the_search_identities_information_for_team(TeamName teamName) {
        employeesFound=new LinkedHashSet<>();
        Team team=helper.teamRepository().findByName(teamName);
        ISearch<Employee> search=new SearchByIdentityInformation();

        employeesFound.addAll(search.search(team.employees(),userInput));
    }


    @Then("I should have these employees {ids}")
    public void i_should_have_these_employees(LinkedHashSet<EmployeeId> listId) {
        LinkedHashSet<Employee> employees=new LinkedHashSet<>();
        listId.forEach(id->employees.add(helper.employeeRepository().findById(id)));

        assertEquals(employees.size(),employeesFound.size());
        employees.forEach(employee -> assertTrue(employeesFound.contains(employee)) );
    }
}

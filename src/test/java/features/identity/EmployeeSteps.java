package features.identity;


import features.helper.KnowTheDomain;
import io.cucumber.java.en.When;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.EmployeeProviderService;
import tollmanager.model.identity.EmployeeRepository;
import tollmanager.model.identity.IEmployeeProviderService;
import tollmanager.model.identity.contact.*;
import tollmanager.model.identity.person.Birthday;
import tollmanager.model.identity.person.FullName;
import tollmanager.model.identity.person.Niss;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.junit.Assert;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class EmployeeSteps {
    private KnowTheDomain helper;
    private IEmployeeProviderService employeeProviderService;

    public EmployeeSteps(KnowTheDomain toInject){
        helper=toInject;
        employeeProviderService=helper.employeeProviderService();
    }

    @Given("I have these employees")
    public void i_have_these_employees(List<Employee> employees) {
        employees.forEach(e ->{helper.employeeRepository().add(e);});
    }

    @When("I fill the employee form with these requirements:")
    public void i_fill_the_employee_form_with_these_requirements(io.cucumber.datatable.DataTable requirements) {
        try {
            List<Map<String, String>> requirementList = requirements.asMaps(String.class, String.class);
            requirementList.forEach(map -> {
                Employee employee = employeeProviderService.registerEmployee(
                    helper.connectedUser(),
                    Niss.of(map.get("niss")),
                    FullName.of(map.get("name"), map.get("forename")),
                    Birthday.of(map.get("birthday")),
                    buildContactInformation(map.get("address"), map.get("mobile"), map.get("email"))
                );
                assertNotNull(helper.employeeRepository().findById(employee.employeeId()));
            });
        }catch (Exception e)
        {
            helper.setErrorMessage(e.getMessage());
        }
    }
    private ContactInformation buildContactInformation(String addressLine,String phoneLine,String emailLine) {
        String[] addressTab=addressLine.split(",");

        LinkedHashSet<Email> emails = new LinkedHashSet<>();
        LinkedHashSet<Phone> phones = new LinkedHashSet<>();
        LinkedHashSet<PostalAddress> addresses = new LinkedHashSet<>();

        emails.add(Email.of(emailLine));
        phones.add(Phone.of(phoneLine));
        addresses.add(PostalAddress.of(addressTab[0], addressTab[1], addressTab[2], addressTab[3], addressTab[4]));
        return ContactInformationBuilder.of()
                .setEmails(emails)
                .setAddresses(addresses)
                .setPhones(phones)
                .create();
    }

    @Then("the employee repository should contain employee {niss}")
    public void the_employee_repository_should_contain_employee(Niss niss) {
        if(niss==null)
            return;
        Employee employee=helper.employeeRepository().findByNiss(niss);
        assertNotNull(employee);
    }
}

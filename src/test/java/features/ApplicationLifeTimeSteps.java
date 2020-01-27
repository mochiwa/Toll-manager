package features;

import static java.lang.Thread.sleep;

public class ApplicationLifeTimeSteps {
  /*  private ApplicationLifeTime applicationLifeTime;
    private KnowTheDomain helper;


    public ApplicationLifeTimeSteps(KnowTheDomain toInject)
    {
        helper=toInject;
        applicationLifeTime=new ApplicationLifeTime(helper.groupRepository(),helper.employeeRepository(),helper.userRepository(),helper.teamRepository());
    }

    @Given("I launch the application for the first time")
    public void i_launch_the_application_for_the_first_time() {

    }

    @When("I fill the admin form with these requirements:")
    public void i_fill_the_admin_form_with_these_requirements(io.cucumber.datatable.DataTable requirements) {
        List<Map<String, String>> requirementList = requirements.asMaps(String.class, String.class);
        Person person=Person.of(
                    Niss.of(requirementList.get(0).get("niss")),
                    FullName.of(requirementList.get(0).get("name"),requirementList.get(0).get("forename")),
                    Birthday.of(requirementList.get(0).get("birthday")),
                    buildContactInformation(requirementList.get(0).get("address"),requirementList.get(0).get("mobile"),requirementList.get(0).get("email"))
            );
        applicationLifeTime.initApplicationForFirstLaunch(person);
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

    @Then("I should have these user {login}")
    public void i_should_have_these_user(Login login) {
        Assert.assertNotNull(helper.userRepository().findByLogin(login));
    }

    @Then("the application is initialized for the user {login}")
    public void the_application_is_initialized_for_the_user(Login login) {
        applicationLifeTime.initApplication(helper.connectedUser());
    }

    @Then("the user connected should be {login}")
    public void the_user_connected_should_be(Login login) {
        if(login==null)
            assertNull(applicationLifeTime.connectedUser());
        else
            assertEquals(login,applicationLifeTime.connectedUser().login());
    }

    @When("I'm logout")
    public void i_m_logout() {
        applicationLifeTime.logout();
    }*/
}

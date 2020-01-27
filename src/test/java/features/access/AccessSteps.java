package features.access;

import features.helper.KnowTheDomain;

import io.cucumber.java.en.Then;

import static org.junit.Assert.assertEquals;


public class AccessSteps {
    private KnowTheDomain helper;

    public AccessSteps(KnowTheDomain toInject)
    {
        helper=toInject;
    }


    @Then("the error message should be {string}")
    public void the_error_message_should_be(String errorMessageExpected) {
        assertEquals(errorMessageExpected,helper.errorMessage());
    }

}

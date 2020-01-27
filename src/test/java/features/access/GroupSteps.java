package features.access;

import features.helper.KnowTheDomain;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import tollmanager.model.access.Group;
import tollmanager.model.access.GroupMember;
import tollmanager.model.access.GroupName;

import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GroupSteps {
    private KnowTheDomain helper;

    public GroupSteps(KnowTheDomain toInject)
    {
        helper=toInject;
    }

    @Given("I have these groups")
    public void i_have_these_groups(List<Group> groups) {
        groups.forEach(group->helper.groupRepository().add(group));
        groups.forEach(group-> assertEquals(group,helper.groupRepository().findByName(group.name())));
    }

    @When("{groupMember} wants to append {groupMember} to {groupName}")
    public void wants_to_append_to(GroupMember caller, GroupMember target, GroupName groupNameWhereAppend){
        try {
            helper.groupService().appendMemberToGroup(caller, groupNameWhereAppend, target);
        }catch (Throwable exception)
        {
            helper.setErrorMessage(exception.getMessage());
        }
    }

    @When("{groupMember} wants to remove {groupMember} to {groupName}")
    public void wants_to_remove_to(GroupMember caller, GroupMember target, GroupName groupNameWhereRemove) {
        try {
            helper.groupService().removeMemberFromGroup(caller, groupNameWhereRemove, target);
        }catch (Throwable exception)
        {
            helper.setErrorMessage(exception.getMessage());
        }
    }

    @Then("the {groupName} should have these members: {groupMembers}")
    public void the_should_have_these_members(GroupName groupName, LinkedHashSet<GroupMember> members){
        Group group=helper.groupRepository().findByName(groupName);
        if(group!=null){
            members.forEach(m -> { if (m != null) assertTrue(group.hasMember(m)); });
            assertEquals(members.size(),group.membersCount());
        }
    }

    @Then("I should have these groups")
    public void i_should_have_these_groups(List<Group> groups) {
        groups.forEach(g->
        {
            Group group=helper.groupRepository().findByName(g.name());
            Assert.assertNotNull(group);
        });

    }

}

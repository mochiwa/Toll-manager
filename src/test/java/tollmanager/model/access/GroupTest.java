package tollmanager.model.access;


import org.junit.Assert;
import org.junit.Before;
import tollmanager.model.identity.EmployeeId;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GroupTest {
    private Group aGroup;
    private GroupMember aGroupMember;

    @Before
    public void setUp() {
        aGroup=GroupBuilder.of().setName("aGroup").create();
        aGroupMember=GroupMember.of(EmployeeId.of("aaa"),"john");
    }

    @Test
    public void appendMember_shouldAppendTheMemberToTheGroup() {
        aGroup.appendMember(aGroupMember);

        assertTrue(aGroup.hasMember(aGroupMember));
    }
    @Test
    public void appendMember_shouldNotAppendTheMemberToTheGroupIfAlreadyPresent() {
        aGroup.appendMember(aGroupMember);
        aGroup.appendMember(aGroupMember);
        assertEquals(1,aGroup.membersCount());
    }

    @Test
    public void appendRole_shouldAppendTheRoleToTheGroup()
    {
        aGroup.appendRole(Role.nullRole());

        assertTrue(aGroup.hasRole(Role.nullRole()));
    }


    @Test
    public void removeMember_shouldRemoveTheMember()
    {
        aGroup.appendMember(aGroupMember);

        aGroup.removeMember(aGroupMember);
        assertFalse(aGroup.hasMember(aGroupMember));
    }

    @Test
    public void memberCount_shouldReturnCountOfMember() {
        aGroup=GroupBuilder.of().setName("aGroup").create();
        aGroupMember=GroupMember.of(EmployeeId.of("aaa"),"john");
        aGroup.appendMember(aGroupMember);
        Assert.assertEquals(1,aGroup.membersCount());
    }


}

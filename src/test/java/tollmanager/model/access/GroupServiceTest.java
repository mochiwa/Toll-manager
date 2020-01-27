package tollmanager.model.access;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import tollmanager.infrastructure.persistance.inMemory.InMemoryGroupRepository;
import tollmanager.model.access.authorization.IAuthorizationService;
import tollmanager.model.access.authorization.IllegalRightException;
import tollmanager.model.identity.EmployeeId;

import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

public class GroupServiceTest {
    private GroupRepository groupRepository;
    private IGroupService service;
    private IAuthorizationService authorizationService;


    @Before
    public void setUp() {
        groupRepository = Mockito.spy( new InMemoryGroupRepository());
        authorizationService= Mockito.mock(IAuthorizationService.class);
        service=new GroupService(groupRepository,authorizationService);
    }

    @Test
    public void appendMemberToGroup_shouldAppendMember_whenTheCaller_Is_Authorized() {
        GroupMember aMember=GroupMember.of(EmployeeId.of("01"),"aMember");
        GroupMember toAppend=GroupMember.of(EmployeeId.of("02"),"toAppend");
        Group group=GroupBuilder.of().setName("admin").create();

        Mockito.doReturn(group).when(groupRepository).findByName(any(GroupName.class));
        Mockito.doReturn(true).when(authorizationService).isAuthorizedToAppendMember(aMember,group.name());

        service.appendMemberToGroup(aMember,group.name(),toAppend);

        assertTrue(group.hasMember(toAppend));
    }

    @Test(expected = IllegalRightException.class)
    public void appendMemberToGroup_shouldThrow_whenTheCaller_IsNot_Authorized() {
        GroupMember aMember=GroupMember.of(EmployeeId.of("01"),"aMember");
        GroupMember toAppend=GroupMember.of(EmployeeId.of("02"),"toAppend");
        Group group=GroupBuilder.of().setName("admin").create();

        Mockito.doReturn(group).when(groupRepository).findByName(any(GroupName.class));
        Mockito.doReturn(false).when(authorizationService).isAuthorizedToAppendMember(aMember,group.name());

        service.appendMemberToGroup(aMember,group.name(),toAppend);
    }

    @Test(expected = NullPointerException.class)
    public void appendMemberToGroup_shouldThrow_whenTheGroupNotFound() {
        GroupMember aMember=GroupMember.of(EmployeeId.of("01"),"aMember");
        GroupMember toAppend=GroupMember.of(EmployeeId.of("02"),"toAppend");
        Group group=GroupBuilder.of().setName("admin").create();

        Mockito.doReturn(null).when(groupRepository).findByName(any(GroupName.class));
        Mockito.doReturn(false).when(authorizationService).isAuthorizedToAppendMember(aMember,group.name());

        service.appendMemberToGroup(aMember,GroupName.Null(),toAppend);
    }


    @Test(expected = GroupRuntimeException.class)
    public void appendMemberToGroup_shouldThrow_whenMemberIsAlreadyPresentInGroup() {
        GroupMember aMember=GroupMember.of(EmployeeId.of("01"),"aMember");
        Group group=GroupBuilder.of().setName("admin").create();
        group.appendMember(aMember);

        Mockito.doReturn(group).when(groupRepository).findByName(any(GroupName.class));
        Mockito.doReturn(true).when(authorizationService).isAuthorizedToAppendMember(aMember,group.name());

        service.appendMemberToGroup(aMember,group.name(),aMember);
    }

    @Test
    public void removeMemberFromGroup_shouldRemoveMember_whenTheCaller_Is_AuthorizedTo() {
        GroupMember aMember=GroupMember.of(EmployeeId.of("01"),"aMember");
        GroupMember toRemove=GroupMember.of(EmployeeId.of("02"),"toAppend");
        Group group=GroupBuilder.of().setName("admin").create();
        group.appendMember(toRemove);

        Mockito.doReturn(group).when(groupRepository).findByName(any(GroupName.class));
        Mockito.doReturn(true).when(authorizationService).isAuthorizedToRemoveMember(aMember,group.name());


        service.removeMemberFromGroup(aMember,group.name(),toRemove);
        assertFalse(group.hasMember(toRemove));
    }

    @Test(expected = IllegalRightException.class)
    public void removeMemberFromGroup_shouldThrow_whenTheCaller_IsNot_AuthorizedTo() {
        GroupMember aMember=GroupMember.of(EmployeeId.of("01"),"aMember");
        GroupMember toRemove=GroupMember.of(EmployeeId.of("02"),"toAppend");
        Group group=GroupBuilder.of().setName("admin").create();
        group.appendMember(toRemove);

        Mockito.doReturn(group).when(groupRepository).findByName(any(GroupName.class));
        Mockito.doReturn(false).when(authorizationService).isAuthorizedToRemoveMember(aMember,group.name());


        service.removeMemberFromGroup(aMember,group.name(),toRemove);
    }
    @Test(expected = NullPointerException.class)
    public void removeMemberFromGroup_shouldThrow_whenTheGroupNotFound() {
        GroupMember aMember=GroupMember.of(EmployeeId.of("01"),"aMember");
        GroupMember toRemove=GroupMember.of(EmployeeId.of("02"),"toAppend");
        Group group=GroupBuilder.of().setName("admin").create();
        group.appendMember(toRemove);

        Mockito.doReturn(null).when(groupRepository).findByName(any(GroupName.class));
        Mockito.doReturn(true).when(authorizationService).isAuthorizedToRemoveMember(aMember,group.name());

        service.removeMemberFromGroup(aMember,group.name(),toRemove);
    }

    @Test
    public void removeMemberFromAllGroups_shouldRemoveMemberFromEveryGroupsWhereHeBelong() {
        GroupMember aMember=GroupMember.of(EmployeeId.of("01"),"aMember");
        GroupMember toRemove=GroupMember.of(EmployeeId.of("02"),"toAppend");
        Group group=GroupBuilder.of().setName("admin").create();
        Group subGroup=GroupBuilder.of().setName("subGroup").create();
        group.appendMember(toRemove);
        subGroup.appendMember(toRemove);

        Mockito.doReturn(true).when(authorizationService).isAuthorizedToRemoveMember(aMember,group.name());

        service.removeMemberFromAllGroups(aMember,toRemove);

        assertTrue(groupRepository.groupsWhereBelong(toRemove).isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void isBelongTo_shouldThrow_whenGroupNotExist() {
        GroupMember aMember=GroupMember.of(EmployeeId.of("01"),"aMember");
        Group group=GroupBuilder.of().setName("admin").create();
        group.appendMember(aMember);

        service.isBelongTo(aMember,GroupName.of("aNotExistGroup"));
    }
    @Test
    public void isBelongTo_shouldReturnTrue_WhenMemberBelongToTheGroup() {
        GroupMember aMember=GroupMember.of(EmployeeId.of("01"),"aMember");
        Group group=GroupBuilder.of().setName("admin").create();
        group.appendMember(aMember);

        Mockito.doReturn(group).when(groupRepository).findByName(any(GroupName.class));

        assertTrue(service.isBelongTo(aMember,group.name()));
    }
    @Test
    public void isBelongTo_shouldReturnFalse_WhenMemberNotBelongToTheGroup() {
        GroupMember aMember=GroupMember.of(EmployeeId.of("01"),"aMember");
        Group group=GroupBuilder.of().setName("admin").create();

        Mockito.doReturn(group).when(groupRepository).findByName(any(GroupName.class));

        assertFalse(service.isBelongTo(aMember,group.name()));
    }

    @Test
    public void getAllGroupInRepository_shouldNotReturnTheGroupAdmin() {
        Group adminGroup=GroupBuilder.of().setName("admin").create();
        Group aGroup=GroupBuilder.of().setName("anotherGroup").create();

        groupRepository.add(adminGroup);
        groupRepository.add(aGroup);
        service.setAdminGroupName(adminGroup.name());

        Set<Group> groups=service.getAllGroupInRepository();
        assertTrue(groups.contains(aGroup));
        assertFalse(groups.contains(adminGroup));
        assertEquals(1,groups.size());

    }

}
package tollmanager.model.access;

import tollmanager.model.access.authorization.IAuthorizationService;
import tollmanager.model.access.authorization.IllegalRightException;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * the service that deal with group and group members
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class GroupService implements IGroupService {
    private GroupRepository groupRepository;
    private IAuthorizationService authorizationService;
    private GroupName administratorGroup;


    public GroupService(GroupRepository groupRepository, IAuthorizationService authorizationService) {
        Objects.requireNonNull(groupRepository, "The group repository cannot be null.");
        Objects.requireNonNull(authorizationService, "The authorization service cannot be null.");

        this.groupRepository = groupRepository;
        this.authorizationService = authorizationService;
    }

    /**
     * try to append a member into a group
     *
     * @exception  GroupRuntimeException if group contain already the member
     * @exception  IllegalRightException if the caller is not authorized to append a member to this group
     *
     * @param caller who called the service
     * @param groupToAppend group where append the member
     * @param memberToAppend the member to append
     */
    @Override
    public void appendMemberToGroup(GroupMember caller, GroupName groupToAppend, GroupMember memberToAppend) {
        Objects.requireNonNull(caller, "The member who calls this service is required.");
        Objects.requireNonNull(groupToAppend, "The group name where append member is required.");
        Objects.requireNonNull(memberToAppend, "The member to append to group is required.");

        Group group = findGroupOrThrow(groupToAppend);
        if(group.hasMember(memberToAppend))
            throw new GroupRuntimeException(caller,groupToAppend);
        if (!authorizationService.isAuthorizedToAppendMember(caller, groupToAppend))
            throw new IllegalRightException(caller, Role.append_member(groupToAppend));
        group.appendMember(memberToAppend);
        groupRepository.update(group);
    }

    /**
     * Remove a member from a group
     * @exception IllegalRightException if caller is not authorized to remove member from this group
     * @param caller who called the service
     * @param groupToRemove group where remove member
     * @param memberToRemove member to remove
     */
    @Override
    public void removeMemberFromGroup(GroupMember caller, GroupName groupToRemove, GroupMember memberToRemove) {
        Objects.requireNonNull(caller, "The member who calls this service is required.");
        Objects.requireNonNull(groupToRemove, "The group name where remove member is required.");
        Objects.requireNonNull(memberToRemove, "The member to remove from group is required.");

        Group group=findGroupOrThrow(groupToRemove);
        assertAllowedToRemoveMember(caller,group);
        group.removeMember(memberToRemove);
        groupRepository.update(group);
    }

    /**
     * ASk if the member belgon to a group
     * @param groupMember
     * @param groupName
     * @return true if groupMember belong to the group
     */
    @Override
    public boolean isBelongTo(GroupMember groupMember, GroupName groupName) {
        Objects.requireNonNull(groupMember, "The group member is required.");
        Objects.requireNonNull(groupName, "The group name is required.");

        return findGroupOrThrow(groupName).hasMember(groupMember);
    }


    /**
     * Remove the member from all where he belong (useful when delete user for example).
     * To work the caller must have the Role to delete member from all group where member to delete belongs
     * @exception IllegalRightException if caller is not authorized to remove member from this group
     * @param caller who called the service
     * @param targetMember member to remove from all group
     */
    @Override
    public void removeMemberFromAllGroups(GroupMember caller, GroupMember targetMember) {
        Objects.requireNonNull(caller, "The member who calls this service is required.");
        Objects.requireNonNull(targetMember, "The member to remove from all group is required.");

        List<Group> groups = groupRepository.groupsWhereBelong(targetMember);
        groups.forEach(group -> assertAllowedToRemoveMember(caller,group));
        groups.forEach(group -> {
            removeMemberFromGroup(caller, group.name(), targetMember);
            groupRepository.update(group);
        });

    }

    /**
     * Find all groups and remove group setted as administrator
     * @return
     */
    @Override
    public Set<Group> getAllGroupInRepository() {
        Set<Group> groups=groupRepository.findAll();
        groups.removeIf(g->g.name().equals(administratorGroup));
        return groups;
    }

    /**
     *
     * @param administratorGroup group will contain super user
     */
    @Override
    public void setAdminGroupName(GroupName administratorGroup) {
        Objects.requireNonNull(administratorGroup,"The admin group name cannot be null.");
        if(administratorGroup.equals(GroupName.Null()))
            throw new IllegalArgumentException("The group name for administrator cannot be null");
        this.administratorGroup=administratorGroup;
    }

    private Group findGroupOrThrow(GroupName name) {
        Group group=groupRepository.findByName(name);
        Objects.requireNonNull(group, "The group '" + name.value() + "' not found.");
        return group;
    }


    private void assertAllowedToRemoveMember(GroupMember caller,Group whereRemove){
        if (!authorizationService.isAuthorizedToRemoveMember(caller, whereRemove.name()))
            throw new IllegalRightException(caller, Role.remove_member(whereRemove.name()));
    }
}

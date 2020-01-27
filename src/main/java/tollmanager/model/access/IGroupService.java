package tollmanager.model.access;

import java.util.Set;

public interface IGroupService {

    /**
     *
     * @param caller who called the service
     * @param groupToAppend group where append the member
     * @param memberToAppend the member to append
     */
    void appendMemberToGroup(GroupMember caller, GroupName groupToAppend, GroupMember memberToAppend);

    /**
     *
     * @param caller who called the service
     * @param groupToRemove group where remove member
     * @param memberToRemove member to remove
     */
    void removeMemberFromGroup(GroupMember caller, GroupName groupToRemove, GroupMember memberToRemove);

    /**
     *
     * @param groupMember
     * @param groupName
     * @return
     */
    boolean isBelongTo(GroupMember groupMember,GroupName groupName);

    /**
     *
     * @param caller who called the service
     * @param targetMember member to remove from all group
     */
    void removeMemberFromAllGroups(GroupMember caller, GroupMember targetMember);


    /**
     *
     * @return a set of all group
     */
    Set<Group> getAllGroupInRepository();


    /**
     *
     * @param administratorGroup group will contain super user
     */
    void setAdminGroupName(GroupName administratorGroup);
}

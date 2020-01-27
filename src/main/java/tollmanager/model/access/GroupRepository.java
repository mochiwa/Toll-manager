package tollmanager.model.access;

import java.util.List;
import java.util.Set;

public interface GroupRepository {
    /**
     * @param group to append into the repository
     */
    void add(Group group);

    /**
     * @param groupName the group name to find
     * @return the group
     */
    Group findByName(GroupName groupName);

    /**
     * @param groupMember the member
     * @return set of Group
     */
    List<Group> groupsWhereBelong(GroupMember groupMember);

    /**
     * @return set of group
     */
    Set<Group> findAll();

    /**
     * @param group to update
     */
    void update(Group group);
}

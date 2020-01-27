package tollmanager.model.access.authorization;

import tollmanager.model.access.*;
import java.util.List;
import java.util.Objects;

/**
 * Service that deals with authorization
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class AuthorizationService implements IAuthorizationService {
    private GroupRepository groupRepository;

    public AuthorizationService(GroupRepository groupRepository) {
        Objects.requireNonNull(groupRepository,"The group repository cannot be null.");

        this.groupRepository = groupRepository;
    }

    /**
     * @param caller groupMember who calls the service
     * @return true if caller belongs to group that have the right, otherwise false
     */
    @Override
    public boolean isAuthorizedToCreateEmployee(GroupMember caller) {
        Objects.requireNonNull(caller,"The group member cannot be null.");

        return isAuthorizedTo(caller, Role.createEmployee());
    }

    /**
     * @param caller groupMember who calls the service
     * @param groupToAppend groupName where groupMember would know if he authorized to append member
     * @return true if caller belongs to a group that has the role to append member for the groutToAppend or
     *  if the caller is authorized to create an user for the groupToAppend,otherwise false.
     */
    @Override
    public boolean isAuthorizedToAppendMember(GroupMember caller, GroupName groupToAppend) {
        Objects.requireNonNull(caller,"The group member cannot be null.");
        Objects.requireNonNull(groupToAppend,"The group cannot be null.");

        return isAuthorizedTo(caller,Role.append_member(groupToAppend)) || isAuthorizedToCreateUser(caller,groupToAppend)  ;
    }

    /**
     * @param caller groupMember who calls the service
     * @param groupToRemove groupName where groupMember would know if he authorized to remove member
     * @return true if caller belongs to a group that has the role to remove member for the groutToRemove or
     *  if the caller is authorized to delete an user for the groupToAppend,otherwise false
     */
    @Override
    public boolean isAuthorizedToRemoveMember(GroupMember caller, GroupName groupToRemove) {
        Objects.requireNonNull(caller,"The group member cannot be null.");
        Objects.requireNonNull(groupToRemove,"The group cannot be null.");

        return isAuthorizedTo(caller,Role.remove_member(groupToRemove)) || isAuthorizedToDeleteUser(caller,groupToRemove);
    }

    /**
     * @param caller groupMember who calls the service
     * @param groupWhereCreate groupName where groupMember should know if he can create a user
     * @return true true if caller belongs to a group that has the role to create user for the groupWHereCreate, otherwise false
     */
    @Override
    public boolean isAuthorizedToCreateUser(GroupMember caller,GroupName groupWhereCreate) {
        Objects.requireNonNull(caller,"The group member cannot be null.");
        Objects.requireNonNull(groupWhereCreate,"The group cannot be null.");

        return isAuthorizedTo(caller,Role.createUser(groupWhereCreate));
    }

    /**
     * @param caller groupMember who calls the service
     * @param groupWhereDelete groupName where groupMember should know if he can delete a user
     * @return true if caller belongs to a group that has the role to delete user for the groupWhereDelete, otherwise false
     */
    @Override
    public boolean isAuthorizedToDeleteUser(GroupMember caller,GroupName groupWhereDelete) {
        Objects.requireNonNull(caller,"The group member cannot be null.");
        Objects.requireNonNull(groupWhereDelete,"The group cannot be null.");

        return isAuthorizedTo(caller,Role.deleteUser(groupWhereDelete));
    }

    /**
     * @param caller  groupMember who calls the service
     * @return true true if caller belongs to a group that has the role to create a team
     */
    @Override
    public boolean isAuthorizedToCreateTeam(GroupMember caller) {
        Objects.requireNonNull(caller,"The group member cannot be null.");

        return isAuthorizedTo(caller,Role.create_team());
    }

    /**
     * @param caller  groupMember who calls the service
     * @return  true if caller belongs to a group that has the role to manage a team
     */
    @Override
    public boolean isAuthorizedToManageTeam(GroupMember caller) {
        Objects.requireNonNull(caller,"The group member cannot be null.");

        return isAuthorizedTo(caller,Role.manage_team(GroupName.wildCard())) || isAuthorizedToCreateTeam(caller);
    }

    /**
     * @param caller  groupMember who calls the service
     * @return true if caller belongs to a group that has the role to create a team or if
     *  the caller is authorized to manage team
     */
    @Override
    public boolean isAuthorizedToAppendEmployeeToATeam(GroupMember caller) {
        return isAuthorizedToCreateTeam(caller) || isAuthorizedToManageTeam(caller);
    }

    /**
     * @param caller groupMember who calls the service
     * @return true if caller belongs to a group that has the role to manage planning
     */
    @Override
    public boolean isAuthorizedToManagePlanning(GroupMember caller) {
        return isAuthorizedTo(caller,Role.manage_planning());
    }

    private boolean isAuthorizedTo(GroupMember member,Role role){
        List<Group> groups=groupRepository.groupsWhereBelong(member);
        return groups.stream().anyMatch(g -> g.hasRole(role));
    }
}

package tollmanager.model.access.authorization;

import tollmanager.model.access.GroupMember;
import tollmanager.model.access.GroupName;
import tollmanager.model.identity.team.TeamName;
import tollmanager.model.identity.user.User;

public interface IAuthorizationService {
    /**
     * @param caller groupMember who calls the service
     * @return true if he is authorized,false otherwise.
     */
    boolean isAuthorizedToCreateEmployee(GroupMember caller);

    /**
     * @param caller groupMember who calls the service
     * @param groupToAppend groupName where groupMember should know if he can append
     * @return true if he is authorized,false otherwise.
     */
    boolean isAuthorizedToAppendMember(GroupMember caller, GroupName groupToAppend);

    /**
     * @param caller groupMember who calls the service
     * @param groupToRemove groupName where groupMember should know if he can remove
     * @return true if he is authorized,false otherwise.
     */
    boolean isAuthorizedToRemoveMember(GroupMember caller, GroupName groupToRemove);

    /**
     * @param caller groupMember who calls the service
     * @param groupWhereCreate groupName where groupMember should know if he can create a user
     * @return true if he is authorized,false otherwise.
     */
    boolean isAuthorizedToCreateUser(GroupMember caller,GroupName groupWhereCreate);

    /**
     * @param caller groupMember who calls the service
     * @param groupWhereDelete groupName where groupMember should know if he can delete a user
     * @return true if he is authorized,false otherwise.
     */
    boolean isAuthorizedToDeleteUser(GroupMember caller,GroupName groupWhereDelete);

    /**
     * @param caller  groupMember who calls the service
     * @return true if he is authorized,false otherwise.
     */
    boolean isAuthorizedToCreateTeam(GroupMember caller);

    /**
     * @param caller  groupMember who calls the service
     * @return true if he is authorized,false otherwise.
     */
    boolean isAuthorizedToManageTeam(GroupMember caller);

    /**
     * @param caller  groupMember who calls the service
     * @return true if he is authorized,false otherwise.
     */
    boolean isAuthorizedToAppendEmployeeToATeam(GroupMember caller);


    /**
     * @param caller groupMember who calls the service
     * @return true if he is authorized,false otherwise
     */
    boolean isAuthorizedToManagePlanning(GroupMember caller);
}

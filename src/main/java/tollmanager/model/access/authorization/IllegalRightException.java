package tollmanager.model.access.authorization;

import tollmanager.model.access.GroupMember;
import tollmanager.model.access.Role;

public class IllegalRightException extends RuntimeException {
    public IllegalRightException(GroupMember caller, Role role) {
        super("'" + caller.name() + "' has not the role to '" + role.name() + "' to the group '"+role.targetToString() + "'.");
    }

    public IllegalRightException(GroupMember caller, String actionNotAuthorized) {
        super("'" + caller.name() + "' is not authorized to '" + actionNotAuthorized + "'.");
    }

    public IllegalRightException(String errorMessage) {
        super(errorMessage);
    }

    public static IllegalRightException manageTeam(GroupMember caller){
        return new IllegalRightException(caller,"Manage planning");
    }
}

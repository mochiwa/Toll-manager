package tollmanager.model.access;

public class GroupRuntimeException extends RuntimeException {
    public GroupRuntimeException(GroupMember caller, GroupName whereAppend) {
        super("'"+caller.name()+"' is already present in group '"+whereAppend.value()+"'.");
    }
}

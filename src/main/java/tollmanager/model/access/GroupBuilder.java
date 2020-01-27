package tollmanager.model.access;

import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * the builder pattern for group
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class GroupBuilder {
    private GroupName name;
    protected LinkedHashSet<Role> roles;
    protected LinkedHashSet<GroupMember> members;

    private GroupBuilder() {
        roles=new LinkedHashSet<>();
        members=new LinkedHashSet<>();
    }

    public static GroupBuilder of()
    {
        return new GroupBuilder();
    }



    public GroupBuilder setName(GroupName name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }
    public GroupBuilder setName(String name) {
        Objects.requireNonNull(name);
        this.name = GroupName.of(name);
        return this;
    }

    public GroupBuilder setRoles(LinkedHashSet<Role> roles) {
        Objects.requireNonNull(roles);
        this.roles = roles;
        return this;
    }
    public GroupBuilder addRole(Role role) {
        Objects.requireNonNull(role);
        roles.add(role);
        return this;
    }

    public GroupBuilder setMembers(LinkedHashSet<GroupMember> members) {
        Objects.requireNonNull(members);
        this.members = members;
        return this;
    }
    public GroupBuilder addMember(GroupMember member) {
        Objects.requireNonNull(member);
        members.add(member);
        return this;
    }


    public Group create() {
        return Group.of(name, roles, members);
    }
}
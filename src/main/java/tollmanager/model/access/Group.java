package tollmanager.model.access;

import tollmanager.model.identity.EmployeeId;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a group of authority with rules and members
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class Group {
    private GroupName name;
    private LinkedHashSet<Role> roles;
    private LinkedHashSet<GroupMember> members;

    private Group(GroupName name, LinkedHashSet<Role> roles, LinkedHashSet<GroupMember> members) {
        this.name = name;
        this.roles = roles;
        this.members = members;
    }

    public static Group of(GroupName name, LinkedHashSet<Role> roles, LinkedHashSet<GroupMember> members) {
        Objects.requireNonNull(name,"The name for the group is required.");
        Objects.requireNonNull(roles,"The list of roles is required.");
        Objects.requireNonNull(members,"The list of members required.");

        return new Group(name, roles, members);
    }

    /**
     * Append a new member to the group
     * @param member to append
     */
    public void appendMember(GroupMember member) {
        Objects.requireNonNull(member,"The member to append is required.");
        members.add(member);
    }

    /**
     * @param member to find
     * @return true if member belongs to the group
     */
    public boolean hasMember(GroupMember member) {
        return members.contains(member);
    }

    /**
     * @param id of the member
     * @return true if member belongs to the group
     */
    public boolean hasMember(EmployeeId id) {
        return members.stream().anyMatch(m->m.getEmployeeId().equals(id));
    }

    /**
     * Append a new role the group
     * @param role to append
     * @see Role
     */
    void appendRole(Role role) {
        Objects.requireNonNull(role,"The role to append is required.");
        roles.add(role);
    }

    /**
     * @param role to find
     * @return true if group has role, false else
     */
    public boolean hasRole(Role role)
    {
        return roles.stream().anyMatch(r->r.isEquivalent(role));
    }

    /**
     * Remove member from the group
     * @param member to remove
     */
    public void removeMember(GroupMember member) {
        members.remove(member);
    }

    public GroupName name() {
        return name;
    }

    /**
     * @return count of member
     */
    public int membersCount() {
        return members.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group)) return false;
        Group group = (Group) o;
        return Objects.equals(name, group.name) &&
                Objects.equals(roles, group.roles) &&
                Objects.equals(members, group.members);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name, roles, members);
    }
    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", roles=" + roles +
                ", members=" + members +
                '}';
    }


    public Set<GroupMember> members() {
        return new LinkedHashSet<GroupMember>(members);
    }

    public Set<Role> roles() {
        return new LinkedHashSet<Role>(roles);
    }

    /**
     * Remove a role from the group
     * @param role to remove
     */
    public void removeRole(Role role) {
        roles.remove(role);
    }

}

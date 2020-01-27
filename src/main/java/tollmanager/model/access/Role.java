package tollmanager.model.access;

import java.util.Objects;

/**
 * Represents a role which a group can have
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class Role {
    private final String name;
    private String description;
    private final GroupName target;

    private Role(String name, String description, GroupName target) {
        Objects.requireNonNull(target,"the target is required");

        this.name = name;
        this.description = description;
        this.target = target;
    }

    public static Role of(String name,String description,GroupName target){
        Objects.requireNonNull(name,"the name is required");
        return new Role(name,description,target);
    }
    public static Role append_member(GroupName target) {
        return new Role("append_member", "allow to append member to the group "+target, target);
    }
    public static Role remove_member(GroupName target) {
        return new Role("remove_member", "allow to remove member to the group "+target, target);
    }
    public static Role manage_team(GroupName target) {
        return new Role("manage_team", "allow to manage the team "+target, target);
    }
    public static Role createEmployee() {
        return new Role("create_employee","allow a user to create an employee",GroupName.wildCard());
    }
    public static Role nullRole() {
        return new Role("","",GroupName.Null());
    }

    public static Role createUser(GroupName target) {
        return new Role("create_user","allow a user to create an user",target);
    }

    public static Role deleteUser(GroupName target) {
        return new Role("delete_user","allow a user to delete an user",target);
    }

    public static Role create_team() {
        return new Role("create_team","allow a user to create a team",GroupName.wildCard());
    }

    public static Role manage_planning() {
        return new Role("manage_planning","allow a user to manage a team",GroupName.wildCard());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name) &&
                Objects.equals(target, role.target);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name, target);
    }
    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", target='" + target + '\'' +
                '}';
    }

    /**
     * Role are equivalent if they are equals (same role) or if the role equals wild card
     *
     * @param role the role
     * @return true if equivalent, false else
     */
    boolean isEquivalent(Role role) {
        Objects.requireNonNull(role,"The role is required");
        return name.equals(role.name) &&
                (target.equals(role.target) || target.equals(GroupName.wildCard()));
    }

    public String name() {
        return name;
    }

    public String targetToString() {
       return target.value();
    }

    public String description() {
        return description;
    }
}

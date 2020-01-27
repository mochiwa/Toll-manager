package tollmanager.model.access;

import java.util.Objects;

/**
 * Value object for group name
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class GroupName {
    private final String groupName;

    private GroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * All name is trimmed and converted to lower case
     * @param name
     * @return
     */
    public static GroupName of(String name) {
        Objects.requireNonNull(name,"The name is required.");

        if( !name.equals("*") && (name.trim().length()<3 || name.trim().length()>55) )
            throw new IllegalArgumentException("The name's length must be between 3 and 55.");
        return new GroupName(name.trim().toLowerCase());
    }

    static public GroupName Null() {
        return new GroupName("");
    }

    public static GroupName wildCard() {
        return new GroupName("*");
    }

    public String value() {
        return groupName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupName)) return false;
        GroupName other = (GroupName) o;
        return Objects.equals(groupName, other.groupName);
    }
    @Override
    public int hashCode() {
        return Objects.hash(groupName);
    }
    @Override
    public String toString() {
        return "GroupName{" +
                "groupName='" + groupName + '\'' +
                '}';
    }


}

package tollmanager.model.identity.person;

import java.util.Objects;
/**
 * Represents a full name with name and forename
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class FullName {
    private final String name;
    private final String forename;

    private FullName(String name, String forename) {
        this.name = name;
        this.forename = forename;
    }

    public static FullName of(String name, String forename) {
        Objects.requireNonNull(name,"The name cannot be null.");
        Objects.requireNonNull(forename,"The forename cannot be null.");

        if(!name.matches("^[a-zA-Z-]{3,55}$"))
            throw new IllegalArgumentException("The name length must be between 3 and 55 and only alpha character.");
        if(!forename.matches("^[a-zA-Z-]{3,55}$"))
            throw new IllegalArgumentException("The forename length must be between 3 and 55 and only alpha character.");

        return new FullName(name.toLowerCase(), forename.toLowerCase());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FullName)) return false;
        FullName fullName = (FullName) o;
        return Objects.equals(name, fullName.name) &&
                Objects.equals(forename, fullName.forename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, forename);
    }

    @Override
    public String toString() {
        return "FullName{" +
                "name='" + name + '\'' +
                ", forename='" + forename + '\'' +
                '}';
    }

    public String name() {
        return name;
    }

    public String forename() {
        return forename;
    }
}

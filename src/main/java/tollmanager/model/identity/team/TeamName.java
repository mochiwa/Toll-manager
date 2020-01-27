package tollmanager.model.identity.team;

import java.util.Objects;


/**
 * Value object for the team name
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class TeamName {
    private final String name;

    private TeamName(String name) {
        this.name = name;
    }

    public static TeamName of(String name) {
        Objects.requireNonNull(name,"The name is required.");

        if(name.trim().length()<3 || name.trim().length()>55 )
            throw new IllegalArgumentException("The name's length must be between 3 and 55.");
        return new TeamName(name.trim().toLowerCase());
    }
    public static TeamName Null() {
        return new TeamName("");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamName teamName = (TeamName) o;
        return Objects.equals(name, teamName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "TeamName{" +
                "name='" + name + '\'' +
                '}';
    }

    public String value() {
        return name;
    }
}

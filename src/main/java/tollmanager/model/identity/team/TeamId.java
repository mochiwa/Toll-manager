package tollmanager.model.identity.team;

import java.util.Objects;
/**
 * Value object for team id
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class TeamId {
    private final String id;

    private TeamId(String id) {
        this.id = id;
    }

    public static TeamId of(String id) {
        if(id==null || id.trim().isEmpty())
            throw new IllegalArgumentException("The id is not valid.");
        return new TeamId(id);
    }

    public static TeamId Null() {
        return new TeamId("");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamId teamId = (TeamId) o;
        return Objects.equals(id, teamId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TeamId{" +
                "id='" + id + '\'' +
                '}';
    }

    public String value() {
        return id;
    }
}

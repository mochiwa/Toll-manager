package tollmanager.model.identity;

import java.util.Objects;
/**
 * Value object for employee id
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class EmployeeId {
    private final String id;

    private EmployeeId(String id) {
        this.id = id;
    }

    public static EmployeeId of(String id) {
        if(id==null || id.trim().isEmpty())
            throw new IllegalArgumentException("The id is not valid.");
        return new EmployeeId(id.trim());
    }

    public static EmployeeId Null() {
        return new EmployeeId("-1");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeId)) return false;
        EmployeeId other = (EmployeeId) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "EmployeeId{" +
                "id='" + id + '\'' +
                '}';
    }

    public String value() {
        return id;
    }
}

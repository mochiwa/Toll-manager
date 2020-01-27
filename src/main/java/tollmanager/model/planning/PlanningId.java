package tollmanager.model.planning;

import java.util.Objects;
/**
 * Value object id planning
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class PlanningId {
    private String planningId;


    private PlanningId(String planningId) {
        this.planningId = planningId;
    }


    public static PlanningId of(String planningId) {
        Objects.requireNonNull(planningId, "the planning id is required");
        return new PlanningId(planningId);
    }

    public static PlanningId Null() {
        return new PlanningId("");
    }

    @Override
    public String toString() {
        return "PlanningId{" +
                "planningId='" + planningId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlanningId that = (PlanningId) o;
        return Objects.equals(planningId, that.planningId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planningId);
    }

    public String value() {
        return planningId;
    }
}

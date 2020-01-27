package tollmanager.model.planning;

import tollmanager.model.identity.EmployeeId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

public interface PlanningRepository {
    /**
     * @return the next id available
     */
    PlanningId nextId();

    /**
     * @param planning planning to append to the repository
     */
    void add(Planning planning);

    /**
     * @param planningId the planning id to search
     * @return planning found.
     */
    Planning findById(PlanningId planningId);

    /**
     *
     * @param employeeId linked to a planning
     * @param date the date of beginning planning
     * @return set of planning
     */
    Set<Planning> findAllPlanningForEmployeeAtDay(EmployeeId employeeId, LocalDate date);

    /**
     *
     * @param employeeId the employee linked to the planning
     * @param begin beginning datetime
     * @param end ending datetime
     * @return set of planning
     */
    Set<Planning> findAllPlanningFromEmployeeBetween(EmployeeId employeeId, LocalDateTime begin, LocalDateTime end);

    /**
     * @param planning to remove
     */
    void removePlanning(Planning planning);
}

package tollmanager.model.planning;

import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.team.TeamId;
import tollmanager.model.identity.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public interface IPlanningService {

    /**
     * @param caller user who's called the service
     * @param begin the beginning date of the planning
     * @param end the ending date of the planning
     * @param comment a comment for the planning
     * @param belonging the employee linked to the planning
     * @param teamId the team where planning is concerned
     * @return Planning
     * @see Planning
     */
    Planning providePlanning(User caller, LocalDateTime begin, LocalDateTime end, String comment, EmployeeId belonging, TeamId teamId);

    /**
     * @param employeeId the id of the employee linked to the planning
     * @param date the date of week
     * @return Map of planning linked by date
     */
    Map<LocalDate, Set<Planning>> getPlanningOfWeek(EmployeeId employeeId, LocalDate date);

    /**
     * @param caller user who's called the service
     * @param planning planning to remove
     */
    void removePlanning(User caller, Planning planning);
}

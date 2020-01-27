package tollmanager.application.query;

import tollmanager.model.identity.EmployeeId;
import tollmanager.model.planning.Planning;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
/**
 * ask the business to give all planning for the week
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class WeekPlanningQuery {
    private LocalDate weekSelected;
    private EmployeeId employeeId;
    private Map<LocalDate, Set<Planning>> weekPlanning;

    public WeekPlanningQuery(EmployeeId employeeId, LocalDate weekSelected) {
        this.employeeId=employeeId;
        this.weekSelected = weekSelected;
        this.weekPlanning=new LinkedHashMap<>();
    }

    public EmployeeId getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(EmployeeId employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getWeekSelected() {
        return weekSelected;
    }

    public void setWeekSelected(LocalDate weekSelected) {
        this.weekSelected = weekSelected;
    }

    public Map<LocalDate, Set<Planning>> getWeekPlanning() {
        return weekPlanning;
    }

    public void setWeekPlanning(Map<LocalDate, Set<Planning>> weekPlanning) {
        this.weekPlanning = weekPlanning;
    }
}

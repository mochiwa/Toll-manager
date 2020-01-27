package tollmanager.model.planning;

import tollmanager.model.identity.Employee;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.team.TeamId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents planning for an employee
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class Planning {
    private PlanningId id;
    private LocalDateTime beginningDate;
    private LocalDateTime endingDate;
    private String comment;
    private EmployeeId belongingEmployee;
    private TeamId teamId;


    public Planning(PlanningId id, LocalDateTime beginningDate, LocalDateTime endingDate, String comment, EmployeeId belongingEmployee, TeamId teamId) {
        this.id = id;
        setBeginningDate(beginningDate);
        setEndingDate(endingDate);
        this.comment = comment;
        this.belongingEmployee = belongingEmployee;
        this.teamId=teamId;
    }

    public static Planning of(PlanningId id, LocalDateTime beginningDate, LocalDateTime endingDate, String comment, EmployeeId employeeId, TeamId teamId) {
        Objects.requireNonNull(id,"The planning id is required.");
        Objects.requireNonNull(beginningDate,"The beginning date is required");
        Objects.requireNonNull(endingDate,"The ending date is required");
        Objects.requireNonNull(comment,"The comment is required");
        Objects.requireNonNull(employeeId,"The employee id is required");
        Objects.requireNonNull(teamId,"The idea of team is required.");
        return new Planning(id,beginningDate,endingDate,comment,employeeId,teamId);
    }

    public static Planning Null() {
        return new Planning(PlanningId.Null(),LocalDateTime.MIN,LocalDateTime.MIN,"",EmployeeId.Null(),TeamId.Null());
    }

    /**
     * If the datetime in argument is before the beginning date, then ending equals the beginning datetime
     * @param date ending date
     */
    private void setEndingDate(LocalDateTime date){
        Objects.requireNonNull(date);
        if(date.isBefore(beginningDate))
            endingDate=beginningDate;
        else
            endingDate=date;
    }

    /**
     * If the datetime in argument is before the ending date or null then the beginning date equals the Min
     * @see LocalDateTime
     * @param date
     */
    private void setBeginningDate(LocalDateTime date){
        Objects.requireNonNull(date);
        if(endingDate!=null && date.isAfter(endingDate))
            beginningDate=LocalDateTime.MIN;
        else
            beginningDate=date;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Planning planning = (Planning) o;
        return Objects.equals(id, planning.id) &&
                Objects.equals(beginningDate, planning.beginningDate) &&
                Objects.equals(endingDate, planning.endingDate) &&
                Objects.equals(comment, planning.comment) &&
                Objects.equals(belongingEmployee, planning.belongingEmployee) &&
                Objects.equals(teamId, planning.teamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, beginningDate, endingDate, comment, belongingEmployee);
    }

    @Override
    public String toString() {
        return "Planning{" +
                "id=" + id +
                ", beginningDate=" + beginningDate +
                ", endingDate=" + endingDate +
                ", comment='" + comment + '\'' +
                ", belongingEmployee=" + belongingEmployee +
                ", teamId=" + teamId +
                '}';
    }

    /**
     * @return a copy of the ending datetime
     */
    public LocalDateTime ending() {
        return LocalDateTime.from(endingDate);
    }

    /**
     * @return a copy of the beginning datetime
     */
    public LocalDateTime beginning() {
        return LocalDateTime.from(beginningDate);
    }

    /**
     * @return the employee id linked to this planning
     */
    public EmployeeId employeeId() {
        return belongingEmployee;
    }
    /**
     * @return the beginning date on local date format (without the time)
     */
    public LocalDate dayBeginningDate() {
        return LocalDate.from(beginningDate);
    }

    /**
     * @return the ending date on local date format (without the time)
     */
    public LocalDate dayEndingDate() {
        return LocalDate.from(endingDate);
    }

    /**
     * @param hour
     * @return true if hour in argument is between begin and end , false any else
     */
    public boolean isHourBetweenPlanning(int hour){
        return hour>=beginning().getHour() && hour<= ending().getHour();
    }

    public PlanningId planningId() {
        return this.id;
    }

    public TeamId teamId() {
        return teamId;
    }

    public String comment() {
        return comment;
    }
}

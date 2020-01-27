package tollmanager.application.command;

import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.team.TeamId;

import java.time.LocalDateTime;
/**
 * Represents the use case : append a new planning for an employee
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class AppendPlanningCommand {
    private LocalDateTime beginningDate;
    private LocalDateTime endingDate;
    private String comment;
    private EmployeeId belongingEmployee;
    private TeamId teamId;

    public AppendPlanningCommand(LocalDateTime beginningDate, LocalDateTime endingDate, String comment) {
        this.beginningDate = beginningDate;
        this.endingDate = endingDate;
        this.comment = comment;
    }

    public AppendPlanningCommand(LocalDateTime beginningDate, LocalDateTime endingDate, String comment, EmployeeId belongingEmployee, TeamId teamId) {
        this.beginningDate = beginningDate;
        this.endingDate = endingDate;
        this.comment = comment;
        this.belongingEmployee = belongingEmployee;
        this.teamId = teamId;
    }

    public LocalDateTime getBeginningDate() {
        return beginningDate;
    }

    public void setBeginningDate(LocalDateTime beginningDate) {
        this.beginningDate = beginningDate;
    }

    public LocalDateTime getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(LocalDateTime endingDate) {
        this.endingDate = endingDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public EmployeeId getBelongingEmployee() {
        return belongingEmployee;
    }

    public void setBelongingEmployee(EmployeeId belongingEmployee) {
        this.belongingEmployee = belongingEmployee;
    }

    public TeamId getTeamId() {
        return teamId;
    }

    public void setTeamId(TeamId teamId) {
        this.teamId = teamId;
    }
}

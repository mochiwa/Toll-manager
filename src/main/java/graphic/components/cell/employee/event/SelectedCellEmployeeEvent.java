package graphic.components.cell.employee.event;

import javafx.event.Event;
import javafx.event.EventType;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.team.Team;

public class SelectedCellEmployeeEvent extends Event {
    public static final EventType<SelectedCellEmployeeEvent> CELL_SELECTED=new EventType<>("CELL_SELECTED");
    private Employee employeeSelected;
    private Team team;


    public SelectedCellEmployeeEvent(Employee employee, Team team) {
        super(CELL_SELECTED);
        employeeSelected=employee;
        this.team=team;
    }

    public Employee getEmployeeSelected() {
        return employeeSelected;
    }

    public Team getTeam() {
        return team;
    }
}

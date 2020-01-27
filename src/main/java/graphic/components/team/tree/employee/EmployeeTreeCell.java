package graphic.components.team.tree.employee;

import graphic.components.cell.employee.event.SelectedCellEmployeeEvent;
import graphic.components.team.TeamBoardCommandEvent;
import graphic.components.team.tree.ITreeTeamCell;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import tollmanager.application.IApplicationLifeTime;
import tollmanager.application.command.RemoveFromTeamEmployeeCommand;
import tollmanager.application.command.DeleteEmployeeCommand;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.team.Team;
import tollmanager.model.identity.team.TeamName;

import java.util.Objects;

public class EmployeeTreeCell implements ITreeTeamCell<Employee> {
    private final String TEAMLEADER_ICON = Objects.requireNonNull(getClass().getClassLoader().getResource("image/teamLeader-icon.png")).toExternalForm();
    private final String WORKER_ICON = Objects.requireNonNull(getClass().getClassLoader().getResource("image/worker-icon.png")).toExternalForm();
    private final String MANAGER_ICON = Objects.requireNonNull(getClass().getClassLoader().getResource("image/manager-icon.png")).toExternalForm();

    private SimpleObjectProperty<Employee> employeeProperty;
    private SimpleObjectProperty<Team> teamProperty;
    private SimpleStringProperty cellName;

    public EmployeeTreeCell(Employee employee, Team teamParent){
        cellName=new SimpleStringProperty(employee.person().fullNameToString());
        employeeProperty=new SimpleObjectProperty<>(employee);
        teamProperty=new SimpleObjectProperty<>(teamParent);
    }

    @Override
    public String getName() {
        return cellName.get();
    }

    @Override
    public Employee getUserData() {
        return employeeProperty.get();
    }

    @Override
    public ContextMenu getContextMenu() {
        EmployeeCellContextMenu menu=new EmployeeCellContextMenu();
        menu.setOnAction(e->{
            if(e.getTarget().equals(menu.getEdit()))
                menu.fireEvent(TeamBoardCommandEvent.editEmployee());
            else if(e.getTarget().equals(menu.getRemoveFromTeam())) {
                Alert popup = new Alert(Alert.AlertType.CONFIRMATION);
                popup.setTitle("Remove employee from team confirmation");
                popup.setContentText("Do you really want to remove '"+getUserData().person().fullNameToString()+
                        "' from team '"+teamProperty.get().name().value()+"' ?");
                popup.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                popup.showAndWait()
                        .filter(r-> r.equals(ButtonType.OK))
                        .ifPresent(r->menu.fireEvent(TeamBoardCommandEvent.of(new RemoveFromTeamEmployeeCommand(teamProperty.get(),getUserData()))));
            }
            else if(e.getTarget().equals(menu.getDelete())){
                Alert popup = new Alert(Alert.AlertType.CONFIRMATION);
                popup.setTitle("Delete employee confirmation");
                popup.setContentText("Do you really want to delete The employee "+employeeProperty.get().person().fullNameToString()+ " ?");
                popup.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                popup.showAndWait()
                        .filter(r-> r.equals(ButtonType.OK))
                        .ifPresent(r->menu.fireEvent(TeamBoardCommandEvent.of(new DeleteEmployeeCommand(employeeProperty.get()))));
            }
        });

        return menu;
    }

    @Override
    public Image getIcon() {
        if(teamProperty.get().leader().employeeId().equals(employeeProperty.get().employeeId()))
            return new Image(TEAMLEADER_ICON);
        else if(teamProperty.get().hasEmployee(employeeProperty.get()) && !teamProperty.get().name().equals(IApplicationLifeTime.managersTeam))
            return new Image(WORKER_ICON);
        else
            return new Image(MANAGER_ICON);

    }

    @Override
    public Event getSelectedEvent() {
       return new SelectedCellEmployeeEvent(employeeProperty.get(),teamProperty.get());
    }

    @Override
    public boolean isDraggable() {
        return true;
    }

    @Override
    public boolean isTarget() {
        return false;
    }

    @Override
    public String getStyleSheet() {
        return "-fx-border-color: black; -fx-border-width: 0 0 2 0; -fx-padding: 3 3 1 3";
    }
}

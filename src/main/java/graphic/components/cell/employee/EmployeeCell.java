package graphic.components.cell.employee;

import graphic.components.team.TeamBoardCommandEvent;
import graphic.components.team.tree.employee.EmployeeCellContextMenu;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import tollmanager.application.command.RemoveFromTeamEmployeeCommand;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.person.FullName;
import tollmanager.model.identity.team.TeamName;

import java.io.Serializable;

public class EmployeeCell extends TextFieldListCell<Employee> implements Serializable {
    private static final DataFormat customFormat=new DataFormat("cellEmployee");
    private final String idCell="cellEmployee-";
    private String teamName;

    public EmployeeCell() {
        this.setOnDragDetected(event -> {
            Dragboard dragboard=startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content=new ClipboardContent();
            content.put(customFormat,this);
            dragboard.setContent(content);
            event.consume();
        });


        EmployeeCellContextMenu menu=new EmployeeCellContextMenu();
        this.setContextMenu(menu);
       /* menu.setOnAction(e->{
            if(e.getTarget().equals(menu.getEdit()))
                fireEvent(TeamBoardCommandEvent.editEmployee());
            else if(e.getTarget().equals(menu.getRemoveFromTeam())) {
                Alert popup = new Alert(Alert.AlertType.CONFIRMATION);
                popup.setTitle("Remove employee from team confirmation");
                popup.setContentText("Do you really want to remove '"+getItem().person().fullNameToString()+"' from team '"+teamName+"' ?");
                popup.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                popup.showAndWait()
                        .filter(r-> r.equals(ButtonType.OK))
                        .ifPresent(r->fireEvent(TeamBoardCommandEvent.of(new RemoveFromTeamEmployeeCommand(TeamName.of(teamName), getItem()))));
            }
        });*/
    }


    public EmployeeCell(TeamName teamName) {
        this();
        this.teamName=teamName.value();
    }

    @Override
    public void updateItem(Employee item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty)
            return;

        setId(idCell+item.employeeId().value());
        FullName fullName = item.person().fullName();
        this.setText(fullName.forename() + " " + fullName.name());

        setGraphic(null);
    }

    public String getTeamName() {
        return teamName;
    }
}

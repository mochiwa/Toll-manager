package graphic.components.team;

import graphic.form.team.RootTeamRegisterDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import tollmanager.application.command.CreateRootTeamCommand;

import java.io.IOException;

public class TeamButtonBox extends VBox {
    @FXML private Button buttonRemove;
    @FXML private Button buttonEdit;
    @FXML private Button buttonAppend;

    private ContextMenu menu;

    public TeamButtonBox() {
        initPane();
        iniContextMenu();
    }

    private void setDisableEditButtons(boolean value){
        buttonEdit.setDisable(value);
        buttonRemove.setDisable(value);
    }

    private void initPane() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("teamButtonBox.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void iniContextMenu() {
        menu=new ContextMenu();
        MenuItem createTeam=new MenuItem("create team");
        MenuItem createEmployee=new MenuItem("create employee");

        createEmployee.setOnAction(event -> fireEvent(TeamBoardCommandEvent.createEmployee()));
        createTeam.setOnAction(event ->{
            RootTeamRegisterDialog dialog=new RootTeamRegisterDialog();
            dialog.showAndWait().ifPresent(p->fireEvent(TeamBoardCommandEvent.of(new CreateRootTeamCommand(p.getKey(),null,p.getValue()))));
        });

        menu.getItems().add(createTeam);
        menu.getItems().add(createEmployee);
    }


    @FXML
    protected void handleEditSelected(MouseEvent e) {
        fireEvent(TeamBoardCommandEvent.editEmployee());

    }

    @FXML
    protected void handleAppendEmployee(MouseEvent e) {
        menu.show(buttonAppend,e.getScreenX(),e.getScreenY());
    }
}

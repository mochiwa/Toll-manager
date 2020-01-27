package graphic.form.employee.selectionMenu;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.transform.Rotate;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.team.Team;
import tollmanager.model.identity.team.TeamName;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class TeamSelectionMenuController implements Initializable {
    @FXML private GridPane mainPane;
    @FXML private MenuButton menu;
    //private ObservableSet<Team> teams;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        menu.setText("Team selection");
        mainPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("css/typeEmployee.css")).toExternalForm());
        mainPane.addEventFilter(RequestRootTeamObservableEvent.ROOT_TEAM_OBSERVABLE,e->setTeams(e.getQuery().getTeams()));
    }

    public void setTeams(ObservableSet<Team> teams){
        if(teams==null)
            return;
        menu.getItems().clear();
        teams.forEach(this::makeItem);
        initArrowRotate();
    }

    private void initArrowRotate(){
        Region arrow=(Region)  menu.lookup(".arrow");
        if(arrow==null)
            return;

        Rotate rotate=new Rotate();
        rotate.pivotXProperty().bind(arrow.widthProperty().divide(2.0));
        rotate.pivotYProperty().bind(arrow.heightProperty().divide(2.0));
        rotate.angleProperty().bind(
                Bindings.when(menu.showingProperty()).then(0.0).otherwise(90.0)
        );
        arrow.getTransforms().add(rotate);
    }

    private void makeItem(Team team) {
        MenuItem item = new MenuItem(team.name().value());
        item.setUserData(team);
        item.setOnAction(e -> {
            if (!menu.isDisable())
                menu.setText(item.getText());
        });
        menu.getItems().add(item);
        menu.setText(item.getText());
    }

    public void setTitleForEmployee(Employee employee){
        if(employee==null)
            return;
        menu.getItems().stream().filter(i -> {
            Team t = (Team) i.getUserData();
            return t.hasEmployee(employee);
        }).findFirst().ifPresent(item -> menu.setText(item.getText()));
    }

    public Team getTeamSelected() {
        if (menu.getText().trim().isEmpty())
            return null;
        MenuItem item = menu.getItems().stream().filter(i -> ((Team) i.getUserData()).name().equals(TeamName.of(menu.getText()))).findFirst().orElse(null);
        if(item==null)
            return null;
        return (Team) item.getUserData();
    }


    public void setVisible(boolean value) {
        mainPane.setVisible(value);
    }

    public void setDisable(boolean value) {
        mainPane.setDisable(value);
        mainPane.fireEvent(new RequestRootTeamObservableEvent());
    }
}

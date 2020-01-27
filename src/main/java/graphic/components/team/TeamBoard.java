package graphic.components.team;

import graphic.components.cell.employee.EmployeeCellFactory;
import graphic.components.event.AccessQueryEvent;
import graphic.components.searchPane.SearchPane;
import graphic.components.team.tree.TreeTeam;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import tollmanager.application.query.IsManagerQuery;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.team.Team;
import tollmanager.model.identity.team.search.SearchByIdentityInformation;
import tollmanager.model.identity.user.User;

import java.io.IOException;
import java.util.LinkedHashSet;

public class TeamBoard extends AnchorPane{
    @FXML protected AnchorPane searchBox;
    @FXML protected AnchorPane treePane;
    @FXML protected CheckBox checkAllInOne;
    @FXML protected TeamButtonBox buttonBox;
    private SearchPane<Employee> searchPane;
    private TreeTeam treeTeam;

    private ObservableSet<Team> teams;
    private SimpleObjectProperty<Employee> selectedEmployee;
    private SimpleObjectProperty<User> connectedUser;


    public TeamBoard(ObservableSet<Team> teams) {
        this.teams = teams;
        initPane();
        dealWithTree();
        initCheckBox();
        initSearchBox();
        initSelectedEmployeeProperty();
        initConnectedUserProperty();
    }

    private void initConnectedUserProperty() {
        connectedUser=new SimpleObjectProperty<>();
        connectedUser.addListener((observableValue, old, user) -> {
            if(user!=null)
                fireEvent(AccessQueryEvent.of(new IsManagerQuery(user)));
        });

        addEventHandler(AccessQueryEvent.IS_MANAGER,e->{
            buttonBox.setDisable(!e.getQuery().isManager());
            e.consume();
        });

    }

    private void initSelectedEmployeeProperty() {
        selectedEmployee=new SimpleObjectProperty<>();
        selectedEmployeeProperty().bindBidirectional(searchPane.getSelectedElement());
    }

    private void initPane() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("teamBoard.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initCheckBox() {
        checkAllInOne.selectedProperty().addListener(e ->treeTeam.loadAllEmployeeToRootTeam(checkAllInOne.isSelected()));
    }

    private void initSearchBox() {
        searchPane = new SearchPane<>( new EmployeeCellFactory(), new SearchByIdentityInformation());
        searchBox.getChildren().add(searchPane);
        addEventFilter(SearchPane.RequestElementSearchable.SEARCHABLE_ELEMENT,e->{
            LinkedHashSet<Employee> employees = new LinkedHashSet<>();
            teams.forEach(t -> employees.addAll(t.employees()));
            e.getQuery().setCollection(employees);
        });
    }

    public SimpleObjectProperty<Employee> selectedEmployeeProperty() {
        return selectedEmployee;
    }

    public User getConnectedUser() {
        return connectedUser.get();
    }

    public SimpleObjectProperty<User> connectedUserProperty() {
        return connectedUser;
    }



    private void dealWithTree(){
        treeTeam=new TreeTeam(teams);
        treePane.getChildren().add(treeTeam);
        treeTeam.prefHeightProperty().bind(treePane.heightProperty());
    }
}



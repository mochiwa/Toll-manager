package graphic;

import graphic.components.cell.employee.event.SelectedCellEmployeeEvent;
import graphic.components.event.AccessQueryEvent;
import graphic.components.planning.PlanningBoardController;
import graphic.components.planning.PlanningCommandEvent;
import graphic.components.planning.PlanningQueryEvent;
import graphic.components.team.TeamBoard;
import graphic.components.team.TeamBoardCommandEvent;
import graphic.form.FormQueryEvent;
import graphic.form.adminRegister.AdminRegistrationDialog;
import graphic.form.employee.EmployeeFormController;
import graphic.form.employee.event.EmployeeFormEvent;
import graphic.form.employee.selectionMenu.RequestRootTeamObservableEvent;
import graphic.form.employee.selectionMenu.RequestTypeEmployeeEvent;
import graphic.form.signIn.SignInDialog;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;
import tollmanager.application.JavaFXApplicationLifeTime;
import tollmanager.infrastructure.persistance.DatabaseConnection;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.team.Team;
import tollmanager.model.identity.user.password.Password;

import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

public class MainWindowController implements Initializable {
    @FXML private BorderPane mainWindow;
    @FXML private AnchorPane boardPane;
    @FXML private Tab tabPlanning;
    @FXML private PlanningBoardController planningBoardController;
    private TeamBoard teamBoard;
    @FXML private EmployeeFormController employeeFormController;
    private JavaFXApplicationLifeTime applicationLifeTime;



    public MainWindowController(JavaFXApplicationLifeTime javaFXApplicationLifeTime) {
        this.applicationLifeTime = javaFXApplicationLifeTime;
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainWindow.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("css/mainWindow.css")).toExternalForm());
        initTeamBoard();
        initPlanningPanel();
        applicationLifeTime.connectedUserProperty().addListener((observableValue, old, user) -> blurWindow(user==null));
        applicationLifeTime.selectedEmployeeProperty().addListener((o, old, employee) -> tabPlanning.setDisable(employee==null));
        blurWindow(true);
    }

    private void blurWindow(boolean value) {
        if (!value)
            mainWindow.setEffect(null);
        else {
            ColorAdjust adj = new ColorAdjust(0, -0.9, -0.5, 0);
            GaussianBlur blur = new GaussianBlur(20);
            adj.setInput(blur);
            mainWindow.setEffect(blur);
        }
    }

    private void initTeamBoard() {
        teamBoard = new TeamBoard(applicationLifeTime.getTeamsObservable());
        boardPane.getChildren().add(teamBoard);


        teamBoard.connectedUserProperty().bind(applicationLifeTime.connectedUserProperty());


        teamBoard.addEventFilter(SelectedCellEmployeeEvent.CELL_SELECTED, event -> {
            employeeFormController.selectedEmployeeProperty().set(event.getEmployeeSelected());
            applicationLifeTime.selectedTeamProperty().set(event.getTeam());
        });


        teamBoard.addEventFilter(TeamBoardCommandEvent.APPEND_SUB_TEAM, event -> {
            applicationLifeTime.execute(event.getCommand());
            event.consume();
        });
        teamBoard.addEventFilter(TeamBoardCommandEvent.REMOVE_SUB_TEAM, event -> {
            applicationLifeTime.execute(event.getCommand());
            event.consume();
        });

        teamBoard.addEventFilter(TeamBoardCommandEvent.CREATE_ROOT_TEAM, event -> {
            applicationLifeTime.execute(event.getCommand());
            event.consume();
        });
        teamBoard.addEventFilter(TeamBoardCommandEvent.REMOVE_ROOT_TEAM, event -> {
            applicationLifeTime.execute(event.getCommand());
            event.consume();
        });

        teamBoard.addEventFilter(TeamBoardCommandEvent.APPEND_EMPLOYEE, event -> {
            applicationLifeTime.execute(event.getCommand());
            event.consume();
        });
        teamBoard.addEventFilter(TeamBoardCommandEvent.REMOVE_EMPLOYEE, event -> {
            applicationLifeTime.execute(event.getCommand());
            event.consume();
        });
        teamBoard.addEventFilter(AccessQueryEvent.IS_MANAGER, event -> applicationLifeTime.query(event.getQuery()));

        teamBoard.addEventHandler(TeamBoardCommandEvent.CREATE_EMPLOYEE_ACTION, event -> {
            employeeFormController.selectedEmployeeProperty().set(null);
            employeeFormController.editForm();

        });
        teamBoard.addEventHandler(TeamBoardCommandEvent.EDIT_EMPLOYEE_ACTION, event -> {
            if (applicationLifeTime.getSelectedEmployee() != null)
                employeeFormController.editForm();
            event.consume();
        });

        teamBoard.addEventHandler(TeamBoardCommandEvent.DELETE_EMPLOYEE, event -> {
            applicationLifeTime.execute(event.getCommand());
            event.consume();
        });
    }


    private void initEmployeeForm() {
        mainWindow.addEventFilter(RequestTypeEmployeeEvent.TYPE_EMPLOYEE, event -> applicationLifeTime.query(event.getQuery()));
        mainWindow.addEventFilter(RequestRootTeamObservableEvent.ROOT_TEAM_OBSERVABLE, event -> applicationLifeTime.query(event.getQuery()));

        mainWindow.addEventFilter(FormQueryEvent.IS_EMAIL_USED, event -> applicationLifeTime.query(event.getQuery()));
        mainWindow.addEventFilter(FormQueryEvent.IS_NISS_USED, event -> applicationLifeTime.query(event.getQuery()));
        mainWindow.addEventFilter(FormQueryEvent.IS_PHONE_USED, event -> applicationLifeTime.query(event.getQuery()));
        mainWindow.addEventFilter(FormQueryEvent.IS_LOGIN_USED, event -> applicationLifeTime.query(event.getQuery()));

        mainWindow.addEventFilter(EmployeeFormEvent.CREATE_EMPLOYEE, event -> applicationLifeTime.execute(event.getCommand()));
        mainWindow.addEventFilter(EmployeeFormEvent.CREATE_MANAGER, event -> applicationLifeTime.execute(event.getCommand()));
        mainWindow.addEventFilter(EmployeeFormEvent.CREATE_TEAMLEADER, event -> applicationLifeTime.execute(event.getCommand()));
        mainWindow.addEventFilter(EmployeeFormEvent.EDIT_EMPLOYEE, event -> applicationLifeTime.execute(event.getCommand()));
    }

    private void initPlanningPanel() {
        planningBoardController.selectedEmployeeProperty().bind(applicationLifeTime.selectedEmployeeProperty());
        mainWindow.addEventFilter(PlanningQueryEvent.WEEK_PLANNING, event -> applicationLifeTime.execute(event.getQuery()));

        mainWindow.addEventFilter(PlanningCommandEvent.APPEND_PLANNING, e -> {
            applicationLifeTime.execute(e.getCommand());
            e.consume();
        });

        mainWindow.addEventFilter(PlanningCommandEvent.REMOVE_PLANNING, e -> {
            applicationLifeTime.execute(e.getCommand());
            planningBoardController.loadPlanning();
            e.consume();
        });

    }

    private void registerAdministrator() {
        AdminRegistrationDialog dialog = new AdminRegistrationDialog();
        dialog.showAndWait().ifPresentOrElse(admin -> {
            applicationLifeTime.initApplicationForFirstTime(admin);
            Pair<Password, Password> password = dialog.getPasswordAndConfirmation();
            applicationLifeTime.getUserService().changePassword(applicationLifeTime.connectedUser(), password.getKey(), password.getValue(), applicationLifeTime.getPasswordEncryptionService());
        }, Platform::exit);
    }

    protected void signIn() {
        SignInDialog dialog = new SignInDialog(applicationLifeTime.getAuthenticationService());
        dialog.showAndWait().ifPresentOrElse(user -> {
            applicationLifeTime.initApplication(user);
        }, Platform::exit);
    }

    public void start() {
        if (applicationLifeTime.isFirstLaunch())
            registerAdministrator();
        else
         signIn();
        initEmployeeForm();
        teamBoard.selectedEmployeeProperty().bindBidirectional(employeeFormController.selectedEmployeeProperty());
        teamBoard.selectedEmployeeProperty().bindBidirectional(applicationLifeTime.selectedEmployeeProperty());
    }

    @FXML
    protected void onLogoutAction(ActionEvent event) {
        applicationLifeTime.logout();
        start();
    }

}

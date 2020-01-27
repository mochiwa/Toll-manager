package graphic.form.employee;

import graphic.form.person.PersonFormController;
import graphic.form.employee.event.EmployeeFormEvent;
import graphic.form.employee.selectionMenu.TeamSelectionMenuController;
import graphic.form.employee.selectionMenu.TypeEmployeeMenuController;
import graphic.form.userForm.UserFormController;
import javafx.beans.property.SimpleObjectProperty;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import tollmanager.application.command.CreateEmployeeCommand;
import tollmanager.application.command.CreateManagerCommand;
import tollmanager.application.command.CreateTeamLeaderCommand;
import tollmanager.application.command.EditEmployeeCommand;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.person.Person;
import tollmanager.model.identity.team.Team;
import tollmanager.model.identity.user.*;
import tollmanager.model.identity.user.password.Password;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class EmployeeFormController implements Initializable {
    @FXML private VBox mainPane;
    @FXML private VBox buttonBox;
    @FXML private Button validateButton;
    @FXML private PersonFormController personFormController;
    @FXML private VBox formBox;
    @FXML private Text errorText;
    @FXML private TypeEmployeeMenuController typeEmployeeMenuController;
    @FXML private TeamSelectionMenuController teamSelectionMenuController;
    @FXML private UserFormController userFormController;
    private SimpleObjectProperty<Employee> selectedEmployee;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initTypeEmployeeMenu();
        initEmployeeSelectedProperty();

        userFormController.setVisible(false);
        setEditable(false);
        mainPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("css/employeeForm.css")).toExternalForm());
    }

    private void setEditable(boolean isEditable){
        personFormController.setEditable(isEditable);
        typeEmployeeMenuController.setDisable(!isEditable);
        buttonBox.setDisable(!isEditable);
        teamSelectionMenuController.setDisable(!isEditable);
        userFormController.setEditable(isEditable);
        mainPane.setDisable(!isEditable);
        buttonBox.setVisible(isEditable);
    }

    private void initEmployeeSelectedProperty() {
        selectedEmployee = new SimpleObjectProperty<>();
        typeEmployeeMenuController.selectedEmployeeProperty().bind(selectedEmployee);

        selectedEmployee.addListener(observable -> clearForm());
        selectedEmployee.addListener((observableValue, old, employee) -> {
            fillFormWithSelectedEmployee();
            teamSelectionMenuController.setTitleForEmployee(employee);
            setEditable(false);
        });
    }

    private void clearForm() {
        personFormController.clearForm();
        userFormController.clearForm();
        userFormController.setVisible(false);
    }

    private void fillFormWithSelectedEmployee() {
        clearForm();
        if (selectedEmployee() != null)
            personFormController.fillPerson(selectedEmployee().person());
    }


    private void initTypeEmployeeMenu() {
        typeEmployeeMenuController.getMenu().textProperty().addListener((observableValue, s, text) -> {
            teamSelectionMenuController.setVisible(false);
            userFormController.setVisible(false);
            if (text.equals("manager") && selectedEmployee.get() == null)
                userFormController.setVisible(true);
            if (text.equals("employee"))
                teamSelectionMenuController.setVisible(true);
            else if (text.equals("teamleader")) {
                teamSelectionMenuController.setVisible(true);
                if (selectedEmployee.get() == null)
                    userFormController.setVisible(true);
            }
        });
    }

    @FXML
    protected void handleCancelButton(MouseEvent event) {
        fillFormWithSelectedEmployee();
        setEditable(false);
    }

    @FXML
    protected void handleValidateButton(MouseEvent event) {
        Login login = null;
        Password password = null;
        Team team;
        Person person;

        try {
            person = registerPerson();
            team = teamSelectionMenuController.getTeamSelected();
            if (isNeedAccount() && userFormController.isVisible()) {
                if(!userFormController.isValid())
                    return;
                login = userFormController.getLogin();
                password = userFormController.getPassword();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        System.err.println("passage------------------------->Employee form controller on niss already used ?");

        if (selectedEmployee()==null) {
            if (!isTeamLeader() && !isManager())
                mainPane.fireEvent(EmployeeFormEvent.of(new CreateEmployeeCommand(person, team)));
            else if (isManager())
                mainPane.fireEvent(EmployeeFormEvent.of(new CreateManagerCommand(person, login, password)));
            else {
                if (team == null || !replaceLeaderConfirmationPopup())
                    return;
                mainPane.fireEvent(EmployeeFormEvent.of(new CreateTeamLeaderCommand(person, login, password, team)));
            }
            clearForm();
        } else
            mainPane.fireEvent(EmployeeFormEvent.of(new EditEmployeeCommand(selectedEmployee.get(), person)));
        setEditable(false);
    }

    private Person registerPerson() throws RuntimeException {
        Person person=personFormController.getPerson();
        if (person==null)
            throw new RuntimeException("Could not return a person.");
        return  personFormController.getPerson();
    }

    private boolean isManager() {
        return typeEmployeeMenuController.isType("manager");
    }
    private boolean isTeamLeader() {
        return typeEmployeeMenuController.isType("teamleader");
    }
    private boolean isNeedAccount() {
        return isManager() || isTeamLeader();
    }

    public void editForm(){
        clearForm();
        typeEmployeeMenuController.refresh();
        fillFormWithSelectedEmployee();
        setEditable(true);

    }

    private boolean replaceLeaderConfirmationPopup() {
        Text underLinePart=new Text(" stay on team like a member !\n");
        underLinePart.setUnderline(true);
        Text before=new Text("A team leader is already present for this team,\nthe old manager will");
        Text confirm=new Text("Replace ?");
        TextFlow textFlow=new TextFlow();
        textFlow.getChildren().addAll(before,underLinePart,confirm);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getDialogPane().setContent(textFlow);
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    public Employee selectedEmployee() {
        return selectedEmployee.get();
    }

    public SimpleObjectProperty<Employee> selectedEmployeeProperty() {
        return selectedEmployee;
    }
}

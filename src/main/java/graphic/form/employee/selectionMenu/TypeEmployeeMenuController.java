package graphic.form.employee.selectionMenu;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.transform.Rotate;
import tollmanager.model.access.Group;
import tollmanager.model.access.GroupName;
import tollmanager.model.identity.Employee;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class TypeEmployeeMenuController implements Initializable {
    @FXML private GridPane mainPane;
    @FXML private MenuButton menu;
    private MenuItem defaultItem;
    private ObservableSet<Group> types;
    private SimpleObjectProperty<Employee> selectedEmployee;

    public TypeEmployeeMenuController(){
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initMenuBehavior();
        initDefaultMenuItem();
        initSelectedEmployee();
        initTypeList();

        mainPane.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("css/typeEmployee.css")).toExternalForm());

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

    private void initMenuBehavior(){
        menu.setOnMouseClicked(e->{
            if(menu.isDisable())
                menu.hide();
        });
        mainPane.addEventFilter(RequestTypeEmployeeEvent.TYPE_EMPLOYEE,e->types.addAll(e.getQuery().getTypes()));
    }


    private void initDefaultMenuItem() {
        defaultItem=new MenuItem("employee");
        defaultItem.setOnAction(e->menu.setText(defaultItem.getText()));
        menu.getItems().add(defaultItem);
        setTitle(GroupName.Null());
    }
    private void makeMenuItem(Group group){
        MenuItem item=new MenuItem(group.name().value());
        item.setUserData(group);
        item.setOnAction(e->menu.setText(item.getText()));
        menu.getItems().add(item);
    }
    private void initTypeList(){
        types=FXCollections.observableSet();
        types.addListener((SetChangeListener<Group>) change -> {
            if(change.wasAdded())
                makeMenuItem(change.getElementAdded());
            else if(change.wasRemoved())
                menu.getItems().removeIf(i->i.getUserData().equals(change.getElementRemoved()));
        });
    }


    private void initSelectedEmployee() {
        selectedEmployee=new SimpleObjectProperty<>();

        selectedEmployee.addListener((observableValue, old, employee) -> {
            mainPane.fireEvent(new RequestTypeEmployeeEvent());
            setTitle(GroupName.Null());
            if(employee==null)
                return;

            types.stream()
                    .filter(g -> g.hasMember(employee.employeeId()))
                    .findFirst()
                    .ifPresent(group -> setTitle(group.name()));
        });
    }

    private void setTitle(GroupName employeeType) {
        menu.setText((menu.getItems()
                .stream()
                .filter(i->i.getText().equals(employeeType.value()))
                .findFirst().orElse(defaultItem)
        ).getText());
    }

    public void refresh(){
        mainPane.fireEvent(new RequestTypeEmployeeEvent());
        initArrowRotate();
    }
    public boolean isType(String type) {
        return menu.getText().equals(type);
    }
    public SimpleObjectProperty<Employee> selectedEmployeeProperty() {
        return selectedEmployee;
    }
    public MenuButton getMenu(){
        return menu;
    }

    public void setDisable(boolean value) {
        mainPane.setDisable(value);
    }
}

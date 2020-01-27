package graphic.components.planning;

import graphic.components.planning.DayPlanningView.DayPlanningView;
import graphic.components.planning.cell.CellHour;
import graphic.components.planning.cell.PlanningCell;
import graphic.components.planning.cell.PlanningCellModel;
import graphic.components.planning.popup.AppendPlanningDialog;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import tollmanager.application.query.WeekPlanningQuery;
import tollmanager.model.identity.Employee;
import tollmanager.model.planning.Planning;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

public class PlanningBoardController implements Initializable {
    @FXML  private Label monthTitle;
    @FXML  private AnchorPane mainPane;
    @FXML  private HBox boxPlanning;
    private SimpleObjectProperty<Employee> selectedEmployee;
    private SimpleObjectProperty<LocalDate> currentDateProperty;


    public PlanningBoardController() {
        selectedEmployee = new SimpleObjectProperty<>();
        currentDateProperty=new SimpleObjectProperty<LocalDate>(LocalDate.now());
        currentDateProperty.addListener((o) -> loadPlanningForWeek());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initHourColumn();
        selectedEmployee.addListener((observableValue) -> loadPlanningForWeek());
        selectedEmployee.addListener((observable -> currentDateProperty.set(LocalDate.now())));
        mainPane.addEventHandler(PlanningQueryEvent.WEEK_PLANNING,e->fillTable(e.getQuery()) );
    }

    private void loadPlanningForWeek() {
        if (getSelectedEmployee() == null)
            return;
        mainPane.fireEvent(PlanningQueryEvent.of(new WeekPlanningQuery(getSelectedEmployee().employeeId(), getCurrentDate())));
        monthTitle.setText(getCurrentDate().getMonth().name());
    }

    private void fillTable(WeekPlanningQuery query){
        boxPlanning.getChildren().clear();
        initHourColumn();
        query.getWeekPlanning().forEach((k, v) -> {
            List<PlanningCell> list = new ArrayList<>();
            Stream.iterate(0, i -> i + 1).limit(24).forEach(i -> list.add(new PlanningCellModel(i, v)));
            DayPlanningView column = new DayPlanningView(k.getDayOfWeek().name()+" "+k.getDayOfMonth(), list);
            boxPlanning.getChildren().add(column);
            if (k.getDayOfWeek().getValue() == 7)
                column.getStyleClass().add("lastColumn");
        });
    }

    private void initHourColumn() {
        ArrayList<PlanningCell> hours = new ArrayList<>();
        Stream.iterate(0, i -> i + 1).limit(24).forEach(i -> hours.add(new CellHour(i)));
        DayPlanningView hourColumn = new DayPlanningView("", hours);
        boxPlanning.getChildren().add(hourColumn);
        hourColumn.getStyleClass().add("firstColumn");
    }


    private Employee getSelectedEmployee() {
        return selectedEmployee.get();
    }

    public SimpleObjectProperty<Employee> selectedEmployeeProperty() {
        return selectedEmployee;
    }


    public void onClickedPreviousWeek(ActionEvent event) {
        currentDateProperty.set(getCurrentDate().minusDays(7));
    }

    public void onClickedNextWeek(ActionEvent event) {
        currentDateProperty.set(getCurrentDate().plusDays(7));
    }

    public LocalDate getCurrentDate() {
        return currentDateProperty.get();
    }

    public void onClickedAppendPlanning(ActionEvent event) {
        if(getSelectedEmployee()==null)
            return;
        AppendPlanningDialog dialog=new AppendPlanningDialog();
        dialog.getDialogPane().addEventFilter(PlanningCommandEvent.APPEND_PLANNING,e->mainPane.fireEvent(e));
        Planning planning=dialog.showAndWait().orElse(Planning.Null());
        if(!planning.equals(Planning.Null()))
            currentDateProperty.set(planning.dayBeginningDate());
    }

    public void loadPlanning() {
        loadPlanningForWeek();
    }
}
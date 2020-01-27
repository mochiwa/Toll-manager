package graphic.components.planning.popup;

import graphic.components.planning.PlanningCommandEvent;
import graphic.components.planning.cell.PlanningCell;
import graphic.components.popupDialog.PopupDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.Callback;
import tollmanager.application.command.AppendPlanningCommand;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.team.TeamId;
import tollmanager.model.planning.Planning;
import tollmanager.model.planning.PlanningId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class AppendPlanningDialog extends PopupDialog<Planning> {
    private @FXML DatePicker daySelector;
    private @FXML Spinner<Integer> beginHourSelector;
    private @FXML Spinner<Integer> endHourSelector;
    private @FXML TextArea commentTextArea;

    public AppendPlanningDialog(){
        super();
        try{
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(AppendPlanningDialog.class.getResource("appendPlanningDialog.fxml"));
            loader.setController(this);
            docker.getChildren().add(loader.load());
            docker.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("css/adminRegistrationDialog.css")).toExternalForm());
        }catch (Exception e){
            e.printStackTrace();
        }
        initSpinner();
        initDaySelector();
    }

    private void initDaySelector() {
        daySelector.setDayCellFactory(new Callback<>() {
            @Override
            public DateCell call(DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty || item.compareTo(LocalDate.now()) < 0) {
                            setText("");
                            setStyle("");
                            setDisable(true);
                        }
                    }
                };
            }
        });
    }

    private void initSpinner(){
        beginHourSelector.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,23));
        beginHourSelector.getEditor().setTextFormatter(new TextFormatter<Object>(new HourTextFormatter()));

        beginHourSelector.valueProperty().addListener((o,old,value)->{
            if(value>endHourSelector.getValue())
                beginHourSelector.decrement();
        });
        endHourSelector.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,23));
        endHourSelector.getEditor().setTextFormatter(new TextFormatter<Object>(new HourTextFormatter()));
        endHourSelector.getEditor().setText("23");
        endHourSelector.increment();
        endHourSelector.valueProperty().addListener((o,old,value)->{
            if(value<beginHourSelector.getValue())
                endHourSelector.increment();
        });

    }

    @Override
    protected Planning getUserData() {
        LocalDate day=daySelector.getValue();
        if(day==null)
            return null;
        LocalDateTime begin=LocalDateTime.of(day, LocalTime.of(beginHourSelector.getValue(),0));
        LocalDateTime end=LocalDateTime.of(day, LocalTime.of(endHourSelector.getValue(),0));
        mainPane.fireEvent(PlanningCommandEvent.of(new AppendPlanningCommand(begin,end,commentTextArea.getText())));
        return Planning.of(PlanningId.Null(),begin,end,commentTextArea.getText(), EmployeeId.Null(), TeamId.Null());
    }
}

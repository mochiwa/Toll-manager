package graphic.components.planning.DayPlanningView;

import graphic.components.planning.PlanningCommandEvent;
import graphic.components.planning.cell.PlanningCell;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.FocusModel;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.AnchorPane;
import tollmanager.model.planning.Planning;

import java.util.List;
import java.util.Objects;


public class DayPlanningView extends AnchorPane {
    @FXML private Label title;
    @FXML private ListView<PlanningCell> list;


    public DayPlanningView(String title, List<PlanningCell> list) {
        Objects.requireNonNull(list);
        loadPanel();
        getStyleClass().add("column");
        this.title.setText(title);
        this.list.getItems().addAll(list);
        this.list.setCellFactory(planningCellListView -> planningCellListView.getItems().get(0).getListOfCell());

        initBehaviorOfListSelection();
        initFocusedProperty();
    }

    private void initFocusedProperty() {
        list.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if(!t1)
             list.getSelectionModel().clearSelection();
        });

    }

    private void loadPanel() {
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("dayPlanningView.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
            getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("css/dayPlanning.css")).toExternalForm());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initBehaviorOfListSelection(){
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        list.getSelectionModel().selectedItemProperty().addListener((observableValue) -> {
            PlanningCell selectedCell=list.getSelectionModel().getSelectedItem();
            if(selectedCell==null)
                return;
            Planning selected=selectedCell.getPlanning();

            list.getItems().forEach(cell->{
                if(!cell.isWorkingHour())
                    return;
                if(cell.getPlanning().planningId().equals(selected.planningId())) {
                     list.getSelectionModel().select(cell);
                    list.getStyleClass().add("planning-selected");
                }
            });
        });

    }

    public ListView<PlanningCell> getList() {
        return list;
    }

}

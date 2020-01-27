package graphic.components.planning.cell;

import graphic.components.planning.PlanningCommandEvent;
import graphic.components.planning.popup.ShowInformationPlanningDialog;
import javafx.scene.control.*;
import tollmanager.application.command.RemovePlanningCommand;
import tollmanager.model.planning.Planning;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Set;

public class PlanningCellModel implements PlanningCell{
    private int hour;
    private Set<Planning> planningsOfDay;
    private ContextMenu menu;

    public PlanningCellModel(int hour, Set<Planning> planningsOfDay) {
        this.hour = hour;
        this.planningsOfDay = planningsOfDay;
    }


    @Override
    public String getText() {
        return String.valueOf(isWorkingHour());
    }

    @Override
    public boolean isWorkingHour() {
         return planningsOfDay.stream().filter(p->p.isHourBetweenPlanning(hour)).findFirst().orElse(null)!=null;
    }

    @Override
    public ListCell<PlanningCell> getListOfCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(PlanningCell item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                    setText("");
                    return;
                }
                PlanningCellModel cell=(PlanningCellModel) item;
                if(item.isWorkingHour()) {
                    getStyleClass().add("workingCell");
                }
                else
                    setStyle("-fx-background-color:red");

                if(cell.getHour()==0)
                    getStyleClass().add("firstCell");
                else if(cell.getHour()==23)
                    getStyleClass().add("lastCell");

                setText("");
                if(item.isWorkingHour()) {
                    setContextMenu(cell.getContextMenu());
                    getContextMenu().addEventFilter(PlanningCommandEvent.REMOVE_PLANNING,this::fireEvent);
                }
            }
        };
        }

    @Override
    public Planning getPlanning() {
        return planningsOfDay.stream().filter(p->p.beginning().getHour()==hour || p.ending().getHour()==hour || p.isHourBetweenPlanning(hour)).findFirst().orElse(Planning.Null());
    }

    private ContextMenu getContextMenu(){
        menu=new ContextMenu();
        MenuItem showComment=new MenuItem("Show information");
        showComment.setOnAction(e->showCommentPopup());
        menu.getItems().add(showComment);

        MenuItem remove=new MenuItem("Remove planning");
        remove.setOnAction(e->confirmationDeletePlanning());
        menu.getItems().add(remove);
        remove.setDisable(planningsOfDay.stream().noneMatch(p->p.beginning().isAfter(ChronoLocalDateTime.from(LocalDateTime.now()))));
        return menu;
    }

    private void showCommentPopup(){
        new ShowInformationPlanningDialog(getPlanning()).showAndWait();
    }

    private void confirmationDeletePlanning(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Team Confirmation");
        alert.setContentText("Do you really want to delete the planning ?");

        if(alert.showAndWait().filter(response->response.equals(ButtonType.OK)).isPresent())
            menu.fireEvent(PlanningCommandEvent.of(new RemovePlanningCommand(getPlanning())));
    }


    private int getHour() {
        return hour;
    }

}

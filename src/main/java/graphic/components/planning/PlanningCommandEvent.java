package graphic.components.planning;

import graphic.components.event.CommandEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import tollmanager.application.command.AppendPlanningCommand;
import tollmanager.application.command.RemovePlanningCommand;

public class PlanningCommandEvent {
    public static final EventType<AppendPlanningEvent> APPEND_PLANNING=new EventType<>("PLANNING_CONTROLLER-APPEND_PLANNING");
    public static final EventType<RemovePlanningEvent> REMOVE_PLANNING=new EventType<>("PLANNING_CONTROLLER-REMOVE_PLANNING");

    public static CommandEvent of(AppendPlanningCommand command){
        return new AppendPlanningEvent(command);
    }

    public static CommandEvent of(RemovePlanningCommand command) { return new RemovePlanningEvent(command); }

    public static class AppendPlanningEvent extends CommandEvent<AppendPlanningCommand> {
        private AppendPlanningEvent(AppendPlanningCommand command){
            super(APPEND_PLANNING);
            setCommand(command);
        }
    }

    public static class RemovePlanningEvent extends CommandEvent<RemovePlanningCommand> {
        private RemovePlanningEvent(RemovePlanningCommand command){
            super(REMOVE_PLANNING);
            setCommand(command);
        }
    }

}

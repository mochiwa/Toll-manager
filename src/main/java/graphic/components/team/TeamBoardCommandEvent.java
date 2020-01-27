package graphic.components.team;

import graphic.components.event.CommandEvent;
import javafx.event.Event;
import javafx.event.EventType;
import tollmanager.application.command.*;

public class TeamBoardCommandEvent {
    public static final EventType<AppendSubTeamEvent> APPEND_SUB_TEAM=new EventType<>("TEAMBOARD-EVENT_APPEND_SUB_TEAM");
    public static final EventType<RemoveSubTeamEvent> REMOVE_SUB_TEAM=new EventType<>("TEAMBOARD-EVENT_REMOVE_SUB_TEAM");
    public static final EventType<CreateRootTeamEvent> CREATE_ROOT_TEAM=new EventType<>("TEAMBOARD-EVENT_CREATE_ROOT_TEAM");
    public static final EventType<RemoveRootTeamEvent> REMOVE_ROOT_TEAM=new EventType<>("TEAMBOARD-EVENT_REMOVE_ROOT_TEAM");

    public static final EventType<ControlPanelEvent> CREATE_EMPLOYEE_ACTION =new EventType<>("TEAMBOARD-EVENT_CREATE_EMPLOYEE_ACTION");
    public static final EventType<ControlPanelEvent> EDIT_EMPLOYEE_ACTION =new EventType<>("TEAMBOARD-EVENT_EDIT_EMPLOYEE_ACTION");

    public static final EventType<AppendToTeamEmployeeEvent> APPEND_EMPLOYEE=new EventType<>("TEAMBOARD-EVENT_APPEND_EMPLOYEE");
    public static final EventType<RemoveFromTeamEmployeeEvent> REMOVE_EMPLOYEE=new EventType<>("TEAMBOARD-EVENT_REMOVE_EMPLOYEE");
    public static final EventType<DeleteEmployeeEvent> DELETE_EMPLOYEE=new EventType<>("TEAMBOARD-EVENT_DELETE_EMPLOYEE");


    public static AppendSubTeamEvent of(AppendSubTeamCommand command){
        return new AppendSubTeamEvent(command);
    }
    public static RemoveSubTeamEvent of(RemoveSubTeamCommand command){
        return new RemoveSubTeamEvent(command);
    }
    public static CreateRootTeamEvent of(CreateRootTeamCommand command) {
        return new CreateRootTeamEvent(command);
    }
    public static RemoveRootTeamEvent of(RemoveRootTeamCommand command) {
        return new RemoveRootTeamEvent(command);
    }

    public static ControlPanelEvent createEmployee(){
        return new ControlPanelEvent(CREATE_EMPLOYEE_ACTION);
    }
    public static ControlPanelEvent editEmployee(){
        return new ControlPanelEvent(EDIT_EMPLOYEE_ACTION);
    }

    public static Event of(RemoveFromTeamEmployeeCommand command) {
        return new RemoveFromTeamEmployeeEvent(command);
    }
    public static Event of(AppendEmployeeToTeamCommand appendEmployeeToTeamCommand) {
        return new AppendToTeamEmployeeEvent(appendEmployeeToTeamCommand);
    }

    public static DeleteEmployeeEvent of(DeleteEmployeeCommand command) {
        return new DeleteEmployeeEvent(command);
    }


    public static class AppendSubTeamEvent extends CommandEvent<AppendSubTeamCommand> {
        private AppendSubTeamEvent(AppendSubTeamCommand command){
            super(APPEND_SUB_TEAM);
            setCommand(command);
        }
    }
    public static class RemoveSubTeamEvent extends CommandEvent<RemoveSubTeamCommand> {
        private RemoveSubTeamEvent(RemoveSubTeamCommand command) {
            super(REMOVE_SUB_TEAM);
            setCommand(command);
        }
    }

    public static class CreateRootTeamEvent extends CommandEvent<CreateRootTeamCommand> {
        private CreateRootTeamEvent(CreateRootTeamCommand command) {
            super(CREATE_ROOT_TEAM);
            setCommand(command);
        }
    }
    public static class RemoveRootTeamEvent extends CommandEvent<RemoveRootTeamCommand> {
        private RemoveRootTeamEvent(RemoveRootTeamCommand command) {
            super(REMOVE_ROOT_TEAM);
            setCommand(command);
        }
    }

    public static class ControlPanelEvent extends Event {
        private ControlPanelEvent(EventType<? extends Event> eventType) {
            super(eventType);
        }
    }
    public static class AppendToTeamEmployeeEvent extends CommandEvent<AppendEmployeeToTeamCommand>{
        private AppendToTeamEmployeeEvent(AppendEmployeeToTeamCommand command){
            super(APPEND_EMPLOYEE);
            setCommand(command);
        }
    }

    public static class RemoveFromTeamEmployeeEvent extends CommandEvent<RemoveFromTeamEmployeeCommand>{
        public RemoveFromTeamEmployeeEvent(RemoveFromTeamEmployeeCommand command) {
            super(REMOVE_EMPLOYEE);
            setCommand(command);
        }
    }

    public static class DeleteEmployeeEvent extends CommandEvent<DeleteEmployeeCommand> {
        private DeleteEmployeeEvent(DeleteEmployeeCommand command){
            super(DELETE_EMPLOYEE);
            setCommand(command);
        }
    }

}

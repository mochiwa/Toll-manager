package graphic.form.employee.event;

import graphic.components.event.CommandEvent;
import javafx.event.Event;
import javafx.event.EventType;
import tollmanager.application.command.CreateEmployeeCommand;
import tollmanager.application.command.CreateManagerCommand;
import tollmanager.application.command.CreateTeamLeaderCommand;
import tollmanager.application.command.EditEmployeeCommand;

public class EmployeeFormEvent {
    public static final EventType<CreateEmployeeEvent> CREATE_EMPLOYEE=new EventType<>("EMPLOYEE_FORM-CREATE_EMPLOYEE");
    public static final EventType<CreateManagerEvent> CREATE_MANAGER=new EventType<>("EMPLOYEE_FORM-CREATE_MANAGER");
    public static final EventType<CreateTeamLeaderEvent> CREATE_TEAMLEADER=new EventType<>("EMPLOYEE_FORM-CREATE_TEAMLEADER");
    public static final EventType<EditEmployeeEvent> EDIT_EMPLOYEE=new EventType<>("EMPLOYEE_FORM-EDIT_EMPLOYEE");



    public static CreateEmployeeEvent of(CreateEmployeeCommand command){return new CreateEmployeeEvent(command);}

    public static Event of(CreateManagerCommand command) {
        return new CreateManagerEvent(command);
    }

    public static Event of(CreateTeamLeaderCommand command) {
        return new CreateTeamLeaderEvent(command);
    }

    public static Event of(EditEmployeeCommand command) {
        return new EditEmployeeEvent(command);
    }


    public static class CreateEmployeeEvent extends CommandEvent<CreateEmployeeCommand> {
        private CreateEmployeeEvent(CreateEmployeeCommand command) {
            super(CREATE_EMPLOYEE);
            setCommand(command);
        }
    }

    public static class CreateManagerEvent extends CommandEvent<CreateManagerCommand> {
        public CreateManagerEvent(CreateManagerCommand command) {
            super(CREATE_MANAGER);
            setCommand(command);
        }
    }

    public static class CreateTeamLeaderEvent extends CommandEvent<CreateTeamLeaderCommand> {

        public CreateTeamLeaderEvent(CreateTeamLeaderCommand command) {
            super(CREATE_TEAMLEADER);
            setCommand(command);
        }
    }
    public static class EditEmployeeEvent extends CommandEvent<EditEmployeeCommand> {

        public EditEmployeeEvent(EditEmployeeCommand command) {
            super(EDIT_EMPLOYEE);
            setCommand(command);
        }

    }
}

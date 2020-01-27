package graphic.form.employee.selectionMenu;

import graphic.components.event.QueryEvent;
import javafx.event.Event;
import javafx.event.EventType;
import tollmanager.application.query.TypeEmployeeQuery;

public class RequestTypeEmployeeEvent extends QueryEvent<TypeEmployeeQuery> {
    public static final EventType<RequestTypeEmployeeEvent> TYPE_EMPLOYEE = new EventType<>("REQUEST_TYPE_EMPLOYEE_EVENT");

    public RequestTypeEmployeeEvent() {
        super(TYPE_EMPLOYEE);
        setQuery(new TypeEmployeeQuery());
    }
}

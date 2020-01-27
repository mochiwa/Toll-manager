package graphic.form.employee.selectionMenu;

import graphic.components.event.QueryEvent;
import javafx.event.Event;
import javafx.event.EventType;
import tollmanager.application.query.TypeEmployeeQuery;

public class RequestRootTeamObservableEvent extends QueryEvent<RequestRootTeamObservable> {
    public static final EventType<RequestRootTeamObservableEvent> ROOT_TEAM_OBSERVABLE = new EventType<>("REQUEST_ROOT_TEAM_OBSERVABLE");

    public RequestRootTeamObservableEvent() {
        super(ROOT_TEAM_OBSERVABLE);
        setQuery(new RequestRootTeamObservable());
    }
}

package graphic.components.event;

import javafx.event.Event;
import javafx.event.EventType;
import tollmanager.application.query.IsManagerQuery;

public class AccessQueryEvent{
    public static final EventType<IsManagerQueryEvent> IS_MANAGER=new EventType<>("ACCESS-QUERY-EVENT_IS_MANAGER");



    public static Event of(IsManagerQuery isManagerQuery) {
        return new IsManagerQueryEvent(isManagerQuery);
    }


    public static class IsManagerQueryEvent extends QueryEvent<IsManagerQuery> {
        private IsManagerQueryEvent(IsManagerQuery query){
            super(IS_MANAGER);
            setQuery(query);
        }
    }
}

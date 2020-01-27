package graphic.components.event;

import javafx.event.Event;
import javafx.event.EventType;

public class QueryEvent <T> extends Event {
    private T query;

    public QueryEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }
    public QueryEvent(EventType<? extends Event> eventType,T query) {
        super(eventType);setQuery(query);
    }


    public void setQuery(T query){
        this.query=query;
    }

    public T getQuery(){
        return query;
    }
}

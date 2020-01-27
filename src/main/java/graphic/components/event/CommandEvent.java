package graphic.components.event;

import javafx.event.Event;
import javafx.event.EventType;

public abstract class CommandEvent<T> extends Event {
    protected static final EventType ANY_COMMAND_EVENT=new EventType<>(" ANY_COMMAND_EVENT");
    private T command;

    public CommandEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

    public void setCommand(T command){
        this.command=command;
    }

    public T getCommand(){
        return command;
    }
}

package tollmanager.model.shared;

public interface Observable {
    void registry(Observer observer);
    void unregister(Observer observer);
    void updateAll();
}

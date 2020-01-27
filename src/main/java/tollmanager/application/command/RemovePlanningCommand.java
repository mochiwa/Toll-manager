package tollmanager.application.command;

import tollmanager.model.planning.Planning;
/**
 * Represents the use case : delete a planning
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class RemovePlanningCommand {
    private Planning planning;

    public RemovePlanningCommand(Planning planning) {
        this.planning= planning;
    }

    public Planning getPlanning() {
        return planning;
    }

    public void setPlanning(Planning planning) {
        this.planning = planning;
    }
}

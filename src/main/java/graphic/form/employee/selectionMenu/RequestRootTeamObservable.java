package graphic.form.employee.selectionMenu;

import javafx.collections.ObservableSet;
import tollmanager.model.identity.team.Team;

public class RequestRootTeamObservable {
    private ObservableSet<Team> teams;

    public RequestRootTeamObservable() {
    }

    public ObservableSet<Team> getTeams() {
        return teams;
    }

    public void setTeams(ObservableSet<Team> teams) {
        this.teams = teams;
    }
}

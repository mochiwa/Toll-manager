package tollmanager.application.command;

import tollmanager.model.identity.team.Team;
import tollmanager.model.identity.user.User;
/**
 * Represents the use case : remove a sub team
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class RemoveSubTeamCommand {
    private Team team;

    public RemoveSubTeamCommand(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}

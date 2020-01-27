package tollmanager.application.command;

import tollmanager.model.identity.team.Team;
import tollmanager.model.identity.user.User;
/**
 * Represents the use case : remove a root team
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class RemoveRootTeamCommand {
    private Team team;


    public RemoveRootTeamCommand(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}

package tollmanager.application.command;

import tollmanager.model.identity.team.Team;
import tollmanager.model.identity.team.TeamName;
import tollmanager.model.identity.user.User;
/**
 * Represents the use case : append a sub team to a team
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class AppendSubTeamCommand {
    private TeamName subTeamName;
    private Team parentTeam;


    public AppendSubTeamCommand(Team parentTeam, String subTeamName) {
        this.parentTeam =parentTeam;
        this.subTeamName = TeamName.of(subTeamName);
    }
    public TeamName getSubTeamName() {
        return subTeamName;
    }

    public void setSubTeamName(TeamName subTeamName) {
        this.subTeamName = subTeamName;
    }

    public Team getParentTeam() {
        return parentTeam;
    }

    public void setParentTeam(Team parentTeam) {
        this.parentTeam = parentTeam;
    }
}

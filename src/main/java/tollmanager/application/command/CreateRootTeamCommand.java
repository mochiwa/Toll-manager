package tollmanager.application.command;

import tollmanager.model.identity.team.TeamName;
import tollmanager.model.identity.user.User;
/**
 * Represents the use case : create a new root team
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class CreateRootTeamCommand {
    private TeamName teamName;
    private User leader;
    private String description;


    public CreateRootTeamCommand( TeamName teamName, User leader, String description) {
        this.teamName = teamName;
        this.leader = leader;
        this.description = description;
    }


    public TeamName getTeamName() {
        return teamName;
    }

    public void setTeamName(TeamName teamName) {
        this.teamName = teamName;
    }

    public User getLeader() {
        return leader;
    }

    public void setLeader(User leader) {
        this.leader = leader;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

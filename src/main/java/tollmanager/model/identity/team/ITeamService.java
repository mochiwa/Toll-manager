package tollmanager.model.identity.team;

import tollmanager.model.identity.Employee;
import tollmanager.model.identity.user.User;

import java.util.LinkedHashSet;

public interface ITeamService {

    /**
     * @param caller  user who asks the service
     * @param teamName name of the team
     * @param teamLeader who will be the leader that operate on team
     * @param description a team description, can be empty
     * @return the team made
     */
    Team provideTeam(User caller, TeamName teamName, User teamLeader,String description);

    /**
     * @param caller user who asks the service
     * @param parentTeam name of parent team where append the subTeam
     * @param subTeamName name of the subTeam
     * @return Team
     */
    Team createSubTeam(User caller, Team parentTeam, TeamName subTeamName);

    /**
     * @param  caller user who asks the service
     * @param team name team where append the employee
     * @param employee the employee to append
     */
    void appendEmployee(User caller, Team team, Employee employee);

    /**
     * @param caller user who asks the service
     * @param team name team where append the employee
     * @param employee the employee to append
     */
    void removeEmployee(User caller, Team team, Employee employee);

    /**
     * @param teamLeader the team leader
     * @return LinkedHashSet Team
     */
    LinkedHashSet<Team> getAllRootTeam(User teamLeader);

    /**
     * @param caller user who asks the service
     * @param team team to update on repository
     */
    void updateTeam(User caller,Team team);

    /**
     *
     * @param caller user who asks the service
     * @param team team to delete
     */
    void removeTeam(User caller, Team team);

    /**
     *
     * @param caller  user who asks the service
     * @param subTeam sub Team to delete
     */
    void removeSubTeam(User caller, Team subTeam);

    /**
     *
     * @param caller user who ask the service
     * @param team the team where put the user
     * @param user the user will become team leader
     */
    void assignLeaderToTeam(User caller, Team team, User user);
}

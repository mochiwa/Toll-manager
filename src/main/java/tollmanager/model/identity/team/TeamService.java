package tollmanager.model.identity.team;

import tollmanager.model.access.authorization.IAuthorizationService;
import tollmanager.model.access.authorization.IllegalRightException;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.EmployeeRepository;
import tollmanager.model.identity.user.User;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.stream.Collectors;
/**
 * Service that deals with team
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class TeamService implements ITeamService {
    private TeamRepository teamRepository;
    private EmployeeRepository employeeRepository;
    private IAuthorizationService authorizationService;

    public TeamService(TeamRepository teamRepository, EmployeeRepository employeeRepository, IAuthorizationService authorizationService) {
        Objects.requireNonNull(teamRepository,"The team repository cannot be null.");
        Objects.requireNonNull(employeeRepository,"The employee repository cannot be null.");
        Objects.requireNonNull(authorizationService,"The authorization service cannot be null.");

        this.teamRepository=teamRepository;
        this.employeeRepository=employeeRepository;
        this.authorizationService = authorizationService;
    }

    /**
     * Provide a team with name,leader and description.
     * The caller should have the right to 'create a team'.
     * The name of the team should be unique , it seeks by repository
     * The leader should be exist like an employee 'cause he is appended like
     * the first employee of the team.
     * If no error occurs the team is append to the repository and returned.
     *
     * @exception IllegalRightException  caller has not role to create a team
     * @exception  IllegalArgumentException name of team already exist
     * @exception  NullPointerException teamLeader not found in employee repository
     *
     * @param caller  user who asks the service
     * @param teamName name of the team
     * @param teamLeader who will be the leader that operate on team
     * @param description a team description, can be empty
     * @return Team created
     */
    @Override
    public Team provideTeam(User caller, TeamName teamName, User teamLeader,String description) {
        Objects.requireNonNull(caller,"The caller is required.");
        Objects.requireNonNull(teamName,"The team name is required.");
        Objects.requireNonNull(teamLeader,"The team leader is required.");
        Objects.requireNonNull(description,"The description is required.");

        if(!authorizationService.isAuthorizedToCreateTeam(caller.toGroupMember()))
            throw new IllegalRightException(caller.toGroupMember(),"create a team");

        if(isRootTeamNameExist(teamName))
            throw new IllegalArgumentException("The team '"+teamName.value()+"' already exist.");

        Employee employee=employeeRepository.findById(teamLeader.employeeId());
        Objects.requireNonNull(employee,"The user '"+teamLeader.login().value()+"' cannot be a team leader.");

        Team team=TeamBuilder.of()
                .setTeamId(teamRepository.nextId())
                .setName(teamName)
                .setLeader(teamLeader)
                .createTeam();
        teamRepository.add(team);
        appendEmployee(caller,team,employee);
        return team;
    }
    private boolean isRootTeamNameExist(TeamName teamName) {
        Team team=teamRepository.findByName(teamName);
        if (team==null)
            return false;
        return team.isRootTeam();
    }

    /**
     * Append a sub team to a team, each name for direct children should be unique and
     * not same as his parent.
     *
     * @exception IllegalArgumentException sub team name same as its parent
     * @exception IllegalRightException caller is not a team leader of this team
     * @exception NullPointerException parent team not found
     * @exception IllegalArgumentException caller is a team leader but team isn't own
     *
     *
     * @param caller user who asks the service
     * @param parentTeam name of parent team where append the subTeam
     * @param subTeamName name of the subTeam
     * @return sub team made
     */
    @Override
    public Team createSubTeam(User caller, Team parentTeam, TeamName subTeamName) {
        Objects.requireNonNull(caller,"The caller is required.");
        Objects.requireNonNull(parentTeam,"The root team is required.");
        Objects.requireNonNull(subTeamName,"The sub team is required.");

        if(parentTeam.name().equals(subTeamName))
            throw new IllegalArgumentException("The sub team cannot have the same name as its parent.");

        if(!authorizationService.isAuthorizedToManageTeam(caller.toGroupMember()))
            throw new IllegalRightException(caller.toGroupMember(),"manage a team");

        if(teamRepository.findByName(parentTeam.name())==null)
            Objects.requireNonNull(parentTeam,"The parent team '"+ parentTeam.name().value()+"' not found.");

        if(parentTeam.hasTeam(subTeamName))
            throw new IllegalArgumentException("A sub team '"+subTeamName.value()+"' already exist in this team.");
        if(!parentTeam.hasTeamLeader(caller) && !authorizationService.isAuthorizedToCreateTeam(caller.toGroupMember()))
            throw new IllegalRightException(caller.toGroupMember(),"create sub team where he is not the leader");


        Team subTeam=parentTeam.appendSubTeam(teamRepository.nextId(),subTeamName);
        teamRepository.add(subTeam);
        return subTeam;
    }

    /**
     *
     * Append an employee to the team
     * @exception NullPointerException team not found on repository
     * @exception IllegalRightException caller try to append an employee to a root team but he doesn't have role to 'createTeam'
     * @exception IllegalRightException caller is not a team leader of this team
     * @exception IllegalRightException caller try to append to a sub team an employee who not belong to the root team
     *@param  caller user who asks the service
     * @param team name team where append the employee
     * @param employee the employee to append
     */
    @Override
    public void appendEmployee(User caller, Team team, Employee employee) {
        Objects.requireNonNull(caller,"The caller is required.");
        Objects.requireNonNull(team,"The team name is required.");
        Objects.requireNonNull(employee,"The employee is required");

        /*if(teamRepository.findByName(team.name())==null);*/
            Objects.requireNonNull(team,"The team '"+ team.name().value()+"' not found.");
        assertAuthorizationToAppendEmployee(team,caller,employee);

        teamRepository.appendEmployee(team,employee);
        team.appendEmployee(employee);
    }

    private void assertAuthorizationToAppendEmployee(Team team,User user,Employee toAppend)
    {
        if(team.isRootTeam()) { // && !team.leader().equals(user)
            if(!authorizationService.isAuthorizedToCreateTeam(user.toGroupMember()))
                throw new IllegalRightException("Only a manager can append member to a root team");
        }
        else if(!authorizationService.isAuthorizedToCreateTeam(user.toGroupMember())) {
            if(!team.hasTeamLeader(user))
                throw new IllegalRightException(user.toGroupMember(),"append employee to this sub team");
            if(!team.getRootTeam().hasEmployee(toAppend))
                throw new IllegalRightException(user.toGroupMember(),"append a foreign employee to this sub team");
        }
    }

    /**
     *
     * Remove an employee to the team , if the employee to remove is a team leader then the new leader is the user connected
     * @exception NullPointerException team not found on repository
     * @exception IllegalRightException caller try to remove an employee to a root team but he doesn't have role to 'createTeam'
     * @exception IllegalRightException caller is not a team leader of this team
     *@param  caller user who asks the service
     * @param team name team where append the employee
     * @param employee the employee to append
     */
    @Override
    public void removeEmployee(User caller, Team team, Employee employee) {
        Objects.requireNonNull(caller,"The caller is required.");
        Objects.requireNonNull(team,"The team name is required.");
        Objects.requireNonNull(employee,"The employee is required");

        if(teamRepository.findByName(team.name())==null)
            Objects.requireNonNull(team,"The team '"+ team.name().value()+"' not found.");

        assertAuthorizationToRemoveEmployee(team,caller);

        if(team.isRootTeam() && willBeDanglingEmployee(employee))
            throw new IllegalStateException("The employee cannot be removed from this team, any employee must belong to at lease one team");

        teamRepository.removeEmployeeFromTeam(team,employee);
        team.removeEmployee(employee);
    }

    private boolean willBeDanglingEmployee(Employee employee){
        return teamRepository.findAllRootTeam().stream().filter(t -> t.hasEmployee(employee)).count()==1;
    }

    /**
     * If the user has role to create team, that's mean he is a manager/admin then
     * all root team is given
     * else return all team where user is a leader
     *
     * @param user that have authority on teams
     * @return LinkedHashSet of teams
     */
    @Override
    public LinkedHashSet<Team> getAllRootTeam(User user) {
        if(authorizationService.isAuthorizedToCreateTeam(user.toGroupMember()))
            return teamRepository.findAllRootTeam();
        return teamRepository.findAllTeamWhereLeaderIs(user);
    }

    /**
     * If the caller has role to manage team or create team then he is allowed to update the team
     *
     * @exception  IllegalRightException if caller is not an admin/manager and team is a rootTeam
     * @exception  IllegalRightException if caller has not role to manage or create team
     *
     * @param caller user who asks the service
     * @param team team to update on repository
     */
    @Override
    public void updateTeam(User caller,Team team) {
        Objects.requireNonNull(caller,"The caller is required!");
        Objects.requireNonNull(team,"The team is required");

        if(team.isRootTeam() && !authorizationService.isAuthorizedToCreateTeam(caller.toGroupMember()))
            throw new IllegalRightException(caller.toGroupMember(),"update a Root team");

        if(!authorizationService.isAuthorizedToManageTeam(caller.toGroupMember()) && !authorizationService.isAuthorizedToCreateTeam(caller.toGroupMember()))
            throw new IllegalRightException(caller.toGroupMember(),"update a team");
        teamRepository.update(team);
    }

    /**
     * Remove a team from repository
     *
     * @exception IllegalRightException if caller has not role to create a team
     *
     * @param caller user who asks the service
     * @param team team to delete
     */
    @Override
    public void removeTeam(User caller, Team team) throws IllegalRightException {
        Objects.requireNonNull(caller,"The caller is required!");
        Objects.requireNonNull(team,"The team is required");

        if(!authorizationService.isAuthorizedToCreateTeam(caller.toGroupMember()))
            throw new IllegalRightException(caller.toGroupMember(),"delete a team");

        if(team.employeeCount()>1)
            throw new IllegalStateException("The team contains one or more employees, it cannot be delete.");
        removeEmployee(caller,team,team.employees().stream().findFirst().orElse(null));
        teamRepository.remove(team);
    }

    /**
     * Remove a subTeam from a a parent team and commit it in repository
     *
     * @exception IllegalArgumentException if the sub team is a root team
     * @exception IllegalRightException if caller has not role to create or manage a team
     *
     * @param caller  user who asks the service
     * @param subTeam sub Team to delete
     */
    @Override
    public void removeSubTeam(User caller, Team subTeam) {
        Objects.requireNonNull(caller,"The caller is required!");
        Objects.requireNonNull(subTeam,"The sub team is required");

        if(subTeam.isRootTeam())
            throw new IllegalArgumentException("A root team cannot be remove as sub team");
        if(!authorizationService.isAuthorizedToManageTeam(caller.toGroupMember()) && !authorizationService.isAuthorizedToCreateTeam(caller.toGroupMember()))
            throw new IllegalRightException(caller.toGroupMember(),"remove a sub team");

        Team parent=subTeam.parent();
        teamRepository.remove(subTeam);
        parent.removeTeam(subTeam);
    }

    /**
     * Assign a user to a team if the user is not present into the team then he is appended
     *
     * @exception IllegalRightException when caller has not create team role and team is root team
     * @exception IllegalRightException when caller is not a teamLeader of the team
     *
     * @param caller user who ask the service
     * @param team the team where put the user
     * @param user the user will become team leader
     */
    @Override
    public void assignLeaderToTeam(User caller, Team team, User user) {
        Objects.requireNonNull(caller,"The caller is required!");
        Objects.requireNonNull(team,"The team is required");
        Objects.requireNonNull(user,"The user is required");

        if(team.leader().equals(user))
            return;

        if(team.isRootTeam()) {
            if (!authorizationService.isAuthorizedToCreateTeam(caller.toGroupMember()))
                throw new IllegalRightException(caller.toGroupMember(), "assign leader to root team");
        }
        else if(!authorizationService.isAuthorizedToManageTeam(caller.toGroupMember()))
            throw new IllegalRightException(caller.toGroupMember(),"assign leader where he has not authorities");

        team.changeLeader(user);
        teamRepository.update(team);
    }

    private void assertAuthorizationToRemoveEmployee(Team team,User user) {
        if(team.isRootTeam()) {
            if(!authorizationService.isAuthorizedToCreateTeam(user.toGroupMember()))
                throw new IllegalRightException(user.toGroupMember(),"remove employee from root team"+team.name().value());
        }
        else {
            if(!team.hasTeamLeader(user) && !authorizationService.isAuthorizedToCreateTeam(user.toGroupMember()))
                throw new IllegalRightException(user.toGroupMember(),"remove employee from this team");
        }
    }

}

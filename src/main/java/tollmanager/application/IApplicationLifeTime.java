package tollmanager.application;

import tollmanager.application.command.*;
import tollmanager.application.query.*;
import tollmanager.model.access.GroupName;
import tollmanager.model.identity.person.Person;
import tollmanager.model.identity.team.Team;
import tollmanager.model.identity.team.TeamBuilder;
import tollmanager.model.identity.team.TeamName;
import tollmanager.model.identity.user.Login;
import tollmanager.model.identity.user.User;

import java.sql.SQLException;


/**
 * This interface is the layer to communicate between graphic and business
 * @author chiappelloni nicolas
 * @version 1.0
 */
public interface IApplicationLifeTime {
    GroupName administratorGroup=GroupName.of("administrator");
    GroupName managerGroup=GroupName.of("manager");
    GroupName teamLeaderGroup=GroupName.of("teamLeader");

    Login administratorLogin=Login.of("admin");
    TeamName managersTeam=TeamName.of("managers");



    void initApplication(User user);

    boolean isFirstLaunch();

    void initApplicationForFirstTime(Person administrator) throws SQLException;

    void logout();


    /**
     * @param command contains the parent team name and the sub team name
     * @see AppendSubTeamCommand
     */
    void execute(AppendSubTeamCommand command);

    /**
     * @param command contains the sub team to remove
     * @see RemoveSubTeamCommand
     */
    void execute(RemoveSubTeamCommand command);

    /**
     * @param command contain the team information to create
     * @see CreateRootTeamCommand
     */
    void execute(CreateRootTeamCommand command);

    /**
     * @param command contain the team to remove
     * @see RemoveRootTeamCommand
     */
    void execute(RemoveRootTeamCommand command);

    /**
     * @param command contain the employee and user information to make a manager
     * @see CreateManagerCommand
     */
    void execute(CreateManagerCommand command);

    /**
     * @param command contains identity information about employee
     * @see CreateEmployeeCommand
     */
    void execute(CreateEmployeeCommand command);

    /**
     * @param command contain the old employee and new identity information to apply
     * @see  EditEmployeeCommand
     */
    void execute(EditEmployeeCommand command);

    /**
     * @param command contains employee, user and team where assign team leader
     * @see CreateTeamLeaderCommand
     */
    void execute(CreateTeamLeaderCommand command);

    /**
     * @param command contains the team name and the employee to append
     * @see AppendEmployeeToTeamCommand
     */
    void execute(AppendEmployeeToTeamCommand command);

    /**
     * @param command contain the employee and team name where remove employee
     * @see RemoveFromTeamEmployeeCommand
     */
    void execute(RemoveFromTeamEmployeeCommand command);


    /**
     * @param query query where set types of employee {employee,manager,teamLeader,....}
     * @see TypeEmployeeQuery
     */
    void query(TypeEmployeeQuery query);


    /**
     * @param query can contain an user and set if the user is manager or admin;
     * @see IsManagerQuery
     */
    void query(IsManagerQuery query);

    /**
     * @param query contains the email and boolean returned if used or not
     * @see IsEmailUsedQuery;
     */
    void query(IsEmailUsedQuery query);

    /**
     * @param query contains the niss and boolean returned if used or not
     * @see IsNissUsedQuery
     */
    void query(IsNissUsedQuery query);

    /**
     *
     * @param query contains the phone number and boolean returned if used or not
     * @see IsPhoneUsedQuery
     */
    void query(IsPhoneUsedQuery query);

    /**
     *
     * @param query contains the login and boolean returned if used or not
     * @see IsLoginUsedQuery
     */
    void query(IsLoginUsedQuery query);

    /**
     * @param query contains the employee id and the week to get plannings.
     * @see WeekPlanningQuery
     */
    void execute(WeekPlanningQuery query);

    /**
     * @param command contains the planning information
     */
    void execute(AppendPlanningCommand command);

    /**
     * @param command contains the planning to remove
     */
    void execute(RemovePlanningCommand command);

    /**
     * @param command contains the employee to delete
     */
    void execute(DeleteEmployeeCommand command);
}

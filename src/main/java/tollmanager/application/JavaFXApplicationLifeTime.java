package tollmanager.application;

import com.googlecode.dummyjdbc.DummyJdbcDriver;
import graphic.components.team.TeamBoardCommandEvent;
import graphic.form.employee.selectionMenu.RequestRootTeamObservable;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import tollmanager.application.command.*;
import tollmanager.application.query.*;
import tollmanager.infrastructure.persistance.DatabaseConnection;
import tollmanager.infrastructure.service.MD5PasswordEncryptionService;
import tollmanager.model.access.*;
import tollmanager.model.access.authorization.AuthorizationService;
import tollmanager.model.access.authorization.IAuthorizationService;
import tollmanager.model.identity.*;
import tollmanager.model.identity.contact.ContactInformation;
import tollmanager.model.identity.person.Birthday;
import tollmanager.model.identity.person.FullName;
import tollmanager.model.identity.person.Niss;
import tollmanager.model.identity.person.Person;
import tollmanager.model.identity.team.*;
import tollmanager.model.identity.user.*;
import tollmanager.model.identity.user.authentication.AuthenticationService;
import tollmanager.model.identity.user.authentication.IAuthenticationService;
import tollmanager.model.identity.user.password.IPasswordEncryptionService;
import tollmanager.model.identity.user.password.Password;
import tollmanager.model.planning.IPlanningService;
import tollmanager.model.planning.PlanningRepository;
import tollmanager.model.planning.PlanningService;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
/**
 * Implementation of the IApplicationLifeTime for JavaFx
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class JavaFXApplicationLifeTime implements IApplicationLifeTime {
    private SimpleObjectProperty<User> connectedUser;
    private SimpleObjectProperty<Employee> selectedEmployee;
    private SimpleObjectProperty<Team> selectedTeam;
    private ObservableSet<Team> teamsObservable;

    private GroupRepository groupRepository;
    private EmployeeRepository employeeRepository;
    private UserRepository userRepository;
    private TeamRepository teamRepository;

    private IPasswordEncryptionService passwordEncryptionService;
    private IAuthorizationService authorizationService;
    private IAuthenticationService authenticationService;
    private IUserService userService;
    private ITeamService teamService;
    private IGroupService groupService;

    private IUserProviderService userProviderService;
    private IEmployeeProviderService employeeProviderService;
    private IEmployeeService employeeService;

    private PlanningRepository planningRepository;
    private IPlanningService planningService;

    private DatabaseConnection db;

    public JavaFXApplicationLifeTime(GroupRepository groupRepository, EmployeeRepository employeeRepository, UserRepository userRepository, TeamRepository teamRepository,PlanningRepository planningRepository) throws SQLException{
        this.groupRepository = groupRepository;
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.planningRepository=planningRepository;




        passwordEncryptionService = new MD5PasswordEncryptionService();
        authorizationService = new AuthorizationService(groupRepository);
        groupService = new GroupService(groupRepository, authorizationService);
        userService = new UserService(userRepository, groupService, authorizationService);
        teamService = new TeamService(teamRepository, employeeRepository, authorizationService);
        authenticationService = new AuthenticationService(userRepository, passwordEncryptionService);

        employeeProviderService = new EmployeeProviderService(employeeRepository, authorizationService);
        userProviderService = new UserProviderService(userRepository, groupService, authorizationService, passwordEncryptionService);
        employeeService = new EmployeeService(employeeRepository, userRepository, groupService, authorizationService);
        planningService=new PlanningService(planningRepository,authorizationService,teamRepository);



        this.db = DatabaseConnection.instance();


        initTeamObservable();
        initConnectedUserProperty();
        initSelectedEmployee();
        initSelectedTeam();
    }

    private void initSelectedTeam() {
        selectedTeam=new SimpleObjectProperty<>();
    }

    private void initSelectedEmployee() {
        selectedEmployee=new SimpleObjectProperty<>();
    }


    private void initTeamObservable() {
        teamsObservable = FXCollections.observableSet();
    }

    /**
     * init operations of connect user,
     * - if connected user is null then clear all team observable
     */
    private void initConnectedUserProperty() {
        connectedUser = new SimpleObjectProperty<>();
        connectedUser.addListener((observableValue, old, user) -> {
            if (user == null)
                teamsObservable.clear();
        });
    }

    /**
     * Init the application each time a new user is connected.
     *
     * @param user the user who's currently connecting
     * @throws RuntimeException if an user is already connected
     * @throws RuntimeException if the application is launch for the first time.
     */
    @Override
    public void initApplication(User user) {
        connectedUser.set(user);
        groupService.setAdminGroupName(administratorGroup);

        if (user.login().equals(administratorLogin))
            teamsObservable.addAll(teamService.getAllRootTeam(connectedUser()));

        teamService.getAllRootTeam(connectedUser()).forEach(t -> {
            if (!t.name().equals(managersTeam))
                teamsObservable.add(t);
        });
    }

    /**
     * If the application doesn't have a group administrator or if the group is empty
     * then the application is launched for the first time and should be initialized
     *
     * @return true if group admin is empty or not exist , otherwise false
     */
    public boolean isFirstLaunch() {
        Group adminGroup = groupRepository.findByName(administratorGroup);
        return adminGroup == null || adminGroup.membersCount() == 0;
    }

    /**
     * When it's the first launch, the process to create all groups, roles and admin user
     * Then create special team.
     *
     * @see JavaFXApplicationLifeTime#initFirstLaunchGroups()
     * @see JavaFXApplicationLifeTime#initSpecialTeam(User)
     * @param administrator administrator personal data
     */
    @Override
    public void initApplicationForFirstTime(Person administrator){
        try {
            db.setAutoCommit(false);
            initFirstLaunchGroups();
            Employee employee = Employee.of(employeeRepository.nextId(), administrator);
            employeeRepository.add(employee);

            User admin = User.of(employee.employeeId(), administratorLogin, Password.of("admin"));
            userRepository.add(admin);

            Group adminGroup = groupRepository.findByName(administratorGroup);
            adminGroup.appendMember(admin.toGroupMember());
            groupRepository.update(adminGroup);

            initSpecialTeam(admin);
            initApplication(admin);
            db.connection().commit();
        } catch (Exception e) {
            db.rollback();
        } finally {
            db.setAutoCommit(true);
        }
        connectedUser.set(userRepository.findByLogin(administratorLogin));
    }

    /**
     * Initialize some special team not deletable and not editable directly.
     * -managersTeam : all manager will be appended.
     * -teamLeadersTeam : all team leader will be appended.
     * -employeeTeam : all employee will be appended.
     * @param admin user will by the leader of theses team.
     */
    private void initSpecialTeam(User admin) {
        teamService.provideTeam(admin, managersTeam, admin, "a team for manager");
    }

    /**
     * Define the connected user to null
     * @see JavaFXApplicationLifeTime#initConnectedUserProperty()
     */
    @Override
    public void logout() {
        connectedUser.set(null);
    }

    /**
     * Init the base group of authority for these application.
     * @see JavaFXApplicationLifeTime#initAdministratorGroup()
     * @see JavaFXApplicationLifeTime#initManagerGroup()
     * @see JavaFXApplicationLifeTime#initTeamLeaderGroup()
     */
    private void initFirstLaunchGroups() {
        initAdministratorGroup();
        initManagerGroup();
        initTeamLeaderGroup();
    }

    /**
     * The administrator group , only one user can belong of this team,
     * and this user can do every (or almost) in the application,
     * he has all roles with wildcard
     * @see Group
     * @see Role
     */
    private void initAdministratorGroup() {
        LinkedHashSet<Role> roles = new LinkedHashSet<>();
        roles.add(Role.append_member(GroupName.wildCard()));
        roles.add(Role.remove_member(GroupName.wildCard()));
        roles.add(Role.manage_team(GroupName.wildCard()));
        roles.add(Role.createEmployee());
        roles.add(Role.createUser(GroupName.wildCard()));
        roles.add(Role.deleteUser(GroupName.wildCard()));
        roles.add(Role.create_team());
        roles.add(Role.manage_planning());

        Group adminGroup = GroupBuilder.of()
                .setName(administratorGroup)
                .setRoles(roles)
                .create();
        groupRepository.add(adminGroup);
    }

    /**
     * The group authority for manager, manager can C.R.U.D the team leader group,
     *  C.R.U.D employee and user(for and in leader group) , an  C.R.U.D team
     * @see Group
     * @see Role
     */
    private void initManagerGroup() {
        LinkedHashSet<Role> roles = new LinkedHashSet<>();
        roles.add(Role.append_member(teamLeaderGroup));
        roles.add(Role.remove_member(teamLeaderGroup));
        roles.add(Role.manage_team(GroupName.wildCard()));
        roles.add(Role.createEmployee());
        roles.add(Role.createUser(teamLeaderGroup));
        roles.add(Role.deleteUser(teamLeaderGroup));
        roles.add(Role.create_team());
        roles.add(Role.manage_planning());

        Group group = GroupBuilder.of()
                .setName(managerGroup)
                .setRoles(roles)
                .create();
        groupRepository.add(group);
    }

    /**
     * The group authority for team leader, a team leader can only manage his team,
     * he can CRUD sub Team of him , change employee to team , but cannot create root team or employee
     * @see Group
     * @see Role
     */
    private void initTeamLeaderGroup() {
        LinkedHashSet<Role> roles = new LinkedHashSet<>();
        roles.add(Role.manage_team(GroupName.wildCard()));
        roles.add(Role.manage_planning());

        Group group = GroupBuilder.of()
                .setName(teamLeaderGroup)
                .setRoles(roles)
                .create();
        groupRepository.add(group);
    }


    /**
     * Creates and append a sub team to a Team
     * @param command contains the parent team name and the sub team name
     * @see ITeamService#createSubTeam(User, Team, TeamName)
     */
    @Override
    public void execute(AppendSubTeamCommand command) {
        try {
            db.setAutoCommit(false);
            teamService.createSubTeam(
                    connectedUser(),
                    command.getParentTeam(),
                    command.getSubTeamName()
            );
            db.connection().commit();
        } catch (Exception e) {
            showError(e.getMessage());
            db.rollback();
        }
        finally {
            db.setAutoCommit(true);
        }
    }

    /**
     * Remove a sub team from its parent
     * @param command contains the sub team to remove
     * @see TeamService#removeSubTeam(User, Team)
     */
    @Override
    public void execute(RemoveSubTeamCommand command) {
        try {
            db.setAutoCommit(false);
            teamService.removeSubTeam(connectedUser(), command.getTeam());
            db.connection().commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        }
        finally {
            db.setAutoCommit(true);
        }
    }

    /**
     * Create a root team and append it to observable team
     * @param command contain the team information to create
     * @see TeamService#provideTeam(User, TeamName, User, String)
     */
    @Override
    public void execute(CreateRootTeamCommand command) {
        try {
            db.setAutoCommit(false);
            Team team = teamService.provideTeam(connectedUser(),
                    command.getTeamName(),
                    command.getLeader() == null ? connectedUser() : command.getLeader(),
                    command.getDescription());
            teamsObservable.add(team);
            db.connection().commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        }
        finally {
            db.setAutoCommit(true);
        }
    }

    /**
     * Remove a root team and also remove from observable teams
     * @param command contain the team to remove
     * @see TeamService#removeTeam(User, Team)
     */
    @Override
    public void execute(RemoveRootTeamCommand command) {
        try {
            db.setAutoCommit(false);
            teamService.removeTeam(
                    connectedUser(),
                    command.getTeam());
            teamsObservable.remove(command.getTeam());
            db.connection().commit();
        } catch (Exception e) {
            e.printStackTrace();
            showError(e.getMessage());
            db.rollback();
        }
        finally {
            db.setAutoCommit(true);
        }
    }

    /**
     * Create an manager, first create an employee then link an account and grant him to manager rang
     * and finally append manager to the team managersTeam.
     * @param command contain the employee and user information to make a manager
     * @see EmployeeProviderService#registerEmployee(User, Niss, FullName, Birthday, ContactInformation)
     * @see UserProviderService#registerUser(User, GroupName, Login, Password, Password, EmployeeId)
     * @see ITeamService#appendEmployee(User, Team, Employee)
     */
    @Override
    public void execute(CreateManagerCommand command) {
        try {
            db.setAutoCommit(false);
            Person person = command.getPerson();
            Employee employee = employeeProviderService.registerEmployee(
                    connectedUser(),
                    person.niss(),
                    person.fullName(),
                    person.birthday(),
                    person.contactInformation());

            userProviderService.registerUser(
                    connectedUser(),
                    managerGroup,
                    command.getLogin(),
                    command.getPassword(),
                    command.getPassword(),
                    employee.employeeId());
            teamService.appendEmployee(
                    connectedUser(),
                    getRootTeamObservableByName(managersTeam),
                    employee);
            db.connection().commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        }
        finally {
            db.setAutoCommit(true);
        }
    }

    /**
     * Create an employee and append him to his team
     * @param command contains identity information about employee
     * @see EmployeeProviderService#registerEmployee(User, Niss, FullName, Birthday, ContactInformation) 
     * @see ITeamService#appendEmployee(User, Team, Employee)
     */
    @Override
    public void execute(CreateEmployeeCommand command) {
        try {
            db.setAutoCommit(false);
            Person person = command.getPerson();
            Employee employee = employeeProviderService.registerEmployee(
                    connectedUser(),
                    person.niss(),
                    person.fullName(),
                    person.birthday(),
                    person.contactInformation());
            teamService.appendEmployee(
                    connectedUser(),
                    command.getTeam(),
                    employee);
            db.connection().commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        }
        finally {
            db.setAutoCommit(true);
        }
    }

    /**
     * Edit an employee
     * @param command contain the old employee and new identity information to apply
     * @see EmployeeService#updateEmployee(User, Employee, Person)
     */
    @Override
    public void execute(EditEmployeeCommand command) {
        try {
            db.setAutoCommit(false);
            employeeService.updateEmployee(connectedUser(), command.getEmployee(), command.getPerson());
            db.connection().commit();
            getTeamsObservable().stream().filter(t->t.hasEmployee(command.getEmployee())).collect(Collectors.toList()).forEach(Team::updateAll);
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        }
        finally {
            db.setAutoCommit(true);
        }
    }

    /**
     * Create a team leader, create an employee, and account , assign it to his team and then grant him to team leader rang
     * @param command contains employee, user and team where assign team leader
     * @see EmployeeProviderService#registerEmployee(User, Niss, FullName, Birthday, ContactInformation)
     * @see UserProviderService#registerUser(User, GroupName, Login, Password, Password, EmployeeId)
     * @see ITeamService#appendEmployee(User, Team, Employee)
     * @see TeamService#assignLeaderToTeam(User, Team, User)
     */
    @Override
    public void execute(CreateTeamLeaderCommand command) {
        try {
            db.setAutoCommit(false);
            Person person = command.getPerson();
            Employee employee = employeeProviderService.registerEmployee(
                    connectedUser(),
                    person.niss(),
                    person.fullName(),
                    person.birthday(),
                    person.contactInformation());

            User user = userProviderService.registerUser(
                    connectedUser(),
                    teamLeaderGroup,
                    command.getLogin(),
                    command.getPassword(),
                    command.getPassword(),
                    employee.employeeId());
            teamService.appendEmployee(
                    connectedUser(),
                    command.getTeam(),
                    employee);
            teamService.assignLeaderToTeam(
                    connectedUser(),
                    command.getTeam(),
                    user
            );
            db.connection().commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        }
        finally {
            db.setAutoCommit(true);
        }
    }

    /**
     * Move an employee to a team
     * @param command contains the team name and the employee to append
     * @see ITeamService#appendEmployee(User, Team, Employee)
     */
    @Override
    public void execute(AppendEmployeeToTeamCommand command){
        try {
            db.setAutoCommit(false);
            teamService.appendEmployee(
                    connectedUser(),
                    command.getTeam(),
                    command.getEmployee());
            db.connection().commit();
            command.getTeam().updateAll();
        }catch (Exception e) {
            showError(e.getMessage());
            db.rollback();
        }
        finally {
            db.setAutoCommit(true);
        }
    }

    /**
     * Remove an employee from a team
     * @param command contain the employee and team name where remove employee
     * @see ITeamService#removeEmployee(User, Team, Employee)
     */
    @Override
    public void execute(RemoveFromTeamEmployeeCommand command)  {
        try {
            db.setAutoCommit(false);
            boolean isNeedNewLeader=command.getTeam().isRootTeam() && command.getTeam().isLeader(command.getEmployee());
            teamService.removeEmployee(
                    connectedUser(),
                    command.getTeam(),
                    command.getEmployee());
            if(isNeedNewLeader){
                teamService.appendEmployee(
                        connectedUser.get(),
                        command.getTeam(),
                        employeeRepository.findById(connectedUser().employeeId())
                );
                teamService.assignLeaderToTeam(
                        connectedUser.get(),
                        command.getTeam(),
                        connectedUser.get()
                );
            }
            db.connection().commit();
        } catch (Exception e) {
            showError(e.getMessage());
            db.rollback();
        }
        finally {
            db.setAutoCommit(true);
        }
    }

    /**
     * Append a planning
     * @param command contains the planning information
     */
    @Override
    public void execute(AppendPlanningCommand command) {
        try {
            db.setAutoCommit(false);
            planningService.providePlanning(
                    connectedUser(),
                    command.getBeginningDate(),
                    command.getEndingDate(),
                    command.getComment(),
                    selectedEmployee.get().employeeId(),
                    selectedTeam.get().teamId());
            db.connection().commit();
        } catch (Exception e) {
            showError(e.getMessage());
            db.rollback();
        }
        finally {
            db.setAutoCommit(true);
        }
    }

    /**
     * Remove a planning
     * @param command contains the planning to remove
     */
    @Override
    public void execute(RemovePlanningCommand command) {
        try {
            db.setAutoCommit(false);
            planningService.removePlanning(
                    connectedUser(),
                    command.getPlanning()
            );
            db.connection().commit();
        } catch (Exception e) {
            showError(e.getMessage());
            db.rollback();
        }
        finally {
            db.setAutoCommit(true);
        }
    }

    /**
     * Delete an employee
     * @param command contains the employee to delete
     */
    @Override
    public void execute(DeleteEmployeeCommand command) {
        Employee employee=command.getEmployee();
        User userLinked=userRepository.findById(command.getEmployee().employeeId());
        if(employee.employeeId().equals(connectedUser().employeeId())){
            showError("You cannot delete yourself !");
            return;
        }
        else if(userLinked!=null && userLinked.login().equals(administratorLogin)){
            showError("You cannot delete Administrator !");
            return;
         }

        try {
            db.setAutoCommit(false);
            employeeService.deleteEmployee(connectedUser.get(),command.getEmployee());
            db.connection().commit();


            teamsObservable.forEach(team->{
                if(team.isLeader(employee)){
                    teamService.appendEmployee(
                            connectedUser.get(),
                            team,
                            employeeRepository.findById(connectedUser().employeeId())
                    );
                    teamService.assignLeaderToTeam(
                            connectedUser.get(),
                            team,
                            connectedUser.get()
                    );
                }
                if(team.hasEmployee(employee)){
                    team.removeEmployee(employee);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showError(e.getMessage());
            db.rollback();
        }
        finally {
            db.setAutoCommit(true);
        }
    }


    /**
     * Insert the list of all groupName (type) of employee
     * @param query query where set types of employee {employee,manager,teamLeader,....}
     */
    @Override
    public void query(TypeEmployeeQuery query) {
        query.setTypes(groupService.getAllGroupInRepository());
    }

    /**
     * Set if user is a manager or administrator
     * @param query can contain an user and set if the user is manager or admin;
     */
    @Override
    public void query(IsManagerQuery query) {
        User user = query.getUser() == null ? connectedUser() : query.getUser();
        query.setIsManager(groupService.isBelongTo(user.toGroupMember(), managerGroup) || groupService.isBelongTo(user.toGroupMember(), administratorGroup));
    }

    /**
     * Set if the email is already used , if the email is email of select employee then set false.
     * @param query contains the email and boolean returned if used or not
     */
    @Override
    public void query(IsEmailUsedQuery query) {
        if(getSelectedEmployee()!=null && getSelectedEmployee().person().contactInformation().emails().contains(query.getEmail()))
            query.setUsed(false);
        else {
            Employee employee = employeeRepository.findByEmail(query.getEmail());
            query.setUsed(employee != null);
        }
    }

    /**
     * Set if the NISS is already used , if the Niss is niss of select employee then set false
     * @param query contains the niss and boolean returned if used or not
     */
    @Override
    public void query(IsNissUsedQuery query) {
        if(getSelectedEmployee()!=null && getSelectedEmployee().niss().equals(query.getNiss()))
            query.setUsed(false);
        else {
            query.setUsed(employeeRepository.findByNiss(query.getNiss()) != null);
        }
    }

    /**
     * Set if the Phone is already used , if the phone is phone of select employee then set false
     * @param query contains the phone number and boolean returned if used or not
     */
    @Override
    public void query(IsPhoneUsedQuery query) {
        if(getSelectedEmployee()!=null && getSelectedEmployee().person().contactInformation().phones().contains(query.getPhone()))
            query.setUsed(false);
        else {
            Employee employee = employeeRepository.findByPhone(query.getPhone());
            query.setUsed(employee != null);
        }
    }

    /**
     * Set if the Login is already used , if the login is login of selected employee then set false
     * @param query contains the login and boolean returned if used or not
     */
    @Override
    public void query(IsLoginUsedQuery query) {
        User user=null;
        if(query.getLogin()!=null)
            user=userRepository.findByLogin(query.getLogin());
        if(user==null)
            query.setUsed(false);
        else if (getSelectedEmployee()!=null && getSelectedEmployee().employeeId().equals(user.employeeId()))
            query.setUsed(false);
        else
            query.setUsed(true);
    }

    /**
     * set all root team except manager team
     * @param query
     */
    public void query(RequestRootTeamObservable query) {
        ObservableSet<Team> list = FXCollections.observableSet();
        list.addAll(getTeamsObservable());
        list.removeIf(t->t.name().equals(managersTeam));
        query.setTeams(list);
    }

    /**
     * query all planning for a week
     * @param query contains the employee id and the week to get plannings.
     */
    @Override
    public void execute(WeekPlanningQuery query) {
        query.setWeekPlanning(
                planningService.getPlanningOfWeek(query.getEmployeeId(),query.getWeekSelected())
        );
    }


    /**
     * Print a message on a dialog
     * @param message
     */
    public void showError(String message){
        Alert alert=new Alert(Alert.AlertType.ERROR,message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
        Event.fireEvent(Event.NULL_SOURCE_TARGET, TeamBoardCommandEvent.createEmployee());
    }

    /**
     * return the connected user from the connecterUser property
     * @return User
     */
    public User connectedUser() {
        return connectedUser.get();
    }

    public SimpleObjectProperty<User> connectedUserProperty() {
        return connectedUser;
    }

    public ObservableSet<Team> getTeamsObservable() {
        return teamsObservable;
    }
    private Team getRootTeamObservableByName(TeamName name){
        return teamsObservable.stream().filter(t->t.name().equals(name)).findFirst().orElse(null);
    }

    public IAuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public IPasswordEncryptionService getPasswordEncryptionService() {
        return passwordEncryptionService;
    }

    public IUserService getUserService() {
        return userService;
    }

    public Employee getSelectedEmployee() {
        return selectedEmployee.get();
    }

    public SimpleObjectProperty<Employee> selectedEmployeeProperty() {
        return selectedEmployee;
    }



    public SimpleObjectProperty<Team> selectedTeamProperty() {
        return selectedTeam;
    }
}

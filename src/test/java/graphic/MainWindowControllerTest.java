package graphic;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import tollmanager.TestHelper;
import tollmanager.application.JavaFXApplicationLifeTime;
import tollmanager.infrastructure.persistance.DatabaseConnection;
import tollmanager.infrastructure.persistance.inMemory.*;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.team.Team;
import tollmanager.model.identity.team.TeamId;
import tollmanager.model.identity.team.TeamName;
import tollmanager.model.identity.user.Login;
import tollmanager.model.identity.user.User;
import tollmanager.model.identity.user.password.Password;
import tollmanager.model.planning.Planning;
import tollmanager.model.planning.PlanningId;
import tollmanager.model.planning.PlanningRepository;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;

public class MainWindowControllerTest extends ApplicationTest {
    TestHelper helper;

    JavaFXApplicationLifeTime applicationLifeTime;
    MainWindowController controller;
    Parent mainNode;

    InMemoryGroupRepository groupRepository;
    InMemoryEmployeeRepository employeeRepository;
    InMemoryUserRepository userRepository;
    InMemoryTeamRepository teamRepository;
    private PlanningRepository planningRepository;


    private User admin;
    private User manager;
    private User teamLeader;

    @Override
    public void start(Stage stage) throws Exception {
        DatabaseConnection d = Mockito.mock(DatabaseConnection.class);


        helper = TestHelper.of();
        groupRepository = Mockito.spy(new InMemoryGroupRepository());
        employeeRepository = Mockito.spy(new InMemoryEmployeeRepository());
        userRepository = Mockito.spy(new InMemoryUserRepository());
        teamRepository = Mockito.spy(new InMemoryTeamRepository());
        planningRepository=Mockito.spy(new InMemoryPlanningRepository());
        applicationLifeTime = new JavaFXApplicationLifeTime(groupRepository, employeeRepository, userRepository, teamRepository,planningRepository);

        controller = new MainWindowController(applicationLifeTime);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainWindow.fxml"));
        loader.setController(controller);
        mainNode = loader.load();

        stage.setMaximized(true);
        stage.initStyle(StageStyle.UNIFIED);

        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
        initBaseData();


    }

    @After
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    private void initBaseData(){
        applicationLifeTime.initApplicationForFirstTime(helper.getEmployee("01", "john", "doe","11.11.11-111.11").person());
        applicationLifeTime.getUserService().changePassword(applicationLifeTime.connectedUser(), Password.of("Secret123@"), Password.of("Secret123@"), applicationLifeTime.getPasswordEncryptionService());
        admin=userRepository.findByLogin(Login.of("admin"));

        //init Manager
        Employee aManagerEmployee=helper.getEmployee("02", "chiap", "nicolas","11.11.11-111.22");
        employeeRepository.add(aManagerEmployee);
        manager=helper.getUser("02","aManager","Secret123@");
        userRepository.add(manager);
        groupRepository.findByName(JavaFXApplicationLifeTime.managerGroup).appendMember(manager.toGroupMember());
        applicationLifeTime.getUserService().changePassword(manager, Password.of("Secret123@"), Password.of("Secret123@"), applicationLifeTime.getPasswordEncryptionService());

        //init TeamLeader
        Employee aTeamLeaderEmployee=helper.getEmployee("03", "charle", "bertrand","11.11.11-111.33");
        employeeRepository.add(aTeamLeaderEmployee);
        teamLeader=helper.getUser("03","aTeamLeader","Secret123@");
        userRepository.add(teamLeader);
        groupRepository.findByName(JavaFXApplicationLifeTime.teamLeaderGroup).appendMember(teamLeader.toGroupMember());
        applicationLifeTime.getUserService().changePassword(teamLeader, Password.of("Secret123@"), Password.of("Secret123@"), applicationLifeTime.getPasswordEncryptionService());

        employeeRepository.add(helper.getEmployee("04", "kurts", "jhonny"));
        employeeRepository.add(helper.getEmployee("05", "meness", "pierre"));
        employeeRepository.add(helper.getEmployee("06", "ellen", "murst"));


        Team aRootTeam = helper.getTeam("01", "root team A", teamLeader);
        aRootTeam.appendEmployee(employeeRepository.findById(teamLeader.employeeId()));
        aRootTeam.appendEmployee(employeeRepository.findById(EmployeeId.of("04")));
        aRootTeam.appendEmployee(employeeRepository.findById(EmployeeId.of("05")));
        aRootTeam.appendEmployee(employeeRepository.findById(EmployeeId.of("06")));
        teamRepository.add(aRootTeam);

        Team otherRootTeam = helper.getTeam("02", "root team B",teamLeader);
        otherRootTeam.appendEmployee(employeeRepository.findById(teamLeader.employeeId()));
        otherRootTeam.appendEmployee(helper.getEmployee("07", "rouly", "susan"));
        teamRepository.add(otherRootTeam);
        teamRepository.add(otherRootTeam.appendSubTeam(TeamId.of("022"), TeamName.of("sub team 1")));
        teamRepository.add(otherRootTeam.appendSubTeam(TeamId.of("023"), TeamName.of("sub team 2")));


        Planning p1=Planning.of(PlanningId.of("a"),
                LocalDateTime.of(2019,10,12,8,0),
                LocalDateTime.of(2019,10,12,10,0),
                "Post E2",
                EmployeeId.of("06"),
                aRootTeam.teamId());
        Planning p2=Planning.of(PlanningId.of("b"),
                LocalDateTime.of(2019,10,11,12,0),
                LocalDateTime.of(2019,10,11,14,0),
                "Post E2",
                EmployeeId.of("06"),
                aRootTeam.teamId());

        Planning p2_2=Planning.of(PlanningId.of("bb"),
                LocalDateTime.of(2019,10,11,16,0),
                LocalDateTime.of(2019,10,11,16,0),
                "Post E2",
                EmployeeId.of("06"),
                aRootTeam.teamId());


        Planning p3=Planning.of(PlanningId.of("c"),
                LocalDateTime.of(2019,10,10,8,0),
                LocalDateTime.of(2019,10,10,19,0),
                "Post E2",
                EmployeeId.of("06"),
                aRootTeam.teamId());

        planningRepository.add(p1);
        planningRepository.add(p2);
        planningRepository.add(p2_2);
        planningRepository.add(p3);


    }

    @Test
    public void LaunchLikeAdmin() {

        sleep(200);
        Platform.runLater(() -> {
            controller.start();
           // controller.setScene(mainNode.getScene());
            applicationLifeTime.initApplication(admin);
        });
        sleep(200);

        sleep(20000000);
        Platform.exit();
    }

    @Test
    public void LaunchLikeTeamLeader() {

        Platform.runLater(() -> {
            controller.start();
         //   controller.setScene(mainNode.getScene());
            applicationLifeTime.initApplication(teamLeader);
        });
        sleep(200);

        sleep(20000000);
        Platform.exit();
    }

    @Test
    public void LaunchLikeTeamManager() {
        Platform.runLater(() -> {
            controller.start();
          //  controller.setScene(mainNode.getScene());
            applicationLifeTime.initApplication(manager);
        });
        sleep(200);

        sleep(20000000);
        Platform.exit();
    }
}

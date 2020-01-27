package tollmanager.model.identity.team;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import tollmanager.TestHelper;
import tollmanager.infrastructure.persistance.inMemory.InMemoryEmployeeRepository;
import tollmanager.infrastructure.persistance.inMemory.InMemoryTeamRepository;
import tollmanager.model.access.authorization.IAuthorizationService;
import tollmanager.model.access.authorization.IllegalRightException;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.EmployeeRepository;
import tollmanager.model.identity.user.Login;
import tollmanager.model.identity.user.User;
import tollmanager.model.identity.user.password.Password;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

public class TeamServiceTest {
    private User aManager;
    private User aLeader;
    private User aLambdaUser;

    private Team aRootTeam;
    private Employee employeeToAppend;

    private EmployeeRepository employeeRepository;
    private TeamRepository teamRepository;
    private TeamService teamService;
    private IAuthorizationService authorizationService;
    @Before
    public void setUp() {
        teamRepository=Mockito.mock(InMemoryTeamRepository.class);
        employeeRepository=Mockito.mock(InMemoryEmployeeRepository.class);
        authorizationService= Mockito.mock(IAuthorizationService.class);

        teamService=new TeamService(teamRepository,employeeRepository,authorizationService);

        aManager=TestHelper.of().getUser("01","aManager");
        aLeader=TestHelper.of().getUser("02","aLeader");
        aLambdaUser =TestHelper.of().getUser("03","lambdaUser");

        aRootTeam=TestHelper.of().getTeam("01","aRootTeam",aLeader);
        employeeToAppend=TestHelper.of().getEmployee("03","eric","johnsen");

        Mockito.doReturn(true).when(authorizationService).isAuthorizedToManageTeam(aLeader.toGroupMember());
        Mockito.doReturn(true).when(authorizationService).isAuthorizedToCreateTeam(aManager.toGroupMember());
        Mockito.doReturn(false).when(authorizationService).isAuthorizedToManageTeam(aLambdaUser.toGroupMember());

        Mockito.doReturn(TestHelper.of().getEmployee("02","john","doe")).when(employeeRepository).findById(aLeader.employeeId());

        Mockito.doReturn(aRootTeam).when(teamRepository).findByName(aRootTeam.name());



    }

    @Test(expected = IllegalRightException.class)
    public void provideTeam_shouldThrow_whenCaller_isNotAuthorized_to_createTeam() {
        //Mockito.doReturn(false).when(authorizationService).isAuthorizedToCreateTeam(any());

        teamService.provideTeam(aLeader, TeamName.of("aTeamName"), aLeader,"");
    }
    @Test(expected = IllegalArgumentException.class)
    public void provideTeam_shouldThrow_whenTeamNameAlreadyExist() {
        Team team=TestHelper.of().getTeam("01","aTeam",aLeader);
        Mockito.doReturn(team).when(teamRepository).findByName(any());

        teamService.provideTeam(aManager,team.name(), aLeader, "");
    }

    @Test
    public void provideTeam_shouldReturnTeam() {
        TeamName teamName=TeamName.of("aTeamName");


        Mockito.doReturn(TeamId.of("01")).when(teamRepository).nextId();

        Team team=teamService.provideTeam(aManager, teamName, aLeader,"");

        Assert.assertNotNull(team);
        assertEquals(teamName,team.name());
        Assert.assertTrue(team.hasTeamLeader(aLeader));
    }

    @Test
    public void provideTeam_shouldAppendLeader_toTheEmployeeList() {
        TeamName teamName=TeamName.of("aTeamName");

        Mockito.doReturn(TeamId.of("01")).when(teamRepository).nextId();

        Team team=teamService.provideTeam(aManager, teamName, aLeader,"");
        Employee leaderAsEmployee=employeeRepository.findById(aLeader.employeeId());

        Assert.assertTrue(team.hasEmployee(leaderAsEmployee));
    }




    @Test(expected = IllegalRightException.class)
    public void createSubTeam_shouldThrow_whenCaller_isNotAuthorized_to_manageTeam() {
        teamService.createSubTeam(aLambdaUser, aRootTeam,TeamName.of("aSubTeamName"));
    }
    /*@Test(expected = NullPointerException.class)
    public void createSubTeam_shouldThrow_whenRootTeamNotExist() {
       teamService.createSubTeam(aLeader,TeamName.of("NotExistingRoot"),TeamName.of("subTeam"));
    }*/
    @Test(expected = IllegalArgumentException.class)
    public void createSubTeam_shouldThrow_whenSubTeamNameAndParentNameAreEquals() {
        TeamName sameTeamName= TeamName.of("SameName");

        Team team=teamService.createSubTeam(aLeader,aRootTeam,aRootTeam.name());
    }
    @Test(expected = IllegalRightException.class)
    public void createSubTeam_shouldThrow_whenForeignTeamLeaderTryToCreateSubTeam() {
        User foreignTeamLeader=TestHelper.of().getUser("01","aForeignTeamLeader");

        Mockito.doReturn(true).when(authorizationService).isAuthorizedToManageTeam(foreignTeamLeader.toGroupMember());
        Mockito.doReturn(aRootTeam).when(teamRepository).findByName(any());

        teamService.createSubTeam(foreignTeamLeader, aRootTeam,TeamName.of("aNewSubTeamName"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void createSubTeam_shouldThrow_whenSubTeamNameAlreadyExistForThisParentTeam() {
        aRootTeam.appendSubTeam(TeamId.of("02"),TeamName.of("aTeamName"));

        Mockito.doReturn(true).when(authorizationService).isAuthorizedToManageTeam(any());
       // Mockito.doReturn(aRootTeam).when(teamRepository).findByName(any());

        Team team=teamService.createSubTeam(aLeader, aRootTeam,TeamName.of("aTeamName"));
    }
   @Test
    public void createSubTeam_shouldReturnASubTeam() {
       Mockito.doReturn(TeamId.of("02")).when(teamRepository).nextId();

       Team team=teamService.createSubTeam(aLeader, aRootTeam,TeamName.of("aNewSubTeamName"));

        Assert.assertNotNull(team);
        assertEquals(team.name(), TeamName.of("aNewSubTeamName"));
        Assert.assertFalse(team.isRootTeam());
    }
    @Test
    public void createSubTeam_shouldHave_parentTeamAsParent() {
        Mockito.doReturn(TeamId.of("02")).when(teamRepository).nextId();

        Team subTeam=teamService.createSubTeam(aLeader, aRootTeam,TeamName.of("aNewSubTeamName"));

        assertEquals(aRootTeam,subTeam.getRootTeam());
    }
    @Test
    public void createSubTeam_shouldAppendSubTeam_ChildrenListOfParent() {
        Mockito.doReturn(TeamId.of("02")).when(teamRepository).nextId();

        Team subTeam=teamService.createSubTeam(aLeader, aRootTeam,TeamName.of("aNewSubTeamName"));

        Assert.assertTrue(aRootTeam.hasTeam(subTeam.name()));
    }
   /* @Test
    public void createSubTeam_shouldAppend_SubTeam_ToTheRepository() {
        TeamName teamName=TeamName.of("aNewSubTeamName");
        teamService.createSubTeam(aTeamLeader, rootTeamName,teamName);
        Assert.assertNotNull(teamRepository.findByName(teamName));
    }*/
    @Test
    public void createSubTeam_shouldReturnASubTeam_whenCallerIsAManager() {
        Mockito.doReturn(TeamId.of("02")).when(teamRepository).nextId();

        Team subTeam=teamService.createSubTeam(aLeader, aRootTeam,TeamName.of("aNewSubTeamName"));

        Assert.assertNotNull(subTeam);
        assertEquals(subTeam.name(), TeamName.of("aNewSubTeamName"));
        Assert.assertFalse(subTeam.isRootTeam());
    }



    @Test(expected = NullPointerException.class)
    public void appendEmployee_shouldThrow_whenTeamNotFound() {
        teamService.appendEmployee(aManager, TestHelper.of().getTeam("xxx","notExist",aManager), employeeToAppend);
    }
    @Test(expected = IllegalRightException.class)
    public void appendEmployee_shouldThrow_whenCaller_IsNotAuthorized_toCreateTeam_AND_teamIsRootTeam() {
        teamService.appendEmployee(aLeader, aRootTeam, employeeToAppend);
    }
    @Test(expected = IllegalRightException.class)
    public void appendEmployee_shouldThrow_whenCaller_isNotTeamLeader_and_teamIsASubTeam() {
        User aNotAuthorizedUser=User.of(EmployeeId.of("notAuthorized"),Login.of("notAuthorized"),Password.of("notAuthorized"));
        teamService.appendEmployee(aLambdaUser,aRootTeam,employeeToAppend);
    }
    @Test(expected = IllegalRightException.class)
    public void appendEmployee_shouldThrow_WhenCaller_isTeamLeader_and_teamIsASubTeamOfHisTeam_BUT_theEmployeeNotBelongToTheRootTeam() {
        Team t=aRootTeam.appendSubTeam(TeamId.of("01"),TeamName.of("xxx"));
        Mockito.doReturn(t).when(teamRepository).findByName(any());
        teamService.appendEmployee(aLeader,t, employeeToAppend);
    }
    @Test
    public void appendEmployee_shouldAppendEmployee(){
        teamService.appendEmployee(aManager, aRootTeam, employeeToAppend);

        Assert.assertTrue(aRootTeam.hasEmployee(employeeToAppend));
    }
    @Test
    public void appendEmployee_shouldAppend_whenCaller_isTeamLeader_and_teamIsASubTeamOfHisTeam_and_theEmployeeBelongToTheRootTeam() {
        aRootTeam.appendEmployee(employeeToAppend);
        Team t=aRootTeam.appendSubTeam(TeamId.of("02"),TeamName.of("aTeamName"));
        Mockito.doReturn(t).when(teamRepository).findByName(TeamName.of("aTeamName"));

        teamService.appendEmployee(aLeader,t, employeeToAppend);
    }



    @Test(expected = IllegalRightException.class)
    public void updateTeam_shouldThrow_whenTeamIsRootAndUserIsNotAuthorizedToCreateTeam() {
        teamService.updateTeam(aLeader,aRootTeam);
    }

    @Test(expected = IllegalRightException.class)
    public void updateTeam_shouldThrow_whenTeamIsSubTeamAndUserIsNotAuthorizedToManageTeam() {
        Team t=aRootTeam.appendSubTeam(TeamId.of("02"),TeamName.of("aTeamName"));
        Mockito.doReturn(t).when(teamRepository).findByName(TeamName.of("aTeamName"));

        teamService.updateTeam(aLambdaUser,t);
    }

    @Test
    public void updateTeam_shouldUpdateTeam_whenCallerIsAManager() {
        Team t=aRootTeam.appendSubTeam(TeamId.of("02"),TeamName.of("aTeamName"));
        Mockito.doReturn(t).when(teamRepository).findByName(TeamName.of("aTeamName"));

        teamService.updateTeam(aManager,t);
    }
    @Test
    public void updateTeam_shouldUpdateTeam_whenCallerIsATeamLeader() {
        Team t=aRootTeam.appendSubTeam(TeamId.of("02"),TeamName.of("aTeamName"));
        Mockito.doReturn(t).when(teamRepository).findByName(TeamName.of("aTeamName"));

        teamService.updateTeam(aLeader,t);
    }


    @Test(expected = IllegalRightException.class)
    public void removeTeam_shouldThrow_whenTeamIsRootAndUserIsNotManagerOrAdmin() {
        Mockito.doReturn(false).when(authorizationService).isAuthorizedToCreateTeam(any());

        teamService.updateTeam(aLeader,aRootTeam);
    }
    @Test
    public void removeSubTeam_shouldRemoveSubTeam_whenCallerIsAuthorizedToManageTeam() {
        Team aSubTeam=aRootTeam.appendSubTeam(TeamId.of("02"),TeamName.of("xxx"));

        assertTrue(aRootTeam.hasTeam(aSubTeam.name()));
        teamService.removeSubTeam(aLeader,aSubTeam);
        assertFalse(aRootTeam.hasTeam(aSubTeam.name()));
    }
    @Test
    public void removeSubTeam_shouldRemoveSubTeam_whenCallerIsAuthorizedToCreateTeam() {
        Team aSubTeam=aRootTeam.appendSubTeam(TeamId.of("02"),TeamName.of("xxx"));

        Mockito.doReturn(false).when(authorizationService).isAuthorizedToManageTeam(any());
        Mockito.doReturn(true).when(authorizationService).isAuthorizedToCreateTeam(any());


        assertTrue(aRootTeam.hasTeam(aSubTeam.name()));
        teamService.removeSubTeam(aManager,aSubTeam);
        assertFalse(aRootTeam.hasTeam(aSubTeam.name()));
    }
    @Test(expected = IllegalRightException.class)
    public void removeSubTeam_shouldThrow_whenCallerIsNotAuthorizedToManageTeam() {
        Mockito.doReturn(false).when(authorizationService).isAuthorizedToManageTeam(any());

        Team aSubTeam=aRootTeam.appendSubTeam(TeamId.of("02"),TeamName.of("xxx"));

        assertTrue(aRootTeam.hasTeam(aSubTeam.name()));
        teamService.removeSubTeam(aLambdaUser,aSubTeam);
        assertTrue(aRootTeam.hasTeam(aSubTeam.name()));
    }
    @Test(expected = IllegalArgumentException.class)
    public void removeSubTeam_shouldThrow_whenTeamIsARootTeam() {
        assertTrue(aRootTeam.isRootTeam());
        teamService.removeSubTeam(aLambdaUser,aRootTeam);
    }



    @Test(expected = IllegalRightException.class)
    public void assignLeaderToTeam_shouldThrow_whenCallerIsATeamLeader_and_TeamIsARootTeam() {
        Team t=aRootTeam.appendSubTeam(TeamId.of("02"),TeamName.of("aTeamName"));
        teamService.assignLeaderToTeam(aLeader,aRootTeam,aLambdaUser);
    }
    @Test(expected = IllegalRightException.class)
    public void assignLeaderToTeam_shouldThrow_whenCallerNotTeamLeaderOfSubTeam() {
        User anotherTeamLeader=TestHelper.of().getUser("xxx","anotherTeamLeader");
        Team t=aRootTeam.appendSubTeam(TeamId.of("02"),TeamName.of("aTeamName"));
        teamService.assignLeaderToTeam(anotherTeamLeader,t,aLambdaUser);
    }
    @Test(expected = IllegalRightException.class)
    public void assignLeaderToTeam_shouldThrow_whenCallerIsNotAuthorizedToManageTeam() {
        Team t=aRootTeam.appendSubTeam(TeamId.of("02"),TeamName.of("aTeamName"));

        Mockito.doReturn(false).when(authorizationService).isAuthorizedToManageTeam(any());
        teamService.assignLeaderToTeam(aLeader,t,aLambdaUser);
    }
    @Test
    public void assignLeaderToTeam_shouldAssignTheLeaderToTheTeam_whenCallerIsAuthorizedToCreateTeam() {
        teamService.assignLeaderToTeam(aManager,aRootTeam,aLambdaUser);
        assertTrue(aRootTeam.hasTeamLeader(aLambdaUser));
    }









    /*
    @Test(expected = NullPointerException.class)
    public void removeEmployee_shouldThrow_whenTeamNotExist() {
        teamService.removeEmployee(aManager, TeamName.of("NotExistingTeamName"), getAnEmployee());
    }
    @Test(expected = IllegalRightException.class)
    public void removeEmployee_shouldThrow_whenCaller_isNotAuthorized_to_createTeam_AND_TeamIsRootTeam() {
        aRootTeam.appendEmployee(getAnEmployee());

        teamService.removeEmployee(aTeamLeader, rootTeamName, getAnEmployee());
    }
    @Test(expected = IllegalRightException.class)
    public void removeEmployee_shouldThrow_whenTeamIsSubTeamAndCallerIsForeignUser() {
        User aNotAuthorizedUser=User.of(EmployeeId.of("NotAuthorizedUser"),Login.of("NotAuthorizedUser"),Password.of("NotAuthorizedUser"));
        aRootTeam.appendEmployee(getAnEmployee());
        aSubTeam.appendEmployee(getAnEmployee());


        Assert.assertTrue(aRootTeam.hasEmployee(getAnEmployee()));
        Assert.assertTrue(aSubTeam.hasEmployee(getAnEmployee()));

        teamService.removeEmployee(aNotAuthorizedUser,subTeamName, getAnEmployee());
    }
    @Test
    public void removeEmployee_shouldRemoveEmployee_whenCallerIsManager_AND_TeamIsSubTeam() {
        aRootTeam.appendEmployee(getAnEmployee());
        aSubTeam.appendEmployee(getAnEmployee());


        Assert.assertTrue(aRootTeam.hasEmployee(getAnEmployee()));
        Assert.assertTrue(aSubTeam.hasEmployee(getAnEmployee()));
        teamService.removeEmployee(aManager,subTeamName, getAnEmployee());
        Assert.assertTrue(aRootTeam.hasEmployee(getAnEmployee()));
        Assert.assertFalse(aSubTeam.hasEmployee(getAnEmployee()));
    }
    @Test
    public void removeEmployee_shouldRemoveEmployee_whenCallerIsLeader_AND_TeamIsSubTeam() {
        aRootTeam.appendEmployee(getAnEmployee());
        aSubTeam.appendEmployee(getAnEmployee());


        Assert.assertTrue(aRootTeam.hasEmployee(getAnEmployee()));
        Assert.assertTrue(aSubTeam.hasEmployee(getAnEmployee()));
        teamService.removeEmployee(aTeamLeader,subTeamName, getAnEmployee());
        Assert.assertTrue(aRootTeam.hasEmployee(getAnEmployee()));
        Assert.assertFalse(aSubTeam.hasEmployee(getAnEmployee()));
    }
    @Test
    public void removeEmployee_shouldRemoveEmployee_fromRootTeam_whenTeamIsRootTeam() {

        aRootTeam.appendEmployee(getAnEmployee());
        Assert.assertTrue(aRootTeam.hasEmployee(getAnEmployee()));

        teamService.removeEmployee(aManager, rootTeamName, getAnEmployee());
        Assert.assertFalse(aRootTeam.hasEmployee(getAnEmployee()));
    }
    @Test
    public void removeEmployee_shouldRemoveEmployee_fromAllSubTeam_whenRemovedFromRootTeam() {
        aRootTeam.appendEmployee(getAnEmployee());
        aSubTeam.appendEmployee(getAnEmployee());

        Assert.assertTrue(aRootTeam.hasEmployee(getAnEmployee()));
        Assert.assertTrue(aSubTeam.hasEmployee(getAnEmployee()));

        teamService.removeEmployee(aManager, rootTeamName, getAnEmployee());
        Assert.assertFalse(aRootTeam.hasEmployee(getAnEmployee()));
        Assert.assertFalse(aSubTeam.hasEmployee(getAnEmployee()));
    }

    @Test
    public void getAllRootTeam_shouldGetAllRootTeamOfTheUserWhereHeIsLeader() {
        User aLeader=User.of(EmployeeId.of("leader"),Login.of("leader"),Password.of("leader"));
        Team teamA=TeamBuilder.of().setTeamId(TeamId.of("teamA")).setName(rootTeamName).setLeader(aLeader).createTeam();
        teamRepository.add(teamA);
        teamRepository.add(teamA.appendSubTeam(TeamId.of("rootTeamId-A"),TeamName.of("SubTeam-A")));
        teamRepository.add(teamA.appendSubTeam(TeamId.of("rootTeamId-B"),TeamName.of("SubTeam-B")));
        teamRepository.add(teamA.appendSubTeam(TeamId.of("rootTeamId-C"),TeamName.of("SubTeam-C")));


        assertEquals(1,teamService.getAllRootTeam(aTeamLeader).size());
    }

    @Test
    public void getAllRootTeam_shouldGetAllRootTeam_whenTheUserIsAuthorizedToCreateTeam() {
        User aLeader=User.of(EmployeeId.of("leader"),Login.of("leader"),Password.of("leader"));
        Team teamA=TeamBuilder.of().setTeamId(TeamId.of("teamA")).setName(rootTeamName).setLeader(aLeader).createTeam();
        Team teamB=TeamBuilder.of().setTeamId(TeamId.of("teamB")).setName(rootTeamName).setLeader(aLeader).createTeam();
        teamRepository.add(teamA);
        teamRepository.add(teamB);

        assertEquals(3,teamService.getAllRootTeam(aManager).size());
    }*/
}

package tollmanager.model.planning;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import tollmanager.TestHelper;
import tollmanager.infrastructure.persistance.inMemory.InMemoryGroupRepository;
import tollmanager.infrastructure.persistance.inMemory.InMemoryPlanningRepository;
import tollmanager.infrastructure.persistance.inMemory.InMemoryTeamRepository;
import tollmanager.model.access.GroupRepository;
import tollmanager.model.access.authorization.AuthorizationService;
import tollmanager.model.access.authorization.IllegalRightException;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.team.Team;
import tollmanager.model.identity.team.TeamId;
import tollmanager.model.identity.team.TeamName;
import tollmanager.model.identity.team.TeamRepository;
import tollmanager.model.identity.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;

public class PlanningServiceTest {
    private PlanningService service;
    private TestHelper helper;

    private PlanningRepository repository;
    private AuthorizationService authorizationService;
    private GroupRepository groupRepository;
    private TeamRepository teamRepository;
    private User caller;
    @Before
    public void setUp(){
        helper=TestHelper.of();
        caller=helper.getUser("01","aUser");

        groupRepository= Mockito.spy(new InMemoryGroupRepository());
        authorizationService=Mockito.spy(new AuthorizationService(groupRepository));
        teamRepository=Mockito.spy(new InMemoryTeamRepository());

        repository=Mockito.spy(new InMemoryPlanningRepository());
        service=new PlanningService(repository,authorizationService,teamRepository);
    }

    private Planning getSample(int begin,int end,String teamId,String employeeId,String planningId) {
        LocalDateTime beginDate=LocalDateTime.of(2019,1,1,begin,0);
        LocalDateTime endDate=LocalDateTime.of(2019, 1,1,end,0);
        String comment="A comment to explain something";
        return Planning.of( PlanningId.of(planningId), beginDate, endDate, comment, EmployeeId.of(employeeId),TeamId.of(teamId));
    }


    @Test
    public void providePlanning_shouldProvidePlanning_whenCallerIsAllowedToManagePlanning(){
        LocalDateTime begin=LocalDateTime.of(2019,1,1,10,0);
        LocalDateTime end=LocalDateTime.of(2019, 1,1,15,0);
        String comment="A comment to explain something";
        TeamId teamId=TeamId.of("xxx");
        Planning expectedPlanning=Planning.of( PlanningId.of("01"), begin, end, comment, EmployeeId.of("0A"),teamId);

        Mockito.doReturn(true).when(authorizationService).isAuthorizedToManagePlanning(caller.toGroupMember());
        Mockito.doReturn(PlanningId.of("01")).when(repository).nextId();


        Planning aPlanning=service.providePlanning(caller,begin,end,comment,EmployeeId.of("0A"),teamId);
        Assert.assertEquals(expectedPlanning,aPlanning);
    }
    @Test (expected = IllegalRightException.class)
    public void providePlanning_shouldThrow_whenCallerIsNotAllowedToManagePlanning(){
        LocalDateTime begin=LocalDateTime.of(2019,1,1,10,0);
        LocalDateTime end=LocalDateTime.of(2019, 1,1,15,0);
        Mockito.doReturn(false).when(authorizationService).isAuthorizedToManagePlanning(caller.toGroupMember());

        service.providePlanning(caller,begin,end,"comment",EmployeeId.of("0A"),TeamId.of("xxx"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void providePlanning_shouldThrow_whenEndingDateIsLessThanBeginningDate(){
        LocalDateTime begin=LocalDateTime.of(2019,1,1,10,0);
        LocalDateTime end=LocalDateTime.of(2019, 1,1,9,0);

        service.providePlanning(caller,begin,end,"comment",EmployeeId.of("0A"),TeamId.of("xxx"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void providePlanning_shouldThrow_whenAPlanningAlreadExistBetweenBeginAndEnd(){
        LocalDateTime begin=LocalDateTime.of(2019,1,1,10,0);
        LocalDateTime end=LocalDateTime.of(2019, 1,1,15,0);
        String comment="A comment to explain something";
        TeamId teamId=TeamId.of("xxx");
        Planning alreadyExisting=Planning.of( PlanningId.of("01"), begin, end, comment, EmployeeId.of("0A"),teamId);
        LinkedHashSet<Planning> existingPlannings=new LinkedHashSet<>();
        existingPlannings.add(alreadyExisting);

        Mockito.doReturn(true).when(authorizationService).isAuthorizedToManagePlanning(caller.toGroupMember());
        Mockito.doReturn(PlanningId.of("01")).when(repository).nextId();
        Mockito.doReturn(existingPlannings).when(repository).findAllPlanningFromEmployeeBetween(any(),any(),any());

        service.providePlanning(caller,begin,end,comment,EmployeeId.of("A"),teamId);

    }



    @Test
    public void getPlanningOfWeek_shouldReturnMapWithOneEntryForEachDay() {
        LinkedHashMap<LocalDate, Set<Planning>> weekPlanning;
        weekPlanning=service.getPlanningOfWeek(caller.employeeId(), LocalDate.of(2019,10,3));  //week 30 -> 6
        Assert.assertEquals(7,weekPlanning.size());
        Assert.assertTrue(weekPlanning.containsKey(LocalDate.of(2019,9,30)));
        Assert.assertTrue(weekPlanning.containsKey(LocalDate.of(2019,10,6)));
    }

    @Test
    public void getPlanningOfWeek_shouldPutEmptySetOfPlanning_whenNoPlanningForOneDay(){
        LinkedHashMap<LocalDate, Set<Planning>> weekPlanning;
        weekPlanning=service.getPlanningOfWeek(caller.employeeId(),LocalDate.of(2019,10,3));

        Assert.assertTrue(weekPlanning.values().stream().findFirst().orElseThrow().isEmpty());
    }

    @Test
    public void removePlanning_shouldRemovePlanning(){
        Planning p=getSample(8,10,"aaa","xxx","aaa");
        repository.add(p);
        Mockito.doReturn(true).when(authorizationService).isAuthorizedToManagePlanning(caller.toGroupMember());
        Mockito.doReturn(true).when(authorizationService).isAuthorizedToCreateTeam(caller.toGroupMember());


        service.removePlanning(caller,p);
        Assert.assertNull(repository.findById(p.planningId()));
    }

    @Test(expected = IllegalRightException.class)
    public void removePlanning_shouldThrow_whenCallerHasNot_RoleToManagePlanning(){
        Planning p=getSample(8,10,"aaa","xxx","aaa");
        repository.add(p);

        Mockito.doReturn(false).when(authorizationService).isAuthorizedToManagePlanning(any());

        service.removePlanning(caller,p);
        Assert.assertNull(repository.findById(p.planningId()));
    }

    @Test(expected = IllegalRightException.class)
    public void removePlanning_shouldThrow_whenCallerIsATeamLeader_AndTryToRemovePlanningOfOtherTeam(){
        Planning p=getSample(8,10,"aaa","xxx","aaa");
        repository.add(p);

        Mockito.doReturn(true).when(authorizationService).isAuthorizedToManagePlanning(any());
        Mockito.doReturn(false).when(authorizationService).isAuthorizedToCreateTeam(any());
        Mockito.doReturn(helper.getTeam("aaa","aName",helper.getUser("anotherLeader","aLoginForLeader"))).when(teamRepository).findById(any());

        service.removePlanning(caller,p);
    }

}

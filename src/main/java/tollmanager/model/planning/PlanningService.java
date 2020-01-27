package tollmanager.model.planning;

import tollmanager.model.access.authorization.IAuthorizationService;
import tollmanager.model.access.authorization.IllegalRightException;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.team.Team;
import tollmanager.model.identity.team.TeamId;
import tollmanager.model.identity.team.TeamRepository;
import tollmanager.model.identity.user.User;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * The service that deals with plannings
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class PlanningService implements IPlanningService {
    private PlanningRepository planningRepository;
    private IAuthorizationService authorizationService;
    private TeamRepository teamRepository;

    public PlanningService(PlanningRepository planningRepository,IAuthorizationService authorizationService,TeamRepository teamRepository){
        Objects.requireNonNull(planningRepository,"The planning repository is required");
        Objects.requireNonNull(authorizationService,"The authorization service is required");
        Objects.requireNonNull(teamRepository,"The team repository is required");
        this.planningRepository=planningRepository;
        this.authorizationService=authorizationService;
        this.teamRepository=teamRepository;
    }


    /**
     * Return a planning if Caller is authorized to manage planning, else throw error.
     *
     * @throws IllegalRightException if the caller is not authorized to manage planning
     * @throws IllegalArgumentException if the ending date is less than the beginning date
     *
     * @return A planning
     */
    @Override
    public Planning providePlanning(User caller, LocalDateTime begin, LocalDateTime end, String comment, EmployeeId belonging, TeamId teamId) {
        Objects.requireNonNull(caller, "The caller is required");
        Objects.requireNonNull(begin, "The beginning date is required");
        Objects.requireNonNull(end, "The ending date is required");
        Objects.requireNonNull(comment, "The comment is required");
        Objects.requireNonNull(belonging, "the employee id of belonging is required");

        if (end.isBefore(begin))
            throw new IllegalArgumentException("The beginning date must be less than ending date, actual:" + begin + ">" + end);

        if (!authorizationService.isAuthorizedToManagePlanning(caller.toGroupMember()))
            throw IllegalRightException.manageTeam(caller.toGroupMember());

        if(!planningRepository.findAllPlanningFromEmployeeBetween(belonging,begin,end).isEmpty()) {
            throw new IllegalArgumentException("There is already a planning for these dates/hours");
        }

        Planning planning = Planning.of(planningRepository.nextId(), begin, end, comment, belonging, teamId);
        planningRepository.add(planning);
        return planning;
    }
    /**
     * Return a complete list of planning for each day of the week for the employee,
     * If no planning for a day then put empty set , else put a linkedList with all planning (1 or more)
     * @param employeeId the id of the employee linked to the planning
     * @param date the date of week
     * @return
     */
    @Override
    public LinkedHashMap<LocalDate, Set<Planning>> getPlanningOfWeek(EmployeeId employeeId,LocalDate date) {
        LinkedHashMap<LocalDate,Set<Planning>> weekPlanning=new LinkedHashMap<>();

        DayOfWeek firstDay=WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        DayOfWeek lastDay=DayOfWeek.of(((firstDay.getValue() + 5) % DayOfWeek.values().length) + 1);

        LocalDate dateBegin=date.with(TemporalAdjusters.previousOrSame(firstDay));
        LocalDate dateEnding=date.with(TemporalAdjusters.nextOrSame(lastDay));

        List<LocalDate> daysOfWeek=Stream.iterate(dateBegin, d->!d.isAfter(dateEnding), d->d.plusDays(1)).collect(Collectors.toList());


        daysOfWeek.forEach(d->{
            Set<Planning> daysPlanning=planningRepository.findAllPlanningForEmployeeAtDay(employeeId,d);
            weekPlanning.put(d,daysPlanning);
        });


        return weekPlanning;
    }

    /**
     * Remove the planning
     *
     * @exception IllegalRightException if the caller is not authorized to manage planning
     * @exception  RuntimeException if the team linked to the planning not found
     * @exception IllegalRightException if the caller is not a team leader of the team linked to the planning
     *
     * @param caller user who's called the service
     * @param planning planning to remove
     */
    @Override
    public void removePlanning(User caller, Planning planning) {
        if(!authorizationService.isAuthorizedToManagePlanning(caller.toGroupMember()))
            throw new IllegalRightException("Manage planning");

        if(!authorizationService.isAuthorizedToCreateTeam(caller.toGroupMember())) {
            Team team=teamRepository.findById(planning.teamId());
            if(team==null)
                throw new RuntimeException("The team linked to the planning not found + "+planning.teamId());
            if(!team.hasTeamLeader(caller))
                throw new IllegalRightException("Remove planning from a foreign team where you are not leader");
        }
        planningRepository.removePlanning(planning);
    }
}

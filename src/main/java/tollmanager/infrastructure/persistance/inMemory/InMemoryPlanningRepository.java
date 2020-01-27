package tollmanager.infrastructure.persistance.inMemory;

import tollmanager.model.identity.EmployeeId;
import tollmanager.model.planning.Planning;
import tollmanager.model.planning.PlanningId;
import tollmanager.model.planning.PlanningRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class InMemoryPlanningRepository implements PlanningRepository {
    private LinkedHashSet<Planning> plannings;

    public InMemoryPlanningRepository(){
        plannings=new LinkedHashSet<>();
    }

    @Override
    public PlanningId nextId() {
        return PlanningId.of(UUID.randomUUID().toString());
    }

    @Override
    public void add(Planning planning) {
        plannings.add(planning);
    }

    @Override
    public LinkedHashSet<Planning> findAllPlanningForEmployeeAtDay(EmployeeId employeeId, LocalDate date) {
        return plannings.stream()
                .filter(p -> p.employeeId().equals(employeeId) && p.dayBeginningDate().equals(date))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<Planning> findAllPlanningFromEmployeeBetween(EmployeeId employeeId, LocalDateTime begin, LocalDateTime end) {
        return plannings.stream()
                .filter(p->{
                    if(!p.employeeId().equals(employeeId))
                        return false;
                    if(p.dayBeginningDate().equals(begin.toLocalDate()) && p.dayEndingDate().equals(end.toLocalDate())) {
                        if(begin.getHour()<= p.beginning().getHour() && end.getHour()>= p.ending().getHour() ||
                                p.beginning().getHour() <= begin.getHour() && p.ending().getHour() >= end.getHour())
                            return true;
                    }
                    return false;
                })
                .collect(Collectors.toSet());
    }

    @Override
    public Planning findById(PlanningId planningId) {
        return plannings.stream().filter(p->p.planningId().equals(planningId)).findFirst().orElse(null);
    }

    @Override
    public void removePlanning(Planning planning) {
        plannings.removeIf(p->p.planningId().equals(planning.planningId()));
    }

}

package tollmanager.infrastructure.persistance.postgres;

import org.junit.Before;
import org.junit.Test;
import tollmanager.TestHelper;
import tollmanager.infrastructure.persistance.DatabaseConnection;
import tollmanager.model.planning.Planning;

import java.sql.SQLException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PostgresPlanningRepositoryTest {
    private PostgresPlanningRepository repository;

    @Before
    public void setUp() throws SQLException {
        DatabaseConnection.instance().setAutoCommit(false);
        repository=new PostgresPlanningRepository();
    }


    @Test
    public void add_shouldAppendPlanningToDB() throws SQLException {
        Planning planning= TestHelper.of().getPlanning("aaa",8,10,"employeeID","TeamId");
        repository.add(planning);
        assertEquals(planning,repository.findById(planning.planningId()));
    }
    @Test
    public void removePlanning_shouldRemovePlanningToDB() throws SQLException {
        Planning planning= TestHelper.of().getPlanning("aaa",8,10,"employeeID","TeamId");
        repository.add(planning);
        assertEquals(planning,repository.findById(planning.planningId()));

        repository.removePlanning(planning);
        assertNull(repository.findById(planning.planningId()));
    }

    @Test
    public void findAllPlanningForEmployeeAtDay_shouldGiveASetWithAllPlanningFound() throws SQLException {
        Planning p1= TestHelper.of().getPlanning("aaa",8,10,"employeeID","TeamId");
        repository.add(p1);
        Planning p2= TestHelper.of().getPlanning("bbb",15,20,"employeeID","TeamId");
        repository.add(p2);

        Set<Planning>set =repository.findAllPlanningForEmployeeAtDay(p1.employeeId(),p1.dayBeginningDate());
        assertEquals(2,set.size());
    }


    @Test
    public void findAllPlanningFromEmployeeBetween_shouldGiveASetWithAllPlanningFound() throws SQLException {
        Planning p1= TestHelper.of().getPlanning("aaa",8,10,"employeeID","TeamId");
        repository.add(p1);
        Planning p2= TestHelper.of().getPlanning("bbb",12,16,"employeeID","TeamId");
        repository.add(p2);
        Planning p3= TestHelper.of().getPlanning("ccc",16,20,"employeeID","TeamId");
        repository.add(p3);

        Set<Planning>set =repository.findAllPlanningFromEmployeeBetween(p1.employeeId(),p1.beginning(),p2.ending());
        assertEquals(2,set.size());
    }





}

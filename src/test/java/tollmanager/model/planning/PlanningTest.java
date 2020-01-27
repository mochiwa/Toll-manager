package tollmanager.model.planning;

import org.junit.Assert;
import org.junit.Test;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.team.TeamId;

import java.time.LocalDateTime;

public class PlanningTest {

    @Test
    public void endingDate_equalsBeginData_whenEndingDateIsLessThanBeginning(){
        Planning expected=Planning.of(PlanningId.of("01"),
                LocalDateTime.of(2000,10,10,10,10),
                LocalDateTime.of(2000,10,10,10,10),
                "",
                EmployeeId.of("x"),
                TeamId.of("xxx"));

        Assert.assertEquals(expected, Planning.of(
                PlanningId.of("01"),
                LocalDateTime.of(2000,10,10,10,10),
                LocalDateTime.of(1000,10,10,10,10),
                "",
                EmployeeId.of("x"),
                TeamId.of("xxx")));
    }

}

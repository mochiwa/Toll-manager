package tollmanager.infrastructure.persistance.postgres;

import tollmanager.infrastructure.persistance.DatabaseConnection;
import tollmanager.model.identity.EmployeeId;
import tollmanager.model.identity.team.TeamId;
import tollmanager.model.planning.Planning;
import tollmanager.model.planning.PlanningId;
import tollmanager.model.planning.PlanningRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
/**
 * Implementation of repository for planning with Postgres
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class PostgresPlanningRepository implements PlanningRepository {
    /**
     * Provide a new unique id from UUID
     * @return PlanningId
     */
    @Override
    public PlanningId nextId() {
        return PlanningId.of(UUID.randomUUID().toString());
    }

    private DatabaseConnection db;

    public PostgresPlanningRepository() throws SQLException {
        this.db=DatabaseConnection.instance();
    }

    /**
     * Append a planning into the database
     * @param planning planning to append to the repository
     */
    @Override
    public void add(Planning planning) {
        try{
            PreparedStatement statement=db.connection().prepareStatement("SELECT * FROM f_create_planning(?,?,?,?,?,?)");
            statement.setString(1,planning.planningId().value());
            statement.setTimestamp(2, Timestamp.valueOf(planning.beginning()));
            statement.setTimestamp(3, Timestamp.valueOf(planning.ending()));
            statement.setString(4, planning.comment());
            statement.setString(5,planning.employeeId().value());
            statement.setString(6,planning.teamId().value());
            statement.executeQuery();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Find all planning linked to an employee for a date
     * @param employeeId linked to a planning
     * @param date the date of beginning planning
     * @return set of planning
     */
    @Override
    public Set<Planning> findAllPlanningForEmployeeAtDay(EmployeeId employeeId, LocalDate date) {
        LinkedHashSet<Planning> set=new LinkedHashSet<>();
        try{
            PreparedStatement statement=db.connection().prepareStatement("SELECT * FROM find_planning_for_employee_at_date(?,?)");
            statement.setString(1,employeeId.value());
            statement.setDate(2, Date.valueOf(date));
            ResultSet resultSet=statement.executeQuery();
            while(resultSet.next())
                set.add(buildPlanning(resultSet));
        }catch (Exception e){
            e.printStackTrace();
        }
        return set;
    }

    private Planning buildPlanning(ResultSet resultSet) throws SQLException {
            return Planning.of(
                    PlanningId.of(resultSet.getString("planning_id")),
                    resultSet.getTimestamp("planning_beginning").toLocalDateTime(),
                    resultSet.getTimestamp("planning_ending").toLocalDateTime(),
                    resultSet.getString("planning_comment"),
                    EmployeeId.of(resultSet.getString("employee_id")),
                    TeamId.of(resultSet.getString("team_id"))
            );
    }

    /**
     * Find all planning linked to an employee between 2 dates
     * @param employeeId the employee linked to the planning
     * @param begin beginning datetime
     * @param end ending datetime
     * @return
     */
    @Override
    public Set<Planning> findAllPlanningFromEmployeeBetween(EmployeeId employeeId, LocalDateTime begin, LocalDateTime end) {
        LinkedHashSet<Planning> set=new LinkedHashSet<>();
        try{
            PreparedStatement statement=db.connection().prepareStatement("SELECT * FROM find_plannings_for_employee_between_date(?,?,?)");
            statement.setString(1,employeeId.value());
            statement.setTimestamp(2,Timestamp.valueOf(begin) );
            statement.setTimestamp(3,Timestamp.valueOf(end));
            ResultSet resultSet=statement.executeQuery();
            while(resultSet.next())
                set.add(buildPlanning(resultSet));
        }catch (Exception e){
            e.printStackTrace();
        }
        return set;
    }

    /**
     * Find the planning by id
     * @param planningId the planning id to search
     * @return planning or null
     */
    @Override
    public Planning findById(PlanningId planningId) {
        Planning planning=null;
        try{
            PreparedStatement statement=db.connection().prepareStatement("SELECT * FROM f_get_planning_by_id(?)");
            statement.setString(1,planningId.value());
            ResultSet resultSet=statement.executeQuery();
            if(resultSet.next() && resultSet.getString("planning_id")!=null)
                planning= buildPlanning(resultSet);
        }catch (Exception e){ e.printStackTrace(); }
        return planning;
    }

    /**
     * Remove planning from database
     * @param planning to remove
     */
    @Override
    public void removePlanning(Planning planning) {
        try{
            PreparedStatement statement=db.connection().prepareStatement("CALL p_remove_planning(?)");
            statement.setString(1,planning.planningId().value());
            statement.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

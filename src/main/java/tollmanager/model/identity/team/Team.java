package tollmanager.model.identity.team;


import tollmanager.model.identity.Employee;
import tollmanager.model.identity.user.User;
import tollmanager.model.shared.Observable;
import tollmanager.model.shared.Observer;

import java.util.*;
/**
 * Represents a team
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class Team implements Observable {
    private TeamId teamId;
    private TeamName teamName;
    private User leader;
    private String description;
    private LinkedHashSet<Employee> employees;
    private Team parentTeam;
    private LinkedHashSet<Team> subTeams;
    private ArrayList<Observer> observers;

    private Team(TeamId teamId, TeamName teamName, User leader, String description, LinkedHashSet<Employee> employees, Team parentTeam, LinkedHashSet<Team> subTeams) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.leader = leader;
        this.employees = employees;
        this.description = description;
        this.parentTeam = (parentTeam==null) ? this: parentTeam;
        this.subTeams = subTeams;
        subTeams.forEach(team -> team.parentTeam=this);

        observers=new ArrayList<>();
    }


    public static Team of(TeamId teamId, TeamName teamName, User leader, String description, LinkedHashSet<Employee> employees, Team parentTeam, LinkedHashSet<Team> subTeams) {
        Objects.requireNonNull(teamId, "The team id is required.");
        Objects.requireNonNull(teamName, "The team name is required.");
        Objects.requireNonNull(leader, "The team leader is required.");
        Objects.requireNonNull(employees, "The set of employees is required.");
        Objects.requireNonNull(description, "The description is required.");
        Objects.requireNonNull(subTeams, "The set of sub Team is required.");

        return new Team(teamId, teamName, leader, description, employees, parentTeam, subTeams);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(teamId, team.teamId) &&
                Objects.equals(teamName, team.teamName) &&
                Objects.equals(leader, team.leader) &&
                Objects.equals(description, team.description);
    }
    @Override
    public int hashCode() {
        return Objects.hash(teamId, teamName, leader, description);

    }

    public TeamName name() {
        return teamName;
    }

    /**
     * Append a subTeam and notify observer
     * @param id
     * @param name
     * @return
     */
    public Team appendSubTeam(TeamId id, TeamName name) {
        Objects.requireNonNull(id,"The team id is required.");
        Objects.requireNonNull(name,"The team name is required.");

        Team team=TeamBuilder.of()
                .setTeamId(id)
                .setName(name)
                .setParent(this)
                .setLeader(leader)
                .createTeam();
        subTeams.add(team);
        updateAll();
        return team;
    }

    /**
     * Append an employee, if team is a subTeam then parent will append
     * employee too, as far as root team.
     * @param employee to append
     */
    public void appendEmployee(Employee employee) { //placer dans un pattern obs ?
        Objects.requireNonNull(employee, "The employee is required.");

        employees.add(employee);
        if(!isRootTeam())
            parentTeam.appendEmployee(employee);
        updateAll();
    }

    /**
     * Since parent have all employees from child , then has employee
     * will found directly if sub team contains the employee
     * and notify observer
     *
     * @param employee to found
     * @return true if found , else false
     */
    public boolean hasEmployee(Employee employee) {
        Objects.requireNonNull(employee, "The employee is required.");

        return employees.stream().anyMatch(e -> e.equals(employee));
    }

    /**
     * Search among all sub teams if a team with name is present
     * @param teamName
     * @return true if found , else false
     */
    public boolean hasTeam(TeamName teamName) {
        Objects.requireNonNull(teamName,"The team name is required.");
        if(teamName.equals(name()))//
            return true;
        return subTeam(teamName)!=null;
    }

    /**
     * @param teamName team looking for
     * @return team or null
     */
    private Team subTeam(TeamName teamName) {
        Team subTeam= subTeams.stream().filter(t->t.teamName.equals(teamName)).findFirst().orElse(null);
        if(subTeam==null && !subTeams.isEmpty())
            return subTeams.stream().filter(t->t.subTeam(teamName)!=null).findFirst().orElse(null);
        return subTeam;
    }

    /**
     * Search among all sub teams if a team with id is present
     * @param teamId
     * @return true if found , else false
     */
    public boolean hasTeam(TeamId teamId) {
        Objects.requireNonNull(teamId,"The team id is required.");
        if(this.teamId.equals(teamId))
            return true;
        return subTeam(teamId)!=null;
    }
    /**
     * @param teamId team looking for
     * @return team or null
     */
    private Team subTeam(TeamId teamId) {
        Team subTeam= subTeams.stream().filter(t->t.teamId.equals(teamId)).findFirst().orElse(null);
        if(subTeam==null && !subTeams.isEmpty())
            return subTeams.stream().filter(t->t.subTeam(teamId)!=null).findFirst().orElse(null);
        return subTeam;
    }


    public boolean hasTeamLeader(User user) {
        if(leader.equals(user))
            return true;
        if(!isRootTeam())
            return parentTeam.hasTeamLeader(user);
        return false;
    }

    /**
     * @return the root team of team
     */
    public Team getRootTeam() {
        return (teamId.equals(parentTeam.teamId)) ? this : parentTeam.getRootTeam();
    }

    /**
     * @return if the team has itself like parent
     */
    public boolean isRootTeam() {
        return teamId.equals(parentTeam.teamId);
    }

    /**
     * Remove the employee of this team and all sub Team
     * of this team.
     * @param employee to remove
     */
    public void removeEmployee(Employee employee) { // obs ?
        Objects.requireNonNull(employee,"The employee is required.");

        subTeams.forEach( team -> {team.removeEmployee(employee);});
        employees.remove(employee);
        getRootTeam().updateAll();
    }

    public User leader() {
        return leader;
    }
    public TeamId teamId(){return teamId;}

    /**
     * @return count of employee in the team
     */
    public int employeeCount() {
        return employees.size();
    }

    /**
     * @return set of all subteam
     */
    public Set<Team> subTeams() {
        return new HashSet<Team>(subTeams);
    }

    /**
     * @return set of all employee
     */
    public Set<Employee> employees() {
        return new HashSet<Employee>(employees);
    }

    /**
     * Collects all employees who not belong to any sub team
     * @return list employees
     */
    public Set<Employee> leafEmployees() {
        HashSet<Employee> leafEmployee=new HashSet<Employee>();
        if(subTeams.isEmpty())
            return new HashSet<>(employees);

        employees.forEach(employee -> {
            if(subTeams.stream().noneMatch(t->t.hasEmployee(employee)))
                leafEmployee.add(employee);
        });
        return leafEmployee;
    }


    @Override
    public void registry(Observer observer) {
        if(!observers.contains(observer))
            observers.add(observer);
    }

    @Override
    public void unregister(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void updateAll() {
        for (Observer observer : ((List<Observer>)observers.clone()))
            observer.update(this);
    }

    public Team parent() {
        return parentTeam;
    }


    private void removeLastParent(Team team) {
        if(!hasTeam(team.name()))
            return;

        subTeams.forEach(t-> t.removeLastParent(team));
        subTeams.remove(team);
    }

    /**
     * Remove the sub team
     * @param team
     */
    public void removeTeam(Team team) {
        removeLastParent(team);
        getRootTeam().updateAll();
    }

    /**
     * Change the leader of the team
     * @param user
     */
    public void changeLeader(User user) {
        Objects.requireNonNull(user,"The user is required.");
        leader=user;
    }

    public String description() {
        return description;
    }

    public void setParent(Team parent) {
        Objects.requireNonNull(parent);
        parentTeam=parent;
    }

    public void setSubTeams(LinkedHashSet<Team> teams){
        subTeams=teams;
    }

    /**
     * @param teamName the team name searched
     * @return the subTeam if found or null
     */
    public Team getSubTeam(TeamName teamName) {
        if(teamName.equals(name()))
            return this;

        Team subTeam=subTeams.stream().filter(t->t.hasTeam(teamName)).findFirst().orElse(null);
        if(subTeam!=null)
            return subTeam.getSubTeam(teamName);

        return null;
    }

    /**
     * @param teamId the id search
     * @return the sub team or null
     */
    public Team getSubTeam(TeamId teamId) {
        if(teamId.equals(this.teamId))
            return this;

        Team subTeam=subTeams.stream().filter(t->t.hasTeam(teamId)).findFirst().orElse(null);
        if(subTeam!=null)
            return subTeam.getSubTeam(teamId);
        return null;
    }

    /**
     * @param employee the employee
     * @return true if the employee is leader , false else
     */
    public boolean isLeader(Employee employee){
        return employee.employeeId().equals(leader().employeeId());
    }


}

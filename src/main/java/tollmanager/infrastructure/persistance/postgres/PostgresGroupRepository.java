package tollmanager.infrastructure.persistance.postgres;

import tollmanager.infrastructure.persistance.DatabaseConnection;
import tollmanager.model.access.*;
import tollmanager.model.identity.EmployeeId;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
/**
 * Implementation of repository for group with Postgres
 * @author chiappelloni nicolas
 * @version 1.0
 */
public class PostgresGroupRepository implements GroupRepository {
    private DatabaseConnection db;
    public PostgresGroupRepository() throws SQLException {
        db= DatabaseConnection.instance();
    }

    /**
     * Append a group into the database
     * @param group to append into the repository
     */
    @Override
    public void add(Group group) {
        PreparedStatement statement= null;
        try {
            statement = db.connection().prepareStatement("SELECT * FROM f_create_group(?)");
            statement.setString(1,group.name().value());
            ResultSet resultSet=statement.executeQuery();

            update(group);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Find  group by his name
     * @param groupName the group name to find
     * @return group or null
     */
    @Override
    public Group findByName(GroupName groupName) {
        PreparedStatement statement= null;
        Group group=null;
        try {
            statement = db.connection().prepareStatement("SELECT * FROM find_group_by_name(?)");
            statement.setString(1,groupName.value());
            ResultSet resultSet=statement.executeQuery();

            if(!resultSet.next())
                return null;
            group=GroupBuilder.of()
                    .setName(groupName)
                    .setRoles(findRoles(groupName))
                    .setMembers(findUser(groupName))
                    .create();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return group;
    }

    private LinkedHashSet<GroupMember> findUser(GroupName groupName){
        LinkedHashSet<GroupMember> members=new LinkedHashSet<>();
        try {
            PreparedStatement statement= null;
            statement = db.connection().prepareStatement("SELECT * FROM find_member_of_group(?)");
            statement.setString(1,groupName.value());
            ResultSet resultSet=statement.executeQuery();

            while (resultSet.next()){
                GroupMember member=GroupMember.of(
                        EmployeeId.of(resultSet.getString("employee_id")),
                        resultSet.getString("member_name")
                );
                members.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    private LinkedHashSet<Role> findRoles(GroupName groupName){
        LinkedHashSet<Role> roles=new LinkedHashSet<>();
        try {
            PreparedStatement statement= db.connection().prepareStatement("SELECT * FROM find_rule_of_group(?)");
            statement.setString(1,groupName.value());
            ResultSet resultSet=statement.executeQuery();

            while (resultSet.next()){

                Role role=Role.of(
                        resultSet.getString("rule_name"),
                        resultSet.getString("rule_description"),
                        GroupName.of(resultSet.getString("rule_target"))
                );
                roles.add(role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    /**
     * Find all group where the groupmember belongs
     * @param groupMember the member
     * @return list of group or null
     */
    @Override
    public List<Group> groupsWhereBelong(GroupMember groupMember) {
        Objects.requireNonNull(groupMember,"the group member is required");
        ArrayList<Group> groups=new ArrayList<>();

        try {
            PreparedStatement statement= null;
            statement = db.connection().prepareStatement("SELECT * FROM find_groupName_where_belong(?) AS group_name");
            statement.setString(1,groupMember.getEmployeeId().value());
            ResultSet resultSet=statement.executeQuery();

            while (resultSet.next()){
                Group group=findByName(GroupName.of(resultSet.getString("group_name")));
                if(group!=null)
                    groups.add(group);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groups;
    }

    /**
     * find all group in repository
     * @return set of group
     */
    @Override
    public Set<Group> findAll() {
        LinkedHashSet<Group> groups=new LinkedHashSet<>();

        try {
            PreparedStatement statement= null;
            statement = db.connection().prepareStatement("SELECT * FROM find_all_groupName() AS group_name");
            ResultSet resultSet=statement.executeQuery();

            while (resultSet.next()){
                Group group=findByName(GroupName.of(resultSet.getString("group_name")));
                if(group!=null)
                    groups.add(group);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groups;
    }

    /**
     * Update the group in database
     * @param group to update
     */
    @Override
    public void update(Group group) {
        Objects.requireNonNull(group,"The group is required");

        appendNewMember(group);
        removeDeletedMember(group);
        appendNewRole(group);
        removeDeletedRole(group);
    }

    private void removeDeletedRole(Group group) {
        Group saved=findByName(group.name());

        saved.roles().forEach(role -> {
            if(!group.hasRole(role))
                try {
                    PreparedStatement statement = db.connection().prepareStatement("CALL p_remove_rule_to_group(?,?,?)");
                    statement.setString(1,group.name().value());
                    statement.setString(2,role.name());
                    statement.setString(3,role.targetToString());
                    statement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        });
    }
    private void appendNewRole(Group group) {
        Group saved=findByName(group.name());

        group.roles().forEach(role -> {
            if(!saved.hasRole(role))
                try {
                    PreparedStatement statement = db.connection().prepareStatement("SELECT * FROM f_create_rule(?,?,?)");
                    statement.setString(1,role.name());
                    statement.setString(2,role.description());
                    statement.setString(3,role.targetToString());
                    ResultSet r=statement.executeQuery();

                    if(!r.next())
                        throw new RuntimeException();

                    statement = db.connection().prepareStatement("CALL p_append_rule_to_group(?,?)");
                    statement.setString(1,group.name().value());
                    statement.setInt(2,r.getInt(1));
                    statement.execute();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
        });
    }
    private void removeDeletedMember(Group group) {
        Group saved=findByName(group.name());

        saved.members().forEach(member -> {
            if(!group.hasMember(member))
                try {
                    PreparedStatement statement = db.connection().prepareStatement("CALL p_remove_member_to_group(?,?)");
                    statement.setString(1,member.getEmployeeId().value());
                    statement.setString(2,group.name().value());
                    statement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        });
    }
    private void appendNewMember(Group group){
        Group saved=findByName(group.name());

        group.members().forEach(member -> {
            if(!saved.hasMember(member))
                try {
                    PreparedStatement statement = db.connection().prepareStatement("CALL p_append_member_to_group(?,?)");
                    statement.setString(1,member.getEmployeeId().value());
                    statement.setString(2,group.name().value());
                    statement.execute();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        });
    }
}

package tollmanager.infrastructure.persistance.inMemory;

import tollmanager.model.access.Group;
import tollmanager.model.access.GroupMember;
import tollmanager.model.access.GroupName;
import tollmanager.model.access.GroupRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class InMemoryGroupRepository implements GroupRepository {
    private LinkedHashSet<Group> groups;

    public InMemoryGroupRepository()
    {
        groups=new LinkedHashSet<>();
    }

    @Override
    public void add(Group group) {
        groups.add(group);
    }

    @Override
    public Group findByName(GroupName name) {
        return groups.stream()
                .filter(g->g.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Group> groupsWhereBelong(GroupMember member) {
        return groups.stream()
                .filter(g->g.hasMember(member))
                .collect(Collectors.toList());
    }

    @Override
    public Set<Group> findAll() {
        return new LinkedHashSet<Group>(groups);
    }

    @Override
    public void update(Group group) {

    }
}

package graphic.components.team.tree;

import graphic.components.team.tree.employee.EmployeeTreeCell;
import graphic.components.team.tree.team.TeamTreeCell;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.team.Team;
import tollmanager.model.shared.Observable;
import tollmanager.model.shared.Observer;

import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

public class TreeTeam extends TreeView<ITreeTeamCell> implements Observer {
    private ObservableSet<Team> rootTeams;
    private TreeItem<ITreeTeamCell> rootItem;

    public TreeTeam(ObservableSet<Team> rootTeams) {
        this.rootTeams = rootTeams;

        rootItem = new TreeItem<>(null);
        setRoot(rootItem);
        rootItem.setExpanded(true);
        this.setCellFactory(teamTreeView -> new TextTreeTeamCellFactory());


        this.rootTeams.addListener((SetChangeListener<Team>) change -> {
            if (change.wasAdded()) {
                appendRootItems(change.getElementAdded());
            } else if (change.wasRemoved()) {
                removeRootItems(change.getElementRemoved());
            }
        });
    }

    private void removeRootItems(Team rootTeam) {
        Objects.requireNonNull(rootTeam);
        rootTeam.unregister(this);
        rootItem.getChildren().removeIf(i->rootTeam.equals(i.getValue().getUserData()));
        sort();
    }

    private void appendRootItems(Team rootTeam) {
        TreeItem<ITreeTeamCell> item=new TreeItem<>(new TeamTreeCell(rootTeam));
        rootTeam.registry(this);
        rootItem.getChildren().add(item);
        appendSubElementRecursively(item,rootTeam);
        item.setExpanded(true);
        sort();
    }

    private void appendSubElementRecursively(TreeItem<ITreeTeamCell> itemParent, Team teamParent){
        itemParent.getChildren().addListener((ListChangeListener<TreeItem<ITreeTeamCell>>) change -> {
            change.next();
            if(change.wasAdded() || change.wasRemoved())
                itemParent.getChildren().sort((firstElement, secondElement) -> {
                    try{
                        Employee a=(Employee) firstElement.getValue().getUserData();
                        if(teamParent.isLeader(a)) {return -1;}
                    }catch (Exception ignored){return 1;}
                    return firstElement.getValue().getName().compareTo(secondElement.getValue().getName());
                });
        });

        teamParent.leafEmployees().forEach(employee->{
            TreeItem<ITreeTeamCell> item=new TreeItem<>(new EmployeeTreeCell(employee,teamParent));
            itemParent.getChildren().add(item);
        });

        teamParent.subTeams().forEach(subTeam->{
            TreeItem<ITreeTeamCell> item=new TreeItem<>(new TeamTreeCell(subTeam));
            itemParent.getChildren().add(item);
            subTeam.registry(this);
            appendSubElementRecursively(item, subTeam);
        });

    }

    @Override
    public void update(Observable observable) {
        Team team=(Team) observable;
        Team root=team.getRootTeam();
        removeRootItems(root);
        appendRootItems(root);
    }

    private void sort(){
        rootItem.getChildren().sort(Comparator.comparing(a -> a.getValue().getName()));
    }


    public void loadAllEmployeeToRootTeam(boolean activated){
        rootItem.getChildren().clear();
        if(activated)
            rootTeams.forEach(team->{
                TreeItem<ITreeTeamCell> itemTeam=new TreeItem<>(new TeamTreeCell(team));
                team.registry(this);
                rootItem.getChildren().add(itemTeam);
                itemTeam.setExpanded(true);

                team.employees().forEach(employee -> {
                    TreeItem<ITreeTeamCell> itemEmployee=new TreeItem<>(new EmployeeTreeCell(employee,team));
                    itemTeam.getChildren().add(itemEmployee);
                });
            });
        else
            rootTeams.forEach(this::appendRootItems);
    }


}

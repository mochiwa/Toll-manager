package graphic.components.team.tree.team;

import graphic.components.cell.employee.event.SelectedCellEmployeeEvent;
import graphic.components.team.tree.ITreeTeamCell;
import graphic.components.team.tree.team.TeamContextMenu;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.ContextMenu;
import javafx.scene.image.Image;
import tollmanager.model.identity.team.Team;

import java.util.Objects;

public class TeamTreeCell implements ITreeTeamCell<Team> {
    private final String TEAM_ICON = Objects.requireNonNull(getClass().getClassLoader().getResource("image/team-icon.png")).toExternalForm();
    private SimpleObjectProperty<Team> teamProperty;
    private SimpleStringProperty name;

    public TeamTreeCell(Team team){
        teamProperty=new SimpleObjectProperty<>(team);
        name=new SimpleStringProperty(team.name().value());
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public Team getUserData() {
        return teamProperty.get();
    }

    @Override
    public ContextMenu getContextMenu() {
        return new TeamContextMenu(getUserData());
    }

    @Override
    public Image getIcon() {
        return new Image(TEAM_ICON);
    }

    @Override
    public Event getSelectedEvent() {
        return new SelectedCellEmployeeEvent(null,teamProperty.get());
    }

    @Override
    public boolean isDraggable() {
        return false;
    }

    @Override
    public boolean isTarget() {
        return true;
    }

    @Override
    public String getStyleSheet() {
        return "-fx-border-color: Red; -fx-border-width: 1 1 1 1; -fx-padding: 3 3 1 3;";
    }
}

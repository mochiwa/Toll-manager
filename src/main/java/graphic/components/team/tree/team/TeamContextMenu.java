package graphic.components.team.tree.team;

import graphic.components.event.AccessQueryEvent;
import graphic.components.team.TeamBoardCommandEvent;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.*;
import tollmanager.application.command.AppendSubTeamCommand;
import tollmanager.application.command.RemoveRootTeamCommand;
import tollmanager.application.command.RemoveSubTeamCommand;
import tollmanager.application.query.IsManagerQuery;
import tollmanager.model.identity.team.Team;

import java.util.function.Predicate;

public class TeamContextMenu extends ContextMenu {
    private SimpleObjectProperty<Team> teamProperty;
    private MenuItem appendItem;
    private MenuItem removeItem;
    private Predicate<ButtonType> isButtonOkClicked = response->response.equals(ButtonType.OK);


    public TeamContextMenu(Team team) {
        teamProperty=new SimpleObjectProperty<>(team);

        setOnShowing(windowEvent -> {
            if(team.isRootTeam())
                fireEvent(AccessQueryEvent.of(new IsManagerQuery()));
        });
        addEventHandler(AccessQueryEvent.IS_MANAGER,e->enableRemoveButton(e.getQuery().isManager()));

        initMenuItemAppend();
        initMenuItemRemove();
    }

    private void initMenuItemAppend() {
        appendItem = new MenuItem("Append sub team");
        getItems().add(appendItem);

        appendItem.setOnAction(e -> {
            AppendSubTeamPopup popup = new AppendSubTeamPopup();
            popup.showAndWait()
                    .filter(isButtonOkClicked)
                    .ifPresent(r->{ fireEvent(TeamBoardCommandEvent.of(new AppendSubTeamCommand( getTeam(),popup.teamName().value()))); });
        });
    }

    private void initMenuItemRemove() {
        removeItem = new MenuItem("Remove team");
        if(!getTeam().isRootTeam())
            removeItem.setText("Remove sub team");
        getItems().add(removeItem);

        removeItem.setOnAction(event -> {
            if(isDeleteConfirmed())
                if(getTeam().isRootTeam())
                    fireEvent(TeamBoardCommandEvent.of(new RemoveRootTeamCommand(getTeam())));
                else
                    fireEvent(TeamBoardCommandEvent.of(new RemoveSubTeamCommand(getTeam())));
        });
    }

    private boolean isDeleteConfirmed() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Team Confirmation");
        alert.setContentText("Do you really want to delete :"+ getTeamName() +" ?");

        return alert.showAndWait().filter(isButtonOkClicked).isPresent();
    }

    public void enableRemoveButton(boolean value){
        removeItem.setDisable(!value);
    }


    public Team getTeam() {
        return teamProperty.get();
    }
    private String getTeamName(){
        return getTeam().name().value();
    }
}

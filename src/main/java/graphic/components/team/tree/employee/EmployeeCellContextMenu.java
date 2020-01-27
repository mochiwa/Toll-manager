package graphic.components.team.tree.employee;

import graphic.components.event.AccessQueryEvent;
import javafx.event.EventTarget;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import tollmanager.application.query.IsManagerQuery;

public class EmployeeCellContextMenu extends ContextMenu {
    private MenuItem edit;
    private MenuItem delete;
    private MenuItem removeFromTeam;

    public EmployeeCellContextMenu() {
        initEditContext();
        initRemoveFromTeamContext();
        initDeleteContext();


        setOnShowing(windowEvent->fireEvent(AccessQueryEvent.of(new IsManagerQuery())));
        addEventHandler(AccessQueryEvent.IS_MANAGER,e-> {
            enableDeleteButton(e.getQuery().isManager());
            edit.setDisable(!e.getQuery().isManager());
        });
    }

    private void enableDeleteButton(boolean value){
        delete.setDisable(!value);
    }

    private void initEditContext() {
        edit = new MenuItem("Edit");
        getItems().add(edit);
    }

    private void initDeleteContext(){
        delete=new MenuItem("Delete");
        getItems().add(delete);
    }

    private void initRemoveFromTeamContext() {
        removeFromTeam = new MenuItem("remove from Team");
        getItems().add(removeFromTeam);
    }

    public MenuItem getEdit() {
        return edit;
    }
    public MenuItem getRemoveFromTeam() {
        return removeFromTeam;
    }

    public MenuItem getDelete() {
        return delete;
    }
}

package graphic.components.team.tree;

import graphic.components.event.AccessQueryEvent;
import graphic.components.team.TeamBoardCommandEvent;
import javafx.application.Platform;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import tollmanager.application.command.AppendEmployeeToTeamCommand;
import tollmanager.model.identity.Employee;
import tollmanager.model.identity.team.Team;

import java.io.Serializable;
import java.util.Objects;

public class TextTreeTeamCellFactory extends TreeCell<ITreeTeamCell> implements Serializable {
    private static final DataFormat customFormat=new DataFormat("textTeamTreeCell");

    @Override
    public void updateItem(ITreeTeamCell item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
            return;
        }
        if (item == null) {
            setText("Teams");
            setGraphic(null);
            return;
        }
        setText(item.getName());
        setIcon(item.getIcon());
        setContextMenu(item.getContextMenu());
        initTeamContextMenuEvent();
        initEmployeeContextMenuEvent();

        if(isSelected())
            fireEvent(item.getSelectedEvent());

        initOnDragAndDrop(item);
    }

    private void setIcon(Image image){
        ImageView img=new ImageView();
        img.setImage(image);
        img.setFitHeight(30);
        img.setFitWidth(30);
        img.setPreserveRatio(true);
        img.setSmooth(true);
        setGraphic(img);
    }

    private void initTeamContextMenuEvent(){
        getContextMenu().addEventFilter(TeamBoardCommandEvent.APPEND_SUB_TEAM,this::fireEvent);
        getContextMenu().addEventFilter(TeamBoardCommandEvent.REMOVE_SUB_TEAM, this::fireEvent);
        getContextMenu().addEventFilter(TeamBoardCommandEvent.REMOVE_ROOT_TEAM, this::fireEvent);
        getContextMenu().addEventFilter(AccessQueryEvent.IS_MANAGER, this::fireEvent);
    }
    private void initEmployeeContextMenuEvent(){
        getContextMenu().addEventFilter(TeamBoardCommandEvent.EDIT_EMPLOYEE_ACTION,this::fireEvent);
        getContextMenu().addEventFilter(TeamBoardCommandEvent.REMOVE_EMPLOYEE,this::fireEvent);
        getContextMenu().addEventFilter(TeamBoardCommandEvent.DELETE_EMPLOYEE,this::fireEvent);
        getContextMenu().addEventFilter(AccessQueryEvent.IS_MANAGER, this::fireEvent);
    }

    private void initOnDragAndDrop(ITreeTeamCell item) {
        initOnDragDetectedEmployee(item);
        initOnDragExitedEmployee();
        initOnDragOverEmployee();
        initOnDragDroppedEmployee();
    }
    private void initOnDragDroppedEmployee() {
        setOnDragDropped(dragEvent -> {

            Employee employeeDragged=getEmployeeFromCell((TextTreeTeamCellFactory) dragEvent.getGestureSource());
            Team target=getTeamFromTreeItem(getTreeItem());

            if(target!=null && employeeDragged != null && !target.hasEmployee(employeeDragged))
                fireEvent(TeamBoardCommandEvent.of(new AppendEmployeeToTeamCommand(employeeDragged,target)));
            dragEvent.setDropCompleted(true);
            dragEvent.consume();
        });
    }
    private void initOnDragOverEmployee() {
        setOnDragOver(dragEvent -> {
                ITreeTeamCell cell=getItem();
                if(cell==null)
                    return;
                if(cell.isTarget())
                    dragEvent.acceptTransferModes(TransferMode.MOVE);
                setStyle(cell.getStyleSheet());
                dragEvent.consume();
        });
    }
    private void initOnDragExitedEmployee() {
        setOnDragExited(e->{setStyle("");});
    }
    private void initOnDragDetectedEmployee(ITreeTeamCell item){
        if(item.isDraggable())
            setOnDragDetected(e->{
                Dragboard dragboard=startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content=new ClipboardContent();
                content.put(customFormat,this);
                dragboard.setContent(content);
                e.consume();
            });
        else
            setOnDragDetected(null);
    }


    private Employee getEmployeeFromCell(TextTreeTeamCellFactory cell){
        Objects.requireNonNull(cell);
        TreeItem item=cell.getTreeItem();
        ITreeTeamCell teamItem=(ITreeTeamCell)item.getValue();
        if(teamItem.getUserData() instanceof Employee)
            return (Employee) teamItem.getUserData();
        return null;
    }
    private Team getTeamFromTreeItem(TreeItem<ITreeTeamCell> item){
        ITreeTeamCell teamItem=item.getValue();
        if(teamItem.getUserData() instanceof Team)
            return (Team) teamItem.getUserData();
        return null;
    }
}

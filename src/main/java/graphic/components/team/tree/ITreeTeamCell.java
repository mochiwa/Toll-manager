package graphic.components.team.tree;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.image.Image;

public interface ITreeTeamCell<T> {

    /**
     * @return the name printed in Cell
     */
    String getName();

    /**
     * @return the data T
     */
    T getUserData();

    /**
     * @return a context menu
     */
    ContextMenu getContextMenu();

    /**
     * @return icon to print side the name
     */
    Image getIcon();

    /**
     * @return get the event to fire when cell selected
     */
    Event getSelectedEvent();

    boolean isDraggable();

    boolean isTarget();

    String getStyleSheet();


}

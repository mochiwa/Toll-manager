package graphic.components.cell.employee;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public abstract class AbstractCellFactory<T> implements Callback<ListView<T>, ListCell<T>> {
}

package graphic.components.cell.employee;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import tollmanager.model.identity.Employee;

public class EmployeeCellFactory extends AbstractCellFactory<Employee> {

    @Override
    public ListCell<Employee> call(ListView<Employee> list) {
        return new EmployeeCell();
    }
}

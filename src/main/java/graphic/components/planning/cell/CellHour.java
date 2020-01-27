package graphic.components.planning.cell;

import javafx.scene.control.ListCell;
import tollmanager.model.planning.Planning;

public class CellHour implements PlanningCell {
    private int hour;

    public CellHour(int hour) {
        this.hour = hour;
    }

    public int getHour() {
        return hour;
    }

    @Override
    public String getText() {
        StringBuilder builder=new StringBuilder();
        if(getHour()<10)
            builder.append("0");
        return builder.append(getHour()).append("h00").toString();
    }

    @Override
    public boolean isWorkingHour() {
        return false;
    }

    @Override
    public ListCell<PlanningCell> getListOfCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(PlanningCell item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                    setText("");
                    return;
                }
                CellHour cell=(CellHour)item;

                if(cell.getHour()==0)
                    getStyleClass().add("firstCell");
                else if(cell.getHour()==23)
                    getStyleClass().add("lastCell");
                setText(item.getText());
            }
        };
    }

    @Override
    public Planning getPlanning() {
        return Planning.Null();
    }
}


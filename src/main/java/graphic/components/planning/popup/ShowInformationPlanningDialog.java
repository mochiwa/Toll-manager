package graphic.components.planning.popup;

import graphic.components.popupDialog.PopupDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import tollmanager.model.planning.Planning;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;

public class ShowInformationPlanningDialog  extends PopupDialog<Boolean> {
    @FXML private Label labelBeginHour;
    @FXML private Label labelEndDay;
    @FXML private Label labelEndHour;
    @FXML private Label labelBeginDay;
    @FXML private Text labelComment;

    public ShowInformationPlanningDialog(Planning planning){
        super();

        DateTimeFormatter dayFormatter=DateTimeFormatter.ofPattern("EEE dd MMMM yyyy");
        DateTimeFormatter hourFormatter=DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM);
        try{
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(ShowInformationPlanningDialog.class.getResource("showInformationPlanningDialog.fxml"));
            loader.setController(this);
            docker.getChildren().add(loader.load());
            docker.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("css/adminRegistrationDialog.css")).toExternalForm());
            labelBeginDay.setText(planning.beginning().format(dayFormatter));
            labelEndDay.setText(planning.ending().format(dayFormatter));
            labelBeginHour.setText(planning.beginning().format(hourFormatter));
            labelEndHour.setText(planning.ending().format(hourFormatter));
            labelComment.setText(planning.comment());
            buttonCancel.setVisible(false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected Boolean getUserData() {
        return true;
    }
}

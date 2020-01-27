package graphic.components.popupDialog;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;

import java.util.Objects;

public abstract class PopupDialog<T> extends Dialog<T> {
    @FXML protected Button buttonValidate;
    @FXML protected Button buttonCancel;
    @FXML protected AnchorPane docker;
    @FXML protected BorderPane mainPane;

    private T userData;

    public PopupDialog(){
        super();
        initPane();
        initButtons();
        initReturnConverter();
    }

    private void initPane() {
        mainPane=new BorderPane();
        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(PopupDialog.class.getResource("popupDialog.fxml"));

        loader.setController(this);
        loader.setRoot(mainPane);
        try{
           loader.load();
        }catch (Exception e){
            e.printStackTrace();
        }
        getDialogPane().setContent(mainPane);
        getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("css/popupDialog.css")).toExternalForm());

    }

    private void initButtons() {
        buttonValidate.addEventFilter(ActionEvent.ACTION, event -> {
            userData=getUserData();
            if (userData==null)
                event.consume();
            else
                setResult(userData);
        });

        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Button closeButton = (Button) getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());
        closeButton.setVisible(false);
        buttonCancel.addEventFilter(ActionEvent.ACTION,event -> closeButton.fire());
    }


    private void initReturnConverter() {
        setResultConverter(buttonType -> {
            if(buttonType==ButtonType.OK)
                return userData;
            return null;
        });
    }


   protected abstract T getUserData();
}

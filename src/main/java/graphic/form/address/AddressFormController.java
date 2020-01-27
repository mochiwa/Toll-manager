package graphic.form.address;

import graphic.components.input.FormTextField;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import tollmanager.model.identity.contact.PostalAddress;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AddressFormController implements Initializable {
    @FXML protected GridPane mainLayout;
    @FXML protected FormTextField fieldStreet;
    @FXML protected FormTextField fieldNumber;
    @FXML protected FormTextField fieldCity;
    @FXML protected FormTextField fieldZip;
    @FXML protected FormTextField fieldCountry;
    private SimpleBooleanProperty isValidProperty;

    public AddressFormController() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainLayout.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("css/address.css")).toExternalForm());

        isValidProperty=new SimpleBooleanProperty();
        fieldStreet.focusedProperty().addListener((observableValue, old, isFocus) -> {
            if(old && !isFocus)
                setErrorOnField(fieldStreet,"The field is required");
        });

        fieldNumber.focusedProperty().addListener((observableValue, old, isFocus) -> {
            if(old && !isFocus)
                setErrorOnField(fieldNumber,"The field is required");
        });

        fieldZip.focusedProperty().addListener((observableValue, old, isFocus) -> {
            if(old && !isFocus)
                setErrorOnField(fieldZip,"The field is required");
        });

        fieldCity.focusedProperty().addListener((observableValue, old, isFocus) -> {
            if(old && !isFocus)
                setErrorOnField(fieldCity,"The field is required");
        });

        fieldCountry.focusedProperty().addListener((observableValue, old, isFocus) -> {
            if(old && !isFocus)
                setErrorOnField(fieldCountry,"The field is required");
        });
        fieldStreet.textProperty().addListener((o) -> isValidProperty.set(fieldStreet.isCorrectlyFilled()));
        fieldNumber.textProperty().addListener((o) -> isValidProperty.set(fieldNumber.isCorrectlyFilled()));
        fieldZip.textProperty().addListener((o) -> isValidProperty.set(fieldZip.isCorrectlyFilled()));
        fieldCity.textProperty().addListener((o) -> isValidProperty.set(fieldCity.isCorrectlyFilled()));
        fieldCountry.textProperty().addListener((o) -> isValidProperty.set(fieldCountry.isCorrectlyFilled()));

    }

    public PostalAddress getAddress() {
        try {
            return PostalAddress.of(
                    fieldNumber.getText(),
                    fieldStreet.getText(),
                    fieldCity.getText(),
                    fieldZip.getText(),
                    fieldCountry.getText()
            );
        }catch (Exception e) {
            setErrorOnField(fieldStreet,e.getMessage());
            setErrorOnField(fieldNumber,e.getMessage());
            setErrorOnField(fieldCity,e.getMessage());
            setErrorOnField(fieldZip,e.getMessage());
            setErrorOnField(fieldCountry,e.getMessage());
        }
        return null;
    }

    private void setErrorOnField(FormTextField field,String errorMessage) {
        if (field.getText().isEmpty())
            field.setError(errorMessage);
        else
            field.setFieldValidate();

        isValidProperty.set(!(fieldNumber.isError() ||
                        fieldStreet.isError() ||
                        fieldCity.isError() ||
                        fieldZip.isError() ||
                        fieldCountry.isError()));
    }


    public void fillAddress(PostalAddress address) {
        fieldNumber.setText(address.number());
        fieldStreet.setText(address.street());
        fieldCity.setText(address.city());
        fieldZip.setText(address.zipCode());
        fieldCountry.setText(address.country());
    }

    public void setEditable(boolean editable) {
        fieldNumber.setEditable(editable);
        fieldStreet.setEditable(editable);
        fieldCity.setEditable(editable);
        fieldZip.setEditable(editable);
        fieldCountry.setEditable(editable);
    }

    public void clearForm() {
        fieldNumber.resetField();
        fieldStreet.resetField();
        fieldCity.resetField();
        fieldZip.resetField();
        fieldCountry.resetField();
    }

    public boolean isValid(){
        return isValidProperty.get();
    }


}

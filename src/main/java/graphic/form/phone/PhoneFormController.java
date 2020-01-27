package graphic.form.phone;

import graphic.components.input.FormTextField;
import graphic.form.FormQueryEvent;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextFormatter;
import tollmanager.model.identity.contact.Phone;

import java.net.URL;
import java.util.ResourceBundle;

public class PhoneFormController implements Initializable {
    @FXML protected FormTextField fieldPhone;

    private SimpleBooleanProperty isValid;

    public PhoneFormController(){
        isValid=new SimpleBooleanProperty();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fieldPhone.setRegex(Phone.MOBILE_FORMAT);
        fieldPhone.setTextFormatter(new TextFormatter<String>(new PhoneTextFormatter()));

        fieldPhone.focusedProperty().addListener((observableValue, old, isFocused) -> {
            if((old && !isFocused))
                if(fieldPhone.isCorrectlyFilled() )
                    fieldPhone.fireEvent(FormQueryEvent.isPhoneUsedEvent(getPhone()));
                else
                    setErrorOnField(fieldPhone,"The phone number must be like 04xxx.xx.xx.xx",true);
        });

        fieldPhone.addEventHandler(FormQueryEvent.IS_PHONE_USED,e->setErrorOnField(fieldPhone,"Phone number already used",e.getQuery().isUsed()));
        fieldPhone.textProperty().addListener((o) -> isValid.set(fieldPhone.isCorrectlyFilled()));
    }


    public Phone getPhone() {
        try {
            return Phone.of(fieldPhone.getText());
        }catch (Exception e) {
            setErrorOnField(fieldPhone,e.getMessage(),fieldPhone.getText().isEmpty());
            fieldPhone.setError(e.getMessage());
        }
        return null;
    }

    public void fillPhone(Phone phone) {
        fieldPhone.setTextFormatter(null);
        fieldPhone.setText(phone.value());
        fieldPhone.setTextFormatter(new TextFormatter<String>(new PhoneTextFormatter()));
    }

    public void setEditable(boolean editable) {
        fieldPhone.setEditable(editable);
    }

    public void clearForm() {
        fieldPhone.resetField();
    }

    public boolean isValid() {
        return isValid.get();
    }

    private void setErrorOnField(FormTextField field,String errorMessage , boolean isError) {
        if (isError)
            field.setError(errorMessage);
        else
            field.setFieldValidate();
        isValid.set(!fieldPhone.isError());
    }
}

package graphic.components.input;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.util.Objects;

public class FormTextField extends TextField {
    private final String CSS_ERROR="error-field";
    private final String CSS_CORRECT="correct-field";

    private SimpleBooleanProperty error;
    private String errorMessage;
    private String regex;

    public FormTextField() {
        regex="";
        errorMessage="";
        initComponent();
        initErrorProperty();

        focusedProperty().addListener((observableValue, old, isFocused) -> {
            if(!old && isFocused)
                onMouseClicked(null);
        });

    }

    private void initComponent(){
        getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("css/textField.css")).toExternalForm());
        setOnMouseClicked(this::onMouseClicked);

    }
    private void initErrorProperty(){
        error=new SimpleBooleanProperty(false);
        error.addListener(observable -> {
            if(isError()){
                getStyleClass().add(CSS_ERROR);
                getStyleClass().remove(CSS_CORRECT);
            }
            else
                getStyleClass().remove(CSS_ERROR);
        });
    }



    private void onMouseClicked(MouseEvent event) {
        if(!isError())
            return;
        resetField();
    }


    public void setError(String cause) {
        errorMessage=cause;
        error.setValue(true);
    }
    public void resetField() {
        error.set(false);
        setText("");
    }



    public void setRegex(String regex){
        this.regex=regex;
    }

    public boolean isCorrectlyFilled(){
        if(regex==null || regex.isEmpty())
            return true;
        return getText().matches(regex);
    }


    public boolean isError() {
        return error.get();
    }
    public SimpleBooleanProperty errorProperty() {
        return error;
    }

    public void setFieldValidate() {
        getStyleClass().add(CSS_CORRECT);
    }
}


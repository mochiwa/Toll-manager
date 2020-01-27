package graphic.components.planning.popup;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class HourTextFormatter implements UnaryOperator<TextFormatter.Change> {
    @Override
    public TextFormatter.Change apply(TextFormatter.Change change) {
        String input = change.getText();
        String currentText=change.getControlText();

        if(change.isDeleted())
            return change;

        if (!isNumeric(input))
            return null;
        if(isLengthEnough(currentText) && !input.isEmpty())
            return null;
        return change;
    }


    private boolean isLengthEnough(String text) {
        int maxLength=2;
        return text.length()>=maxLength;
    }

    private boolean isNumeric(String input) {
        return input.matches("\\d") || input.trim().isEmpty();
    }
}

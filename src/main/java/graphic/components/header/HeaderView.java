package graphic.components.header;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class HeaderView implements Initializable {
    private final String IMAGE_PATH="image/logoTollManager.png";
    @FXML private ImageView headerImageView;


    public HeaderView(){

    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        headerImageView.setImage(new Image(Objects.requireNonNull(getClass().getClassLoader().getResource(IMAGE_PATH)).toExternalForm()));

    }
}

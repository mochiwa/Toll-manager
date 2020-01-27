package graphic;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tollmanager.application.JavaFXApplicationLifeTime;
import tollmanager.infrastructure.persistance.DatabaseConnection;
import tollmanager.infrastructure.persistance.postgres.*;

import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/graphic/mainWindow.fxml"));
        JavaFXApplicationLifeTime applicationLifeTime=null;

        try{
            applicationLifeTime=new JavaFXApplicationLifeTime(
                    new PostgresGroupRepository(),
                    new PostgresEmployeeRepository(),
                    new PostgresUserRepository(),
                    new PostgresTeamRepository(),
                    new PostgresPlanningRepository()
            );
        }catch (SQLException e){
            System.exit(3);
        }

        if(!DatabaseConnection.instance().isValid()){
            applicationLifeTime.showError("Connection with the database impossible\n The application will be closed.");
            Platform.exit();
        }

        MainWindowController controller=new MainWindowController(applicationLifeTime);
        loader.setController(controller);
        Parent root =loader.load();
        Scene scene=new Scene(root,300,200);


        primaryStage.setTitle("Toll Manager");
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);



        primaryStage.show();

        if(DatabaseConnection.instance().isValid()) {
            controller = loader.getController();
            controller.start();
        }
        primaryStage.setOnCloseRequest(windowEvent -> {
            try {
                DatabaseConnection.instance().close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

    }

    public static void main(String[] args) throws SQLException {
        launch(args);
    }

}

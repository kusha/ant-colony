/*
* SFC project 2015, Ant Colony Optimization.
* Main class, loads FXML and ViewController.
* @author Mark, Birger (xbirge00@stud.fit.vutbr.cz)
*/

package aco;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // load FXML scene
        Parent root = FXMLLoader.load(getClass().getResource("layout.fxml"));
        Scene scene = new Scene(root, 800, 600);
        // specify window parameters
        stage.setTitle("Ant Colony Optimization");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}


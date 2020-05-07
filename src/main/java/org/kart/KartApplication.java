package org.kart;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kart.view.MainView;

import static org.kart.configuration.KartConfiguration.kartLogService;

public class KartApplication extends Application {

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new MainView(kartLogService()), 800, 600);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnCloseRequest(closeEvent -> System.exit(0));
        stage.show();
    }

}

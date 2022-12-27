package com.rubiks.simulator.forms;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class InitialStateForm extends Application {

    private Stage primaryStage;
    private AnchorPane rootLayout;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Initial State Form");

        initRootLayout();

    }
    
    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/forms/InitialStateForm.fxml"));
            rootLayout = (AnchorPane) loader.load();
            
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
	/**
	 * Returns the main stage.
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

//    public static void main(String[] args) {
//        launch(args);
//    }
	public void launchForm()
	{
		myLaunch(InitialStateForm.class);
	}
	
	public static void myLaunch(Class<? extends Application> applicationClass) {
	    if (!javaFxLaunched) { // First time
	        Platform.setImplicitExit(false);
	        new Thread(()->Application.launch(applicationClass)).start();
	        javaFxLaunched = true;
	    } else { // Next times
	        Platform.runLater(()->{
	            try {
	                Application application = applicationClass.newInstance();
	                Stage primaryStage = new Stage();
	                primaryStage.setAlwaysOnTop(true);
	                application.start(primaryStage);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        });
	    }
	}
	
	private static volatile boolean javaFxLaunched = false;
}
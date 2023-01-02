package com.rubiks;

import javafx.application.Application;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

import com.rubiks.simulator.processing.mainRubiksSimulator;
import com.rubiks.view.MainLayoutController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private mainRubiksSimulator rubiksSimulator;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Rubiks Cube Game");

        initRootLayout();
        
        initMainControlsLayout();
        
        initMenuLayout();
        
        // TODO: find a way to embed the PApplet in JAVAFX root
        // TODO: handle exception: unable to find database 
        // TODO: add button re-reset to solved 
        
        // launch Simulator PApplet
        rubiksSimulator = new mainRubiksSimulator();
        rubiksSimulator.run();
    }


    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void initMainControlsLayout() {
        try {
            // Load root layout from fxml file.
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/MainControlsLayout.fxml"));
            AnchorPane mainControls = (AnchorPane) loader.load();
            
            // Show the scene containing the root layout.
            rootLayout.setCenter(mainControls);
            
            // Give the controller access to the main app
            MainLayoutController controller = loader.getController();
            controller.setMainApp(this);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void initMenuLayout() {
        try {
            // Load root layout from fxml file.
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/MenuLayout.fxml"));
            MenuBar menuBar = (MenuBar) loader.load();
            
            // Show the scene containing the root layout.
            rootLayout.setTop(menuBar);
            
            // Give the controller access to the main app
//            MainLayoutController controller = loader.getController();
//            controller.setMainApp(this);
            
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
	
	public mainRubiksSimulator getRubikSimulator()
	{
		return rubiksSimulator;
	}

    public static void main(String[] args) {
        launch(args);
    }
}

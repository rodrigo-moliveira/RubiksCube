package com.rubiks;

import javafx.application.Application;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

import com.rubiks.simulator.processing.mainRubiksSimulator;
import com.rubiks.view.MainLayoutController;
import com.rubiks.view.MenuController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;

public class MainApp extends Application {

    private Stage primaryStage = null;
    private BorderPane rootLayout = null;
    private mainRubiksSimulator rubiksSimulator = null;
    private MenuController menuController = null;
    private MainLayoutController layouController = null;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Rubiks Cube Game");

        initRootLayout();
        
        initMainControlsLayout();
        
        initMenuLayout();
        
        // TODO: add button re-reset to solved 
        // TODO: add function to close application. Call it on critical points of code
        // TODO: build, create readme...
        
        // launch Simulator PApplet
        rubiksSimulator = new mainRubiksSimulator();
        rubiksSimulator.run();
        rubiksSimulator.setMainApp(this);
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
            layouController = loader.getController();
            layouController.setMainApp(this);
            
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
            menuController = loader.getController();
            menuController.setMainApp(this);
            
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
	
	public void animationStarting()
    {
		// alert all modules that the simulator has started moving the cube
		layouController.updateAnimation(true);
    	
    }
    public void animationEnding()
    {
    	// alert all modules that the simulator has ended moving the cube
    	layouController.updateAnimation(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
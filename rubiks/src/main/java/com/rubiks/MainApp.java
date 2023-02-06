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
    private MainLayoutController mainControlsController = null;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Rubiks Cube Game");

        initRootLayout();
        
        initMainControlsLayout();
        
        initMenuBar();
                
        // launch Simulator PApplet
        rubiksSimulator = new mainRubiksSimulator();
        rubiksSimulator.run();
        rubiksSimulator.setMainApp(this);
    }


    //Initializes the root layout.
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
    
    // Initializes the Main Control Layout
    public void initMainControlsLayout() {
        try {
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/MainControlsLayout.fxml"));
            AnchorPane mainControls = (AnchorPane) loader.load();
            
            rootLayout.setCenter(mainControls);
            
            // Give the controller access to the main app
            mainControlsController = loader.getController();
            mainControlsController.setMainApp(this);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Initializes the Menu Bar
    public void initMenuBar() {
        try {
        	FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/MenuBarLayout.fxml"));
            MenuBar menuBar = (MenuBar) loader.load();
            
            rootLayout.setTop(menuBar);
            
            // Give the controller access to the main app
            menuController = loader.getController();
            menuController.setMainApp(this);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
	// Returns the main stage.
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	// Returns the rubiks cube simulator PApplet
	public mainRubiksSimulator getRubikSimulator()
	{
		return rubiksSimulator;
	}
	
	// alert all application that the simulator has started moving the cube
	public void animationStarting()
    {
		// we need to activate / deactivate some buttons when the simulator is in animation mode
		mainControlsController.updateAnimation(true);	
    }
	
	// alert all application that the simulator has ended moving the cube
    public void animationEnding()
    {
    	// we need to activate / deactivate some buttons when the simulator is in animation mode
    	mainControlsController.updateAnimation(false);
    }
    
    @Override
    public void stop()
    {
    	closeProgram();
    }
    
    public void closeProgram(){
    	// close simulator (PApplet)
    	getRubikSimulator().reallyExit();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
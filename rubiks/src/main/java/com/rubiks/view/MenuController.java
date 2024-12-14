package com.rubiks.view;

import com.rubiks.MainApp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MenuController {

	// Reference to the main application.
    private MainApp mainApp;
	
	@FXML
    private void openAbout(ActionEvent event) {
        event.consume();
        String text = "This Rubiks Cube application is my first solo project. I developed it\n"
        		+ "to gain experience with Java and JavaFX. I always loved rubiks cubes as a child,\n"
        		+ "so this was really a 'mandatory' and natural project for me.\n"
        		+ "Refer to the README for more technical information about this program.";
        String details = "Created by: Rodrigo Oliveira\n"
        		+ "Release: v1.0\n"
        		+ "Contact: rodrigo.moroliveira97@gmail.com\n"
        		+ "Github: https://github.com/rodrigo-moliveira";
        ControllerUtils.informationDialogue("About Rubiks Cube", text, details);
	}
	
	/**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) 
    {
        this.mainApp = mainApp;
    }
	
}

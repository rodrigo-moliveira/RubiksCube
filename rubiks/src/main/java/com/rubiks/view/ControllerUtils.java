package com.rubiks.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;

public class ControllerUtils {
	public static void informationDialogue(String title, String header, String content)
	{
		Alert alert = new Alert(AlertType.INFORMATION, "Content here", ButtonType.OK);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.setAlwaysOnTop(true);

		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		stage.showAndWait();
		
	}
	
	public static void errorDialogue(String title, String header, String content)
	{
		Alert alert = new Alert(AlertType.ERROR, "Content here", ButtonType.OK);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.setAlwaysOnTop(true);

		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		stage.showAndWait();
		
	}
}

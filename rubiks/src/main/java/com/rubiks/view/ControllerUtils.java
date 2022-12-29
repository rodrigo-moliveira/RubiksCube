package com.rubiks.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ControllerUtils {
	public static void informationDialogue(String title, String header, String content)
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);

		alert.showAndWait();
		
	}
	
	public static void errorDialogue(String title, String header, String content)
	{
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);

		alert.showAndWait();
		
	}
}

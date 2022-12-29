package com.rubiks.view;

import java.io.IOException;
import java.util.Optional;

import com.rubiks.MainApp;
import com.rubiks.view.UserScrambleController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RootLayoutController {
	
	@FXML
    private Button buttonScramble;
	
	@FXML
    private Button buttonMove;
	
	@FXML
    private Button buttonSolve;
	
	// Reference to the main application.
    private MainApp mainApp;
	
	@FXML
    private void initialize() {
    	
    }
	
	@FXML
    private void clickScramble(ActionEvent event) {
        event.consume();
        

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/InitialStateForm.fxml"));
            
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            
            // Give the controller access to the main app
            UserScrambleController controller = fxmlLoader.getController();
            controller.setMainApp(this.mainApp);
            
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Set Cube Initial State Form");
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
        	e.printStackTrace();
        }        
    }
	
	@FXML
    private void clickSolve(ActionEvent event) {
        event.consume();
        this.mainApp.getRubikSimulator().Solve();
    }
	
	@FXML
    private void clickMove(ActionEvent event) {
        event.consume();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Move");
        dialog.setHeaderText("Insert the move sequence\nola");
        dialog.setContentText("Moves:");
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
        	String moves = result.get();
            System.out.println("Moves: " + moves);
        }
    }
	
	private void setButtonsEnabled(boolean flag)
	{
		buttonScramble.setDisable(!flag);
		buttonMove.setDisable(!flag);
		buttonSolve.setDisable(!flag);
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

package com.rubiks.view;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import com.rubiks.MainApp;
import com.rubiks.utils.Exceptions.InvalidMoveString;
import com.rubiks.utils.Exceptions.RubiksSolutionException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainLayoutController {
	
	@FXML
    private Button buttonScramble;
	
	@FXML
    private Button buttonMove;
	
	@FXML
    private Button buttonReset;
	
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
            fxmlLoader.setLocation(getClass().getResource("/view/UserScrambleForm.fxml"));
            
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            
            // Give the controller access to the main app
            UserScrambleController controller = fxmlLoader.getController();
            controller.setMainApp(this.mainApp);
            controller.setStage(stage);
            
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
        
        try 
        {	
        	String solution = this.mainApp.getRubikSimulator().Solve();

			if(solution != null)
			{
				ControllerUtils.informationDialogue("Solving Cube", "Solution found", "Applying solution: " + solution);
			}
			else 
			{
				ControllerUtils.informationDialogue("Solving Cube", null, 
						"Cube is already solved.");
			}
		} catch (InvalidMoveString | RubiksSolutionException e) 
        {
			ControllerUtils.errorDialogue("Solving Cube", "The following error occurred while attempting to find a solution:", 
					e.getMessage());
		}
    }
	
	@FXML
    private void clickMove(ActionEvent event) {
        event.consume();
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Move");
        dialog.setHeaderText("Insert the move sequence (separated by white spaces or commas)\n"
        		+ "Valid Moves: R1 R2 R3 L1 L2 L3 F1 F2 F3"
        		+ " B1 B2 B3 U1 U2 U3 D1 D2 D3");
        dialog.setContentText("Moves:");
        
        Optional<String> result = dialog.showAndWait();
        
        if (result.isPresent())
        {
        	String moves = result.get();
        	moves = moves.replaceAll("\\s+","");
        	moves = moves.replaceAll(",","");

        	try 
        	{
				this.mainApp.getRubikSimulator().ApplyMove(moves);
			} catch (InvalidMoveString e) 
        	{
				ControllerUtils.errorDialogue("Invalid Move Sequence Error", 
						"Input move sequence: " + result.get(), e.getMessage());
			}
        }
    }
	
	@FXML
    private void clickReset(ActionEvent event) {
        event.consume();
        this.mainApp.getRubikSimulator().resetSolved();
	}
	
	public void updateAnimation(boolean animating)
	{
		buttonScramble.setDisable(animating);
		buttonReset.setDisable(animating);
		buttonMove.setDisable(animating);
		buttonSolve.setDisable(animating);
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

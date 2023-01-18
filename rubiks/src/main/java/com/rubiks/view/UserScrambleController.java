package com.rubiks.view;

import java.io.InputStream;

import com.rubiks.MainApp;
import com.rubiks.utils.Exceptions.SingmasterError;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class UserScrambleController {

	@FXML
    private Button buttonSeeNotation;
	
	@FXML
    private Button buttonRandomScramble;
	
	@FXML
	private Button buttonScramble;
	
	@FXML
	private TextField textFieldScramble;
	
	// Reference to the main application.
    private MainApp mainApp;
    
    private Stage thisStage;
	
    @FXML
    private void initialize() {
    	buttonScramble.setDisable(true);
    }
    
    
    @FXML
    private void applyUserScramble(ActionEvent event) {
        event.consume();
        String scramble = textFieldScramble.getText();

        try 
        {
			this.mainApp.getRubikSimulator().singmasterScramble(scramble);
			
			// close the dialog.
		    Node  source = (Node)  event.getSource(); 
		    Stage stage  = (Stage) source.getScene().getWindow();
		    stage.close();
			
		} catch (SingmasterError e) {
			
			ControllerUtils.errorDialogue("Error applying Singmaster Scramble", e.getMessage(), 
					"Error description:\n"
					+ "\t-1: There is not exactly one facelet of each colour or there exist unknown colors\n"
					+ "\t-2: Not all 12 edges exist exactly once\n"
					+ "\t-3: Flip error: One edge has to be flipped\n"
					+ "\t-4: Not all 8 corners exist exactly once\n"
					+ "\t-5: Twist error: One corner has to be twisted\n"
					+ "\t-6: Parity error: Two corners or two edges have to be exchanged\n"
					+ "\t-7 Wrong Notation: Singmaster notation is wrong (see SingmasterNotationMap.png)" );
		}
    }
    
    @FXML
    private void updateScrambleButtonState(KeyEvent event)
    {
    	event.consume();
    	String text = textFieldScramble.getText();
    	
    	if (text == null || text.isBlank())
    	{
    		buttonScramble.setDisable(true);
    	}
    	else
    	{
    		buttonScramble.setDisable(false);
    	}
    }
    
    @FXML
    private void doRandomScramble(ActionEvent event) {
        event.consume();
        this.mainApp.getRubikSimulator().randomScramble();
        ControllerUtils.informationDialogue("Information Dialog", null, "Random scramble applied");
        
        thisStage.close();
    }
    
    @FXML
    private void showSingmasterNotation(ActionEvent event) {
        event.consume();
                
    	buttonSeeNotation.setDisable(true); // disable button until the user closes this image
    	
        // Loading and showing image
		InputStream instream = this.getClass().getResourceAsStream("/SingmasterNotationMap.png");
		if (instream == null)
		{
			ControllerUtils.errorDialogue("ERROR", null, "Image SingmasterNotationMap.png not found");
		}
		else
		{
			Image image = new Image(instream);
	        ImageView imageView = new ImageView();
	        imageView.setImage(image);
	        imageView.setFitWidth(500);
	        imageView.setPreserveRatio(true);
	        
	        //Setting the Scene and Stage objects
	        Group root = new Group(imageView);
	        Scene scene = new Scene(root, 600, 400);
	        Stage stage = new Stage();
	        stage.setTitle("Singmaster Notation");
	        stage.setScene(scene);
	        stage.showAndWait();
		}
		
		buttonSeeNotation.setDisable(false); // enable button
        
    }
    
    public void setMainApp(MainApp mainApp) 
    {
        this.mainApp = mainApp;
    }
    
    public void setStage(Stage thisStage) 
    {
        this.thisStage = thisStage;
    }
    
}


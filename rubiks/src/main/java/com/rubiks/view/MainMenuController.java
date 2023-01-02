package com.rubiks.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MainMenuController {

	@FXML
    private void openPhase1(ActionEvent event) {
        event.consume();
        System.out.println("Open Phase 1");
	}
}

package com.rubiks.simulator.processing;

import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PMatrix2D;

import java.util.ArrayList;

import com.rubiks.MainApp;
import com.rubiks.simulator.Thisletwaite.ThisletwaiteSolver;
import com.rubiks.simulator.cube.Cube;
import com.rubiks.utils.Exceptions.DatabaseGenerationError;
import com.rubiks.utils.Exceptions.InvalidMoveString;
import com.rubiks.utils.Exceptions.RubiksSolutionException;
import com.rubiks.utils.Exceptions.SingmasterError;


// this processing package is based on https://thecodingtrain.com/challenges/142-rubiks-cube 
public class mainRubiksSimulator extends PApplet {

	// Variables to compute the solution
	private Cube internalState = new Cube();
	private ThisletwaiteSolver solver;
    
    // Variables to model the cube in Processing
	private UtilsSimulator myUtils = new UtilsSimulator(this);
	private Cubie[] cube = new Cubie[3*3*3]; // processing representation of the cube
	private ArrayList<Move> sequence = new ArrayList<>(); // move sequence
	private Move currentMove; // current move to be performed

    // other Processing related variables
	private PeasyCam cam;
	protected float speed = 0.1f;//0.05f;
	private boolean animating = false;
    
    // Reference to the main application.
	private MainApp mainApp;

    // setting up all moves
	private Move[] allMoves = new Move[] {
            new Move(0, 1, 0, 1,this),    // D'
            new Move(0, 1, 0, -1,this),   // D
            new Move(0, -1, 0, 1,this),   // U
            new Move(0, -1, 0, -1,this),  // U'
            new Move(1, 0, 0, 1,this),    // R
            new Move(1, 0, 0, -1,this),   // R'
            new Move(-1, 0, 0, 1,this),   // L'
            new Move(-1, 0, 0, -1,this),  // L
            new Move(0, 0, 1, 1,this),    // F
            new Move(0, 0, 1, -1,this),   // F'
            new Move(0, 0, -1, 1,this),   // B'
            new Move(0, 0, -1, -1,this)   // B
    };

    // set up PApplet function
    public void setup() {
    	
    	// initialize solver    	
        try {
            solver = new ThisletwaiteSolver();
        } catch (DatabaseGenerationError databaseGenerationError) {
            databaseGenerationError.printStackTrace();
        }
        
        ellipseMode(CENTER);
        currentMove = allMoves[0];

        cam = new PeasyCam(this, 400);
        myUtils.UpdateSingmasterState(cube,internalState.toSingMasterNotation());
        
        surface.setTitle("Cube Engine");
        surface.setLocation(100, 100);
    }


    // function to draw the PApplet
    public void draw() 
    {
        background(40);
        cam.beginHUD();
        fill(255);
        textSize(20);

        cam.endHUD();
        rotateX(-0.5f);
        rotateY(0.4f);
        rotateZ(0.1f);
        
        // updating the current move
        currentMove.update();
        if (currentMove.finished()) {
            if (sequence.size() > 0) {
                currentMove = sequence.remove(0);
                currentMove.start();
            }
            else if (animating)
            {
            	// alert main App that motion has ended.
                animationEnding();
            }
        }

        scale(50);
        
        for (int i = 0; i < cube.length; i++) {
            push();
            if (abs(cube[i].z) > 0 && cube[i].z == currentMove.z) {
                rotateZ(currentMove.angle);
            } else if (abs(cube[i].x) > 0 && cube[i].x == currentMove.x) {
                rotateX(currentMove.angle);
            } else if (abs(cube[i].y) > 0 && cube[i].y == currentMove.y) {
                rotateY(-currentMove.angle);
            }

            cube[i].show();
            pop();
        }

    }
    
    // randomly set the cube
    public void randomScramble(){
        internalState = new Cube().randomize();
        String str = internalState.toSingMasterNotation();
        myUtils.UpdateSingmasterState(cube,str);
    }
    
    // reset the cube to the solved state
    public void resetSolved(){
        internalState = new Cube();
        String str = internalState.toSingMasterNotation();
        myUtils.UpdateSingmasterState(cube,str);
    }

    // update the cube to the provided state in Singmaster form
    public void singmasterScramble(String singmaster) throws SingmasterError{

    	// throws SingmasterError if scramble is illegal
        internalState = new Cube().fromSingMasterNotation(singmaster); 
        myUtils.UpdateSingmasterState(cube,internalState.toSingMasterNotation());
    }

    // function to solve the cube and start the animation
    public String Solve() throws RubiksSolutionException, InvalidMoveString
    {
    	String solution = null;
    	
    	if (!internalState.isSolved())
    	{
    		solution = solver.solve(internalState.clone());
    	
    		if (solution == null || solution.isBlank())
    			return null; // no solution available
    		ApplyMove(solution);
    	}
    	
    	return solution;
    }
    
    // apply the provided move sequence
    public void ApplyMove(String moves) throws InvalidMoveString
    {
    	animationStarting();
        sequence.clear();
        
        Cube.checkMoveSequence(moves); // throws InvalidMoveString in case of invalid notation

        internalState.Move(moves);
        int i = 0;
        while (i < moves.length()) {
            String move = moves.substring(i,i+2);

            if (move.contains("2")){
                move = move.charAt(0) + "1";
                sequence.add(allMoves[Move.moveDictionary.get(move)]);
            }
            sequence.add(allMoves[Move.moveDictionary.get(move)]);
            i+=2;
        }
        currentMove = sequence.remove(0);
        currentMove.start();
    }

    // function to turn the Z layer
    protected void turnZ(int index, int dir) {
        for (int i = 0; i < cube.length; i++) {
            Cubie qb = cube[i];
            if (qb.z == index) {
                PMatrix2D matrix = new PMatrix2D();
                matrix.rotate(dir*HALF_PI);
                matrix.translate(qb.x, qb.y);
                qb.update(round(matrix.m02), round(matrix.m12), round(qb.z));
                qb.turnFacesZ(dir);
            }
        }
    }
    
    // function to turn the Y layer
    protected void turnY(int index, int dir) {
        for (int i = 0; i < cube.length; i++) {
            Cubie qb = cube[i];
            if (qb.y == index) {
                PMatrix2D matrix = new PMatrix2D();
                matrix.rotate(dir*HALF_PI);
                matrix.translate(qb.x, qb.z);
                qb.update(round(matrix.m02), qb.y, round(matrix.m12));
                qb.turnFacesY(dir);
            }
        }
    }
    
    // function to turn the X layer
    protected void turnX(int index, int dir) {
        for (int i = 0; i < cube.length; i++) {
            Cubie qb = cube[i];
            if (qb.x == index) {
                PMatrix2D matrix = new PMatrix2D();
                matrix.rotate(dir*HALF_PI);
                matrix.translate(qb.y, qb.z);
                qb.update(qb.x, round(matrix.m02), round(matrix.m12));
                qb.turnFacesX(dir);
            }
        }
    }
    
    public String getSingmasterErrorsDoc()
    {
    	return myUtils.getSingmasterErrorsDoc();
    }
    
    public void settings() 
    {
        // System.setProperty("jogl.disable.openglcore", "false");
        size(600, 600, P3D); 
    }

    public void run(){
        String[] processingArgs = {""};
        PApplet.runSketch(processingArgs, this);
    }
    
    public void reallyExit()
    {
    	super.exit();
    }
    
    public void exit() 
    {
    	this.mainApp.closeProgram();
	}
    
    
    public void setMainApp(MainApp mainApp) 
    {
        this.mainApp = mainApp;
    }
    
    private void animationStarting()
    {
    	animating = true;
    	mainApp.animationStarting();
    }
    private void animationEnding()
    {
    	animating = false;
    	mainApp.animationEnding();
    }
}
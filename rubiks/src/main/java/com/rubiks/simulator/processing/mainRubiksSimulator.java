package com.rubiks.simulator.processing;


import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PMatrix2D;


import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import com.rubiks.MainApp;
import com.rubiks.simulator.Thisletwaite.ThisletwaiteSolver;
import com.rubiks.simulator.cube.Cube;
import com.rubiks.utils.Exceptions.DatabaseGenerationError;
import com.rubiks.utils.Exceptions.InvalidMoveString;
import com.rubiks.utils.Exceptions.RubiksSolutionException;
import com.rubiks.utils.Exceptions.SingmasterError;


public class mainRubiksSimulator extends PApplet {
    //Solver and Internal Representation Variables
    protected Cube internalState = new Cube();
    protected ThisletwaiteSolver solver;
    {
        try {
            solver = new ThisletwaiteSolver();
        } catch (DatabaseGenerationError databaseGenerationError) {
            databaseGenerationError.printStackTrace();
        }
    }


    //Class utils
    UtilsSimulator myUtils = new UtilsSimulator(this);
    int dim = 3;
    Cubie[] cube = new Cubie[dim*dim*dim];
    ArrayList<Move> sequence = new ArrayList<>();
    Move currentMove;

    //Processing related variables
    PeasyCam cam;
    float speed = 0.1f;//0.05f;
    //Form form;
    int rectSizeX = 120;     // Diameter of rect
    int rectSizeY = 80;     // Diameter of rect
    int rectColor, baseColor;
    int rectHighlight;
    int currentColor;
    SingmasterSketch singmastersketch;
    boolean animating = false;
    
    // Reference to the main application.
    private MainApp mainApp;

    //setting up all moves
    Move[] allMoves = new Move[] {
            new Move(0, 1, 0, 1,this),    //D'
            new Move(0, 1, 0, -1,this),   //D
            new Move(0, -1, 0, 1,this),   //U
            new Move(0, -1, 0, -1,this),   //U'
            new Move(1, 0, 0, 1,this),     //R
            new Move(1, 0, 0, -1,this),   //R'
            new Move(-1, 0, 0, 1,this),   //L'
            new Move(-1, 0, 0, -1,this),   //L
            new Move(0, 0, 1, 1,this),     //F
            new Move(0, 0, 1, -1,this),     //F'
            new Move(0, 0, -1, 1,this),   //B'
            new Move(0, 0, -1, -1,this)   //B
    };
    private static final HashMap<String,Integer > moveDictionary = new HashMap<String,Integer>() {{
        put("D3",0);put("D1",1);put("U1",2);put("U3",3);put("R1",4);put("R3",5);
        put("L3",6);put("L1",7);put("F1",8);put("F3",9);put("B3",10);put("B1",11);

    }};


    public void setup() {

        rectColor = color(69,216,255);
        rectHighlight = color(31,61,68);
        baseColor = color(102);
        currentColor = baseColor;
        ellipseMode(CENTER);
        currentMove = allMoves[0];

        cam = new PeasyCam(this, 400);
        myUtils.UpdateSingmasterState(cube,internalState.toSingMasterNotation());
        
        surface.setTitle("Cube Engine");
        surface.setLocation(100, 100);
        
    }



    public void draw() {

        background(40);
        cam.beginHUD();
        fill(255);
        textSize(20);

        cam.endHUD();
        rotateX(-0.5f);
        rotateY(0.4f);
        rotateZ(0.1f);

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

    public boolean overRect(int x, int y, int width, int height)  {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    public void randomScramble(){
        internalState = new Cube().randomize();
        String str = internalState.toSingMasterNotation();
        myUtils.UpdateSingmasterState(cube,str);
    }
    
    public void resetSolved(){
        internalState = new Cube();
        String str = internalState.toSingMasterNotation();
        myUtils.UpdateSingmasterState(cube,str);
    }

    public void singmasterScramble(String singmaster) throws SingmasterError{

            internalState = new Cube().fromSingMasterNotation(singmaster); // throws SingmasterError if scramble is illegal
            
            myUtils.UpdateSingmasterState(cube,internalState.toSingMasterNotation());
    }

    public void launchSingmasterSketch(){
        if (singmastersketch == null) {
            singmastersketch = new SingmasterSketch();
            String[] processingArgs = {"SingmasterSketch"};
            PApplet.runSketch(processingArgs, singmastersketch);
        }
        else {
            singmastersketch.setVisible();
        }
    }

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
                sequence.add(allMoves[moveDictionary.get(move)]);
            }
            sequence.add(allMoves[moveDictionary.get(move)]);
            i+=2;
        }
        currentMove = sequence.remove(0);
        currentMove.start();
    }



    public void turnZ(int index, int dir) {
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

    public void turnY(int index, int dir) {
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

    public void turnX(int index, int dir) {
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
    
    public void settings() {
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
    	System.out.println("Calling exit");
    	this.mainApp.closeProgram();
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
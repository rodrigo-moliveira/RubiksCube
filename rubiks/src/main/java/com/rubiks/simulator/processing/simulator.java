package com.rubiks.simulator.processing;


import com.rubiks.simulator.cube.Cube;
import com.rubiks.simulator.Thisletwaite.ThisletwaiteSolver;

import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PMatrix2D;


import com.rubiks.utils.Exceptions.DatabaseGenerationError;

import java.util.ArrayList;
import java.util.HashMap;


public class simulator extends PApplet {
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
    int button1X, button2X, button3X, buttonsY;
    int rectSizeX = 120;     // Diameter of rect
    int rectSizeY = 80;     // Diameter of rect
    int rectColor, baseColor;
    int rectHighlight;
    int currentColor;
    boolean button1over = false,button2over = false, button3over = false;
    SingmasterSketch singmastersketch;

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
        button1X = width / 10; button2X = button1X + 40 + rectSizeX ; button3X = button2X + 40 + rectSizeX;
        buttonsY =  height/10;
        ellipseMode(CENTER);
        currentMove = allMoves[0];

        cam = new PeasyCam(this, 400);
        myUtils.UpdateSingmasterState(cube,internalState.toSingMasterNotation());
        
    }



    public void draw() {
        update();

        background(40);
        cam.beginHUD();
        fill(255);
        textSize(20);


        //button 1
        if (button1over) {
            fill(rectHighlight);
        } else {
            fill(rectColor);
        }
        rect(button1X, buttonsY, rectSizeX, rectSizeY);
        fill(color(255,255,255));
        text("Scramble",button1X+10,buttonsY+45);


        //button 2
        if (button2over) {
            fill(rectHighlight);
        } else {
            fill(rectColor);
        }
        rect(button2X, buttonsY, rectSizeX, rectSizeY);
        fill(color(255,255,255));
        text("Move",button2X+30,buttonsY+45);


        //button 3
        if (button3over) {
            fill(rectHighlight);
        } else {
            fill(rectColor);
        }
        rect(button3X, buttonsY, rectSizeX, rectSizeY);
        fill(color(255,255,255));
        text("Solve",button3X+30,buttonsY+45);


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

    void update() {
        if ( overRect(button1X, buttonsY, rectSizeX, rectSizeY) ) {
            button1over = true;
            button2over = false; button3over = false;

        } else if( overRect(button2X, buttonsY, rectSizeX, rectSizeY) ) {
            button2over = true;
            button1over = false; button3over = false;
        }
        else if( overRect(button3X, buttonsY, rectSizeX, rectSizeY) ) {
            button3over = true;
            button1over = false; button2over = false;
        }
        else{
            button1over = false; button2over = false; button3over = false;
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
        //form.close();

    }

    public void singmasterScramble(){
//        String singmaster = (String) form.getByLabel("Insert initial state using Singmaster Notation:").getValue();
//        try {
//            internalState = new Cube().fromSingMasterNotation(singmaster);
//            myUtils.UpdateSingmasterState(cube,internalState.toSingMasterNotation());
//        } catch (SingmasterError singmasterError) {
//            booster.showErrorDialog(singmasterError.toString() + "\n" +
//                    "Please see documentation for error codes","ERROR");
////            singmasterError.printStackTrace();
//        }
//        form.close();
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


    public void mousePressed() {

        if (button1over) {
//        	try {
//	        	InitialStateForm form = new InitialStateForm();
//	        	form.launchForm();
//        	} catch(Exception e)
//        	{
//        		e.printStackTrace();
//        	}
        	
        } else if (button2over) {
//            String InputSequence = booster.showTextInputDialog("Insert move sequence " +
//                    "\npossible moves:" +
//                    "\nR1,R2,R3,L1,L2,L3,F1,F2,F3,\n" +
//                    "B1,B2,B3,U1,U2,U3,D1,D2,D3");
//            ApplyMove(InputSequence);


        }else if (button3over) {
//            if (internalState.isSolved())
//                booster.showInfoDialog("Cube is already solved. No solution was processed.");
//            else {
//                String solution = solver.solve(internalState.clone());
//                ApplyMove(solution);
//            }
        }

    }



    public void ApplyMove(String moves){
        sequence.clear();
        if(!Cube.checkMoveSequence(moves)) {
//            booster.showErrorDialog("Move sequence is not in correct form.", "ERROR");
            return;
        }

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
    public void settings() {
        //System.setProperty("jogl.disable.openglcore", "false");
        size(600, 600, P3D); }


    public void run(){
        String[] processingArgs = {"simulator"};
        PApplet.runSketch(processingArgs, this);


    }
}
package com.rubiks;

import com.rubiks.cube.Cube;
import com.rubiks.cube.Thisletwaite.ThisletwaiteSolver;
import com.rubiks.utils.Exceptions.DatabaseGenerationError;
import com.rubiks.utils.Exceptions.RubiksSolutionException;
import com.rubiks.utils.Exceptions.SingmasterError;

public class mainCommandLine {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: Solve <cube string (singmaster notation)>. See SingmasterNotationMap.png for the visualization of the notation map.\n"+
                    "Solved cube: UUUUUUUUUFFFFFFFFFRRRRRRRRRDDDDDDDDDBBBBBBBBBLLLLLLLLL");
        }
        else{
            Cube cube = null;
            ThisletwaiteSolver solver;
                        
            //Read Cube state
            try {
                cube = new Cube().fromSingMasterNotation(args[0]);
            } catch (SingmasterError singmasterError) {
                System.out.println("The provided singmaster notation is not correct\n"+
                        "Error codes\n"+
                        "return  0: Cube is solvable\n"+
                                "-1: There is not exactly one facelet of each colour or there exist unknown colors\n"+
                                "-2: Not all 12 edges exist exactly once\n"+
                                "-3: Flip error: One edge has to be flipped\n"+
                                "-4: Not all 8 corners exist exactly once\n"+
                                "-5: Twist error: One corner has to be twisted\n"+
                                "-6: Parity error: Two corners or two edges have to be exchanged\n"+
                                "-7 Wrong Notation: Singmaster notation is wrong (see SingmasterNotationMap.png)\n");
                singmasterError.printStackTrace();
            }

            //Initialize Cube Solver
            try {
                solver = new ThisletwaiteSolver();
            } catch (DatabaseGenerationError databaseGenerationError) {
                databaseGenerationError.printStackTrace();
                return;
            }

            //Solve cube
            assert cube != null;
            String solution;
			try {
				solution = solver.solve(cube);
				System.out.println("\n\n--------------\nSolution is:\n" + solution);
	            System.out.println("Solution successful?: "+ cube.isSolved());
			} catch (RubiksSolutionException e) {
				e.printStackTrace();
			}            
        }
    }
}

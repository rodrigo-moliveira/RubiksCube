package src.main;

import src.Thisletwaite.ThisletwaiteSolver;
import src.Cube.Cube;
import src.utils.Exceptions.DatabaseGenerationError;
import src.utils.Exceptions.SingmasterError;

public class mainCommandLine {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("""
                    Usage: Solve <cube string (singmaster notation)>. See SingmasterNotationMap.png for the visualization of the notation map.
                    Solved cube: UUUUUUUUUFFFFFFFFFRRRRRRRRRDDDDDDDDDBBBBBBBBBLLLLLLLLL""");
        }
        else{
            Cube cube = null;
            ThisletwaiteSolver solver;

            //Read Cube state
            try {
                cube = new Cube().fromSingMasterNotation(args[0]);
            } catch (SingmasterError singmasterError) {
                System.out.println("""
                        The provided singmaster notation is not correct
                        Error codes        
                        return  0: Cube is solvable               
                                -1: There is not exactly one facelet of each colour or there exist unknown colors
                                -2: Not all 12 edges exist exactly once
                                -3: Flip error: One edge has to be flipped
                                -4: Not all 8 corners exist exactly once
                                -5: Twist error: One corner has to be twisted
                                -6: Parity error: Two corners or two edges have to be exchanged
                                -7 Wrong Notation: Singmaster notation is wrong (see SingmasterNotationMap.png)""");
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
            String solution = solver.solve(cube);
            System.out.println("\n\n--------------\nSolution is:\n" + solution);
            System.out.println("Solution successful?: "+ cube.isSolved());
//            System.out.println(cube.toString());
        }
    }
}

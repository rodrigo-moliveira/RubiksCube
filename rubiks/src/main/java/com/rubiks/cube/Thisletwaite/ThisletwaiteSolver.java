package com.rubiks.cube.Thisletwaite;

import com.rubiks.cube.Cube;
import com.rubiks.utils.Exceptions.DatabaseGenerationError;
import com.rubiks.utils.Exceptions.RubiksSolutionException;


public class ThisletwaiteSolver {

    ThisletwaiteDB db;

    public ThisletwaiteSolver () throws DatabaseGenerationError {

        db = new ThisletwaiteDB();
    }
    
    public String solve(Cube c) throws RubiksSolutionException {
        StringBuilder totalSolution = new StringBuilder();

        for (int phase = 1; phase <= 4; phase++) 
        {
            String solution = db.getPhaseSolution(phase, c);
            if (!solution.contains("E")) 
            {
                c.Move(solution);
                totalSolution.append(solution);
            }
        }
        return totalSolution.toString();
    }

}

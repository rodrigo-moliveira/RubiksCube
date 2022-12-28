package com.rubiks.simulator.Thisletwaite;

import com.rubiks.simulator.cube.Cube;
import com.rubiks.utils.Exceptions.DatabaseGenerationError;


public class ThisletwaiteSolver {

    ThisletwaiteDB db;

    public ThisletwaiteSolver () throws DatabaseGenerationError {

        db = new ThisletwaiteDB();
    }
    public String solve(Cube c) {
        StringBuilder totalSolution = new StringBuilder();

        for (int phase = 1; phase <= 4; phase++) {
            String solution = db.getPhaseSolution(phase, c);
            if (!solution.contains("E")) {
                c.Move(solution);
                totalSolution.append(solution);
            }
        }
        return totalSolution.toString();
    }

}

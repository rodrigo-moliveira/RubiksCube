package src.Thisletwaite;

import src.Cube.Cube;
import src.utils.Exceptions.DatabaseGenerationError;


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
//            switch (phase){
//                case 1 ->
//                        System.out.println("ID after phase 1 - " + db.idPhase1(c));
//                case 2 ->
//                        System.out.println("ID after phase 2 - " + db.idPhase2(c));
//                case 3 ->
//                        System.out.println("ID after phase 3 - " + db.idPhase3(c));
//                case 4 ->
//                        System.out.println("ID after phase 4 - " + db.idPhase4(c));
//            }

        }
        return totalSolution.toString();
    }

}

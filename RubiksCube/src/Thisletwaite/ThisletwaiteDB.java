package src.Thisletwaite;

import src.Search.CubeNode;
import src.Cube.Cube;
import src.utils.Exceptions.DatabaseFormatError;
import src.utils.Exceptions.DatabaseGenerationError;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;


class Line{
    long id;
    String path;
    public Line (){
    }
    public long getId(){return this.id;}
    public String getPath(){return this.path;}
    public void setId(long val){this.id = val;}
    public void setPath(String val){this.path = val;}

}


//Implementation of Thisletwaite Database
public class ThisletwaiteDB {
    private final int[] allowable_moves = new int[18];
    private File file;
    private FileWriter myWriter;
    private final String PATH = "/Database/Thisletwaite/";

    //coset G3 to G4
    private static final int[] g34cornerOrbit1 = {0,3,5,6};
    private static final int[] g34cornerOrbit2 = {1,2,4,7};
    private static final int[] g34edgeOrbit1 = {0,3,8,11};
    private static final int[] g34edgeOrbit2 = {1,2,9,10};
    private static final int[] g34edgeOrbit3 = {4,5,6,7};

    //array with hashes for each phase
    List<HashMap<Long, String>> phaseHashList = new ArrayList<>();




    public ThisletwaiteDB() throws DatabaseGenerationError {
        System.out.println("Initializing Thisletwaite Database");

        //activating all moves
        for (int i = 0; i < 18; i++)
            allowable_moves[i] = 1;

        for (int phase = 1; phase <= 4; phase++) {
            InputStream path = getClass().getResourceAsStream("/src/Database/Thisletwaite/phase" + phase + ".txt");
            HashMap<Long, String> currentHash;
            try {
                currentHash = new HashMap<>();
                BufferedReader rd = new BufferedReader(new InputStreamReader(path));
                String line;

                //process data & fill the hash table
                while ((line = rd.readLine()) != null) {
                    Line data = process_data(line);
                    currentHash.put(data.getId(), data.getPath());
                }
                phaseHashList.add(currentHash);
                nextPhase(phase);
                continue;
            } catch (IOException | DatabaseFormatError e) {
                System.out.println("Database file not found or some errors were found. " +
                        "Generating new file (this process might take a while)");
                buildDBs(phase);
            }
            phase--;
        }

//        seeAllowedCornerPermutationsG3();


        //assert if each hash has the correct size
        if (phaseHashList.get(0).size() != 2048 || phaseHashList.get(1).size() != 1082565)
            throw new DatabaseGenerationError("Thisletwaite databases not correctly created. Aborting");
     }


    Line process_data(String line) throws DatabaseFormatError {
        Line new_line = new Line();
        String[] parts;


        //split each line into (phaseID) + (Sequence of Moves)
        parts = line.split(", ");
        if (parts.length != 2)
            throw new DatabaseFormatError("Error reading database at line " + line);

        //check phase ID
        try {
            new_line.setId(Long.parseLong(parts[0]));
        }catch(NumberFormatException e){
            throw new DatabaseFormatError("Error reading database at line '" + line + "' : '" +
                    parts[0] + "' was not successfully converted to type Long");
        }

        //check sequence of moves
        new_line.setPath(parts[1]);
        for (int i = 0; i < new_line.getPath().length(); i++){
            char c = new_line.getPath().charAt(i);

            if ( i % 2 == 0 ) {
                //even chars should be letters in the set {'L','R','B','D','F','U','E'}
                if (c != 'F' && c != 'B' && c != 'R' && c != 'L' && c != 'D' && c != 'U' && c != 'E')
                    throw new DatabaseFormatError("Error reading database at line '" + line + "' : '" +
                            parts[1] + "' is not a valid Sequence of moves : Unknown move '" + c +"'.");
            }
            else {
                //odd chars should be ints in the set {1,2,3}
                if (c != '1' && c != '2' && c != '3')
                    throw new DatabaseFormatError("Error reading database at line '" + line + "' : '" +
                            parts[1] + "' is not a valid Sequence of moves : Unknown move '" +
                            new_line.getPath().charAt(i-1)+c +"'.");
            }
        }
        return new_line;
    }


    void buildDBs(int phase){
        int size = 1;
        long id, id_goal;
        int counter = 0;
        Cube cube = new Cube(); //initialize a solved cube object

        //get phaseID function
        Method idFunction = null;
        try {
            idFunction = this.getClass().getMethod("idPhase" + (phase), Cube.class);
            id_goal = (long) idFunction.invoke(this, cube);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            id_goal = -1;
        }

        //create open list of search and add node with solved state
        Queue<CubeNode<Cube,Integer,Long>> open_list = new LinkedList<>();
        CubeNode<Cube,Integer,Long> searchNode = new CubeNode<>(cube.clone(), id_goal);
        open_list.add(searchNode);

        //create closed list (contains all states that have been successfully expanded in the BFS)
        List<Long> closed_list=new ArrayList<>();


        //create file to write current phase database
        try {
            this.createFile(phase);
            this.myWriter = new FileWriter(file);
            this.writeToFile(id_goal + ", E\n");
        } catch (IOException  e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        //Do breath first search of current phase
        while (size != 0) {
            searchNode = open_list.remove();

            for (int move = 0; move < 6; move++) {
                for (int amount = 0; amount < 3; amount++) {
                    searchNode.getState().move(3 * move);

                    //check if move is allowed in this phase
                    if (allowable_moves[3 * move + amount] == 1) {
                        try {
                            assert idFunction != null;
                            id = (long) idFunction.invoke(this, searchNode.getState()); // pass arg
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                            id = -1;
                        }

                        if (id != id_goal) {
                            if (!closed_list.contains(id)) {
                                closed_list.add(id);
                                String action = Cube.move_dict.getKey((byte)(3 * move + 2 - amount));
                                CubeNode<Cube,Integer,Long> new_node = new CubeNode<>(searchNode.getState().clone(), id,
                                        action + searchNode.getPath());
                                open_list.add(new_node);
                                counter++;

                                //insert in file
                                this.writeToFile(id + ", " + new_node.getPath() + "\n");
                            }
                        }
                    }
                }
                searchNode.getState().move(3 * move); // 4th move
            }

            size = open_list.size();
        }
        this.closeFile();
    }


    private void nextPhase(int phase){
        switch (phase) {
            case 1 -> {
                allowable_moves[6] = 0;
                allowable_moves[8] = 0;
                allowable_moves[15] = 0;
                allowable_moves[17] = 0;
            }
            case 2 -> {
                allowable_moves[3] = 0;
                allowable_moves[5] = 0;
                allowable_moves[12] = 0;
                allowable_moves[14] = 0;
            }
            case 3 -> {
                allowable_moves[0] = 0;
                allowable_moves[2] = 0;
                allowable_moves[9] = 0;
                allowable_moves[11] = 0;
            }
        }
    }


    public long idPhase1(Cube c){
        long id = 0;
        byte [] edges = c.getEdgesArray();
        for (int i = 0; i < 12; i++)
            id += (long) (Cube.edge_ori(edges[i])) * (1L << Cube.edge_perm(edges[i]));

        return id;
    }

    public long idPhase2(Cube c){
        long id = 0;
        byte [] corners = c.getCornersArray();
        byte [] edges = c.getEdgesArray();

        for (int i = 0; i < 8; i++) {
            id += (long) (Cube.corner_ori(corners[i])) * (1L << 2*Cube.corner_perm(corners[i]));
        }


        for (int i = 0; i < 12; i++){
            id += Cube.isEdgeInUDSlice(i) * (1L << (16 + Cube.edge_perm(edges[i])));
        }

        return id;
    }

    public long idPhase3(Cube c){
        long id = 0;
        byte [] corners = c.getCornersArray();
        byte [] edges = c.getEdgesArray();

        /*
        * bit 1 -> corner belongs to Orbit 1
        * bit 0 -> corner belongs to Orbit 0
        */
        int perm = Cube.corner_perm(corners[0]);
        id +=  (1L << 2*perm);
        perm = Cube.corner_perm(corners[3]);
        id +=  (1L << 2*perm);


        perm = Cube.corner_perm(corners[1]);
        id +=  (2L << 2*perm);
        perm = Cube.corner_perm(corners[2]);
        id +=  (2L << 2*perm);

        perm = Cube.corner_perm(corners[4]);
        id +=  (3L << 2*perm);
        perm = Cube.corner_perm(corners[7]);
        id +=  (3L << 2*perm);



        for (int index = 0; index < 12; index++){
            if (index >= 4 && index <= 7)
                continue;
            perm = Cube.edge_perm(edges[index]);
            int finalIndex = index;
            int finalPerm = perm;
            boolean isCorrectOrbit =  (Arrays.stream(g34edgeOrbit1).anyMatch(j -> j == finalIndex) && Arrays.stream(g34edgeOrbit1).anyMatch(j -> j == finalPerm) ||
                    (!Arrays.stream(g34edgeOrbit1).anyMatch(j -> j == finalIndex) && Arrays.stream(g34edgeOrbit2).anyMatch(j -> j == finalPerm)));
            if (!isCorrectOrbit) {
                id +=  (1L << (perm + 30));
            }
        }




//        //parity check
        id <<= 1;
        for (int i = 0; i < 8; i++ )
            for( int j = i + 1; j < 8; j++ )
                id ^= (Cube.corner_perm(corners[i]) > Cube.corner_perm(corners[j])) ? 1 : 0;



        return id;
    }

    boolean checkPermutation(byte [] corners){
        int[] orbit1 = new int[4];
        int[] orbit2 = new int[4];

        for (int i = 0; i < 4; i++) {
            int perm = Cube.corner_perm(corners[g34cornerOrbit1[i]]);
            int index = Arrays.stream(g34cornerOrbit1).boxed().collect(Collectors.toList()).indexOf(perm);
            orbit1[i] = index;

            perm = Cube.corner_perm(corners[g34cornerOrbit2[i]]);
            index = Arrays.stream(g34cornerOrbit2).boxed().collect(Collectors.toList()).indexOf(perm);
            orbit2[i] = index;
        }
        int[] possible1 = {orbit1[0],orbit1[1],orbit1[2],orbit1[3]};
        int[] possible2 = {orbit1[2],orbit1[3],orbit1[0],orbit1[1]};
        int[] possible3 = {orbit1[1],orbit1[0],orbit1[3],orbit1[2]};
        int[] possible4 = {orbit1[3],orbit1[2],orbit1[1],orbit1[0]};

        return Arrays.equals(orbit2, possible1) || Arrays.equals(orbit2, possible2) || Arrays.equals(orbit2, possible3) ||
                Arrays.equals(orbit2, possible4);
    }

    public long idPhase4(Cube c){
        long id = 0;
        byte [] corners = c.getCornersArray();
        byte [] edges = c.getEdgesArray();

        //corner Orbits
        for (int i = 0; i < 3; i++){
            int perm = Cube.corner_perm(corners[g34cornerOrbit1[i]]);
            int index = Arrays.stream(g34cornerOrbit1).boxed().collect(Collectors.toList()).indexOf(perm);
            id += index;
            id <<= 2;
        }
        for (int i = 0; i < 3; i++){
            int perm = Cube.corner_perm(corners[g34cornerOrbit2[i]]);
            int index = Arrays.stream(g34cornerOrbit2).boxed().collect(Collectors.toList()).indexOf(perm);
            id += index;
            id <<= 2;
        }

        //Edge Orbits
        for (int i = 0; i < 3; i++){
            int perm = Cube.edge_perm(edges[g34edgeOrbit1[i]]);
            int index = Arrays.stream(g34edgeOrbit1).boxed().collect(Collectors.toList()).indexOf(perm);
            id += index;
            id <<= 2;
        }
        for (int i = 0; i < 3; i++){
            int perm = Cube.edge_perm(edges[g34edgeOrbit2[i]]);
            int index = Arrays.stream(g34edgeOrbit2).boxed().collect(Collectors.toList()).indexOf(perm);
            id += index;
            id <<= 2;
        }
        for (int i = 0; i < 3; i++){
            int perm = Cube.edge_perm(edges[g34edgeOrbit3[i]]);
            int index = Arrays.stream(g34edgeOrbit3).boxed().collect(Collectors.toList()).indexOf(perm);
            id += index;
            id <<= 2;
        }
        return id;
    }


    public long idPhase5(Cube c){
        long id = 0;
        byte [] corners = c.getCornersArray();
        byte [] edges = c.getEdgesArray();

        for (int i = 0; i < 8; i++){
            int perm = Cube.corner_perm(corners[i]);
            id += perm;
            id <<=3;
        }

        return id;
    }



    private void createFile(int phase){
        try {
            this.file = new File(PATH + "phase" + phase + ".txt");
            if (this.file.createNewFile()) {
                System.out.println("File created: " + this.file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            this.file = null;
        }
        assert this.file != null;
    }


    private void writeToFile(String msg){
        try {
            myWriter.write(msg);
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    private void closeFile(){
        try {
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    HashMap<Long, String> getPhaseDB(int phase){
        return phaseHashList.get(phase-1);
    }

    public String getPhaseSolution(int phase, Cube c){
        String solution = "";
        long id = -1;

        //get phaseID function
        Method idFunction = null;
        try {
            idFunction = this.getClass().getMethod("idPhase" + (phase), Cube.class);
            id = (long) idFunction.invoke(this, c); // pass arg
            solution = getPhaseDB(phase).get(id);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return solution;
    }

    void seeAllowedCornerPermutationsG3(){
        int size = 1;
        long id, id_goal;
        int counter = 0;
        Cube cube = new Cube(); //initialize a solved cube object
        System.out.println("phase 5\n\n\n");
        id_goal = idPhase5(cube);

        System.out.println(id_goal);

        //create open list of search and add node with solved state
        Queue<CubeNode<Cube,Integer,Long>> open_list = new LinkedList<>();
        CubeNode<Cube,Integer,Long> searchNode = new CubeNode<>(cube.clone(), id_goal);
        open_list.add(searchNode);

        //create closed list (contains all states that have been successfully expanded in the BFS)
        List<Long> closed_list=new ArrayList<>();



        //Do breath first search of current phase
        while (size != 0) {
            searchNode = open_list.remove();

            for (int move = 0; move < 6; move++) {
                for (int amount = 0; amount < 3; amount++) {
                    searchNode.getState().move(3 * move );

                    //check if move is allowed in this phase
                    if (allowable_moves[3 * move + amount] == 1) {
                        id = idPhase5(searchNode.getState()); // pass arg


                        if (id != id_goal) {
                            if (!closed_list.contains(id)) {
                                closed_list.add(id);
                                String action = Cube.move_dict.getKey((byte)(3 * move + 2 - amount));
                                CubeNode<Cube,Integer,Long> new_node = new CubeNode<>(searchNode.getState().clone(), id,
                                        action + searchNode.getPath());
                                open_list.add(new_node);
                                counter++;

                                //insert in file
                                int[] orbit1 = new int[4];
                                int[] orbit2 = new int[4];
                                int[] orbit1indx = new int[4];
                                int[] orbit2indx = new int[4];
                                int[] tot = new int[8];
                                byte [] corners = new_node.getState().getCornersArray();

                                for (int i = 0; i < 4; i++) {
                                    int perm = Cube.corner_perm(corners[g34cornerOrbit1[i]]);
                                    int index = Arrays.stream(g34cornerOrbit1).boxed().collect(Collectors.toList()).indexOf(perm);
                                    orbit1[i] = perm;
                                    orbit1indx[i] = index;

                                    perm = Cube.corner_perm(corners[g34cornerOrbit2[i]]);
                                    index = Arrays.stream(g34cornerOrbit2).boxed().collect(Collectors.toList()).indexOf(perm);
                                    orbit2indx[i] = index;
                                    orbit2[i] = perm;
                                }
                                for (int i = 0; i < 8; i++)
                                        tot[i] = Cube.corner_perm(corners[i]);

                                System.out.println(Arrays.toString(orbit1) + " " + Arrays.toString(orbit2) + " " + Arrays.toString(orbit1indx) + " " + Arrays.toString(orbit2indx) + " " + idPhase3(new_node.getState()));
                            }
                        }
                    }
                }
                searchNode.getState().move(3 * move); // 4th move
            }
            size = open_list.size();
        }
    }



    public static int indexOfSmallest(int[] array){

        // add this
        if (array.length == 0)
            return -1;

        int index = 0;
        int min = array[index];

        for (int i = 1; i < array.length; i++){
            if (array[i] <= min){
                min = array[i];
                index = i;
            }
        }
        return index;
    }

}

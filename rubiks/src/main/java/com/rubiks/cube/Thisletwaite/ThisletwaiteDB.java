package com.rubiks.cube.Thisletwaite;

import com.rubiks.cube.Cube;
import com.rubiks.cube.Node;
import com.rubiks.utils.Exceptions.DatabaseFormatError;
import com.rubiks.utils.Exceptions.RubiksSolutionException;

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
    private static final String PATH = "/db/";

    //map G2 to G3
    private static final int[] phase3CornerMap = {0, 3, 1, 2, 4, 7};

    //coset G3 to G4
    private static final int[] g34cornerOrbit1 = {0,3,5,6};
    private static final int[] g34cornerOrbit2 = {1,2,4,7};
    private static final int[] g34edgeOrbit1 = {0,3,8,11};
    private static final int[] g34edgeOrbit2 = {1,2,9,10};
    private static final int[] g34edgeOrbit3 = {4,5,6,7};

    //array with hashes for each phase
    List<HashMap<Long, String>> phaseHashList = new ArrayList<HashMap<Long, String>>();


    public ThisletwaiteDB() {
        
      //activating all moves
      for (int i = 0; i < 18; i++)
          allowable_moves[i] = 1;

        for (int phase = 1; phase <= 4; phase++) 
        {
        	
        	InputStream inputStream = getStreamForPhase(phase);
        	
        	if (inputStream == null)
        	{
        		buildDBs(phase);
        		inputStream = getStreamForPhase(phase);
        		
        		if (inputStream == null)
        		{
        			System.out.println("CRITIAL ERROR: not able to generate database for phase " + phase);
        			System.exit(-1);
        		}
        	}
        	
            try 
            {
            	readPhase(phase, inputStream);
            } catch (IOException | NullPointerException  | DatabaseFormatError e) {
            	System.out.println("CRITIAL ERROR: not able to read database for phase " + phase);
    			System.exit(-1);
            }
            
            nextPhase(phase);
        }
    }
    
    private InputStream getStreamForPhase(int phase)
    {
    	// try to get database from resource directory
    	InputStream inputStream = getClass().getResourceAsStream(PATH + "phase" + phase + ".txt");
    	
    	// try to get database from target directory
    	if (inputStream == null)
    	{
    		String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        	path += "/" + PATH + "/phase" + phase + ".txt";

        	try 
        	{
        		inputStream = new FileInputStream(path);
			} catch (FileNotFoundException e) {
				inputStream = null;
			}
    	}
    	return inputStream;
    }
    
    private void readPhase(int phase, InputStream inputStream) throws IOException, DatabaseFormatError
    {
    	System.out.println("Reading phase " + phase + "...");
    	HashMap<Long, String> currentHash = new HashMap<Long, String>();

        BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
        String line;

        //process data & fill the hash table
        while ((line = rd.readLine()) != null) {
            Line data = process_data(line);
            currentHash.put(data.getId(), data.getPath());
        }
        phaseHashList.add(currentHash);
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
        Cube cube = new Cube(); //initialize a solved cube object

        //get phaseID function
        Method idFunction = null;
        try {
            idFunction = this.getClass().getMethod("idPhase" + (phase), Cube.class);
            id_goal = (long) idFunction.invoke(this, cube);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        //create open list of search and add node with solved state
        Queue<Node<Cube,Long>> open_list = new LinkedList<>();
        Node<Cube,Long> searchNode = new Node<>(cube.clone(), id_goal);
        open_list.add(searchNode);

        //create closed list (contains all states that have been successfully expanded in the BFS)
        List<Long> closed_list=new ArrayList<>();

        //create file to write current phase database
        try {
            this.createFile(phase);
            this.myWriter = new FileWriter(file);
            this.writeToFile(id_goal + ", E\n");
        } catch (IOException  e) {
            e.printStackTrace();
        }

        //Do breath first search of current phase
        while (size != 0) 
        {
            searchNode = open_list.remove();

            for (int move = 0; move < 6; move++) 
            {
                for (int amount = 0; amount < 3; amount++) 
                {
                    searchNode.getState().move(3 * move);

                    //check if move is allowed in this phase
                    if (allowable_moves[3 * move + amount] == 1) 
                    {
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
                                Node<Cube,Long> new_node = new Node<>(searchNode.getState().clone(), id,
                                        action + searchNode.getPath());
                                open_list.add(new_node);


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
            case 1: {
                allowable_moves[6] = 0;
                allowable_moves[8] = 0;
                allowable_moves[15] = 0;
                allowable_moves[17] = 0;
            }
            case 2 : {
                allowable_moves[3] = 0;
                allowable_moves[5] = 0;
                allowable_moves[12] = 0;
                allowable_moves[14] = 0;
            }
            case 3 : {
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
        int perm,k,multiplier;

        //mapping corner orbits
        for (int i = 0; i < phase3CornerMap.length; i++) {
            k = phase3CornerMap[i];
            multiplier = (i / 2) + 1;
            perm = Cube.corner_perm(corners[k]);
            id += ((long) multiplier << 2 * perm);
        }

        //mapping edge orbits
        for (int index = 0; index < 12; index++){
            if (Cube.isEdgeInUDSlice(index) == 1)
                continue;
            final int finalIndex = index;
            int finalPerm = Cube.edge_perm(edges[index]);
            boolean isCorrectOrbit =  (Arrays.stream(g34edgeOrbit1).anyMatch(j -> j == finalIndex) && Arrays.stream(g34edgeOrbit1).anyMatch(j -> j == finalPerm) ||
                    (Arrays.stream(g34edgeOrbit1).noneMatch(j -> j == finalIndex) && Arrays.stream(g34edgeOrbit2).anyMatch(j -> j == finalPerm)));
            if (!isCorrectOrbit) {
                id +=  (1L << (finalPerm + 30));
            }
        }

       //parity check
        id <<= 1;
        for (int i = 0; i < 8; i++ )
            for( int j = i + 1; j < 8; j++ )
                id ^= (Cube.corner_perm(corners[i]) > Cube.corner_perm(corners[j])) ? 1 : 0;
        return id;
    }


    public long idPhase4(Cube c){
        long id = 0;
        int perm,index;
        byte [] corners = c.getCornersArray();
        byte [] edges = c.getEdgesArray();

        int[][] arrays_c = {g34cornerOrbit1,g34cornerOrbit2};
        int[][] arrays_e = {g34edgeOrbit1,g34edgeOrbit2,g34edgeOrbit3};

        for (int[] array : arrays_c) {
            //corner Orbits
            for (int i = 0; i < 3; i++) {
                perm = Cube.corner_perm(corners[array[i]]);
                index = Arrays.stream(array).boxed().collect(Collectors.toList()).indexOf(perm);
                id += index;
                id <<= 2;
            }
        }

        for (int[] array : arrays_e) {
            //corner Orbits
            for (int i = 0; i < 3; i++) {
                perm = Cube.edge_perm(edges[array[i]]);
                index = Arrays.stream(array).boxed().collect(Collectors.toList()).indexOf(perm);
                id += index;
                id <<= 2;
            }
        }
        return id;
    }


    private void createFile(int phase){
        try {
        	String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        	path += "/" + PATH + "/phase" + phase + ".txt";
            this.file = new File(path);

            this.file.createNewFile();
            
        } catch (IOException e) {
            e.printStackTrace();
            this.file = null;
        }
        assert this.file != null;
    }


    private void writeToFile(String msg){
        try {
            myWriter.write(msg);
        } catch (IOException e) {
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

    public String getPhaseSolution(int phase, Cube c) throws RubiksSolutionException{
        String solution = "";
        long id = -1;

        //get phaseID function
        Method idFunction = null;
        try 
        {
            idFunction = this.getClass().getMethod("idPhase" + (phase), Cube.class);
            id = (long) idFunction.invoke(this, c); // pass arg
            solution = getPhaseDB(phase).get(id);
            
            if (solution == null)
            	throw new RubiksSolutionException("No solution found in phase " + phase + 
            			" for cube " + c.toSingMasterNotation() + "\nInternal database phase ID: " + id);
            
        } catch (NullPointerException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RubiksSolutionException("No solution found in phase " + phase + 
        			"for cube " + c.toSingMasterNotation());
        }

        return solution;
    }

//    public static void main(String[] args){
//
//        ThisletwaiteDB local = new ThisletwaiteDB();
//
//        //activating all moves
//        for (int i = 0; i < 18; i++)
//            local.allowable_moves[i] = 1;
//
//        local.buildDBs(1); local.nextPhase(1);
//        local.buildDBs(2); local.nextPhase(2);
//        local.buildDBs(3); local.nextPhase(3);
//        local.buildDBs(4);
//    }

}

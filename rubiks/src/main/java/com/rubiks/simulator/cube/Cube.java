package com.rubiks.simulator.cube;

import com.rubiks.utils.Exceptions.InvalidMoveString;
import com.rubiks.utils.Exceptions.SingmasterError;
import com.rubiks.utils.HashMapInvert;
import com.rubiks.utils.RotateArrays;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.rubiks.utils.UtilityFunctions.getRandomIntegerBetweenRange;


public class Cube implements Cloneable{


    /* 1. - State Variables - Arrays to store the state of the cube (8 corners + 12 edges)*/
    private byte[] c;
    private byte[] e;

    /* 2. Public Getters*/
    public byte[] getEdgesArray(){return Arrays.copyOf(e,e.length);}
    public byte[] getCornersArray(){ return Arrays.copyOf(c,c.length);}

    /*2. - Static class constant variables*/
    static final byte NMOVES = 18;
    static final byte TWISTS= 3;
    static final byte FACES= 6;
    static final byte CUBIES = 24;

    /*3. - Convenience arrays*/
    static public final byte[] corner_ori_inc = new byte[CUBIES]; //array that increments the orientation of corners
    static public final byte[] corner_ori_dec = new byte[CUBIES];//same as above, but decrements orientation of state
    static public final byte[] corner_ori_neg_strip = new byte[CUBIES];
    static public final byte[] mod24 = new byte[2*CUBIES];//mod24 operator helper


    /*4. - Utility functions*/
    static public byte edge_perm(byte cubieval) { return (byte) (cubieval >> 1); }
    static public byte edge_ori(byte cubieval) { return (byte)(cubieval & 1) ; }
    static public byte corner_perm(byte cubieval) {return (byte)(cubieval & 7) ;  }
    static public byte corner_ori(byte cubieval) {return (byte)(cubieval >> 3 );  }
    static public byte edge_flip(byte cubieval) { return (byte)(cubieval ^ 1 ); }
    static public byte edge_val(byte perm, byte ori) { return (byte)(perm * 2 + ori) ; }
    static public byte corner_val(byte perm, byte ori) { return (byte)  (ori * 8 + perm) ; }
    static public byte edge_ori_add(byte cv1, byte cv2) { return (byte)(cv1 ^ edge_ori(cv2)) ; }
    static public byte corner_ori_add(byte cv1, byte cv2){ return (mod24[cv1 + (cv2 & 0x18)] );    }
    static public byte corner_ori_sub(byte cv1, byte cv2){return (byte)(cv1 + corner_ori_neg_strip[cv2] );}
    static public int isEdgeInUDSlice(int perm){ if (perm >= 4 && perm <= 7)return 1;else return 0;}
    void swap_pieces(byte[] arr, int i, int j){byte aux = arr[i];arr[i] = arr[j];arr[j] = aux;}

    //parity checks
    public boolean cornerParity() {
        int p = 0;
        for (int i = 0; i < 8; i++)
            for (int j = i + 1; j < 8; j++)
                p ^= (corner_perm(c[i]) > corner_perm(c[j])) ? 1 : 0;
        return p == 1;
    }
    public boolean edgeParity() {
        int p = 0;
        for (int i = 0; i < 12; i++)
            for (int j = i + 1; j < 12; j++)
                p ^= (edge_perm(e[i]) > edge_perm(e[j])) ? 1 : 0;
        return p == 1;
    }


    /*5. - Dictionaries*/
    public static final HashMapInvert<Byte,String > corner_table = new HashMapInvert<Byte,String>() {{
        put((byte) 0,"UBL");put((byte) 1,"URB");put((byte) 2,"ULF");put((byte) 3,"UFR");
        put((byte) 4,"DLB");put((byte) 5,"DBR");put((byte) 6,"DFL");put((byte) 7,"DRF");
    }};

    private static final HashMapInvert<Byte,String > edge_table = new HashMapInvert<Byte,String>() {{
        put((byte)0,"UB"); put((byte)4,"LB"); put((byte)8,"DB");
        put((byte)1,"UL"); put((byte)5,"RB"); put((byte)9,"DL");
        put((byte)2,"UR"); put((byte)6,"LF"); put((byte)10,"DR");
        put((byte)3,"UF"); put((byte)7,"RF"); put((byte)11,"DF");
    }};

    public static final HashMapInvert<String,Byte> move_dict = new HashMapInvert<String, Byte>() {{
        put("U1", (byte) 0); put("U2", (byte) 1); put("U3", (byte) 2);
        put("F1", (byte) 3); put("F2", (byte) 4); put("F3", (byte) 5);
        put("R1", (byte) 6); put("R2", (byte) 7); put("R3", (byte) 8);
        put("D1", (byte) 9); put("D2", (byte) 10); put("D3", (byte) 11);
        put("B1", (byte) 12); put("B2", (byte) 13); put("B3", (byte) 14);
        put("L1", (byte) 15); put("L2", (byte) 16); put("L3", (byte) 17);
    }};

    private static final Map<Character,Byte> faces = new HashMap<Character, Byte>() {{
        put('U', (byte) 0); put('F', (byte) 1); put('R', (byte) 2);
        put('D', (byte) 3); put('B', (byte) 4); put('L', (byte) 5);
    }};

    /*6. - Move tables Variables*/
    static byte[][] edge_trans = new byte[NMOVES][CUBIES];
    static byte[][] corner_trans = new byte[NMOVES][CUBIES];
    static final byte[][] corner_twist_perm = {{0,1,3,2},{2,3,7,6},{3,1,5,7},
            {4,6,7,5},{1,0,4,5},{0,2,6,4}}; //corner permutation
    static final byte [][]corner_change = {{0,0,0,0},{1,2,1,2},{1,2,1,2},{0,0,0,0},
            {1,2,1,2},{1,2,1,2},}; //corner orientation
    static private final byte[] edge_change = { 0, 0, 1, 0, 0, 1 } ;
    static private final byte[][] edge_twist_perm = {{ 0, 2, 3, 1 },{ 3, 7, 11, 6 },{ 2, 5, 10, 7 },
            { 9, 11, 10, 8 },{ 0, 4, 8, 5 },{ 1, 6, 9, 4 }} ;


    /*6. Static initialization of the move tables*/
    static{
        /*Initialization of auxiliary arrays to help in corner moves (changing orientation)*/
        for(byte i= 0;i <CUBIES;i++){
            byte perm = corner_perm(i);
            byte ori = corner_ori(i);
            corner_ori_inc[i] = corner_val( perm,(byte) ((ori+ 1)%3));
            corner_ori_dec[i] =corner_val( perm, (byte) ((ori+ 2)%3));
            corner_ori_neg_strip[i] = corner_val((byte) 0,(byte)((3-ori)%3));
            mod24[i] = mod24[i+CUBIES] =i;
        }


        /*Initialization of the move tables*/
        //Default unchanged values for all entries (as if there was no move)
        for (byte m=0; m<NMOVES; m++)
            for (byte c=0; c<CUBIES; c++){
                corner_trans[m][c] = c;
                edge_trans[m][c] = c ;
            }


        //Modify the entries affected by the corresponding moves according to corner_twist_perm
        for(byte f= 0;f <FACES;f++)
            for(byte t= 0;t <3;t++) {
                byte m = (byte) (f * TWISTS + t);
                boolean isquarter = (t == 0 || t == 2);
                byte perm_inc = (byte) (t + 1);
                if (m < 0)
                    continue;
                for (byte i = 0; i < 4; i++) {
                    byte ii = (byte) ((i + perm_inc) % 4);
                    for(byte o= 0; o <2; o++){
                        byte oo=o;/*new orientation*/
                        if(isquarter)
                            oo ^= edge_change[f];
                        edge_trans[m][edge_val(edge_twist_perm[f][i], o)] = edge_val(edge_twist_perm[f][ii],oo);
                    }
                    for (byte o = 0; o < 3; o++) {
                        byte oo = o;/*new orientation*/
                        if (isquarter)
                            oo = (byte) ((corner_change[f][i] + oo) % 3);
                        corner_trans[m][corner_val(corner_twist_perm[f][i], o)] = corner_val(corner_twist_perm[f][ii], oo);
                    }
                }
            }
    }

    private static final RotateArrays<Byte> rotate = new RotateArrays<Byte>();
    private static final RotateArrays<Character> rotateChars = new RotateArrays<Character>();

    //Constructors
    public Cube(){

        /*State variables*/
        c = new byte[8]; //corner array
        e = new byte[12]; //edge array

        for(byte i = 0; i <8; i++)
            c[i] = corner_val( i, (byte) 0);
        for(byte i= 0;i <12;i++)
            e[i] =edge_val(i, (byte) 0);
    }

    public Cube randomize(){
        //re-initialize solved cube
        for(byte i = 0; i <8; i++)
            c[i] = corner_val( i, (byte) 0);
        for(byte i= 0;i <12;i++)
            e[i] =edge_val(i, (byte) 0);

        int parity = 0 ; //every time there is swap, parity increments (in the end, parity must be even)
        for (int i=0; i<7; i++) {
            int j = i + getRandomIntegerBetweenRange(0,7-i);
            if (i != j) {
                swap_pieces(c,i,j);
                parity++ ;
            }
        }
        for (int i=0; i<11; i++) {
            int j = i + getRandomIntegerBetweenRange(0,11-i);
            if (i != j) {
                swap_pieces(e,i, j) ;
                parity++ ;
            }
        }
        //fix parity
        if ((parity & 1) == 1)
            swap_pieces(e,10, 11) ;

        //randomize corner orientations
        int s = 24;
        for (int i=0; i<7; i++) {
            int a = getRandomIntegerBetweenRange(0,2);
            s -= a ;
            c[i] = corner_val(corner_perm(c[i]),(byte)a) ;
        }
        //fix corner orientation
        c[7] = corner_val(corner_perm(c[7]), (byte) ((s % 3)));

        //randomize edge orientations
        s = 0 ;
        for (int i=0; i<11; i++) {
            int a = getRandomIntegerBetweenRange(0,1);
            e[i] = edge_ori_add(e[i],(byte)a) ;
            s ^= a ;
        }
        e[11] ^= s ;
        return this;
    }


    /*Move Functions*/
    public void Move (String sequence) {
        int i = 0;
        while (i < sequence.length()) {
            String move = sequence.substring(i,i+2);
            move(move_dict.get(move));
            i+=2;
        }
    }

    public void move (int mov){
        /*move method*/
        c[0] = corner_trans[mov][c[0]];
        c[1] = corner_trans[mov][c[1]];
        c[2] = corner_trans[mov][c[2]];
        c[3] = corner_trans[mov][c[3]];
        c[4] = corner_trans[mov][c[4]];
        c[5] = corner_trans[mov][c[5]];
        c[6] = corner_trans[mov][c[6]];
        c[7] = corner_trans[mov][c[7]];

        e[0] = edge_trans[mov][e[0]];
        e[1] = edge_trans[mov][e[1]];
        e[2] = edge_trans[mov][e[2]];
        e[3] = edge_trans[mov][e[3]];
        e[4] = edge_trans[mov][e[4]];
        e[5] = edge_trans[mov][e[5]];
        e[6] = edge_trans[mov][e[6]];
        e[7] = edge_trans[mov][e[7]];
        e[8] = edge_trans[mov][e[8]];
        e[9] = edge_trans[mov][e[9]];
        e[10] = edge_trans[mov][e[10]];
        e[11] = edge_trans[mov][e[11]];
    }

    @Override
    public String toString() {
        System.out.println("Printing current cube state-");
        StringBuilder ret = new StringBuilder();
        for (byte i = 0; i < 8; i++){
            byte perm = corner_perm(c[i]);
            byte ori = corner_ori(c[i]);
            String cubie = corner_table.get(i);
            String place = rotate.rightRotate(corner_table.get(perm),ori);
            ret.append("Corner ").append(cubie).append(" is in spot ").append(place).append('\n');
        }
        for (byte i = 0; i < 12; i++){
            byte perm = edge_perm(e[i]);
            byte ori = edge_ori(e[i]);
            String cubie = edge_table.get(i);
            String place = rotate.rightRotate(edge_table.get(perm),ori);
            ret.append("Edge ").append(cubie).append(" is in spot ").append(place).append("\n");
        }
        return ret.toString();
    }



    @Override
    public Cube clone() {
        Cube result = null;
        try {
            result = (Cube) super.clone();
            result.c = result.c.clone();
            result.e = result.e.clone();
        } catch (CloneNotSupportedException error) {
            error.printStackTrace(); // should never happen...
        }
        return result;
    }


    /*Export and Import cube state to and from SingMaster Notation*/
    public String toSingMasterNotation(){
        byte[][] singmaster_map_corners = {{1,3,1}, {3,3,1}, {7,3,1}, {9,3,1},
                                            {7,7,9}, {9,7,9},{1,7,9}, {3,7,9}};
        byte[][] singmaster_map_edges = {{2,2},{4,2},{6,2},{8,2},
                {4,6},{6,4},{6,4},{4,6},
                {8,8},{4,8},{6,8},{2,8}};


        char[][] faceletState = {{'0','0','0','0','U','0','0','0','0'},
                {'0','0','0','0','F','0','0','0','0'},
                {'0','0','0','0','R','0','0','0','0'},
                {'0','0','0','0','D','0','0','0','0'},
                {'0','0','0','0','B','0','0','0','0'},
                {'0','0','0','0','L','0','0','0','0'}};

        //corners
        for (byte i = 0; i < 8; i++) {
            byte perm = corner_perm(c[i]);
            byte ori = corner_ori(c[i]);
            char[] cubie = corner_table.get(i).toCharArray();
            char[] place = rotate.rightRotate(corner_table.get(perm),ori).toCharArray();

            //fill piece in the right facelet spots, according to the map "singmaster_map_corners"
            for (byte j = 0; j < 3; j++)
                faceletState[faces.get(place[j])][singmaster_map_corners[perm][(j - ori+3) % 3]-1] = cubie[j];
        }
        //edges
        for (byte i = 0; i < 12; i++) {
            byte perm = edge_perm(e[i]);
            byte ori = edge_ori(e[i]);
            char[] cubie = edge_table.get(i).toCharArray();
            char[] place = rotate.rightRotate(edge_table.get(perm),ori).toCharArray();

            for (byte j = 0; j < 2; j++)
                faceletState[faces.get(place[j])][singmaster_map_edges[perm][(j - ori+2) % 2]-1] = cubie[j];
        }

        return  new String(faceletState[0]) + new String(faceletState[1]) +
                new String(faceletState[2]) + new String(faceletState[3]) +
                new String(faceletState[4]) + new String(faceletState[5]) ;
    }

    public Cube fromSingMasterNotation(String singmaster) throws SingmasterError {
        /*Singmaster Notation - facelet level
        solved cube: UUUUUUUUUFFFFFFFFFRRRRRRRRRDDDDDDDDDBBBBBBBBBLLLLLLLLL

        Error codes
        return  0: Cube is solvable
                -1: There is not exactly one facelet of each colour or there exist unknown colors
                -2: Not all 12 edges exist exactly once
                -3: Flip error: One edge has to be flipped
                -4: Not all 8 corners exist exactly once
                -5: Twist error: One corner has to be twisted
                -6: Parity error: Two corners or two edges have to be exchanged
                -7 Wrong Notation: Singmaster notation is wrong (see SingmasterNotationMap.png)
        */

        if (verifySingmasterNotation(singmaster) != 0)
            throw new SingmasterError("Error 1 in Singmaster notation");


        byte[][] singmaster_map_corners = {{1,3,1}, {3,3,1}, {7,3,1}, {9,3,1}, {7,7,9}, {9,7,9},
                {1,7,9}, {3,7,9}};
        byte[][] singmaster_map_edges = {{2,2},{4,2},{6,2},{8,2},
                {4,6},{6,4},{6,4},{4,6},
                {8,8},{4,8},{6,8},{2,8}};

        for (int i = 0; i < 8; i++){
            //get the colors of the cubie at corner i (with given orientation)
            byte[] map = singmaster_map_corners[i];
            Character[] corner = new Character[3];
            byte ori = 0;
            String spot = corner_table.get(corner_perm((byte)i));
            for (byte j = 0 ; j < 3; j++){
                corner[j] = singmaster.charAt(faces.get(spot.charAt(j))*9 + map[j]-1);
                if ((corner[j] == 'U') || (corner[j] == 'D'))
                    ori = (byte)((3 - j) % 3);
            }
            rotateChars.rightRotate(corner, ori,3);
            StringBuilder str = new StringBuilder();
            for (int j = 0; j < 3; j++)
                str.append(corner[j]);

            try {
                byte perm = corner_table.getKey(str.toString());
                c[perm] = corner_val((byte)i,ori);
            }catch (NullPointerException e) {
                throw new SingmasterError("Error 7 in Singmaster Notation");
            }
        }


        for (int i = 0; i < 12; i++){
            //get the colors of the cubie at corner i (with given orientation)
            byte[] map = singmaster_map_edges[i];
            Character[] edges = new Character[2];
            byte ori = 0,perm;
            String spot = edge_table.get((byte)i);
            for (byte j = 0 ; j < 2; j++)
                edges[j] = singmaster.charAt(faces.get(spot.charAt(j))*9 + map[j]-1);

            StringBuilder str = new StringBuilder();
            str.append(edges[0]).append(edges[1]);


            try {
                perm = edge_table.getKey(str.toString());
            }catch (NullPointerException e){
                rotateChars.rightRotate(edges, 1,2);
                ori = 1;
                str = new StringBuilder();
                str.append(edges[0]).append(edges[1]);
                try {
                    perm = edge_table.getKey(str.toString());
                }catch (NullPointerException f){
                    throw new SingmasterError("Error 7 in Singmaster Notation");
                }
            }
            e[perm] = edge_val((byte)i,ori);
        }

        int ret = verifyCubeState();
        if (ret != 0)
            throw new SingmasterError("Error " + ret + " in Singmaster notation:");
        return this;
    }


    int verifySingmasterNotation(String singmaster){
        /*Returns -1 if there exist unknown characters in string, different from (URFDBL),
        * or if there aren't exactly 9 characters of each face*/
        int[] count = new int[6];
        char[] chars = singmaster.toCharArray();

        try {
            for (char ch : chars) {
                count[faces.get(ch)]++;
            }
        }catch (NullPointerException e){
            return -1;
        }
        for (int i = 0; i < 6; i++)
            if (count[i] != 9)
                return -1;
        return 0;
    }

    int verifyCubeState(){
        /* return codes
        0: Solvable cube
        -2: Not all 12 edges exist exactly once
        -3: Flip error: One edge has to be flipped
        -4: Not all 8 corners exist exactly once
        -5: Twist error: One corner has to be twisted
        -6: Parity error: Two corners ore two edges have to be exchanged
        */

        int sum_perm = 0,sum_ori=0;

        //Edge permutations and orientations
        for (int i = 0; i < 12; i++){
            sum_perm += edge_perm(e[i]);
            sum_ori += edge_ori(e[i]);
        }
        if (sum_perm != 66)
            return 2;
        if (sum_ori % 2 != 0)
            return 3;

        //Corner permutations and orientations
        sum_perm = 0; sum_ori = 0;
        for (int i = 0; i < 8; i++){
            sum_perm += corner_perm(c[i]);
            sum_ori += corner_ori(c[i]);
        }
        if (sum_perm != 28)
            return 4;
        if (sum_ori % 3 != 0)
            return 5;

        //check parity
        if ((edgeParity() ^ cornerParity()))
            return 6;
        return 0;
    }


    public boolean isSolved(){
        for (int i = 0; i < 12; i++)
            if (!(edge_perm(e[i]) == i && edge_ori(e[i]) == 0))
                return false;
        for (int i = 0; i < 8; i++)
            if (!(corner_perm(c[i]) == i && corner_ori(c[i]) == 0))
                return false;
        return true;
    }

    public static void checkMoveSequence(String sequence) throws InvalidMoveString{
        int len = sequence.length();
        if (len % 2 != 0)
        {
        	throw new InvalidMoveString("Illegal Move: each individual move has 2 characters, "
        			+ "first is a letter (U, F, R, B, L or D) and second is integer (1, 2 or 3).");
        }
        if (len == 0)
        {
        	throw new InvalidMoveString("Illegal Move: input move sequence is empty.");
        }
            
        for (int i = 0; i < len; i++){
            char ch = sequence.charAt(i);
            if (i % 2 == 0){
                //even: must be the face to move ('U','R','L','D','B' or 'F')
                if (ch != 'U' && ch != 'D' && ch != 'F' && ch != 'B' && ch != 'R' && ch != 'L')
                	throw new InvalidMoveString("Illegal Move: input letter (" + ch + ") is illegal. "
                			+ "Valid ones are U, F, R, B, L or D");
            }else{
                //odd: must be '1', '2' or '3'
                if (ch != '1' && ch != '2' && ch != '3')
                	throw new InvalidMoveString("Illegal Move: input integer (" + ch + ") is illegal. "
                			+ "Valid ones are 1, 2 or 3");
            }
        }
        return;
    }


}
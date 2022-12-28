package com.rubiks.simulator.processing;


import processing.core.PMatrix3D;

import java.util.HashMap;

public class UtilsSimulator {
    mainRubiksSimulator mysketch;

    HashMap<Character, Integer> color_dict;

    public UtilsSimulator(mainRubiksSimulator sketch){
        mysketch = sketch;
        color_dict= new HashMap<>();{{
            color_dict.put('B', mysketch.color(255,255,0));color_dict.put('D',mysketch.color(0,255,0));
            color_dict.put('F',mysketch.color(255,255,255));color_dict.put('R',mysketch.color(255,0,0));
            color_dict.put('L',mysketch.color(255,150,0));color_dict.put('U',mysketch.color(0,0,255));
            color_dict.put('0',mysketch.color(0,0,0)); //default value BLACK STICKER
        }}
    }


    public void UpdateSingmasterState(Cubie[] cube,String state){
        int index = 0;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    PMatrix3D matrix = new PMatrix3D();
                    matrix.translate(x, y, z);
                    cube[index] = new Cubie(matrix, x, y, z,mysketch);
                    index++;
                }
            }
        }


        String[] initial_state = new String[]{state.substring(0, 9), state.substring(9, 18),
                state.substring(18, 27), state.substring(27, 36),
                state.substring(36, 45), state.substring(45, 54),};

        String Up_face = initial_state[0];
        String Front_face = initial_state[1];
        String Right_face = initial_state[2];
        String Down_face = initial_state[3];
        String Back_face = initial_state[4];
        String Left_face = initial_state[5];


        char[] UP_CHARS = Up_face.toCharArray();
        cube[0].faces[3].c = color_dict.get(UP_CHARS[0]);
        cube[1].faces[3].c = color_dict.get(UP_CHARS[3]);
        cube[2].faces[3].c = color_dict.get(UP_CHARS[6]);
        cube[9].faces[3].c = color_dict.get(UP_CHARS[1]);
        cube[10].faces[3].c = color_dict.get(UP_CHARS[4]);
        cube[11].faces[3].c = color_dict.get(UP_CHARS[7]);
        cube[18].faces[3].c = color_dict.get(UP_CHARS[2]);
        cube[19].faces[3].c = color_dict.get(UP_CHARS[5]);
        cube[20].faces[3].c = color_dict.get(UP_CHARS[8]);

        char[] RIGHT_CHARS = Right_face.toCharArray();
        cube[18].faces[4].c = color_dict.get(RIGHT_CHARS[2]);
        cube[19].faces[4].c = color_dict.get(RIGHT_CHARS[1]);
        cube[20].faces[4].c = color_dict.get(RIGHT_CHARS[0]);
        cube[21].faces[4].c = color_dict.get(RIGHT_CHARS[5]);
        cube[22].faces[4].c = color_dict.get(RIGHT_CHARS[4]);
        cube[23].faces[4].c = color_dict.get(RIGHT_CHARS[3]);
        cube[24].faces[4].c = color_dict.get(RIGHT_CHARS[8]);
        cube[25].faces[4].c = color_dict.get(RIGHT_CHARS[7]);
        cube[26].faces[4].c = color_dict.get(RIGHT_CHARS[6]);

        char[] FRONT_CHARS = Front_face.toCharArray();
        cube[2].faces[1].c = color_dict.get(FRONT_CHARS[0]);
        cube[5].faces[1].c = color_dict.get(FRONT_CHARS[3]);
        cube[8].faces[1].c = color_dict.get(FRONT_CHARS[6]);
        cube[11].faces[1].c = color_dict.get(FRONT_CHARS[1]);
        cube[14].faces[1].c = color_dict.get(FRONT_CHARS[4]);
        cube[17].faces[1].c = color_dict.get(FRONT_CHARS[7]);
        cube[20].faces[1].c = color_dict.get(FRONT_CHARS[2]);
        cube[23].faces[1].c = color_dict.get(FRONT_CHARS[5]);
        cube[26].faces[1].c = color_dict.get(FRONT_CHARS[8]);

        char[] LEFT_CHARS = Left_face.toCharArray();
        cube[0].faces[5].c = color_dict.get(LEFT_CHARS[0]);
        cube[1].faces[5].c = color_dict.get(LEFT_CHARS[1]);
        cube[2].faces[5].c = color_dict.get(LEFT_CHARS[2]);
        cube[3].faces[5].c = color_dict.get(LEFT_CHARS[3]);
        cube[4].faces[5].c = color_dict.get(LEFT_CHARS[4]);
        cube[5].faces[5].c = color_dict.get(LEFT_CHARS[5]);
        cube[6].faces[5].c = color_dict.get(LEFT_CHARS[6]);
        cube[7].faces[5].c = color_dict.get(LEFT_CHARS[7]);
        cube[8].faces[5].c = color_dict.get(LEFT_CHARS[8]);

        char[] BACK_CHARS = Back_face.toCharArray();
        cube[0].faces[0].c = color_dict.get(BACK_CHARS[2]);
        cube[3].faces[0].c = color_dict.get(BACK_CHARS[5]);
        cube[6].faces[0].c = color_dict.get(BACK_CHARS[8]);
        cube[9].faces[0].c = color_dict.get(BACK_CHARS[1]);
        cube[12].faces[0].c = color_dict.get(BACK_CHARS[4]);
        cube[15].faces[0].c = color_dict.get(BACK_CHARS[7]);
        cube[18].faces[0].c = color_dict.get(BACK_CHARS[0]);
        cube[21].faces[0].c = color_dict.get(BACK_CHARS[3]);
        cube[24].faces[0].c = color_dict.get(BACK_CHARS[6]);

        char[] DOWN_CHARS = Down_face.toCharArray();
        cube[6].faces[2].c = color_dict.get(DOWN_CHARS[6]);
        cube[7].faces[2].c = color_dict.get(DOWN_CHARS[3]);
        cube[8].faces[2].c = color_dict.get(DOWN_CHARS[0]);
        cube[15].faces[2].c = color_dict.get(DOWN_CHARS[7]);
        cube[16].faces[2].c = color_dict.get(DOWN_CHARS[4]);
        cube[17].faces[2].c = color_dict.get(DOWN_CHARS[1]);
        cube[24].faces[2].c = color_dict.get(DOWN_CHARS[8]);
        cube[25].faces[2].c = color_dict.get(DOWN_CHARS[5]);
        cube[26].faces[2].c = color_dict.get(DOWN_CHARS[2]);

    }

}
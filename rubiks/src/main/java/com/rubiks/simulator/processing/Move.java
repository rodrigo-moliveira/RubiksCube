package com.rubiks.simulator.processing;

import java.util.HashMap;

import com.rubiks.simulator.processing.Move;

import processing.core.PApplet;

public class Move {
	
	@SuppressWarnings("serial")
    protected static final HashMap<String,Integer > moveDictionary = new HashMap<String,Integer>() {{
        put("D3",0);put("D1",1);put("U1",2);put("U3",3);put("R1",4);put("R3",5);
        put("L3",6);put("L1",7);put("F1",8);put("F3",9);put("B3",10);put("B1",11);

    }};
	
    private static mainRubiksSimulator mysketch = null;
    
    protected float angle = 0;
    protected int x = 0;
    protected int y = 0;
    protected int z = 0;
    protected int dir;
    protected boolean animating = false;
    protected boolean finished = false;

    protected Move(int x, int y, int z, int dir, mainRubiksSimulator sketch) {
        if (mysketch == null)
            mysketch = sketch;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dir = dir;
    }

    protected Move copy() {
        return new Move(x, y, z, dir,mysketch);
    }

    protected void reverse() {
        dir *= -1;
    }

    protected void start() {
        animating = true;
        finished = false;
        this.angle = 0;
    }

    protected boolean finished() {
        return finished;
    }

    protected void update() {
        if (animating) {
            angle += dir * mysketch.speed;
            if (PApplet.abs(angle) > PApplet.HALF_PI) {
                angle = 0;
                animating = false;
                finished = true;
                if (PApplet.abs(z) > 0) {
                    mysketch.turnZ(z, dir);
                } else if (PApplet.abs(x) > 0) {
                    mysketch.turnX(x, dir);
                } else if (PApplet.abs(y) > 0) {
                    mysketch.turnY(y, dir);
                }
            }
        }
    }
}
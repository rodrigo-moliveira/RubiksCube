package com.rubiks.simulator;

import processing.core.PConstants;
import processing.core.PMatrix3D;
import processing.core.PVector;

// class to store one of the Cubies
public class Cubie {
	
    private static mainRubiksSimulator mysketch = null;
    private PMatrix3D matrix;
    
    // internal coordinates
    protected int x = 0;
    protected int y = 0;
    protected int z = 0;
    protected int c;
    protected Face[] faces = new Face[6];

    protected Cubie(PMatrix3D m, int x, int y, int z,mainRubiksSimulator sketch) {
        if (mysketch == null)
            mysketch = sketch;

        this.matrix = m;
        this.x = x;
        this.y = y;
        this.z = z;

        faces[0] = new Face(new PVector(0, 0, -1),mysketch.color(0,0,0),mysketch);
        faces[1] = new Face(new PVector(0, 0, 1),mysketch.color(0,0,0),mysketch);
        faces[2] = new Face(new PVector(0, 1, 0),mysketch.color(0,0,0),mysketch);
        faces[3] = new Face(new PVector(0, -1, 0),mysketch.color(0,0,0),mysketch);
        faces[4] = new Face(new PVector(1, 0, 0),mysketch.color(0,0,0),mysketch);
        faces[5] = new Face(new PVector(-1, 0, 0),mysketch.color(0,0,0),mysketch);
    }

    protected void turnFacesZ(int dir) {
        for (Face f : faces) {
            f.turnZ(dir*PConstants.HALF_PI);
        }
    }

    protected void turnFacesY(int dir) {
        for (Face f : faces) {
            f.turnY(dir*PConstants.HALF_PI);
        }
    }

    protected void turnFacesX(int dir) {
        for (Face f : faces) {
            f.turnX(dir*PConstants.HALF_PI);
        }
    }

    protected void update(int x, int y, int z) {
        matrix.reset();
        matrix.translate(x, y, z);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    protected void show() {
        mysketch.noFill();
        mysketch.stroke(0);
        mysketch.strokeWeight(0.1f);
        mysketch.pushMatrix();
        mysketch.applyMatrix(matrix);
        mysketch.box(1);
        for (Face f : faces) {
            f.show();
        }
        mysketch.popMatrix();
    }
}
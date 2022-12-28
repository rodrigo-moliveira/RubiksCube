package com.rubiks.simulator.processing;

import processing.core.PMatrix3D;
import processing.core.PVector;

public class Cubie {
    static simulator mysketch = null;
    PMatrix3D matrix;
    int x = 0;
    int y = 0;
    int z = 0;
    int c;
    Face[] faces = new Face[6];

    Cubie(PMatrix3D m, int x, int y, int z,simulator sketch) {
        if (mysketch == null)
            mysketch = sketch;

        this.matrix = m;
        this.x = x;
        this.y = y;
        this.z = z;
        //c = color(255);

        faces[0] = new Face(new PVector(0, 0, -1),mysketch.color(0,0,0),mysketch);
        faces[1] = new Face(new PVector(0, 0, 1),mysketch.color(0,0,0),mysketch);
        faces[2] = new Face(new PVector(0, 1, 0),mysketch.color(0,0,0),mysketch);
        faces[3] = new Face(new PVector(0, -1, 0),mysketch.color(0,0,0),mysketch);
        faces[4] = new Face(new PVector(1, 0, 0),mysketch.color(0,0,0),mysketch);
        faces[5] = new Face(new PVector(-1, 0, 0),mysketch.color(0,0,0),mysketch);
    }

    public void turnFacesZ(int dir) {
        for (Face f : faces) {
            f.turnZ(dir*mysketch.HALF_PI);
        }
    }

    public void turnFacesY(int dir) {
        for (Face f : faces) {
            f.turnY(dir*mysketch.HALF_PI);
        }
    }

    public void turnFacesX(int dir) {
        for (Face f : faces) {
            f.turnX(dir*mysketch.HALF_PI);
        }
    }



    public void update(int x, int y, int z) {
        matrix.reset();
        matrix.translate(x, y, z);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void show() {
        //fill(c);
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
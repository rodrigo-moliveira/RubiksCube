package src.simulator;

import processing.core.PApplet;
import processing.core.PVector;

public class Face {
    static simulator mysketch=null;
    PVector normal;
    int c;

    Face(PVector normal, int c,simulator sketch) {
        if (mysketch == null)
            mysketch = sketch;
        this.normal = normal;
        this.c = c;
    }


    public void turnZ(float angle) {
        PVector v2 = new PVector();
        v2.x = PApplet.round(normal.x * PApplet.cos(angle) - normal.y * PApplet.sin(angle));
        v2.y = PApplet.round(normal.x * PApplet.sin(angle) + normal.y * PApplet.cos(angle));
        v2.z = PApplet.round(normal.z);
        normal = v2;
    }

    public void turnY(float angle) {
        PVector v2 = new PVector();
        v2.x = PApplet.round(normal.x * PApplet.cos(angle) - normal.z * PApplet.sin(angle));
        v2.z = PApplet.round(normal.x * PApplet.sin(angle) + normal.z * PApplet.cos(angle));
        v2.y = PApplet.round(normal.y);
        normal = v2;
    }

    public void turnX(float angle) {
        PVector v2 = new PVector();
        v2.y = PApplet.round(normal.y * PApplet.cos(angle) - normal.z * PApplet.sin(angle));
        v2.z = PApplet.round(normal.y * PApplet.sin(angle) + normal.z * PApplet.cos(angle));
        v2.x = PApplet.round(normal.x);
        normal = v2;
    }

    public void show() {
        mysketch.pushMatrix();
        mysketch.fill(c);
        mysketch.noStroke();
        mysketch.rectMode(mysketch.CENTER);
        mysketch.translate(0.5f*normal.x, 0.5f*normal.y, 0.5f*normal.z);
        if (PApplet.abs(normal.x) > 0) {
            mysketch.rotateY(mysketch.HALF_PI);
        } else if (PApplet.abs(normal.y) > 0) {
            mysketch.rotateX(mysketch.HALF_PI);
        }
        mysketch.square(0, 0, 1);
        mysketch.popMatrix();
    }
}
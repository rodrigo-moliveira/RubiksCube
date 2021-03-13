package src.simulator;

import processing.core.PApplet;

public class Move {
    static simulator mysketch = null;
    float angle = 0;
    int x = 0;
    int y = 0;
    int z = 0;
    int dir;
    boolean animating = false;
    boolean finished = false;

    Move(int x, int y, int z, int dir,simulator sketch) {
        if (mysketch == null)
            mysketch = sketch;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dir = dir;
    }

    public Move copy() {
        return new Move(x, y, z, dir,mysketch);
    }

    public void reverse() {
        dir *= -1;
    }

    public void start() {
        animating = true;
        finished = false;
        this.angle = 0;
    }

    public boolean finished() {
        return finished;
    }

    public void update() {
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
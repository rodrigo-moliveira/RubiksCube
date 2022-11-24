package com.rubiks.simulator;

import processing.awt.PSurfaceAWT;
import processing.core.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;


public class SingmasterSketch extends PApplet {

    PImage img;
    PFont f;
    JFrame frame;


    public static PImage getImage(InputStream url) throws Exception {
        BufferedImage image = ImageIO.read(url);
        PImage img=new PImage(image.getWidth(),image.getHeight(), PConstants.ARGB);
        image.getRGB(0, 0, img.width, img.height, img.pixels, 0, img.width);
        img.updatePixels();
        return img;

    }


    public void setup() {

        PSurface surface = this.getSurface();
        PSurfaceAWT.SmoothCanvas smoothCanvas = (PSurfaceAWT.SmoothCanvas)surface.getNative();
         frame = (JFrame) smoothCanvas.getFrame();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        imageMode(CENTER);

        InputStream path = getClass().getResourceAsStream("/src/SingmasterNotationMap.png");
        try {
            img = getImage(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        background(color(255,255,255));
        f = createFont("Rosario SemiBold",6,false); // Arial, 16 point, anti-aliasing on
        textFont(f,12);

    }

    public void exit() {
        setInvisible();
    }
    public void setInvisible(){
        frame.setVisible(false);
    }
    public void setVisible(){
        frame.setVisible(true);
    }


    public void draw() {
        image(img, (float)width/2, (float)height/2-70);
        fill(color(0,0,0));
        textAlign(CENTER);
        text("Singmaster Map - The names of the facelet positions of the cube\n"+
                       "(letters stand for Up, Left, Front, Right, Back, and Down) - 54 character string\n"+
                "A cube definition string 'UBL...' means that in position U1 we have the U-color, in position\n"+
                        "U2 we have the B-color, in position U3 we have the L color etc. according to the order:\n"+
                        "U1, U2, U3, U4, U5, U6, U7, U8, U9, R1, R2, R3, R4, R5, R6, R7, R8, R9, F1, F2, F3, F4,\n"+
                        "F5, F6, F7, F8, F9, D1, D2, D3, D4, D5, D6, D7, D8, D9, L1, L2, L3, L4, L5, L6, L7, L8,\n"+
                        "L9, B1, B2, B3, B4, B5, B6, B7, B8, B9.\n"+
                "So, for example, a definition of a solved cube would be\n"+
                "UUUUUUUUUFFFFFFFFFRRRRRRRRRDDDDDDDDDBBBBBBBBBLLLLLLLLL",(float)width/2,330);
        noLoop();
    }

    public void run(){
        String[] processingArgs = {"SingmasterSketch"};
        PApplet.runSketch(processingArgs, this);

    }

    public static void main(String []args){
        SingmasterSketch sim = new SingmasterSketch();
        sim.run();
    }

    public void settings() {
        size(700, 500);
    }

    public void keyPressed(){

    }


}
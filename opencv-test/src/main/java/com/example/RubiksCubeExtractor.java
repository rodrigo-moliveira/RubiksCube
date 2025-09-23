package com.example;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

import java.util.*;
import java.util.Arrays;

public class RubiksCubeExtractor {
    
    public static void main(String[] args) {
        // Automatically load OpenCV native libraries
        Loader.load(opencv_core.class);
        
        String imagePath = "C:\\Users\\rooo\\Documents\\other_projects\\rubiks\\RubiksCube\\opencv-test\\resources\\face.png";
        Mat image = imread(imagePath);

        if (image.empty()) {
            System.out.println("Error: Could not load image.");
            return;
        }

        List<Rect> facelets = detectFacelets(image);
        String[][] stateVector = extractColors(image, facelets);

        printStateVector(stateVector);
    }

    private static List<Rect> detectFacelets(Mat image) {
        Mat gray = new Mat(), blurred = new Mat(), edges = new Mat();
        opencv_imgproc.cvtColor(image, gray, opencv_imgproc.COLOR_BGR2GRAY);
        opencv_imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 0);
        opencv_imgproc.Canny(blurred, edges, 50, 150);

        //PointVectorVector contours = new PointVectorVector();
        MatVector contours = new MatVector();
        Mat hierarchy = new Mat();
        opencv_imgproc.findContours(edges, contours, hierarchy, opencv_imgproc.RETR_EXTERNAL, opencv_imgproc.CHAIN_APPROX_SIMPLE);
        
        List<Rect> squares = new ArrayList<>();
        for (long i = 0; i < contours.size(); i++) {
            Mat contour = contours.get(i);
            //PointVector approx = new PointVector();
            Mat approx = new Mat();
            opencv_imgproc.approxPolyDP(new Mat(contour), approx, 0.02 * opencv_imgproc.arcLength(contour, true), true);
            //opencv_imgproc.approxPolyDP(contour, approx, 0.02 * opencv_imgproc.arcLength(contour, true), true);
            
            if (approx.rows() == 4) { // Check if it's a quadrilateral
                Rect rect = opencv_imgproc.boundingRect(approx);
                squares.add(rect);
            }
        }

        // Sort squares to form a 3x3 grid
        squares.sort(Comparator.comparingInt(r -> r.y() * image.cols() + r.x()));
        return squares.size() >= 9 ? squares.subList(0, 9) : new ArrayList<>();
    }

    private static String[][] extractColors(Mat image, List<Rect> facelets) {
        String[][] stateVector = new String[3][3];
        String[] colors = {"Red", "Orange", "White", "Yellow", "Blue", "Green"};
        //Scalar[] referenceColors = {
        //    new Scalar(0, 0, 255), new Scalar(0, 165, 255), new Scalar(255, 255, 255),
        //    new Scalar(0, 255, 255), new Scalar(255, 0, 0), new Scalar(0, 255, 0)
        //};
        
        Scalar[] referenceColors = {
        	    new Scalar(0.0, 0.0, 255.0, 0.0),   // Red
        	    new Scalar(0.0, 165.0, 255.0, 0.0), // Orange
        	    new Scalar(255.0, 255.0, 255.0, 0.0), // White
        	    new Scalar(0.0, 255.0, 255.0, 0.0), // Yellow
        	    new Scalar(255.0, 0.0, 0.0, 0.0), // Blue
        	    new Scalar(0.0, 255.0, 0.0, 0.0)  // Green
        	};

        for (int i = 0; i < facelets.size(); i++) {
            Rect rect = facelets.get(i);
            Mat roi = new Mat(image, rect);
            Scalar avgColor = opencv_core.mean(roi);
            stateVector[i / 3][i % 3] = mapColor(avgColor, referenceColors, colors);
        }
        return stateVector;
    }

    private static String mapColor(Scalar color, Scalar[] referenceColors, String[] colors) {
        double minDist = Double.MAX_VALUE;
        String bestMatch = "Unknown";
        for (int i = 0; i < referenceColors.length; i++) {
            double dist = Math.sqrt(Math.pow(color.get(0) - referenceColors[i].get(0), 2) +
                                    Math.pow(color.get(1) - referenceColors[i].get(1), 2) +
                                    Math.pow(color.get(2) - referenceColors[i].get(2), 2));
            if (dist < minDist) {
                minDist = dist;
                bestMatch = colors[i];
            }
        }
        return bestMatch;
    }

    private static void printStateVector(String[][] stateVector) {
        for (String[] row : stateVector) {
            System.out.println(Arrays.toString(row));
        }
    }
}

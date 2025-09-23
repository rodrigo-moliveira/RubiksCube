package com.example;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.*;
import java.util.*;
import java.util.Arrays;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_highgui.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class Test2 {
    
    public static void main(String[] args) {
        Loader.load(opencv_core.class);
        
        String imagePath = "C:\\Users\\rooo\\Documents\\other_projects\\rubiks\\RubiksCube\\opencv-test\\resources\\face.png";
        Mat image = imread(imagePath);

        if (image.empty()) {
            System.out.println("Error: Could not load image.");
            return;
        }

        List<Rect> facelets = detectFacelets(image);
        markDetectedFacelets(image, facelets);
        String[][] stateVector = extractColors(image, facelets);
        printStateVector(stateVector);
    }

    private static List<Rect> detectFacelets(Mat image) {
        Mat gray = new Mat(), blurred = new Mat(), edges = new Mat();
        cvtColor(image, gray, COLOR_BGR2GRAY);
        GaussianBlur(gray, blurred, new Size(5, 5), 0);
        Canny(blurred, edges, 50, 150);

        MatVector contours = new MatVector();
        Mat hierarchy = new Mat();
        findContours(edges, contours, hierarchy, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);
        
        List<Rect> squares = new ArrayList<>();
        for (long i = 0; i < contours.size(); i++) {
            Mat contour = contours.get(i);
            Mat approx = new Mat();
            approxPolyDP(contour, approx, 0.02 * arcLength(contour, true), true);
            
            if (approx.rows() == 4) {
                Rect rect = boundingRect(approx);
                squares.add(rect);
                System.out.println("Detected facelet at: " + rect);
            }
        }

        squares.sort(Comparator.comparingInt(r -> r.y() * image.cols() + r.x()));
        return squares.size() >= 9 ? squares.subList(0, 9) : new ArrayList<>();
    }

    private static void markDetectedFacelets(Mat image, List<Rect> facelets) {
        for (Rect rect : facelets) {
            Point center = new Point(rect.x() + rect.width() / 2, rect.y() + rect.height() / 2);
            circle(image, center, 5, new Scalar(0, 255, 0, 0));
        }
        imshow("Detected Facelets", image);
        waitKey(0);
    }

    private static String[][] extractColors(Mat image, List<Rect> facelets) {
        String[][] stateVector = new String[3][3];
        String[] colors = {"Red", "Orange", "White", "Yellow", "Blue", "Green"};
        Scalar[] referenceColors = {
            new Scalar(0.0, 0.0, 255.0, 0.0),
            new Scalar(0.0, 165.0, 255.0, 0.0),
            new Scalar(255.0, 255.0, 255.0, 0.0),
            new Scalar(0.0, 255.0, 255.0, 0.0),
            new Scalar(255.0, 0.0, 0.0, 0.0),
            new Scalar(0.0, 255.0, 0.0, 0.0)
        };
        
        System.out.println("here");
        for (int i = 0; i < facelets.size(); i++) {
            Rect rect = facelets.get(i);
            Mat roi = new Mat(image, rect);
            Scalar avgColor = opencv_core.mean(roi);
            System.out.println("Facelet " + (i + 1) + " RGB: " + avgColor);
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

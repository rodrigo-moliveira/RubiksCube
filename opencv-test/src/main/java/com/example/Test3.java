package com.example;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_highgui.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;
import static org.bytedeco.opencv.global.opencv_imgproc.*;
import static org.bytedeco.opencv.global.opencv_highgui.*;

import java.util.*;
import java.util.Arrays;

public class Test3 {
    
    public static void main(String[] args) {
        Loader.load(opencv_core.class);
        
        String imagePath = "C:\\Users\\rooo\\Documents\\other_projects\\rubiks\\RubiksCube\\opencv-test\\resources\\face.png";
        Mat image = imread(imagePath);

        if (image.empty()) {
            System.out.println("Error: Could not load image.");
            return;
        }

        List<Rect> facelets = detectFacelets(image);
        System.out.println(facelets.size());
        drawDebugFacelets(image, facelets);
    }

    private static List<Rect> detectFacelets(Mat image) {
        Mat hsv = new Mat(), mask = new Mat(), blurred = new Mat(), thresh = new Mat();
        cvtColor(image, hsv, COLOR_BGR2HSV);
        GaussianBlur(hsv, blurred, new Size(5, 5), 0);

        // Adaptive thresholding instead of Canny
        cvtColor(blurred, mask, COLOR_BGR2GRAY);
        adaptiveThreshold(mask, thresh, 255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 11, 2);
        
        // Morphological operations to refine edges
        Mat kernel = getStructuringElement(MORPH_RECT, new Size(3, 3));
        morphologyEx(thresh, thresh, MORPH_CLOSE, kernel);

        MatVector contours = new MatVector();
        Mat hierarchy = new Mat();
        findContours(thresh, contours, hierarchy, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);
        
        List<Rect> squares = new ArrayList<>();
        for (long i = 0; i < contours.size(); i++) {
            Mat contour = contours.get(i);
            Mat approx = new Mat();
            approxPolyDP(contour, approx, 0.02 * arcLength(contour, true), true);
            
            if (approx.rows() == 4) { // Check if it's a quadrilateral
                Rect rect = boundingRect(approx);
                squares.add(rect);
            }
        }

        squares.sort(Comparator.comparingInt(r -> r.y() * image.cols() + r.x()));
        return squares.size() >= 9 ? squares.subList(0, 9) : new ArrayList<>();
    }

    private static void drawDebugFacelets(Mat image, List<Rect> facelets) {
        for (Rect rect : facelets) {
            Point center = new Point(rect.x() + rect.width() / 2, rect.y() + rect.height() / 2);
            circle(image, center, 5, new Scalar(0, 255, 0, 0)); // Green dots on centers
            System.out.println("Facelet center: (" + center.x() + ", " + center.y() + ")");
            Mat roi = new Mat(image, rect);
            Scalar avgColor = opencv_core.mean(roi);
            System.out.println("RGB: " + avgColor.get(2) + ", " + avgColor.get(1) + ", " + avgColor.get(0));
        }
        imshow("Detected Facelets", image);
        waitKey(0);
    }
    
    private static void printStateVector(String[][] stateVector) {
        for (String[] row : stateVector) {
            System.out.println(Arrays.toString(row));
        }
    }
}

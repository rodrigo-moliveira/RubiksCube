package com.example;

import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.javacpp.Loader;

public class OpenCVTest {
    public static void main(String[] args) {
        // Automatically load native OpenCV library from Maven repo
        Loader.load(opencv_core.class);

        // Print OpenCV version
        System.out.println("OpenCV version: " + opencv_core.CV_VERSION);

        // Create a black image
        Mat img = new Mat(300, 300, opencv_core.CV_8UC3);
        
        // Draw a white rectangle
        opencv_imgproc.rectangle(img,
                new Point(50, 50),
                new Point(250, 250),
                new Scalar(255, 255, 255, 0),
                3, 8, 0);

        // Save the image
        opencv_imgcodecs.imwrite("opencv_test_output.png", img);
        System.out.println("Test image saved as 'opencv_test_output.png'");
    }
}

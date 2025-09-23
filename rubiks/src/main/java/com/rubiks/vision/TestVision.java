import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

public class TestVision {
    public static void main(String[] args) {
        // Load OpenCV Library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        // Print OpenCV version
        System.out.println("OpenCV version: " + Core.VERSION);

        // Create a simple black image (300x300 pixels)
        Mat img = Mat.zeros(300, 300, CvType.CV_8UC3);
        
        // Draw a white rectangle
        org.opencv.imgproc.Imgproc.rectangle(img, 
            new org.opencv.core.Point(50, 50), 
            new org.opencv.core.Point(250, 250), 
            new Scalar(255, 255, 255), 3);
        
        // Save the image to check if OpenCV works
        Imgcodecs.imwrite("opencv_test_output.png", img);
        System.out.println("Test image saved as 'opencv_test_output.png'");

        // Show the image (requires HighGUI support)
        HighGui.imshow("OpenCV Test Image", img);
        HighGui.waitKey(0);
    }
}

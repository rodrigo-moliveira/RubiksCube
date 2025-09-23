/*import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RubiksCubeColorReader {

    // Predefined Rubik's cube colors (approximate HSV values)
    private static final Scalar BLUE = new Scalar(120, 255, 255);
    private static final Scalar GREEN = new Scalar(60, 255, 255);
    private static final Scalar RED = new Scalar(0, 255, 255);
    private static final Scalar ORANGE = new Scalar(15, 255, 255);
    private static final Scalar WHITE = new Scalar(0, 0, 255);
    private static final Scalar YELLOW = new Scalar(30, 255, 255);

    private static final List<Scalar> RUBIKS_COLORS = Arrays.asList(BLUE, GREEN, RED, ORANGE, WHITE, YELLOW);
    private static final List<String> COLOR_NAMES = Arrays.asList("Blue", "Green", "Red", "Orange", "White", "Yellow");

    public static void main(String[] args) {
        // Load OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Load the image
        String imagePath = "path/to/face.png"; // Replace with your image path
        Mat image = Imgcodecs.imread(imagePath);
        if (image.empty()) {
            System.out.println("Image not found!");
            return;
        }

        // Detect and crop the Rubik's cube face
        Mat croppedFace = detectRubiksFace(image);

        if (croppedFace == null) {
            System.out.println("Failed to detect Rubik's cube face!");
            return;
        }

        // Process the cropped face and get colors
        List<String> colors = processRubiksFace(croppedFace);

        // Output the result
        System.out.println("Detected Colors: " + colors);
    }

    private static Mat detectRubiksFace(Mat image) {
        // Convert to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Apply GaussianBlur to reduce noise
        Imgproc.GaussianBlur(gray, gray, new Size(5, 5), 0);

        // Detect edges using Canny
        Mat edges = new Mat();
        Imgproc.Canny(gray, edges, 50, 150);

        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find the largest square contour
        MatOfPoint2f approxCurve = new MatOfPoint2f();
        Mat largestSquare = null;

        for (MatOfPoint contour : contours) {
            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
            double peri = Imgproc.arcLength(contour2f, true);
            Imgproc.approxPolyDP(contour2f, approxCurve, 0.02 * peri, true);

            if (approxCurve.total() == 4 && Imgproc.isContourConvex(new MatOfPoint(approxCurve.toArray()))) {
                Rect boundingRect = Imgproc.boundingRect(contour);
                if (largestSquare == null || boundingRect.area() > largestSquare.size().area()) {
                    largestSquare = new Mat(image, boundingRect);
                }
            }
        }

        return largestSquare;
    }

    private static List<String> processRubiksFace(Mat image) {
        int rows = 3, cols = 3; // Rubik's face is a 3x3 grid
        int width = image.width() / cols;
        int height = image.height() / rows;

        List<String> detectedColors = new ArrayList<>();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                // Extract region (cubelet)
                Rect rect = new Rect(col * width, row * height, width, height);
                Mat cubelet = new Mat(image, rect);

                // Compute average color of the region
                Scalar averageColor = computeAverageColor(cubelet);

                // Find the closest predefined Rubik's color
                String colorName = findClosestColor(averageColor);
                detectedColors.add(colorName);
            }
        }
        return detectedColors;
    }

    private static Scalar computeAverageColor(Mat region) {
        Mat hsv = new Mat();
        Imgproc.cvtColor(region, hsv, Imgproc.COLOR_BGR2HSV); // Convert to HSV for robustness
        Scalar mean = Core.mean(hsv); // Compute mean color
        return mean;
    }

    private static String findClosestColor(Scalar color) {
        double minDistance = Double.MAX_VALUE;
        String closestColor = "Unknown";

        for (int i = 0; i < RUBIKS_COLORS.size(); i++) {
            double distance = colorDistance(color, RUBIKS_COLORS.get(i));
            if (distance < minDistance) {
                minDistance = distance;
                closestColor = COLOR_NAMES.get(i);
            }
        }
        return closestColor;
    }

    private static double colorDistance(Scalar color1, Scalar color2) {
        // Compute Euclidean distance between two colors
        double dH = color1.val[0] - color2.val[0];
        double dS = color1.val[1] - color2.val[1];
        double dV = color1.val[2] - color2.val[2];
        return Math.sqrt(dH * dH + dS * dS + dV * dV);
    }
}
*/
//package src.RubiksVision;
//
///*TODO:
//*   Falta converter isto para HSV -> que burrso*/
//
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.Slider;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.input.MouseEvent;
//import org.opencv.core.Mat;
//import org.opencv.core.Core;
//import org.opencv.core.Scalar;
//import org.opencv.imgcodecs.Imgcodecs;
//import src.RubiksVision.utils.Utils;
//
//import java.io.InputStream;
//import java.util.Arrays;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//public class Controller {
//
//    @FXML
//    private ImageView inputCubeView;
//    @FXML
//    private ImageView outputCubeView;
//    @FXML
//    private Button buttFilter;
//    @FXML
//    private Label hueValues;
//    @FXML
//    private Slider hueStart;
//    @FXML
//    private Slider hueStop;
//    private final Mat image = Imgcodecs.imread("./RubiksCube/src/RubiksVision/data/sample-solved.jpg");;
//
//    private boolean filtering;
//
//
//
//    /**
//     * Initialize method, automatically called by @{link FXMLLoader}
//     */
//    public void initialize(){
//        Image input = Utils.mat2Image(this.image);
//        updateImageView(inputCubeView, input);
//        filtering = false;
////        updateSlider();
//    }
//
//
//
//
//    @FXML
//    protected void startFiltering(){
//        if (!filtering) {
//            Mat filtered = image.clone();
//            Mat filteredShow = new Mat();
//
//            filtering = true;
//            buttFilter.setText("Stop Filtering");
//
//
//            Core.inRange(image, new Scalar((int)hueStart.getValue(), 0, 0),
//                    new Scalar((int)hueStop.getValue(), 255, 255), filtered);
//            Core.bitwise_and(image, image, filteredShow, filtered);
//            Image toShow = Utils.mat2Image(filteredShow);
//            updateImageView(outputCubeView, toShow);
//
//
//        }else{
//            filtering = false;
//            buttFilter.setText("Start Filtering");
//            outputCubeView.setImage(null);
//
//        }
//    }
//
//    @FXML
//    protected void updateSlider(){
//        hueValues.setText("Values: [" + (int)hueStart.getValue() + " " + (int)hueStop.getValue()+"]");
//    }
//
//    @FXML
//    protected void mouseClicked(MouseEvent e) {
//        int rows = this.image.rows(); //Calculates number of rows
//        int cols = this.image.cols(); //Calculates number of columns
//        double AR = (double)cols / (double)rows;
//        double width = this.inputCubeView.getFitWidth();
//
//        double[] data = this.image.get((int) (e.getY()*rows/width*AR), (int)(e.getX()*cols/width));
//        System.out.println(Arrays.toString(data));
//    }
//
//    /**
//     * Update the {@link ImageView} in the JavaFX main thread
//     *
//     * @param view
//     *            the {@link ImageView} to update
//     * @param image
//     *            the {@link Image} to show
//     */
//    private void updateImageView(ImageView view, Image image)
//    {
//        Utils.onFXThread(view.imageProperty(), image);
//    }
//
//
//
//    protected void setClosed(){
//    }
//
//}
//
//

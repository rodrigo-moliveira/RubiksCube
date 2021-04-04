package src.RubiksVision;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import src.RubiksVision.utils.Utils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller {

    @FXML
    private ImageView cubeIm;
    private Mat image;

    // a timer for acquiring the video stream
    private ScheduledExecutorService timer;


    /**
     * Initialize method, automatically called by @{link FXMLLoader}
     */
    public void initialize()
    {
        this.image = Imgcodecs.imread("./RubiksCube/src/RubiksVision/data/sample-solved.jpg");
    }


    @FXML
    protected void mouseClicked(MouseEvent e) {
        int rows = this.image.rows(); //Calculates number of rows
        int cols = this.image.cols(); //Calculates number of columns
        int ch = this.image.channels(); //Calculates number of channels (Grayscale: 1, RGB: 3, etc.)
        double AR = (double)cols / (double)rows;
        double width = this.cubeIm.getFitWidth();

        double[] data = this.image.get((int) (e.getY()*rows/width*AR), (int)(e.getX()*cols/width));
        System.out.println(Arrays.toString(data));




    }


    /**
     * Update the {@link ImageView} in the JavaFX main thread
     *
     * @param view
     *            the {@link ImageView} to update
     * @param image
     *            the {@link Image} to show
     */
    private void updateImageView(ImageView view, Image image)
    {
        Utils.onFXThread(view.imageProperty(), image);
    }



    protected void setClosed(){}

}

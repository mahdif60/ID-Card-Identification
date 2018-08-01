package my.rafftech;


import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;



public class ImageProcessing {

		
	//-----------------  Resize Images to prepare  for OCR --------
	public void imProcess(Mat src){
		
		Mat resImage = new Mat();
		Size sz = new Size(2*src.width(),2*src.height());
		Imgproc.resize( src, resImage, sz );
		 
		//------------------    Denoising   ------------------------
	
		Mat denoiseImage = new Mat(resImage.rows(),resImage.cols(),resImage.type());
		denoiseImage = resImage;
		Photo.fastNlMeansDenoisingColored(resImage,denoiseImage, 3, 3, 5, 21);
		//Imgcodecs.imwrite("res/1D.jpg",denoiseImage);    // Writing the image

		//------------------     Crop        ----------------------

		Rect rectCrop = new Rect(1, 220, 375, 50);
		Mat cropImage = new Mat (denoiseImage,rectCrop);    		

		//------------------    Sharpening   -----------------------

		// Creating an empty matrix to store the result
		Mat sharpImage = new Mat();

		// Applying GaussianBlur on the Image
		Imgproc.GaussianBlur(cropImage, sharpImage, new Size(35, 35), 0);
		Core.addWeighted(cropImage, 1.5, sharpImage, -.5, 0, sharpImage);	

		//Imgcodecs.imwrite("res/1s.jpg",cropImage);    // Writing the image


		//------------------- convert to gray -------------------

		Mat grayImage = new Mat();
		grayImage = sharpImage;
		Imgproc.cvtColor(sharpImage, grayImage, Imgproc.COLOR_RGB2GRAY);
		

		//--------------- Convert Gray Image to Binary Image ------------

		Mat dst = new Mat();
		Imgproc.threshold(grayImage, dst, 120, 255, Imgproc.THRESH_BINARY);
		Imgcodecs.imwrite("data/b.jpg",dst);   // Writing the image

	}


}

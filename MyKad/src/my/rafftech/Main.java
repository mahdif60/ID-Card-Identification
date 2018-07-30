package my.rafftech;

import java.io.File;
import java.io.IOException;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.photo.Photo;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class Main {
	

	public static void main(String[] args) throws IOException {
		

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);	  //Loading the core library 

		Mat src = new Mat();
		src=MyUtil.getImagefromfile();
		try {

			
			//-----------------  Resize Images to prepare  for OCR --------

			//Mat img =Imgcodecs.imread("E:\\Java\\My Programs\\MyKad\\res\\1r.jpg",Imgcodecs.CV_LOAD_IMAGE_COLOR);			
			Mat resImage = new Mat();
			Size sz = new Size(2*src.width(),2*src.height());
			Imgproc.resize( src, resImage, sz );
			 

			//------------------    Denoising   ------------------------


			//Mat resImage =Imgcodecs.imread("res/1r.jpg",Imgcodecs.CV_LOAD_IMAGE_COLOR);	
			Mat denoiseImage = new Mat(resImage.rows(),resImage.cols(),resImage.type());
			denoiseImage = resImage;
			Photo.fastNlMeansDenoisingColored(resImage,denoiseImage, 3, 3, 5, 21);
			//Imgcodecs.imwrite("res/1D.jpg",denoiseImage);    // Writing the image
/*
			// -----------------    Delation     -----------------------

			Mat delationImage = new Mat();     							 					// Creating an empty matrix to store the result
			Mat kernel1 = Imgproc.getStructuringElement
					(Imgproc.MORPH_RECT, new  Size((2*2) + 1, (2*2)+1));				    // Preparing the kernel matrix object		
			Imgproc.dilate(denoiseImage, delationImage, kernel1);        					// Applying dilate on the Image
			Imgcodecs.imwrite("res/1D.jpg",delationImage);    // Writing the image
*/
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

			//----------------  Apply OCR  to the cropped image  --------------
			
			ITesseract instance = new Tesseract();  // JNA Interface Mapping
			instance.setDatapath("data/tessdata");
			File imageFile = new File("data/b.jpg");
			String result = instance.doOCR(imageFile);


			// --------------------------------  Face Detection -----------------------

			String xmlClassifier = "classifier/lbpcascade_frontalface.xml";  // Instantiating the CascadeClassifier for Face Detection
			CascadeClassifier classifier = new CascadeClassifier(xmlClassifier);

			// Detecting the face in the snap
			MatOfRect faceDetections = new MatOfRect();
			classifier.detectMultiScale(src, faceDetections);
			int i=faceDetections.toArray().length;


			System.out.println("****** Identification Result *******");
			System.out.println(String.format("Number of Detected faces : %s",i));

			if (i!=2) {
				System.out.println("Candidat Name : " +result);
				System.out.println("This is a Fake IdCard");
				System.out.println("*********************************");
			}else {
				System.out.println("Candidat Name : " + result);
				System.out.println("This is an Original IdCard");
				System.out.println("*********************************");
			}



		} catch (TesseractException e) {
			System.err.println(e.getMessage());
		}
	}

}


package my.rafftech;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class Main {
	

	public static void main(String[] args) throws IOException {
		

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);	  //Loading the core library 

		Mat src = new Mat();
		MyUtil myUtil=new MyUtil();
		src=myUtil.getImagefromfile();
		
		try {

			ImageProcessing imageProcess=new ImageProcessing();
			imageProcess.imProcess(src);
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
			
			// Drawing boxes
		      for (Rect rect : faceDetections.toArray()) {
		         Imgproc.rectangle(
		            src,                                               // where to draw the box
		            new Point(rect.x, rect.y),                            // bottom left
		            new Point(rect.x + rect.width, rect.y + rect.height), // top right
		            new Scalar(0, 0, 255),
		            3                                                     // RGB color
		         );
		      }
		      Imgcodecs.imwrite("res/fd.jpg", src);
		      File f1=new File("res/fd.jpg");
		      BufferedImage image2 = ImageIO.read(f1); 
		      myUtil.displayImage(image2);
		      
		      
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


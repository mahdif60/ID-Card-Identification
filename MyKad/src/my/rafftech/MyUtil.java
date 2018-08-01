package my.rafftech;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class MyUtil {
	public void displayImage(Image img){   
		ImageIcon icon = new ImageIcon(img);
		JFrame frame = new JFrame();
		frame.setLayout(new FlowLayout());        
		frame.setSize(img.getWidth(null)+50, img.getHeight(null)+50);     
		JLabel lbl = new JLabel();
		lbl.setIcon(icon);
		frame.add(lbl);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	String current = null;
	
	public Mat getImagefromfile() throws IOException{
	try {

		current = new java.io.File( "." ).getCanonicalPath();
	} catch (IOException e1) {
		e1.printStackTrace();
	}

	final JFileChooser fc = new JFileChooser();
	fc.setDialogTitle ("Choose Image..");
	String s=new String(current + "\\res");

	fc.setCurrentDirectory(new File(s));
	//In response to a button click:
	int returnVal = fc.showOpenDialog(fc);
	FileNameExtensionFilter filter = 
			new FileNameExtensionFilter("JPG Images", "jpg");
	fc.setFileFilter(filter);

	File file1 = null;
	if (returnVal == JFileChooser.APPROVE_OPTION)
		file1 = fc.getSelectedFile();

	BufferedImage image = ImageIO.read(file1); 
	displayImage(image);
	byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	Mat src = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
	src.put(0, 0, data);
	return src;
	}
}

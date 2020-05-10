import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Webcam-based drawing 
 *
 * 
 * @author Isuru Abeysekara)
 */
public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor=null;         // color of regions of interest (set by mouse press)
	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();
		clearPainting();
	}

	public Point track() {
		int cx = 0, cy = 0; // coordinates with best matching color
		int closest = 10000; // start with a too-high number so that everything will be smaller
		// Nested loop over every pixel
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				// Euclidean distance squared between colors
				Color c = new Color(image.getRGB(x,y));
				int d = (c.getRed() - targetColor.getRed()) * (c.getRed() - targetColor.getRed())
						+ (c.getGreen() - targetColor.getGreen()) * (c.getGreen() - targetColor.getGreen())
						+ (c.getBlue() - targetColor.getBlue()) * (c.getBlue() - targetColor.getBlue());
				if (d < closest) {
					closest = d;
					cx = x; cy = y;
				}
			}
		}
		return new Point(cx,cy);
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting, 
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) {
		if (displayMode=='w'){
			g.drawImage(image, 0, 0, null);
		}
		else if (displayMode=='r'){
			g.drawImage(finder.getRecoloredImage(), 0, 0, null);
		}

		else if (displayMode=='p') {
			g.drawImage(painting, 0, 0, null);
		}

	}

	/**
	 * Webcam method, here finding regions and updating the painting.
	 */
	@Override
	public void processImage() {
		if (targetColor != null){
			// Feeding in image to process and to obtain the largest region //
			finder.setImage(image);
			finder.findRegions(targetColor);
			finder.recolorImage();
			ArrayList<Point> lregion = finder.largestRegion();
			for (int i=0; i<lregion.size(); i++) {
				Point p = lregion.get(i);
				p = lregion.get(i);
				for (int y = 0; y < image.getHeight(); y++) {
					for (int x = 0; x < image.getWidth(); x++) {
						if (p.x == x && p.y == y) {
							painting.setRGB(x, y, paintColor.getRGB());
						}
					}
				}
			}
		}
	}

	/**
	 * Overrides the DrawingGUI method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		if (image != null) {
			targetColor = new Color(image.getRGB(x, y));
			System.out.println("tracking " + targetColor);
		}

	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			saveImage(painting, "pictures/painting.png", "png");
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}


	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}


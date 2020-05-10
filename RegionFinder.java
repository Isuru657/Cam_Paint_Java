import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.util.Random;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 *
 * 
 * @author Isuru Abeysekara
 *
 */
public class RegionFinder {
	private static final int maxColorDiff = 20;				// how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50; 				// how many points in a region to be worth considering
	private static final int radius=1;                      // the radius from the center of box
	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored
	private ArrayList<ArrayList<Point>> regions;			// a region is a list of points
															// so the identified regions are in a list of lists of points

	// Initializes the image used to find regions //
	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;		
	}
	// Get and set methods for the image to be processed //
	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}
	// Get method for the recolored image //
	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) {
		// Array List that contain a list of regions declared //
		regions= new ArrayList<ArrayList<Point>>();
		// Picture containing all visited points //
		BufferedImage visited= new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int y=0; y <image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				// Retrieves color at point and checks if the algorithm has visited it. If not visited, added to a list of points to visit//
				Color cmatch = new Color(image.getRGB(x, y));
				if (colorMatch(cmatch, targetColor)== true && visited.getRGB(x, y) == 0){
						ArrayList<Point> region = new ArrayList<Point>();
						ArrayList<Point> tovisit= new ArrayList<Point>();
						Point p = new Point(x, y);
						tovisit.add(p);
						while(tovisit.size()>0){
							/* Point removed from list to be visited and added to region list */
							Point p1= tovisit.remove(0);
							if(visited.getRGB(p1.x, p1.y)==0) {
								visited.setRGB(p1.x, p1.y, 1);
								region.add(p1);
								// Checks for neighbors and adds it to to visit list //
								for (int ny = Math.max(0, p1.y - radius); ny < Math.min(image.getHeight(), p1.y + 1 + radius); ny++) {
									for (int nx = Math.max(0, p1.x - radius);
										 nx < Math.min(image.getWidth(), p1.x + 1 + radius);
										 nx++) {
										Color cnmatch = new Color(image.getRGB(nx, ny));
										if (colorMatch(cnmatch, targetColor) == true) {
											Point np = new Point(nx, ny);
											tovisit.add(np);
										}
									}
								}
							}
					}
					// If the size of the region is greater than 50, it is added to array list contain all regions //
					if (region.size()>minRegion){
						regions.add(region);
					}
				}

			}

		}
	}

	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary).
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		int rdiff= Math.abs(c1.getRed()-c2.getRed());
		int gdiff= Math.abs(c1.getGreen()- c2.getGreen());
		int bdiff= Math.abs(c1.getBlue()- c2.getBlue());
		if (rdiff<= maxColorDiff && gdiff<=maxColorDiff && bdiff<=maxColorDiff){
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		int count=0;
		ArrayList<Point> rlist=null;
		for (ArrayList<Point> region:regions) {
			if(region.size()>count){
				count=region.size();
				rlist=region;
			}
		}
		return rlist;
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		System.out.println(regions.size());
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		for (ArrayList<Point> region:regions) {
			Random random= new Random();
			Color color = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
			for (Point p: region) {
				recoloredImage.setRGB(p.x, p.y, color.getRGB());
			}
		}
	}
}

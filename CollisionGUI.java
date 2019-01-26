/**
 * CollisionGUI class
 * 
 * @author Aadil Islam, Spring 2018
 */

import java.awt.*;

import javax.swing.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Using a quadtree for collision detection
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, updated for blobs
 * @author CBK, Fall 2016, using generic PointQuadtree
 */
public class CollisionGUI extends DrawingGUI {
	private static final int width=800, height=600;		// size of the universe

	private List<Blob> blobs;						// all the blobs
	private List<Blob> colliders;					// the blobs who collided at this step
	private char blobType = 'b';						// what type of blob to create
	private char collisionHandler = 'c';				// when there's a collision, 'c'olor them, or 'd'estroy them
	private int delay = 100;							// timer control

	public CollisionGUI() {
		super("super-collider", width, height);

		blobs = new ArrayList<Blob>();
		colliders = new ArrayList<Blob>();

		// Timer drives the animation.
		startTimer();
	}

	/**
	 * Adds an blob of the current blobType at the location
	 */
	private void add(int x, int y) {
		if (blobType=='b') {
			blobs.add(new Bouncer(x,y,width,height));
		}
		else if (blobType=='w') {
			blobs.add(new Wanderer(x,y));
		}
		else {
			System.err.println("Unknown blob type "+blobType);
		}
	}

	/**
	 * DrawingGUI method, here creating a new blob
	 */
	public void handleMousePress(int x, int y) {
		add(x,y);
		repaint();
	}

	/**
	 * DrawingGUI method
	 */
	public void handleKeyPress(char k) {
		if (k == 'f') { // faster
			if (delay>1) delay /= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 's') { // slower
			delay *= 2;
			setTimerDelay(delay);
			System.out.println("delay:"+delay);
		}
		else if (k == 'r') { // add some new blobs at random positions
			for (int i=0; i<10; i++) {
				add((int)(width*Math.random()), (int)(height*Math.random()));
				repaint();
			}			
		}
		else if (k == 'c' || k == 'd') { // control how collisions are handled
			collisionHandler = k;
			System.out.println("collision:"+k);
		}
		else { // set the type for new blobs
			blobType = k;			
		}
	}

	/**
	 * DrawingGUI method, here drawing all the blobs and then re-drawing the colliders in red
	 */
	public void draw(Graphics g) {
		// TODO: YOUR CODE HERE
		// Ask all the blobs to draw themselves.
		// Ask the colliders to draw themselves in red.
		
		// if colliders is null, new it to an ArrayList
		if(colliders == null)
			colliders = new ArrayList<Blob>();
		
		// draw all blob objects on canvas black with appropriate size 
		// non-colliding balls are black
		g.setColor(Color.BLACK);
		for(Blob b : blobs) {
			g.fillOval((int)b.getX(),(int)b.getY(),(int)(2*b.getR()),(int)(2*b.getR()));
		}
		
		// only re-draw blob objects that have collided from colliders ArrayList 
		// draw them red
		g.setColor(Color.RED);
		for(Blob c : colliders) {
			g.fillOval((int)c.getX(),(int)c.getY(),(int)(2*c.getR()),(int)(2*c.getR()));
		}
		
	}

	/**
	 * Sets colliders to include all blobs in contact with another blob
	 */
	private void findColliders() {
		// TODO: YOUR CODE HERE
		
		// clear colliders ArrayList after each run-through
		// blobs stay in ArrayList as long as they are currently colliding
		// reason: blobs cannot be drawn red forever
		colliders.clear();
	
		// create new PointQuadtree, root is first blob in blobs ArrayList
		// note blobs canNOT be empty, findColliders won't even be run in such case
		PointQuadtree<Blob> tree = new PointQuadtree<Blob>(blobs.get(0),0,0,width,height);
		
		// all other blobs are inserted into PointQuadtree
		for(int i = 1; i<blobs.size(); i++)
			tree.insert(blobs.get(i));
		
		// for each blob, see if anybody else collided with it
		for(Blob b : blobs) {
			// create ArrayList to hold blobs in vicinity of current blob
			List<Blob> found = tree.findInCircle(b.getX(), b.getY(), 2*b.getR());
			// notice that found ArrayList will necessarily hold current blob
			// only care about found ArrayList if it also contains additional blobs!
			if(found.size()>1)
				for(int i = 0; i < found.size(); i++) 
					// add all blobs in found to colliders
					colliders.add(found.get(i));
		}	
	}

	/**
	 * DrawingGUI method, here moving all the blobs and checking for collisions
	 */
	public void handleTimer() {
		// Ask all the blobs to move themselves.
		for (Blob blob : blobs) {
			blob.step();
		}
		// Check for collisions
		if (blobs.size() > 0) {
			findColliders();
			if (collisionHandler=='d') {
				blobs.removeAll(colliders);
				colliders = null;
			}
		}
		// Now update the drawing
		repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CollisionGUI();
			}
		});
	}
}

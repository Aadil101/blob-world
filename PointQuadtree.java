/**
 * PointQuadtree class
 * 
 * @author Aadil Islam, Spring 2018
 */

import java.util.ArrayList;
import java.util.List;

/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, explicit rectangle
 * @author CBK, Fall 2016, generic with Point2D interface
 * 
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters
	
	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {
		// TODO: YOUR CODE HERE
		
		// outer if statement identifies which quadrant the parameterized point belongs to
		// based its x/y coords, in relation to  (point's x, point's y), (x1,y1), and (x2,y2) 
		
		// Q1, includes axes
		if(p2.getX() >= point.getX() && p2.getX() <= x2 && p2.getY() >= y1 && p2.getY() <= point.getY()) {
			// if a point already exists in this quadrant, must recurse insert into this existing point
			if(c1 != null) 
				c1.insert(p2);
			// once empty quadrant is found, insert parameterized point here as a new PointQuadtree
			else 
				c1 = new PointQuadtree<E>(p2, (int)point.getX(), y1, x2, (int)point.getY());
		}
		// Q2
		else if(p2.getX() > x1 && p2.getX() < point.getX() && p2.getY() > y1 && p2.getY() < point.getY()) {
			if(c2 != null) 
				c2.insert(p2);
			else 
				c2 = new PointQuadtree<E>(p2, x1,y1,(int)point.getX(), (int)point.getY());
		}
		// Q3, includes axes
		else if(p2.getX() >= x1 && p2.getX() <= point.getX() && p2.getY() >= point.getY() && p2.getY() <= y2)  {
			if(c3 != null) 
				c3.insert(p2);
			else 
				c3 = new PointQuadtree<E>(p2, x1, (int)point.getY(), (int)point.getX(), y2);
		}
		// Q4
		else if(p2.getX() > point.getX() && p2.getX() < x2 && p2.getY() > point.getY() && p2.getY() < y2) {
			if(c4 != null) 
				c4.insert(p2);
			else 
				c4 = new PointQuadtree<E>(p2, (int)point.getX(), (int)point.getY(), x2,y2);
		}
	}
	
	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */


	public int size() {
		// TODO: YOUR CODE HERE
		
		// if assessing a PointQuadtree w/ root, size must be at least 1 
		int num = 1;
		// add to size recursively, depending on whether children have more PointQuadtree elements
		if(hasChild(1))
			num += c1.size();
		if(hasChild(2))
			num += c2.size();
		if(hasChild(3))
			num += c3.size();
		if(hasChild(4))
			num += c4.size();
		
		// once all recursion is over, return total size
		return num;
	}
	
	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public List<E> allPoints() {
		// TODO: YOUR CODE HERE
		
		// ArrayList will hold points found, initially just holding root
		List<E> points = new ArrayList<E>();
		points.add(point);
		
		// base case: PointQuadtree with root and no children
		if(getChildren().isEmpty())
			return(points);
		// all other cases:
		else {
			// using every non-null child given by getChildren helper method (see below)
			for(PointQuadtree<E> child : getChildren()) {
				// recurse allPoints using child
				List<E> points2 = child.allPoints();
				// add all of child's points to main points ArrayList
				for(E point : points2)
					points.add(point);
			}
		}
		
		return(points);
		
	}	


	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		// TODO: YOUR CODE HERE
		
		// ArrayList will hold all found points for query
		List<E> found = new ArrayList<E>();
		
		// check if query circle intersects PointQuadtree region using Geometry class
		if(Geometry.circleIntersectsRectangle(cx, cy, cr, x1, y1, x2, y2)) {
			// check if point lies in circle (matches query) using Geometry class
			if(Geometry.pointInCircle(point.getX(), point.getY(), cx, cy, cr))
				found.add(point);
			// base case: PointQuadtree with root and no children to test
			if(getChildren().isEmpty())
				return(found);
			// using every non-null child given by getChildren helper method
			for(PointQuadtree<E> child : getChildren()) {
				// recurse findInCircle using child, querying it 
				List<E> points2 = child.findInCircle(cx, cy, cr);
				// all of child's found points belong to main found ArrayList
				for(E point : points2) {
					found.add(point);
				}
				
			}
		}
		
		return(found);
		
	}

	// TODO: YOUR CODE HERE for any helper methods
	
	// this helper method returns all non-null children in PointQuadtree
	public List<PointQuadtree<E>> getChildren(){
		
		// ArrayList to hold all children
		List<PointQuadtree<E>> children = new ArrayList<PointQuadtree<E>>();
		
		if(c1 != null)
			children.add(c1);
		if(c2 != null)
			children.add(c2);
		if(c3 != null)
			children.add(c3);
		if(c4 != null)
			children.add(c4);
		
		return children;
		
	}
}

package asf.medieval.utility;

import com.badlogic.gdx.utils.Array;

/**
 * Created by daniel on 11/27/15.
 */
public class Brush {
	public Array<Point> pairs = new Array<Point>(false, 16, Point.class);
	private int minX=0;
	private int maxX=0;
	private int minY=0;
	private int maxY=0;
	private Shape shape = Shape.Circle;
	private int radius = 1;
	private int softness = 0;
	public Brush() {
		set(shape, radius, softness);
	}

	public Shape getShape(){
		return shape;
	}

	public void setShape(Shape shape){
		set(shape, radius, softness);
	}

	public int getRadius(){
		return radius;
	}

	public void setRadius(int radius){
		set(shape, radius, softness);
	}

	public int getSoftness(){
		return softness;
	}

	public void setSoftness(int softness){
		set(shape, radius, softness);
	}

	public void set(Shape shape, int radius, int softness){
		if(shape == null) shape = Shape.Circle;
		if(radius <0) radius =0;
		if(softness < 0 ) softness =0;

		this.shape = shape;
		this.radius =radius;
		this.softness = softness;
		pairs.clear();

		if(shape == Shape.Circle){
			setCircle();
		}else if(shape == Shape.Square){
			putPixel(0,0);
		}else{
			putPixel(0,0);
		}
	}

	private void setCircle()
	{
		if(radius <= 0){
			putPixel(0,0);
			return;
		}else if(radius ==1){
			putPixel(0,0);
			putPixel(1,0);
			putPixel(-1,0);
			putPixel(0,1);
			putPixel(0,-1);
			return;
		}

		setMidpointCircle();
	}

	//https://en.wikipedia.org/wiki/Midpoint_circle_algorithm
	// http://www.dailyfreecode.com/mysearchresult.aspx?q=program+midpoint+circle+drawing+using+java&stype=all
	private void setMidpointCircle()
	{
		int x0 =0;
		int y0 =0;
		int x = radius;
		int y = 0;
		int decisionOver2 = 1 - x;   // Decision criterion divided by 2 evaluated at x=r, y=0

		while( y <= x )
		{
			putPixel(x + x0, y + y0); // Octant 1
			putPixel(y + x0, x + y0); // Octant 2
			putPixel(-x + x0, y + y0); // Octant 4
			putPixel(-y + x0, x + y0); // Octant 3
			putPixel(-x + x0, -y + y0); // Octant 5
			putPixel(-y + x0, -x + y0); // Octant 6
			putPixel(x + x0, -y + y0); // Octant 8
			putPixel(y + x0, -x + y0); // Octant 7
			y++;
			if (decisionOver2<=0)
			{
				decisionOver2 += 2 * y + 1;   // Change in decision criterion for y -> y+1
			}
			else
			{
				x--;
				decisionOver2 += 2 * (y - x) + 1;   // Change for y -> y+1, x -> x-1
			}
		}

		floodFill(0,0);
	}

	private void setBresenhamCircle()
	{
		int x=0;
		int y =radius;
		int p = (3-(2*radius));
		int h=0;
		int k=0;

		do {
			putPixel((h+x),(k+y));
			putPixel((h+y),(k+x));
			putPixel((h+y),(k-x));

			putPixel((h+x),(k-y));
			putPixel((h-x),(k-y));
			putPixel((h-y),(k-x));

			putPixel((h-y),(k+x));
			putPixel((h-x),(k+y));

			x++;
			if(p<0){
				p+=((4*x)+6);
			}else{
				y--;
				p+=((4*(x-y))+10);
			}

		}while(x<=y);

		floodFill(0,0);
	}

	private int floodFill(int x, int y)
	{
		if(x < minX || x > maxX || y <minY || y > maxY) return 0;
		if(hasPixel(x,y)) return 0;
		putPixel(x,y);
		int count = 1;
		count+= floodFill(x-1, y);
		count+= floodFill(x+1, y);
		count+= floodFill(x, y-1);
		count+= floodFill(x, y+1);
		return count;
	}

	private boolean hasPixel(int x, int y){
		for (Point point : pairs) {
			if(point.x == x && point.y == y)
				return true;
		}
		return false;
	}

	private void putPixel(int x, int y){
		pairs.add(new Point(x,y, 1));
		if(x<minX) minX = x;
		else if(x>maxX) maxX = x;
		if(y<minY) minY = y;
		else if(y>maxY) maxY = y;
	}


	public enum Shape{
		Circle,
		Square;
	}

	@Override
	public String toString() {
		return "Brush{" +
			"shape=" + shape +
			", radius=" + radius +
			", softness=" + softness +
			'}';
	}
}

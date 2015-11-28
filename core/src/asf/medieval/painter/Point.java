package asf.medieval.painter;

import com.badlogic.gdx.math.MathUtils;

/**
 * Created by daniel on 11/27/15.
 */
public class Point {
	public int x;
	public int y;
	public int color;

	public Point(int x, int y, int color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Point point = (Point) o;

		if (x != point.x) return false;
		if (y != point.y) return false;
		return color == point.color;

	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		result = 31 * result + color;
		return result;
	}
}

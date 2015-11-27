package asf.medieval.utility;

import com.badlogic.gdx.math.MathUtils;

/**
 * Created by daniel on 11/27/15.
 */
public class Pair {
	public int x;
	public int y;
	public float opacity;

	public Pair(int x, int y, float opacity) {
		this.x = x;
		this.y = y;
		this.opacity = opacity;
	}

	public boolean equals(int x, int y, float opacity){
		return x == this.x && y == this.y && MathUtils.isEqual(opacity, this.opacity, 0.0001f);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Pair pair = (Pair) o;

		if (x != pair.x) return false;
		if (y != pair.y) return false;
		return Float.compare(pair.opacity, opacity) == 0;

	}

	@Override
	public int hashCode() {
		int result = x;
		result = 31 * result + y;
		result = 31 * result + (opacity != +0.0f ? Float.floatToIntBits(opacity) : 0);
		return result;
	}
}

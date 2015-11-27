package asf.medieval.utility;

/**
 * Created by daniel on 11/27/15.
 */
public class Pair {
	public int x;
	public int y;

	public Pair(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean equals(int x, int y){
		return x == this.x && y == this.y;
	}
}

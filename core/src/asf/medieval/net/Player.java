package asf.medieval.net;

/**
 * Created by daniel on 11/15/15.
 */
public class Player {

	public int id, x, y;
	public String name;
	public String otherStuff;

	@Override
	public String toString() {
		return "Player{"+id+"-"+name+"}";
	}
}

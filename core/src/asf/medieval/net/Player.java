package asf.medieval.net;

/**
 * Created by daniel on 11/15/15.
 */
public class Player {

	public int id;
	public String name;
	public float loading;


	@Override
	public String toString() {
		return id+"."+name;
	}

	public void set(Player player){
		id = player.id;
		name = player.name;
	}
}

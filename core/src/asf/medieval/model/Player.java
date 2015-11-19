package asf.medieval.model;

/**
 * Created by daniel on 11/15/15.
 */
public class Player {

	public int id;
	public int team;
	public String name;
	public float loading;

	public Player() {
	}


	public Player(Player cpy) {
		set(cpy);
	}


	@Override
	public String toString() {
		return id+"."+name;
	}

	public void set(Player player){
		id = player.id;
		team = player.team;
		name = player.name;
		loading = player.loading;
	}

	public Player cpy(){
		return new Player(this);
	}
}

package asf.medieval.model;

import asf.medieval.net.User;

/**
 * Created by daniel on 11/15/15.
 */
public class Player {

	public int playerId;
	public int team;
	public String name;

	public int food;


	public Player(User init) {
		playerId = init.id;
		team = init.team;
		name = init.name;
	}


	@Override
	public String toString() {
		return "Player{" +
			"playerId=" + playerId +
			", team=" + team +
			", name='" + name + '\'' +
			", food=" + food +
			'}';
	}
}

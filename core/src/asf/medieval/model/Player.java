package asf.medieval.model;

import asf.medieval.net.User;

import java.util.Arrays;

/**
 * Created by daniel on 11/15/15.
 */
public class Player {

	public int playerId;
	public int team;
	public String name;

	public int[] resources;

	public int pop;
	public int popcap;

	public final int[] lockedModels;


	public Player(User init) {
		playerId = init.id;
		team = init.team;
		name = init.name;
		lockedModels = new int[0];

		resources = new int[ResourceId.values().length];
		resources[ResourceId.Gold.ordinal()] = 250;
		resources[ResourceId.Food.ordinal()] = 100;
		resources[ResourceId.Wood.ordinal()] = 100;
		resources[ResourceId.Stone.ordinal()] = 0;
		resources[ResourceId.Iron.ordinal()] = 0;

		popcap = 9;

	}


	@Override
	public String toString() {
		return "Player{" +
			"playerId=" + playerId +
			", team=" + team +
			", name='" + name + '\'' +
			", resources=" + Arrays.toString(resources) +
			", lockedModels=" + Arrays.toString(lockedModels) +
			'}';
	}
}

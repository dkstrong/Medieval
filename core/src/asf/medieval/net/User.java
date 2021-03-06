package asf.medieval.net;

import asf.medieval.strictmath.StrictPoint;

/**
 * Created by daniel on 12/4/15.
 */
public class User {
	public int cid;
	public int id;
	public int team;
	public String name;
	public StrictPoint loading;

	public User() {
	}


	public User(User cpy) {
		set(cpy);
	}


	@Override
	public String toString() {
		return id+"."+name;
	}

	public void set(User user){
		cid = user.cid;
		id = user.id;
		team = user.team;
		name = user.name;
		loading = new StrictPoint(user.loading);
	}

	public User cpy(){
		return new User(this);
	}
}

package asf.medieval.net.message;

import asf.medieval.model.Player;
import asf.medieval.net.User;

/**
 * sent by the client to server to login, contains
 * a player object, but the server only reads
 * information it think is relevant (eg the username)
 *
 * Created by daniel on 11/15/15.
 */
public class Login {
	public User user;
}

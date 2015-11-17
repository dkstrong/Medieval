package asf.medieval.net.message;

import asf.medieval.model.Command;
import asf.medieval.model.Scenario;

/**
 * Created by daniel on 11/16/15.
 */
public class ReadyToStart {


	public int playerId;

	@Override
	public String toString() {
		return "ReadyToStart{" +
			"playerId=" + playerId +
			'}';
	}
}

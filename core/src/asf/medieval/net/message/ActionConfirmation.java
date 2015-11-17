package asf.medieval.net.message;

/**
 * Created by daniel on 11/16/15.
 */
public class ActionConfirmation {
	public int confirmedByPlayerId;
	public int playerId;
	public long lockstepFrame;

	@Override
	public String toString() {
		return "ActionConfirmation{" +
			"confirmedByPlayerId=" + confirmedByPlayerId +
			", playerId=" + playerId +
			", lockstepFrame=" + lockstepFrame +
			'}';
	}
}

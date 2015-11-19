package asf.medieval.model;

import asf.medieval.ai.SteerGraph;
import asf.medieval.terrain.HeightField;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class Scenario {
	protected transient Listener listener;

	protected final ScenarioRand rand;
	public transient HeightField heightField;
	protected final SteerGraph steerGraph = new SteerGraph();

	protected IntMap<Player> players = new IntMap<Player>(2);

	protected Array<Token> tokens = new Array<Token>(false, 256, Token.class);

	public Scenario(ScenarioRand rand) {
		this.rand = rand;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
		if (this.listener == null)
			return;

		for (Player player : players.values()) {
			listener.onNewPlayer(player);
		}

		for (Token token : tokens) {
			listener.onNewToken(token);
		}

	}

	public void addPlayer(Player player){
		Player existingPlayer = players.get(player.id);

		if(existingPlayer == null)
		{
			Player newPlayer = player.cpy();
			players.put(newPlayer.id, newPlayer);
			if(listener!=null)
				listener.onNewPlayer(newPlayer);
		}
		else
		{
			existingPlayer.set(player);
			if(listener!=null)
				listener.onUpdatePlayer(existingPlayer);
		}
	}

	public void removePlayer(Player player){
		Player removedPlayed = players.remove(player.id);
		if(removedPlayed!=null && listener!=null)
			listener.onRemovePlayer(removedPlayed);
	}

	public Player getPlayer(int id){
		return players.get(id);
	}

	public SoldierToken getSoldier(int soldierId)
	{
		for (Token token : tokens) {
			if (token instanceof SoldierToken)
			{
				SoldierToken soldierToken = (SoldierToken) token;
				if(soldierToken.id == soldierId){
					return soldierToken;
				}
			}
		}
		return null;
	}

	private int lastTokenId = 0;

	public SoldierToken newSoldier(int owner, Vector3 location)
	{
		SoldierToken token= new SoldierToken();
		++lastTokenId;
		token.id = lastTokenId;
		token.location.set(location);
		token.owner = players.get(owner);
		token.init(this);
		steerGraph.agents.add(token);
		tokens.add(token);

		setNonOverlappingPosition(token, location);

		if(listener!=null)
			listener.onNewToken(token);
		return token;
	}

	public StructureToken newStructure(int owner, Vector3 location)
	{
		StructureToken token= new StructureToken();
		++lastTokenId;
		token.id = lastTokenId;
		token.owner = players.get(owner);
		token.location.set(location);
		token.init(this);
		steerGraph.agents.add(token);
		tokens.add(token);
		if(listener!=null)
			listener.onNewToken(token);
		return token;
	}

	public void setRandomNonOverlappingPosition (SoldierToken character, Array<SoldierToken> others,
							float minDistanceFromBoundary) {
		int maxTries = UtMath.largest(100, others.size * others.size);
		SET_NEW_POS:
		while (--maxTries >= 0) {

			character.location.set(rand.range(-50f,50f),0,rand.range(-50f,50f));

			for (int i = 0; i < others.size; i++) {
				SoldierToken other = (SoldierToken)others.get(i);
				if (character.location.dst(other.location) <= character.radius + other.radius + minDistanceFromBoundary)
					continue SET_NEW_POS;
			}
			return;
		}
		throw new IllegalStateException("Probable infinite loop detected");
	}

	public void setNonOverlappingPosition(SoldierToken token, Vector3 location )
	{
		// THis is a workaround for the bug where if two tokens are in the same location, one of them
		// will end up with a location of "NaN" i suppose due to the steering system.
		// ideally id like to figure out whats causing the NaN and let steering just naturally seperate
		// the soldiers...
		token.location.set(location);
		token.location.y = heightField.getElevation(token.location);
		int maxTries = UtMath.largest(100, tokens.size * tokens.size);
		final float eps = 0.001f;
		SET_NEW_POS:
		while (--maxTries >= 0) {
			for (Token t : tokens) {

				if(t!= token && (t instanceof SoldierToken)){
					SoldierToken other = (SoldierToken) t;
					//if(token.location.dst(other.location) <= token.radius){
					if(UtMath.abs(token.location.x-other.location.x) <eps && UtMath.abs(token.location.z-other.location.z) <eps){
						token.location.x = location.x + rand.range(-token.radius,token.radius);
						token.location.z = location.z + rand.range(-token.radius,token.radius);
						token.location.y = heightField.getElevation(token.location);
						continue SET_NEW_POS;
					}
				}
			}
			return;
		}

		throw new IllegalStateException("Probable infinite loop detected");
	}


	public void update(float delta)
	{


		for (Token token : tokens) {
			token.update(delta);
		}
	}

	public static interface Listener {
		public void onNewPlayer(Player player);
		public void onUpdatePlayer(Player player);
		public void onRemovePlayer(Player player);
		public void onNewToken(Token token);
	}
}
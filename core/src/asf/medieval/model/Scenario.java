package asf.medieval.model;

import asf.medieval.model.steer.InfantryController;
import asf.medieval.model.steer.SteerGraph;
import asf.medieval.model.steer.StructureController;
import asf.medieval.net.User;
import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictRand;
import asf.medieval.strictmath.StrictVec2;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public strictfp class Scenario {
	private transient Listener listener;

	public final StrictRand rand;
	public final ModelInfo[] modelInfo;
	public TerrainInfo terrain;

	public final SteerGraph steerGraph = new SteerGraph();
	public IntMap<Player> players = new IntMap<Player>(2);
	public Array<Token> tokens = new Array<Token>(false, 256, Token.class);

	public Scenario(long seed) {
		this.rand = new StrictRand(seed);
		this.modelInfo = ModelInfo.standardConfiguration();
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

	public void addPlayer(User user){
		Player existingPlayer = players.get(user.id);
		if(existingPlayer == null)
		{
			// new player
			Player newPlayer = new Player(user);
			players.put(newPlayer.playerId, newPlayer);
			if(listener!=null)
				listener.onNewPlayer(newPlayer);
		}
		else
		{
			// update exisisting player

			existingPlayer.name = user.name;
			existingPlayer.team = user.team;
			if(listener!=null)
				listener.onUpdatePlayer(existingPlayer);
		}
	}

	public void removePlayer(User user){
		Player removedPlayed = players.remove(user.id);
		if(removedPlayed!=null && listener!=null)
			listener.onRemovePlayer(removedPlayed);
	}

	public Player getPlayer(int id){
		return players.get(id);
	}

	public Token getSoldier(int tokenId)
	{
		for (Token token : tokens) {
			if(token.id == tokenId){
				return token;
			}
		}
		return null;
	}

	private int lastTokenId = 0;

	public Token buildToken(int owner, StrictVec2 location, int modelId){

		ModelInfo modelInfo = this.modelInfo[modelId];

		if(modelInfo.structure){
			return newStructure(owner, location, modelId);
		}else{
			Token barracks = getBarracksToBuild(owner,location, modelId);
			final StrictVec2 spawnLoc = barracks.location;

			Token soldier =  newSoldier(owner, spawnLoc, modelId);

			InfantryController infantry = (InfantryController)soldier.agent;
			infantry.setTarget(location);

			return soldier;
		}
	}

	public Token newSoldier(int owner, StrictVec2 location, int modelId)
	{
		Token token= new Token();
		++lastTokenId;
		token.id = lastTokenId;
		token.scenario = this;
		token.owner = players.get(owner);
		token.modelId = modelId;
		token.mi = modelInfo[modelId];
		token.location.set(location);
		token.attack = new AttackController(token);
		token.damage = new DamageController(token);
		token.agent = new InfantryController(token);
		steerGraph.agents.add(token.agent);
		tokens.add(token);

		setNonOverlappingPosition(token, location);

		terrain.getElevation(location, token.elevation);
		//token.elevation.val = terrain.getElevation(location.x.val,location.y.val);


		if(listener!=null)
			listener.onNewToken(token);
		return token;
	}

	public Token newStructure(int owner, StrictVec2 location, int modelId)
	{
		Token token= new Token();
		++lastTokenId;
		token.id = lastTokenId;
		token.scenario = this;
		token.modelId = modelId;
		token.mi = modelInfo[modelId];
		token.owner = players.get(owner);
		token.location.set(location);
		terrain.getElevation(location, token.elevation);
		//token.elevation.val = terrain.getElevation(location.x.val,location.y.val);
		token.barracks = new BarracksController(token);
		token.agent = new StructureController(token);
		steerGraph.agents.add(token.agent);
		tokens.add(token);

		if(listener!=null)
			listener.onNewToken(token);
		return token;
	}

	public Token newMineable(StrictVec2 location, int modelId){
		Token token= new Token();
		++lastTokenId;
		token.id = lastTokenId;
		token.scenario = this;
		token.modelId = modelId;
		token.mi = modelInfo[modelId];
		token.owner = Player.NULL_PLAYER;
		token.location.set(location);
		terrain.getElevation(location, token.elevation);
		//token.elevation.val = terrain.getElevation(location.x.val,location.y.val);
		token.mineable = new MineController(token);

		token.agent = new StructureController(token);
		steerGraph.agents.add(token.agent);
		tokens.add(token);

		if(listener!=null)
			listener.onNewToken(token);
		return token;
	}

	private Array<Token> tempTokens = new Array<Token>(false, 16, Token.class);
	private static final StrictPoint tempPoint = new StrictPoint();
	private static final StrictPoint tempPoint2 = new StrictPoint();
	private static final StrictPoint tempPoint3 = new StrictPoint();

	public Token getBarracksToBuild(int owner, StrictVec2 location, int modelId) {
		Token closestBarracks = null;
		StrictPoint closestDist2 = tempPoint.set(StrictPoint.MAX_VALUE);
		for (Token token : tokens) {
			if (token.owner.playerId == owner) {
				if (token.barracks != null) {
					if(location == null){
						return token;
					}else if(closestBarracks == null){
						closestBarracks = token;
						token.location.dst2(location,closestDist2);
					}else{
						StrictPoint dist2 = token.location.dst2(location,tempPoint2);
						if(dist2.val < closestDist2.val){
							closestBarracks = token;
							closestDist2 = dist2;
						}
					}

				}
			}
		}

		return closestBarracks;
	}


	public void setRandomNonOverlappingPosition (Token token,
						     StrictPoint minX, StrictPoint maxX,
						     StrictPoint minY, StrictPoint maxY) {
		int maxTries = UtMath.largest(100, tokens.size * tokens.size);
		StrictPoint eps = tempPoint.set("0.001");
		SET_NEW_POS:
		while (--maxTries >= 0) {

			rand.range(minX,maxX,token.location.x);
			rand.range(minY,maxY,token.location.y);

			for (Token t : tokens) {
				if(t!= token){
					Token other =  t;
					if(token.location.epsilonEquals(other.location, eps)){
						continue SET_NEW_POS;
					}
				}
			}
			return;
		}
		throw new IllegalStateException("Probable infinite loop detected");
	}

	public void setNonOverlappingPosition(Token token, StrictVec2 location )
	{
		// THis is a workaround for the bug where if two tokens are in the same location, one of them
		// will end up with a location of "NaN" i suppose due to the steering system.
		// ideally id like to figure out whats causing the NaN and let steering just naturally seperate
		// the soldiers...
		token.location.set(location);
		int maxTries = UtMath.largest(100, tokens.size * tokens.size);
		StrictPoint eps = tempPoint.set("0.001");
		StrictPoint radius = token.mi.shape.radius;
		StrictPoint negRadius = tempPoint2.set(token.mi.shape.radius).negate();
		SET_NEW_POS:
		while (--maxTries >= 0) {
			for (Token t : tokens) {

				if(t!= token){
					Token other =  t;
					//if(token.location.dst(other.location) <= token.radius){

					if(token.location.epsilonEquals(other.location, eps)){
						rand.range(negRadius,radius,token.location.x).add(location.x);
						rand.range(negRadius,radius,token.location.y).add(location.y);
						continue SET_NEW_POS;
					}
				}
			}
			return;
		}

		throw new IllegalStateException("Probable infinite loop detected");
	}


	public void update(StrictPoint delta)
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
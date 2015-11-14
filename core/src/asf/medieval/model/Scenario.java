package asf.medieval.model;

import asf.medieval.ai.SteerGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class Scenario {
	protected transient Listener listener;

	protected final ScenarioRand rand;
	protected final SteerGraph steerGraph = new SteerGraph();

	protected Array<Token> tokens = new Array<Token>(false, 256, Token.class);

	public Scenario(ScenarioRand rand) {
		this.rand = rand;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
		if (this.listener == null)
			return;

		for (Token token : tokens) {
			if (token instanceof SoldierToken)
			{
				listener.onNewSoldier((SoldierToken) token);
			}
		}

	}

	public SoldierToken newSoldier()
	{
		SoldierToken soldierToken= new SoldierToken();
		soldierToken.init(this);
		steerGraph.agents.add(soldierToken);
		tokens.add(soldierToken);
		if(listener!=null)
			listener.onNewSoldier(soldierToken);
		return soldierToken;
	}

	public void setRandomNonOverlappingPosition (SoldierToken character, Array<SoldierToken> others,
							float minDistanceFromBoundary) {
		int maxTries = Math.max(100, others.size * others.size);
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
		throw new GdxRuntimeException("Probable infinite loop detected");
	}


	public void update(float delta)
	{


		for (Token token : tokens) {
			token.update(delta);
		}
	}

	public static interface Listener {
		public void onNewSoldier(SoldierToken soldierToken);
	}
}

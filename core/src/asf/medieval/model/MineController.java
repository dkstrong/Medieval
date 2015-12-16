package asf.medieval.model;

/**
 * Created by daniel on 11/19/15.
 */
public strictfp class MineController {
	public Token token;

	public int remainingResource;

	public MineController(Token token) {
		this.token = token;

		remainingResource = 100;
	}

	public int getMineResourceId(){
		ModelInfo mi = token.scenario.modelInfo[token.modelId];
		return mi.mineResourceId;
	}


}

package asf.medieval.model;

/**
 * Created by daniel on 11/19/15.
 */
public strictfp class ResourceController {
	public Token token;

	public int remainingResource;

	public ResourceController(Token token) {
		this.token = token;

		remainingResource = 100;
	}

	public int getResourceId(){
		ModelInfo mi = token.scenario.modelInfo[token.modelId];
		return mi.resourceId;
	}


}

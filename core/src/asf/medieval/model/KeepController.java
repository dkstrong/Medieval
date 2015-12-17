package asf.medieval.model;

import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;

/**
 * Created by daniel on 12/17/15.
 */
public strictfp class KeepController {

	public Token token;

	public final StrictPoint populationGrowRate = new StrictPoint("5");
	public final StrictPoint populationGrowT= new StrictPoint(populationGrowRate);

	public KeepController(Token token) {
		this.token = token;

		this.token.direction.set("0.78539816339");
	}

	public void update(StrictPoint delta)
	{
		if(token.owner.pop < token.owner.popcap){
			populationGrowT.sub(delta);
			if(populationGrowT.lessThanOrEqual(StrictPoint.ZERO)){
				populationGrowT.set(populationGrowRate);
				token.scenario.buildWorker(this);
			}
		}
	}

	private static final StrictPoint TEMP_POINT2 = new StrictPoint();
	private static void setAngle(StrictVec2 vector, StrictPoint value) {

		vector.x.set(TEMP_POINT2.set(value).sin()); // x = cos(value) * len
		vector.y.set(TEMP_POINT2.set(value).cos()); // y = sin(value) * len
	}

	public StrictVec2 getCampfireLocation(StrictVec2 store){

		//store.set("1","0");
		//store.setAngleRad(token.direction);

		setAngle(store, token.direction);
		store.scl("10");
		store.add(token.location);

		return store;
	}
}

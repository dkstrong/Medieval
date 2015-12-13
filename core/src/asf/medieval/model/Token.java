package asf.medieval.model;

import asf.medieval.model.steer.SteerController;
import asf.medieval.shape.Shape;
import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictVec2;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public strictfp class Token {
	public Scenario scenario;
	public Player owner;
	public int id;
	public int modelId;
	public ModelInfo mi;
	public final StrictVec2 location = new StrictVec2();
	public final StrictPoint elevation = new StrictPoint();
	public final StrictPoint direction = new StrictPoint();
	public SteerController agent;
	public AttackController attack;
	public DamageController damage;
	public BarracksController barracks;
	public ResourceController resource;

	public void update(StrictPoint delta)
	{
		if(attack != null){
			attack.update(delta);
		}

		if(damage != null){
			damage.update(delta);
		}

		if(agent !=null) {
			agent.update(delta);
		}

		// TODO: terrain is not strict..
		// TODO: need to have a seperate model to store strict points of the terrain
		// used for getting elevation values..
		elevation.val = scenario.terrain.getElevation(location.x.val,location.y.val);

	}



}

package asf.medieval.model;

import asf.medieval.model.steer.SteerController;
import asf.medieval.shape.Shape;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class Token {
	public Scenario scenario;
	public Player owner;
	public int id;
	public int modelId;
	public final Vector2 location = new Vector2();
	public float elevation;
	public float direction;
	public Shape shape;
	public SteerController agent;
	public AttackController attack;
	public DamageController damage;
	public BarracksController barracks;
	public ResourceController resource;

	public void update(float delta)
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

		elevation = scenario.terrain.getElevation(location.x,location.y);

	}



}

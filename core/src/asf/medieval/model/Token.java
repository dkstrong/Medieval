package asf.medieval.model;

import asf.medieval.ai.SteerAgent;
import asf.medieval.shape.Shape;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class Token {
	public Scenario scenario;
	public Player owner;
	public int id;
	public ModelId modelId;
	public final Vector2 location = new Vector2();
	public float elevation;
	public final Vector2 direction = new Vector2(0,1);
	public Shape shape;
	public SteerAgent agent;
	public AttackComponent attack;
	public DamageComponent damage;


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

		elevation = scenario.heightField.getElevation(location.x,location.y);
	}
}

package asf.medieval.model;

import asf.medieval.ai.SteerAgent;
import asf.medieval.shape.Shape;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class Token {
	public Scenario scenario;
	public Player owner;
	public int id;
	public ModelId modelId;
	public final Vector3 location = new Vector3();
	public Shape shape;
	public SteerAgent agent;
	public AttackComponent attack;
	public DamageComponent damage;

	public void update(float delta)
	{
		if(attack != null)
			attack.update(delta);

		if(damage != null)
			damage.update(delta);

		if(agent !=null)
			agent.update(delta);

	}
}

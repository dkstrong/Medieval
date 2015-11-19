package asf.medieval.model;

import asf.medieval.ai.SteerAgent;
import asf.medieval.shape.Shape;
import com.badlogic.gdx.math.Intersector;
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
	public SteerAgent steerAgent;

	public void update(float delta)
	{
		if(steerAgent!=null)
			steerAgent.update(delta);

	}
}

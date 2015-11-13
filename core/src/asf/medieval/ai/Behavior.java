package asf.medieval.ai;

/**
 * Created by Danny on 11/13/2015.
 */
public enum Behavior {

	/**
	 * Seeks a static target
	 */
	Seek,
	/**
	 * flees from a static target
	 */
	Flee,
	/**
	 * persues a moving SteeringAgent
	 */
	Persuit,
	/**
	 * evades a moving SteeringAgent
	 */
	Evade,
	/**
	 * avoid obstacles (SteeringAgents with velocity == Zero)
	 */
	Avoid,
	/**
	 * avoid neighbors (SteeringAgents with velocity != Zero)
	 */
	Separation,
	/**
	 * always returns Zero
	 */
	PassThrough
}

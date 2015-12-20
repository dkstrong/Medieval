package asf.medieval.model;

import asf.medieval.model.steer.SteerController;
import asf.medieval.strictmath.StrictPoint;
import asf.medieval.strictmath.StrictShape;
import asf.medieval.strictmath.StrictVec2;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public strictfp class Token {
	public Scenario scenario;
	public Player owner;
	public int id;
	public StrictShape shape;
	public StructureInfo si;
	public MilitaryInfo mi;
	public final StrictVec2 location = new StrictVec2();
	public final StrictPoint elevation = new StrictPoint();
	public final StrictPoint direction = new StrictPoint(StrictPoint.HALF_PI);
	public SteerController agent;
	public AttackController attack;
	public DamageController damage;
	public StructureController structure;
	public WorkerController worker;


	public void update(StrictPoint delta)
	{
		if(damage != null)
			damage.update(delta);

		if(structure != null)
			structure.update(delta);

		if(worker != null)
			worker.update(delta);

		if(attack != null)
			attack.update(delta);

		if(agent !=null)
			agent.update(delta);

		scenario.terrain.getElevation(location, elevation);
		//elevation.val = scenario.terrain.getElevation(location.x.val,location.y.val);

	}



}

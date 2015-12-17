package asf.medieval.model;

import asf.medieval.model.steer.InfantrySteerController;
import asf.medieval.strictmath.StrictPoint;

/**
 * Created by daniel on 11/19/15.
 */
public strictfp class DamageController {
	public Token token;

	public final StrictPoint health = new StrictPoint("10");
	public final StrictPoint meleeDefense = new StrictPoint("0");
	public final StrictPoint chargeDefense= new StrictPoint("0");

	public Token attacker;

	public final StrictPoint hitU= new StrictPoint("0");
	public final StrictPoint hitDuration= new StrictPoint("0");

	public DamageController(Token token) {
		this.token = token;
	}

	public void update(StrictPoint delta)
	{
		if(attacker != null)
		{
			hitU.set(attacker.attack.attackU);
		}

	}

	public void attackedBy(Token attacker){


		if(this.attacker == null && token.attack.target == null){
			this.attacker = attacker;
			hitU.set(StrictPoint.ZERO);
			hitDuration.set(attacker.attack.attackDuration);

			((InfantrySteerController) token.agent).setCombatTarget(attacker.agent);
		}

	}

	public void clearAttacked(Token attacker){

		if(this.attacker == attacker)
		{
			this.attacker = null;
			hitU.set("-1");

			// TODO; i think this is what causes the issue of rotating dead bodies
			// when did clearTarget() isnt called and i dont think the PostBehavior
			// gets reset
			if(health.val >StrictPoint.ZERO.val){
				((InfantrySteerController)token.agent).clearTarget();
			}else{
				((InfantrySteerController)token.agent).setDeath(token.location);
			}
		}

	}
}

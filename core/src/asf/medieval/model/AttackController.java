package asf.medieval.model;

import asf.medieval.model.steer.InfantrySteerController;
import asf.medieval.strictmath.StrictPoint;

/**
 * Created by daniel on 11/19/15.
 */
public strictfp class AttackController {
	public Token token;
	public final StrictPoint meleeAttack = new StrictPoint();
	public final StrictPoint chargeAttack = new StrictPoint();

	public Token target;

	public final StrictPoint attackU = new StrictPoint();
	public final StrictPoint attackDuration = new StrictPoint();

	public AttackController(Token token) {
		this.token = token;
	}

	private static final StrictPoint dst = new StrictPoint();
	private static final StrictPoint targetingRadius = new StrictPoint();
	private static final StrictPoint dmg = new StrictPoint();

	public void update(StrictPoint delta){
		if(target == null){
			if(token.damage.attacker == null && token.damage.health.greaterThan(StrictPoint.ZERO)){
				Token newTarget = null;
				for (int i = 0; i < token.scenario.tokens.size; i++) {
					Token other = token.scenario.tokens.items[i];
					if(token == other) continue;
					if(other.damage == null) continue;
					if(token.owner.team == other.owner.team) continue;
					if(other.damage.attacker != null) continue;
					if(other.damage.health.lessThanOrEqual(StrictPoint.ZERO)) continue;

					// TODO: also compare velocities (unless both characters have the same velocity- the one with
					// higher velocity gets attack precedence
					// also maybe consider in height and give height precedence
					// also precedence when attacking a target that is already in combat.
					// maybe also consider the direction theyre facing and so on.
					other.location.dst(token.location,dst);
					targetingRadius.set(other.mi.shape.radius).mul("2").add(token.mi.shape.radius);
					if(dst.lessThan(targetingRadius))
					{
						newTarget = other;
					}
				}
				if(newTarget!=null)
					attackTarget(newTarget);
			}
		}else {
			attackU.add(delta);
			if(attackU.greaterThanOrEqual(attackDuration)){
				dmg.set(meleeAttack).sub(target.damage.meleeDefense);
				if(dmg.lessThan(StrictPoint.ONE))
					dmg.set(StrictPoint.ONE);


				((InfantrySteerController)token.agent).clearTarget();

				target.damage.health.sub(dmg);

				//System.out.println("Wham! "+token.modelId+" attacked "+target.modelId);
				// TODO: the reason units get stuck when moving while mid attack animation
				// is because clear attack target clears the command.
				// Instead i need to have a smart command queueing system that can queue to
				// different controllers, so move command wont override the attack animation
				// and when the attack animation finished itll perform the queued move command.
				// etc
				clearAttackTarget();
			}
		}
	}

	public void attackTarget(Token target){
		//System.out.println(token.modelId+ " attack "+target.modelId);
		this.target = target;
		attackU.set(StrictPoint.ZERO);
		attackDuration.set("2");

		((InfantrySteerController) token.agent).setCombatTarget(target.agent);

		if(target.damage != null){
			target.damage.attackedBy(token);
		}

	}

	public void clearAttackTarget(){
		((InfantrySteerController)token.agent).clearTarget();
		attackU.set("-1");
		if(target != null){
			target.damage.clearAttacked(token);
			target = null;
		}


	}

}

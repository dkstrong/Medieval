package asf.medieval.model;

/**
 * Created by daniel on 11/19/15.
 */
public class AttackController {
	public Token token;
	public float meleeAttack;
	public float chargeAttack;

	public Token target;

	public float attackU;
	public float attackDuration;

	public AttackController(Token token) {
		this.token = token;
	}

	public void update(float delta){
		if(target == null){
			if(token.damage.attacker == null && token.damage.health >0){
				Token newTarget = null;
				for (int i = 0; i < token.scenario.tokens.size; i++) {
					Token other = token.scenario.tokens.items[i];
					if(token == other) continue;
					if(other.damage == null) continue;
					if(token.owner.team == other.owner.team) continue;
					if(other.damage.attacker != null) continue;
					if(other.damage.health <=0) continue;

					// TODO: also compare velocities (unless both characters have the same velocity- the one with
					// higher velocity gets attack precedence
					// also maybe consider in height and give height precedence
					// also precedence when attacking a target that is already in combat.
					// maybe also consider the direction theyre facing and so on.
					if(other.location.dst(token.location) < token.shape.radius + other.shape.radius *2f)
					{
						newTarget = other;
					}
				}
				if(newTarget!=null)
					attackTarget(newTarget);
			}
		}else {
			attackU += delta;
			if(attackU >= attackDuration){
				float dmg = meleeAttack - target.damage.meleeDefense;
				if(dmg < 1) dmg = 1;

				((InfantryAgent)token.agent).clearTarget();

				target.damage.health -= dmg;
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
		attackU = 0;
		attackDuration = 2f;

		((InfantryAgent) token.agent).setCombatTarget(target.agent);

		if(target.damage != null){
			target.damage.attackedBy(token);
		}

	}

	public void clearAttackTarget(){
		((InfantryAgent)token.agent).clearTarget();
		attackU = -1;
		if(target != null){
			target.damage.clearAttacked(token);
			target = null;
		}


	}

}

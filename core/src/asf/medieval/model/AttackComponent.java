package asf.medieval.model;

import asf.medieval.ai.SteerAgent;

/**
 * Created by daniel on 11/19/15.
 */
public class AttackComponent {
	public Token token;
	public float meleeAttack;
	public float chargeAttack;

	public Token target;

	public float attackU;
	public float attackDuration;

	public AttackComponent(Token token) {
		this.token = token;
	}

	public void update(float delta){
		if(target == null){
			if(token.damage.attacker == null){
				Token newTarget = null;
				for (int i = 0; i < token.scenario.tokens.size; i++) {
					Token other = 	token.scenario.tokens.items[i];
					if(token == other) continue;
					if(token.owner.team == other.owner.team) continue;

					// TODO: also compare velocities (unless both characters have the same velocity- the one with
					// higher velocity gets attack precedence
					// also maybe consider in height and give height precedence
					// also precedence when attacking a target that is already in combat.
					// maybe also consider the direction theyre facing and so on.
					if(other.location.dst(token.location) < token.shape.radius + other.shape.radius)
					{
						newTarget = other;
					}
				}
				if(newTarget!=null)
					setTarget(newTarget);
			}
		}else {
			attackU += delta;
			if(attackU >= attackDuration){
				float dmg = meleeAttack - target.damage.meleeDefense;
				if(dmg < 1) dmg = 1;

				((InfantryAgent)token.agent).clearTarget();

				target.damage.health -= dmg;
				System.out.println("Wham! "+token.modelId+" attacked "+target.modelId);
				if(target.damage.attacker == token){
					target.damage.attacker = null;
					((InfantryAgent)target.agent).clearTarget();
				}
				target = null;
			}
		}
	}

	public void setTarget(Token target){
		this.target = target;
		attackU = 0;
		attackDuration = 1f;

		((InfantryAgent) token.agent).setTargetAttack(target.agent);

		if(target.damage.attacker == null){
			target.damage.attacker = token;
			((InfantryAgent) target.agent).setTargetAttack(token.agent);
		}
	}

}

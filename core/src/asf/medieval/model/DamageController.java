package asf.medieval.model;

/**
 * Created by daniel on 11/19/15.
 */
public class DamageController {
	public Token token;

	public float health;
	public float meleeDefense;
	public float chargeDefense;

	public Token attacker;

	public float hitU;
	public float hitDuration;

	public DamageController(Token token) {
		this.token = token;
	}

	public void update(float delta)
	{
		if(attacker != null)
		{
			hitU = attacker.attack.attackU;
		}

	}

	public void attackedBy(Token attacker){


		if(this.attacker == null){
			this.attacker = attacker;
			hitU = 0;
			hitDuration = attacker.attack.attackDuration;

			((InfantryAgent) token.agent).setCombatTarget(attacker.agent);
		}

	}

	public void clearAttacked(Token attacker){

		if(this.attacker == attacker)
		{
			this.attacker = null;
			hitU = -1;
			((InfantryAgent)token.agent).clearTarget();
		}

	}
}

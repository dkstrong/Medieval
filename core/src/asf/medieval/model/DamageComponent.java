package asf.medieval.model;

/**
 * Created by daniel on 11/19/15.
 */
public class DamageComponent {
	public Token token;

	public float health;
	public float meleeDefense;
	public float chargeDefense;

	public Token attacker;

	public DamageComponent(Token token) {
		this.token = token;
	}

	public void update(float delta)
	{

	}
}

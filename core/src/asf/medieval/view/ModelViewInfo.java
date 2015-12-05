package asf.medieval.view;

import asf.medieval.model.ModelId;
import asf.medieval.model.ModelInfo;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.utils.IntMap;

/**
 * Created by daniel on 12/4/15.
 */
public class ModelViewInfo {

	public String name;
	public String[] assetLocation;
	public String[] idleAnims;
	public boolean loopWalkAnimation = true;
	public String[] walkAnims;
	public String[] attackAnims;
	public String[] hitAnims;
	public String[] dieAnims;

	public Attribute[] materialAttributes;

	public void setAssetLocation(String... assetLocation) {
		this.assetLocation = assetLocation;
	}

	public void setIdleAnims(String... idleAnims) {
		this.idleAnims = idleAnims;
	}

	public void setWalkAnims(String... walkAnims) {
		this.walkAnims = walkAnims;
	}

	public void setAttackAnims(String... attackAnims) {
		this.attackAnims = attackAnims;
	}

	public void setHitAnims(String... hitAnims) {
		this.hitAnims = hitAnims;
	}

	public void setDieAnims(String... dieAnims) {
		this.dieAnims = dieAnims;
	}


	public static ModelViewInfo[] standardConfiguration()
	{
		ModelViewInfo[] store = new ModelViewInfo[ModelId.values().length];
		ModelViewInfo tree = new ModelViewInfo();
		tree.name = "Tree";
		tree.setAssetLocation("Models/Foliage/Tree.g3db");
		tree.materialAttributes = new Attribute[]{
			new IntAttribute(IntAttribute.CullFace, 0),
			new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA),
			new FloatAttribute(FloatAttribute.AlphaTest, 0.5f)
		};

		ModelViewInfo church = new ModelViewInfo();
		church.name = "Church";
		church.setAssetLocation("Models/Church/Church.g3db");

		ModelViewInfo knight = new ModelViewInfo();
		knight.name = "Knight";
		knight.setAssetLocation("Models/Characters/knight_01.g3db","Models/Loot/Sword/BasicSword.g3db","Models/Loot/Sword/Shield.g3db");
		knight.setIdleAnims("idle");
		knight.setWalkAnims("walk");  // sprint
		knight.setAttackAnims("AttackSword"); // AttackUnarmed
		knight.setHitAnims("damage");
		knight.setDieAnims("die");

		ModelViewInfo skeleton = new ModelViewInfo();
		skeleton.name = "Skeleton";
		skeleton.setAssetLocation("Models/Characters/Skeleton.g3db");
		skeleton.setIdleAnims("Idle");
		skeleton.setWalkAnims("Walk");
		skeleton.setAttackAnims("Attack");
		skeleton.setHitAnims("Hit");
		skeleton.setDieAnims("Die");

		ModelViewInfo rockMonster = new ModelViewInfo();
		rockMonster.name = "Rock Monster";
		rockMonster.setAssetLocation("Models/Characters/rockMonster_01.g3db");
		rockMonster.setIdleAnims("idle");
		rockMonster.setWalkAnims("walk");
		rockMonster.setAttackAnims("attack");
		rockMonster.setHitAnims("damage");
		rockMonster.setDieAnims("die");

		ModelViewInfo jimmy = new ModelViewInfo();
		jimmy.name = "Jimmy";
		jimmy.setAssetLocation("Models/Jimmy/Jimmy_r1.g3db");
		jimmy.setIdleAnims("Idle01");
		jimmy.setWalkAnims("MoveForward");
		jimmy.loopWalkAnimation = false;
		jimmy.setAttackAnims("FlareThrow");
		jimmy.setHitAnims("ThrustersOn");
		jimmy.setDieAnims("Death01");

		store[ModelId.Tree.ordinal()] = tree;
		store[ModelId.Church.ordinal()] = church;
		store[ModelId.Knight.ordinal()] = knight;
		store[ModelId.Skeleton.ordinal()] = skeleton;
		store[ModelId.RockMonster.ordinal()] = rockMonster;
		store[ModelId.Jimmy.ordinal()] = jimmy;

		return store;
	}
}

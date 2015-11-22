package asf.medieval.view;

import asf.medieval.model.steer.InfantryController;
import asf.medieval.model.ModelId;
import asf.medieval.model.Token;
import asf.medieval.shape.Shape;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class InfantryView implements View, SelectableView, AnimationController.AnimationListener {
	private MedievalWorld world;
	public final Token token;
	public final InfantryController agent;
	public final Shape shape;
	private ModelInstance modelInstance;
	private AnimationController animController;
	public final Vector3 translation = new Vector3();
	public final Quaternion rotation = new Quaternion();

	public boolean selected = false;
	private final Decal selectionDecal = new Decal();

	private Animation[] idle;
	private Animation[] walk;
	private Animation[] attack ;
	private Animation[] hit ;
	private Animation[] die ;

	public InfantryView(MedievalWorld world, Token soldierToken) {
		this.world = world;
		this.token = soldierToken;
		agent = (InfantryController)token.agent;
		shape = token.shape;

		//world.addGameObject(new DebugShapeView(world).shape(token.location,token.shape));
		String asset;
		if(token.modelId == ModelId.Jimmy){
			asset = "Models/Jimmy/Jimmy_r1.g3db";
		}else if(token.modelId == ModelId.RockMonster){
			asset = "Models/Characters/rockMonster_01.g3db";
		}else if(token.modelId == ModelId.Knight){
			asset = "Models/Characters/knight_01.g3db";
		}else{
			asset = "Models/Characters/Skeleton.g3db";
		}


		Model model = world.assetManager.get(asset);

		modelInstance = new ModelInstance(model);
		if (modelInstance.animations.size > 0) {
			if(token.modelId == ModelId.Jimmy){
				for (Animation anim : modelInstance.animations) {
					if(anim.id.startsWith("Idle01")){
						idle = new Animation[]{anim};
					}else if(anim.id.startsWith("MoveForward")){
						walk = new Animation[]{anim};
					}else if(anim.id.startsWith("FlareThrow")){
						attack = new Animation[]{anim};
					}else if(anim.id.startsWith("ThrustersOn")){
						hit = new Animation[]{anim};
					}else if(anim.id.startsWith("Death01")){ // Death01  // TurtleOnDizzyWithFall // TurtleOnDizzy
						die = new Animation[]{anim};
					}
					//System.out.println(anim.id);
				}
			}else if(token.modelId == ModelId.RockMonster){
				for (Animation anim : modelInstance.animations) {
					if(anim.id.startsWith("idle")){
						idle = new Animation[]{anim};
					}else if(anim.id.startsWith("walk")){ // walk_old
						walk = new Animation[]{anim};
					}else if(anim.id.startsWith("attack")){
						attack = new Animation[]{anim};
					}else if(anim.id.startsWith("damage")){
						hit = new Animation[]{anim};
					}else if(anim.id.startsWith("die")){ // pile_of_rocks
						die = new Animation[]{anim};
					}
					//System.out.println(anim.id);
				}
			}else if(token.modelId == ModelId.Knight){
				for (Animation anim : modelInstance.animations) {
					if(anim.id.startsWith("idle")){
						idle = new Animation[]{anim};
					}else if(anim.id.startsWith("walk")){ // sprint
						walk = new Animation[]{anim};
					}else if(anim.id.startsWith("AttackSword")){ // AttackUnarmed
						attack = new Animation[]{anim};
					}else if(anim.id.startsWith("damage")){
						hit = new Animation[]{anim};
					}else if(anim.id.startsWith("die")){ // pile_of_rocks
						die = new Animation[]{anim};
					}
					//System.out.println(anim.id);
				}
			}else{
				for (Animation anim : modelInstance.animations) {
					if(anim.id.startsWith("Idle")){
						idle = new Animation[]{anim};
					}else if(anim.id.startsWith("Walk")){
						walk = new Animation[]{anim};
					}else if(anim.id.startsWith("Attack")){
						attack = new Animation[]{anim};
					}else if(anim.id.startsWith("Hit")){
						hit = new Animation[]{anim};
					}else if(anim.id.startsWith("Die")){
						die = new Animation[]{anim};
					}
				}
			}


			animController = new AnimationController(modelInstance);
			animController.animate(idle[MathUtils.random.nextInt(idle.length)].id, 0, -1, -1, 1, this, 0.2f);
		}
		//rotation.setEulerAngles(180f, 0, 0);

		loadKnightWeapons();


		//shape = new Disc(token.radius);
		//shape = new Sphere(token.radius,0, token.height/2f, 0);

		selectionDecal.setTextureRegion(world.pack.findRegion("Textures/MoveCommandMarker"));
		selectionDecal.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		selectionDecal.setDimensions(token.shape.radius *3.5f, token.shape.radius *3.5f);
		selectionDecal.setColor(1, 1, 0, 1);
		selectionDecal.rotateX(-90);

		translation.set(token.location.x,token.elevation,token.location.y);
	}

	private Node weaponAttachmentNode;
	private ModelInstance weaponModelInstance;
	private ModelInstance offhandModelInstance;
	private Node offhandAttachmentNode;

	private void loadKnightWeapons()
	{
		if (token.modelId != ModelId.Knight) return;


		boolean offhandIsPrimary = false;

		weaponAttachmentNode = offhandIsPrimary ? modelInstance.getNode("attach_l", true, true) : modelInstance.getNode("attach_r", true, true);


		Model weaponModel = world.assetManager.get("Models/Loot/Sword/BasicSword.g3db", Model.class);
		weaponModelInstance = new ModelInstance(weaponModel);

		Model weaponOffhandModel = world.assetManager.get("Models/Loot/Sword/Shield.g3db", Model.class);
		offhandModelInstance = new ModelInstance(weaponOffhandModel);
		offhandAttachmentNode = modelInstance.getNode("shield", true, true);

	}

	@Override
	public void update(float delta) {
		//System.out.println(token.id + ": " + token.location);

		translation.set(token.location.x,token.elevation,token.location.y);
		rotation.setFromAxisRad(0,1,0,token.direction);
		float speed = agent.getVelocity().len();

		if(token.damage != null && token.damage.health <=0){
			if (!animController.current.animation.id.startsWith(die[0].id)){
				animController.animate(die[0].id, 0, -1, 1, 1, this, 0.2f);
			}
		}else if(token.attack.attackU >0){
			if (!animController.current.animation.id.startsWith(attack[0].id)){
				animController.animate(attack[0].id, 0, -1, 1, attack[0].duration/token.attack.attackDuration, this, 0.2f);
			}
		}else if(token.damage!=null && token.damage.hitU >0){
			if (!animController.current.animation.id.startsWith(hit[0].id)){
				animController.animate(hit[0].id, 0, -1, 1, hit[0].duration/token.damage.hitDuration, this, 0.2f);
			}
		}else if(speed < 0.75f){
			if (!animController.current.animation.id.startsWith("Idle"))
				animController.animate(idle[MathUtils.random.nextInt(idle.length)].id, 0, -1, -1, 1, this, 0.2f);
		}
		else
		{
			float animSpeed = speed  /agent.getMaxSpeed() * 0.5f;
			if (!animController.current.animation.id.startsWith(walk[0].id)){
				if(token.modelId == ModelId.Jimmy)
					animController.animate(walk[0].id, 0, -1, 1, animSpeed, this, 0.2f);
				else{
					animController.animate(walk[0].id, 0, -1, -1, animSpeed, this, 0.2f);
				}
			}else{
				animController.current.speed = animSpeed;
			}

			//float angle = agent.getVelocity().cpy().nor().angleRad(Vector2.Y);


			/*
			Vector3 dir = UtMath.toVector3(agent.getVelocity(),new Vector3());
			dir.x *= -1f; // TODO: why do i need to flip the x direction !?!?!
			dir.y  =0;
			dir.nor();
			rotation.setFromCross(dir, Vector3.Z);
			*/

		}


		if (animController != null)
			animController.update(delta);
	}


	private static Vector3 vec1 = new Vector3();
	@Override
	public void render(float delta) {
		modelInstance.transform.set(
			translation.x, translation.y, translation.z,
			rotation.x, rotation.y, rotation.z, rotation.w,
			1, 1, 1
		);
		world.shadowBatch.render(modelInstance);
		world.modelBatch.render(modelInstance, world.environment);

		if (weaponModelInstance != null) {
			weaponModelInstance.transform.set(modelInstance.transform).mul(weaponAttachmentNode.globalTransform);
			weaponModelInstance.transform.rotate(Vector3.Z, -90);
			world.shadowBatch.render(weaponModelInstance);
			world.modelBatch.render(weaponModelInstance, world.environment);
			if (offhandModelInstance != null) {
				offhandModelInstance.transform.set(modelInstance.transform).mul(offhandAttachmentNode.globalTransform);
				offhandModelInstance.transform.rotate(Vector3.Z, -90);
				world.shadowBatch.render(offhandModelInstance);
				world.modelBatch.render(offhandModelInstance, world.environment);
			}
		}


		if(selected)
		{
			selectionDecal.setPosition(translation);
			world.scenario.terrain.getWeightedNormalAt(translation, vec1);
			selectionDecal.setRotation(vec1, Vector3.Y);
			vec1.scl(0.1f);
			selectionDecal.translate(vec1);
			world.decalBatch.add(selectionDecal);
		}
	}

	/**
	 * @return -1 on no intersection,
	 * or when there is an intersection: the squared distance between the center of this
	 * object and the point on the ray closest to this object when there is intersection.
	 */
	public float intersects(Ray ray) {
		return shape.intersects(modelInstance.transform, ray);
	}

	@Override
	public Token getToken() {
		return token;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public Vector3 getTranslation() {
		return translation;
	}

	@Override
	public void onEnd(AnimationController.AnimationDesc animation) {
		//animController.animate("SomethingElse", 0, -1, 1, 1, this, 0.2f);
	}

	@Override
	public void onLoop(AnimationController.AnimationDesc animation) {
		if (animation.animation.id.startsWith("Idle"))
			animController.animate(idle[MathUtils.random.nextInt(idle.length)].id, 0, -1, -1, 1, this, 0.2f);
	}
}

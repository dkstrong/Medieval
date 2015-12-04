package asf.medieval.view;

import asf.medieval.model.steer.InfantryController;
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

	public ModelViewInfo mvi;
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
		mvi = world.modelViewInfo.get(soldierToken.modelId);


		Model model = world.assetManager.get(mvi.assetLocation[0]);

		modelInstance = new ModelInstance(model);
		if (modelInstance.animations.size > 0) {
			for (Animation animation : modelInstance.animations) {
				if(animation.id.startsWith(mvi.idleAnims[0])){
					idle = new Animation[]{animation};
				}else if(animation.id.startsWith(mvi.walkAnims[0])){
					walk = new Animation[]{animation};
				}else if(animation.id.startsWith(mvi.attackAnims[0])){
					attack = new Animation[]{animation};
				}else if(animation.id.startsWith(mvi.hitAnims[0])){
					hit = new Animation[]{animation};
				}else if(animation.id.startsWith(mvi.dieAnims[0])){
					die = new Animation[]{animation};
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
		if(mvi.assetLocation.length ==3)
		{
			// TODO: if im going to have attachable submodels as a thing, then i need tobetter
			// integrate this with ModelViewInfo.. but I think I'm going to phase this out when
			// i get better models and have all infantry just be single models
			boolean offhandIsPrimary = false;

			weaponAttachmentNode = offhandIsPrimary ? modelInstance.getNode("attach_l", true, true) : modelInstance.getNode("attach_r", true, true);


			Model weaponModel = world.assetManager.get(mvi.assetLocation[1], Model.class);
			weaponModelInstance = new ModelInstance(weaponModel);

			Model weaponOffhandModel = world.assetManager.get(mvi.assetLocation[2], Model.class);
			offhandModelInstance = new ModelInstance(weaponOffhandModel);
			offhandAttachmentNode = modelInstance.getNode("shield", true, true);
		}

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
				if(mvi.loopWalkAnimation)
					animController.animate(walk[0].id, 0, -1, -1, animSpeed, this, 0.2f);
				else{
					animController.animate(walk[0].id, 0, -1, 1, animSpeed, this, 0.2f);
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

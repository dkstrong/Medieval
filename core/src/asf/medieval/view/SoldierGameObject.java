package asf.medieval.view;

import asf.medieval.model.SoldierToken;
import asf.medieval.utility.UtMath;
import asf.medieval.view.shape.Box;
import asf.medieval.view.shape.Shape;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class SoldierGameObject implements GameObject, AnimationController.AnimationListener {
	private MedievalWorld world;
	public final SoldierToken token;
	public final Shape shape;
	private ModelInstance modelInstance;
	private AnimationController animController;
	public final Vector3 translation = new Vector3();
	public final Quaternion rotation = new Quaternion();

	public boolean selected = false;
	private final Decal selectionDecal = new Decal();

	private static final String[] idle = new String[]{"Idle", "Idle", "Idle"};
	private static final String[] walk = new String[]{"Walk"};

	public SoldierGameObject(MedievalWorld world, SoldierToken soldierToken) {
		this.world = world;
		this.token = soldierToken;

		shape = new Box( token.radius, token.height/2f, token.radius, 0, token.height/2f, 0);

		Model model = world.assetManager.get("Models/Characters/Skeleton.g3db");
		modelInstance = new ModelInstance(model);
		if (modelInstance.animations.size > 0) {
			animController = new AnimationController(modelInstance);
			animController.animate(idle[MathUtils.random.nextInt(idle.length)], 0, -1, -1, 1, this, 0.2f);
		}
		//rotation.setEulerAngles(180f, 0, 0);



		//shape = new Disc(token.radius);
		//shape = new Sphere(token.radius,0, token.height/2f, 0);

		selectionDecal.setTextureRegion(world.pack.findRegion("Textures/MoveCommandMarker"));
		selectionDecal.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		selectionDecal.setDimensions(token.radius *3.5f, token.radius *3.5f);
		selectionDecal.setColor(1, 1, 0, 1);
		selectionDecal.rotateX(-90);

	}

	@Override
	public void update(float delta) {
		translation.set(token.location);





		float speed = token.getVelocity().len();
		if(speed < .75f)
		{
			if (!animController.current.animation.id.startsWith("Idle"))
				animController.animate(idle[MathUtils.random.nextInt(idle.length)], 0, -1, -1, 1, this, 0.2f);
		}
		else
		{
			if (!animController.current.animation.id.startsWith("Walk")){
				animController.animate("Walk", 0, -1, -1, 1, this, 0.2f);
			}else{
				animController.current.speed = speed  /token.getMaxSpeed();
			}


			Vector3 dir = token.getVelocity().cpy();
			dir.x *= -1f; // TODO: why do i need to flip the x direction !?!?!
			dir.y  =0;
			dir.nor();
			rotation.setFromCross(dir, Vector3.Z);
		}


		if (animController != null)
			animController.update(delta);
	}


	@Override
	public void render(float delta) {
		modelInstance.transform.set(
			translation.x, translation.y, translation.z,
			rotation.x, rotation.y, rotation.z, rotation.w,
			1, 1, 1
		);
		world.shadowBatch.render(modelInstance);
		world.modelBatch.render(modelInstance, world.environment);

		if(selected)
		{
			selectionDecal.setPosition(translation);
			selectionDecal.translateY(0.05f);
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
	public void onEnd(AnimationController.AnimationDesc animation) {
		//animController.animate("SomethingElse", 0, -1, 1, 1, this, 0.2f);
	}

	@Override
	public void onLoop(AnimationController.AnimationDesc animation) {
		if (animation.animation.id.startsWith("Idle"))
			animController.animate(idle[MathUtils.random.nextInt(idle.length)], 0, -1, -1, 1, this, 0.2f);
	}
}

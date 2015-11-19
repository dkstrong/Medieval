package asf.medieval.view;

import asf.medieval.model.Token;
import asf.medieval.shape.Shape;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class StructureView implements View, SelectableView, AnimationController.AnimationListener {
	private MedievalWorld world;
	public final Token token;
	public final Shape shape;
	private ModelInstance modelInstance;
	private AnimationController animController;
	public final Vector3 translation = new Vector3();
	public final Quaternion rotation = new Quaternion();

	public boolean selected = false;
	private final Decal selectionDecal = new Decal();

	private final String openAnim;

	public StructureView(MedievalWorld world, Token structureToken) {
		this.world = world;
		this.token = structureToken;

		shape = token.shape;

		//world.addGameObject(new DebugShapeView(world).shape(token.location,token.shape));

		Model model = world.assetManager.get("Models/Church/Church.g3db");
		modelInstance = new ModelInstance(model);
		if (modelInstance.animations.size > 0) {
			animController = new AnimationController(modelInstance);
			openAnim = modelInstance.animations.get(0).id;

			//animController.animate(idle[MathUtils.random.nextInt(idle.length)], 0, -1, -1, 1, this, 0.2f);
		}else{
			openAnim = null;
		}
		//rotation.setEulerAngles(180f, 0, 0);



		//shape = new Disc(token.radius);
		//shape = new Sphere(token.radius,0, token.height/2f, 0);

		selectionDecal.setTextureRegion(world.pack.findRegion("Textures/MoveCommandMarker"));
		selectionDecal.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		selectionDecal.setDimensions(token.shape.radius *3.5f, token.shape.radius *3.5f);
		selectionDecal.setColor(1, 1, 0, 1);
		selectionDecal.rotateX(-90);

	}

	private boolean triggerAnim = false;

	@Override
	public void update(float delta) {
		translation.set(token.location);


		if (animController != null)
		{
			if(triggerAnim ){
				triggerAnim = false;
				animController.setAnimation(openAnim,1,1,this);
			}

			animController.update(delta);
		}

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

		if(selected)
		{
			selectionDecal.setPosition(translation);
			world.scenario.heightField.getWeightedNormalAt(translation, vec1);
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
		animController.setAnimation(openAnim,1,-1,this);
	}

	@Override
	public void onLoop(AnimationController.AnimationDesc animation) {
	}
}

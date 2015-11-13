package asf.medieval.view;

import asf.medieval.utility.ModelFactory;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class SceneGameObject implements GameObject{


	private MedievalWorld world;
	private ModelInstance modelInstance;
	public final Vector3 translation = new Vector3();
	public final Quaternion rotation = new Quaternion();

	public SceneGameObject(MedievalWorld world) {
		this.world = world;
		// Model model = world.assetManager.get("Scenes/Desert/Desert_Mobile_r1.g3db", Model.class);
		Model model = ModelFactory.box(100, 10, 100, Color.BROWN);
		modelInstance = new ModelInstance(model);
		//translation.z = -270f;
		translation.y = -5;


		modelInstance.transform.set(
			translation.x, translation.y, translation.z,
			rotation.x, rotation.y, rotation.z, rotation.w,
			1, 1, 1
		);

	}

	@Override
	public void update(float delta) {

	}


	@Override
	public void render(float delta) {
		world.shadowBatch.render(modelInstance);
		world.modelBatch.render(modelInstance, world.environment);

	}
}

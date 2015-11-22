package asf.medieval.view;

import asf.medieval.terrain.Terrain;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 *
 *
 * Created by Daniel Strong on 11/11/2015.
 */
public class TerrainView implements View {


	private MedievalWorld world;
	private ModelInstance skydome;
	Quaternion skyRotation = new Quaternion();

	public Terrain terrain;

	public TerrainView(MedievalWorld world) {
		this.world = world;


		//Model skyModel = world.assetManager.get("Models/skydome.g3db", Model.class);
		//skydome = new ModelInstance(skyModel);

		terrain = world.assetManager.get("Models/Terrain/terrain.txt", Terrain.class);


	}

	@Override
	public void update(float delta) {


	}


	@Override
	public void render(float delta)
	{
		//world.shadowBatch.render(terrain);
		//world.modelBatch.render(terrain);
		world.modelBatch.render(terrain, world.environment);



		if(skydome != null)
		{
			Vector3 translation = world.cameraManager.rtsCamController.center;
			skydome.transform.set(
				translation.x, translation.y-60f, translation.z,
				skyRotation.x, skyRotation.y, skyRotation.z, skyRotation.w,
				1f, 1f, 1f
			);

			world.modelBatch.render(skydome);
		}





	}
}

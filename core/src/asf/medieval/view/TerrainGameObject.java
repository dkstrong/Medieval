package asf.medieval.view;

import asf.medieval.utility.UtMath;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;

/**
 * TODO: need to restructure this so the asset manager is used and everything that needs to be disposed will be..
 *
 * Created by Daniel Strong on 11/11/2015.
 */
public class TerrainGameObject implements GameObject{


	private MedievalWorld world;

	private Renderable ground;

	public TerrainGameObject(MedievalWorld world) {
		this.world = world;
		// Model model = world.assetManager.get("Scenes/Desert/Desert_Mobile_r1.g3db", Model.class);


		Texture texture = new Texture(Gdx.files.internal("Textures/Terrain/sand512.jpg"));

		ground = new Renderable();
		ground.environment = world.environment;
		ground.meshPart.mesh = world.scenario.heightField.mesh;
		ground.meshPart.primitiveType = GL20.GL_TRIANGLES;
		ground.meshPart.offset = 0;
		ground.meshPart.size = world.scenario.heightField.mesh.getNumIndices();
		ground.meshPart.update();
		ground.material = new Material(TextureAttribute.createDiffuse(texture));


		getPositionOnTerrain();

	}

	private void getPositionOnTerrain()
	{
		Vector3 coord00 = new Vector3();
		Vector3 coord10 = new Vector3();
		Vector3 coordHalf = new Vector3();

		world.scenario.heightField.getPositionAt(coord00, 0,0);
		world.addGameObject(new DebugPosGameObject(world, coord00,2,Color.YELLOW));
		System.out.println("0,0: "+ UtMath.round(coord00,2));

		world.scenario.heightField.getPositionAt(coord10, 1,1);
		world.addGameObject(new DebugPosGameObject(world, coord10,2,Color.YELLOW));
		System.out.println("1,0: "+ UtMath.round(coord10,2));

		coordHalf.x = (coord00.x + coord10.x)/2f;
		coordHalf.z = (coord00.z + coord10.z)/2f;

		coordHalf.y = world.scenario.heightField.getElevationRaycast(coordHalf);
		world.addGameObject(new DebugPosGameObject(world, coordHalf,2, Color.RED));

		coordHalf.y = world.scenario.heightField.getElevation(coordHalf);
		world.addGameObject(new DebugPosGameObject(world, coordHalf,2, Color.BLUE));


	}



	@Override
	public void update(float delta) {


	}


	@Override
	public void render(float delta)
	{
		//world.shadowBatch.render(ground);
		world.modelBatch.render(ground); // , world.environment


	}
}

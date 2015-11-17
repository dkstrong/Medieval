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


		//"Textures/Terrain/sand512.jpg"
		//"Textures/Floor/wallTiles.png"
		Texture texture = new Texture(Gdx.files.internal("Textures/Terrain/sand512.jpg"));
		texture.setWrap(Texture.TextureWrap.Repeat,Texture.TextureWrap.Repeat);

		ground = new Renderable();
		ground.environment = world.environment;
		ground.meshPart.mesh = world.scenario.heightField.mesh;
		ground.meshPart.primitiveType = GL20.GL_TRIANGLES;
		ground.meshPart.offset = 0;
		ground.meshPart.size = world.scenario.heightField.mesh.getNumIndices();
		ground.meshPart.update();
		ground.material = new Material(TextureAttribute.createDiffuse(texture));


		//world.addGameObject(new TerrainDebugGameObject(world));

	}





	@Override
	public void update(float delta) {


	}


	@Override
	public void render(float delta)
	{
		//world.shadowBatch.render(ground);
		world.modelBatch.render(ground); // environmet variable is set directly on the ground renderable


	}
}

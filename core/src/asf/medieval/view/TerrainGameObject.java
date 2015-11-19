package asf.medieval.view;

import asf.medieval.terrain.Terrain;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ModelInfluencer;
import com.badlogic.gdx.math.Vector3;

/**
 * TODO: need to restructure this so the asset manager is used and everything that needs to be disposed will be..
 *
 * Created by Daniel Strong on 11/11/2015.
 */
public class TerrainGameObject implements GameObject{


	private MedievalWorld world;

	public Terrain terrain;

	public TerrainGameObject(MedievalWorld world) {
		this.world = world;
		terrain = world.assetManager.get("Models/Terrain/terrain.txt", Terrain.class);


	}

	@Override
	public void update(float delta) {


	}


	@Override
	public void render(float delta)
	{
		//world.shadowBatch.render(terrain);
		world.modelBatch.render(terrain, world.environment);




	}
}

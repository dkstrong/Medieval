package asf.medieval.view;

import asf.medieval.shape.Box;
import asf.medieval.shape.Shape;
import asf.medieval.terrain.TerrainTextureAttribute;
import asf.medieval.utility.shadertest.ShaderTestAttribute;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by daniel on 11/21/15.
 */
public class ShaderTestView implements View{
	private final MedievalWorld world;

	private final Shape shaper;
	private ModelInstance modelInstance;
	public final Vector3 translation = new Vector3();
	public final Quaternion rotation = new Quaternion();

	public ShaderTestView(MedievalWorld world) {
		this.world = world;

		// https://github.com/mattdesl/lwjgl-basics/wiki/LibGDX-Meshes
		// https://github.com/mattdesl/lwjgl-basics/wiki/Shaders
		shaper = new Box(5,10);
		Vector3 dim = shaper.dimensions;
		Texture diffuseMap = new Texture(Gdx.files.internal("Textures/Terrain/grass_1.png"));
		Texture diffuseMap2 = new Texture(Gdx.files.internal("Textures/Terrain/dirt_1.png"));
		Texture maskMap = new Texture(Gdx.files.internal("Textures/mask.png"));


		Material mat = new Material(
			//ColorAttribute.createDiffuse(Color.BLUE),
			new ColorAttribute(ShaderTestAttribute.AlbedoColor, Color.ORANGE),
			TextureAttribute.createDiffuse(diffuseMap),
			new TextureAttribute(TerrainTextureAttribute.Tex1,diffuseMap),
			new TextureAttribute(TerrainTextureAttribute.Tex2,diffuseMap2),
			new TextureAttribute(TerrainTextureAttribute.TexMask,maskMap)
		);


		int attributes = VertexAttributes.Usage.Position |  VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates;
		ModelBuilder modelBuilder = new ModelBuilder();

		Model model = modelBuilder.createBox(dim.x, dim.y, dim.z,mat, attributes);


		modelInstance = new ModelInstance(model);


		translation.set(0,0,0).add(shaper.center);
	}

	@Override
	public void update(float delta) {
		translation.y = world.getElevationAt(translation.x, translation.z);
		translation.add(shaper.center);
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
	}
}

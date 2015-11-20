package asf.medieval.terrain;

import asf.medieval.utility.OpenSimplexNoise;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by daniel on 11/18/15.
 */
public class Terrain implements RenderableProvider,Disposable {

	public HeightField field;
	public Renderable ground;

	private Texture diffusemap;

	public Terrain(long seed, Texture diffusemap)
	{
		this.diffusemap = diffusemap;
		createHeightField(seed);
		configureField();
		createRenderable(null);

	}

	public Terrain(Pixmap heightmap, Texture diffusemap)
	{
		this.diffusemap = diffusemap;
		createHeightField(heightmap);
		configureField();
		createRenderable(null);

	}

	private void createHeightField(Pixmap heightmap)
	{
		field = new HeightField(true, heightmap, true, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates);
		heightmap.dispose();
	}

	private void createHeightField(long seed)
	{
		final int fieldWidth = 64;
		final int fieldHeight = 64;
		final float data[] = new float[fieldWidth*fieldHeight];

		final double featureSize = 20d;

		OpenSimplexNoise noise = new OpenSimplexNoise(seed);
		for (int x = 0; x < fieldWidth; x++){
			for (int y = 0; y < fieldHeight; y++){
				//data[y * fieldWidth + x] = (float) noise.eval(x / featureSize, y / featureSize);
				data[y * fieldWidth + x] = UtMath.smallest((float) noise.eval(x / featureSize, y / featureSize), 0.999f);
			}
		}

		field = new HeightField(true, data, fieldWidth,fieldHeight,true, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates);
	}

	private void configureField()
	{
		int w = 150, h = 150;
		float magnitude = 20f;
		field.uvScale.set(4f,4f);
		field.corner00.set(-w, 0, -h);
		field.corner10.set(w, 0, -h);
		field.corner01.set(-w, 0, h);
		field.corner11.set(w, 0, h);
		field.color00.set(0.75f, 0.75f, 0.75f, 1);
		field.color01.set(0.75f, 0.75f, 0.75f, 1);
		field.color10.set(0.75f, 0.75f, 0.75f, 1);
		field.color11.set(0.75f, 0.75f, 0.75f, 1);
		field.magnitude.set(0f, magnitude, 0f);
		field.update();
	}

	private void createRenderable(Environment environment)
	{


		ground = new Renderable();
		ground.environment = environment;
		ground.meshPart.mesh = field.mesh;
		ground.meshPart.primitiveType = GL20.GL_TRIANGLES;
		ground.meshPart.offset = 0;
		ground.meshPart.size = field.mesh.getNumIndices();
		ground.meshPart.update();
		ground.material = new Material(TextureAttribute.createDiffuse(diffusemap));
	}

	@Override
	public void dispose() {
		diffusemap.dispose();
		field.dispose();
	}

	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		renderables.add(ground);
	}
}

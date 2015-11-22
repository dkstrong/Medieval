package asf.medieval.terrain;

import asf.medieval.utility.OpenSimplexNoise;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.MeshPart;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by daniel on 11/18/15.
 */
public class Terrain implements RenderableProvider,Disposable {

	public TerrainChunk field;
	public TerrainChunk[] chunks;


	public Terrain(long seed)
	{
		MeshPart mp;
		createHeightField(seed);
		configureField();
	}

	public Terrain(Pixmap heightmap)
	{
		createHeightField(heightmap);
		configureField();
	}

	private void createHeightField(Pixmap heightmap)
	{

		chunks = new TerrainChunk[1];
		chunks[0] = new TerrainChunk(true, heightmap, true, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates);
		field = chunks[0];
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

		field = new TerrainChunk(true, data, fieldWidth,fieldHeight,true, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates);
	}

	private void configureField()
	{
		int w = 150, h = 150;
		float magnitude = 20f;
		field.uvScale.set(1f,1f);
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

	public Vector3 getWeightedNormalAt(Vector3 worldCoordinate,Vector3 store) {
		// convert world coordinates to field coordinates
		float fieldX = UtMath.scalarLimitsInterpolation(worldCoordinate.x, field.corner00.x, field.corner10.x, 0, field.width - 1);
		float fieldY = UtMath.scalarLimitsInterpolation(worldCoordinate.z, field.corner00.z, field.corner01.z, 0, field.height - 1);
		int x0 = (int) fieldX;
		int y0 = (int) fieldY;
		return field.getWeightedNormalAt(x0, y0 + 1, store);
	}

	public float getElevation(Vector3 worldCoordinate)
	{
		// convert world coordinates to field coordinates
		float x = UtMath.scalarLimitsInterpolation(worldCoordinate.x, field.corner00.x, field.corner10.x, 0, field.width - 1);
		float y = UtMath.scalarLimitsInterpolation(worldCoordinate.z, field.corner00.z, field.corner01.z, 0, field.height - 1);
		return field.getElevationField(x, y);
	}

	public float getElevation(float worldX, float worldZ)
	{
		// convert world coordinates to field coordinates
		float x = UtMath.scalarLimitsInterpolation(worldX, field.corner00.x, field.corner10.x, 0, field.width - 1);
		float y = UtMath.scalarLimitsInterpolation(worldZ, field.corner00.z, field.corner01.z, 0, field.height - 1);
		return field.getElevationField(x, y);
	}

	public boolean intersect(Ray ray, Vector3 store) {
		return Intersector.intersectRayTriangles(ray, field.vertices, field.indices, field.stride, store);
	}


	@Override
	public void dispose() {
		if(field !=null)
			field.dispose();
	}

	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		renderables.add(field.renderable);
	}
}

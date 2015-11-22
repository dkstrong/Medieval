package asf.medieval.terrain;

import asf.medieval.utility.OpenSimplexNoise;
import asf.medieval.utility.UtLog;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.graphics.Color;
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

	private TerrainChunk[] chunks;
	/**
	 * how many verts each chunk can have at most. Because meshes can not have more than 32k verts, terrain chunks can not excede this either
	 * you may want to reduce the verts per chunk so you'll have more chunks to apply frustum culling or LOD optimizations with etc.
	 */
	//public int maxVertsPerChunks = 32767;
	public int maxVertsPerChunks = 1000;

	public final Vector3 corner00 = new Vector3(-150, 0, -150);
	public final Vector3 corner10 = new Vector3(150, 0, -150);
	public final Vector3 corner01 = new Vector3(-150, 0, 150);
	public final Vector3 corner11 = new Vector3(150, 0, 150);
	public final Color color = new Color(0.75f,0.75f,0.75f,1f);
	public final Vector3 magnitude = new Vector3(0, 20f, 0f);

	public Terrain()
	{

	}

	protected void createHeightField(Pixmap heightmap)
	{

		chunks = new TerrainChunk[1];
		int vertexAttributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates;
		boolean isStatic = true;
		boolean smooth = true;
		chunks[0] = new TerrainChunk(isStatic, heightmap.getWidth(), heightmap.getHeight(),smooth, vertexAttributes);
		chunks[0].set(heightmap);
		heightmap.dispose();
	}

	protected void createHeightField(long seed)
	{

		final int vertexAttributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates;
		final boolean isStatic = true;
		final boolean smooth = true;
		final int maxDataPerChunk = smooth ? maxVertsPerChunks : maxVertsPerChunks /2;

		// max smooth: 181 x 181
		// max unsmooth: 128 x 128
		final int fieldWidth = 60;
		final int fieldHeight = 60;

		final int totalData = fieldWidth * fieldHeight;
		//final int numChunks = (int)((totalData / (float)maxDataPerChunk)+1f);
		final float numChunksF = totalData/(float)maxDataPerChunk;
		final int numChunks = (int)Math.ceil(totalData/(float)maxDataPerChunk);
		final int dataPerChunk = totalData / numChunks;

		final int chunkDataMaxWidth = (int)(fieldWidth / (float)numChunks);
		final int chunkDataMaxHeight =(int)(fieldHeight / (float)numChunks);

		System.out.println("fieldWidth: "+fieldWidth);
		System.out.println("fieldHeight: "+fieldHeight);
		System.out.println("totalData: "+totalData);
		System.out.println("numChunksF: "+numChunksF);
		System.out.println("numChunks: "+numChunks);
		System.out.println("dataPerChunk: "+dataPerChunk);
		System.out.println("chunkDataMaxWidth: "+chunkDataMaxWidth);
		System.out.println("chunkDataMaxHeight: "+chunkDataMaxHeight);

		final float data[] = new float[totalData];

		final double featureSize = 20d;
		OpenSimplexNoise noise = new OpenSimplexNoise(seed);
		for (int x = 0; x < fieldWidth; x++){
			for (int y = 0; y < fieldHeight; y++){
				//data[y * fieldWidth + x] = UtMath.clamp((float) noise.eval(x / featureSize, y / featureSize), 0, 0.999f);
				data[y * fieldWidth + x] = 1;
			}
		}

		Array<TerrainChunk> chunks = new Array<TerrainChunk>(true,numChunks, TerrainChunk.class);

		int chunk =0;
		for(int chunkStartX = 0; chunkStartX < fieldWidth; chunkStartX+=chunkDataMaxWidth){
			for(int chunkStartY=0; chunkStartY< fieldHeight; chunkStartY+=chunkDataMaxHeight){
				int chunkEndX = UtMath.smallest(chunkStartX + chunkDataMaxWidth, fieldWidth-1);
				int chunkEndY = UtMath.smallest(chunkStartY + chunkDataMaxHeight, fieldHeight-1);
				int chunkWidth = chunkEndX - chunkStartX;
				int chunkHeight = chunkEndY - chunkStartY;
				final float[] chunkData = new float[chunkWidth*chunkHeight];
				int chunkX =0;
				int chunkY=0;
				for (int x = chunkStartX; x < chunkEndX; x++){
					chunkY=0;
					for (int y = chunkStartY; y < chunkEndY; y++){
						chunkData[chunkY * chunkWidth + chunkX] = data[y * fieldWidth + x];
						chunkY++;
					}
					chunkX++;
				}


				System.out.println("creating chunk: "+chunk);
				TerrainChunk terrainChunk = new TerrainChunk(isStatic,chunkWidth,chunkHeight,smooth,vertexAttributes);
				terrainChunk.set(chunkData);

				//UtMath.scalarLimitsInterpolation()
				float chunkWorldStartX = UtMath.scalarLimitsInterpolation(chunkStartX, 0, fieldWidth, corner00.x, corner10.x);
				float chunkWorldStartY = UtMath.scalarLimitsInterpolation(chunkStartY, 0, fieldHeight, corner00.z, corner01.z);
				float chunkWorldEndX = UtMath.scalarLimitsInterpolation(chunkEndX, 0, fieldWidth, corner00.x, corner10.x);
				float chunkWorldEndY = UtMath.scalarLimitsInterpolation(chunkEndY, 0, fieldHeight, corner00.z, corner01.z);

				terrainChunk.configureField(chunkWorldStartX, chunkWorldStartY, chunkWorldEndX, chunkWorldEndY, color, magnitude);
				chunk++;
				chunks.add(terrainChunk);
			}
		}
		System.out.println("copying chunks");
		this.chunks = new TerrainChunk[chunks.size];
		for (int i = 0; i < chunks.size; i++) {
			this.chunks[i] = chunks.items[i];
		}

	}

	protected void createRenderables(TerrainLoader terrainLoader, TerrainLoader.TerrainParameter parameter)
	{
		for (TerrainChunk chunk : chunks) {
			chunk.createRenderable(terrainLoader.getDiffuseMap(chunk,parameter));
		}
	}

	public Vector3 getWeightedNormalAt(Vector3 worldCoordinate,Vector3 store) {
		for (TerrainChunk chunk : chunks) {
			// see if this chunk represents the provided world coords
			store.set(worldCoordinate).y =0;
			if(UtMath.isPointInQuadrilateral(store, chunk.corner00, chunk.corner10, chunk.corner01, chunk.corner11))
			{
				// convert world coordinates to field coordinates
				float fieldX = UtMath.scalarLimitsInterpolation(worldCoordinate.x, chunk.corner00.x, chunk.corner10.x, 0, chunk.width - 1);
				float fieldY = UtMath.scalarLimitsInterpolation(worldCoordinate.z, chunk.corner00.z, chunk.corner01.z, 0, chunk.height - 1);
				int x0 = (int) fieldX;
				int y0 = (int) fieldY;
				return chunk.getWeightedNormalAt(x0, y0 + 1, store);
			}
		}
		UtLog.warning("No normal found for: " + UtMath.round(worldCoordinate, 2) + ", returning Vector3.Y");
		store.set(Vector3.Y);
		return store;

	}

	public float getElevation(Vector3 worldCoordinate)
	{
		return getElevation(worldCoordinate.x, worldCoordinate.z);
	}

	private final Vector3 temp = new Vector3();

	public float getElevation(float worldX, float worldZ)
	{
		for (TerrainChunk chunk : chunks) {
			temp.set(worldX,0,worldZ);
			if(UtMath.isPointInQuadrilateral(temp, chunk.corner00, chunk.corner10, chunk.corner01, chunk.corner11))
			{
				// convert world coordinates to field coordinates
				float x = UtMath.scalarLimitsInterpolation(worldX, chunk.corner00.x, chunk.corner10.x, 0, chunk.width - 1);
				float y = UtMath.scalarLimitsInterpolation(worldZ, chunk.corner00.z, chunk.corner01.z, 0, chunk.height - 1);
				return chunk.getElevationField(x, y);
			}
		}
		UtLog.warning("No elevation found for: ("+worldX+", "+worldZ+"), returning 0");
		return 0;
	}

	public boolean intersect(Ray ray, Vector3 store) {
		for (TerrainChunk chunk : chunks) {
			boolean intersect = Intersector.intersectRayTriangles(ray, chunk.vertices, chunk.indices, chunk.stride, store);
			if(intersect)
				return true;
		}
		return false;
	}


	@Override
	public void dispose() {
		for (TerrainChunk chunk : chunks) {
			chunk.dispose();
		}
	}

	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		for (TerrainChunk chunk : chunks) {
			renderables.add(chunk.renderable);
		}
	}
}

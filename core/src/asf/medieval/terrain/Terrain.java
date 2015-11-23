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

	protected TerrainChunk masterChunk;
	private TerrainChunk[] chunks;
	/**
	 * how many verts each chunk can have at most. Because meshes can not have more than 32k verts, terrain chunks can not excede this either
	 * you may want to reduce the verts per chunk so you'll have more chunks to apply frustum culling or LOD optimizations with etc.
	 */
	public int maxVertsPerChunks = 32767 ;
	//public int maxVertsPerChunks = 1000;

	public final Vector3 corner00 = new Vector3(-250, 0, -250);
	public final Vector3 corner10 = new Vector3(250, 0, -250);
	public final Vector3 corner01 = new Vector3(-250, 0, 250);
	public final Vector3 corner11 = new Vector3(250, 0, 250);
	public final Color color = new Color(0.75f,0.75f,0.75f,1f);
	public final Vector3 magnitude = new Vector3(0, 30f, 0f);

	public Terrain()
	{

	}

	protected void createHeightField(Pixmap heightmap)
	{


		int vertexAttributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates;
		boolean isStatic = true;
		boolean smooth = true;
		masterChunk = new TerrainChunk(isStatic, heightmap.getWidth(), heightmap.getHeight(),smooth, vertexAttributes);
		masterChunk.set(heightmap);
		masterChunk.configureField(corner00.x,corner00.z,corner11.x,corner11.z,color, magnitude);

		heightmap.dispose();
		buildSubchunks();
	}

	protected void createHeightField(long seed)
	{

		final int vertexAttributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates;
		final boolean isStatic = true;
		final boolean smooth = true;
		final int maxDataPerChunk = smooth ? maxVertsPerChunks : maxVertsPerChunks /2;

		// max smooth: 181 x 181
		// max unsmooth: 128 x 128
		final int fieldWidth = 128;
		final int fieldHeight = 350;
		final int totalData = fieldWidth * fieldHeight;

		final float data[] = new float[totalData];

		final double featureSize = 20d;
		OpenSimplexNoise noise = new OpenSimplexNoise(seed);
		for (int x = 0; x < fieldWidth; x++){
			for (int y = 0; y < fieldHeight; y++){
				data[y * fieldWidth + x] = UtMath.clamp((float) noise.eval(x / featureSize, y / featureSize), 0, 0.999f);
				//data[y * fieldWidth + x] = 1;
			}
		}

		masterChunk = new TerrainChunk(isStatic, fieldWidth, fieldHeight,smooth,vertexAttributes);
		masterChunk.terrain = this;
		masterChunk.set(data);
		masterChunk.configureField(corner00.x,corner00.z,corner11.x,corner11.z,color, magnitude);

		//chunks = new TerrainChunk[1];
		//chunks[0] = masterChunk;
		buildSubchunks();
		//build2x2Subchunks();

	}

	private void buildSubchunks()
	{
		final int vertexAttributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates;
		final boolean isStatic = true;
		final boolean smooth = true;
		final int maxDataPerChunk = smooth ? maxVertsPerChunks : maxVertsPerChunks /2;

		// max smooth: 181 x 181
		// max unsmooth: 128 x 128
		final int fieldWidth = masterChunk.width;
		final int fieldHeight = masterChunk.height;

		final int totalData = fieldWidth * fieldHeight;

		final int chunkDataMaxWidth = 128;
		final int chunkDataMaxHeight =128;

		System.out.println("fieldWidth: "+fieldWidth);
		System.out.println("fieldHeight: "+fieldHeight);
		System.out.println("totalData: "+totalData);
		System.out.println("chunkDataMaxWidth: "+chunkDataMaxWidth);
		System.out.println("chunkDataMaxHeight: "+chunkDataMaxHeight);

		Array<TerrainChunk> chunks = new Array<TerrainChunk>(true,8, TerrainChunk.class);

		int chunk =0;
		int meshCoordX=0;
		int meshCoordY=0;
		for(int chunkStartX = 0; chunkStartX < fieldWidth; chunkStartX+=chunkDataMaxWidth){
			meshCoordY = 0;
			for(int chunkStartY=0; chunkStartY< fieldHeight; chunkStartY+=chunkDataMaxHeight){
				int chunkEndX = UtMath.smallest(chunkStartX + chunkDataMaxWidth+1, fieldWidth);
				int chunkEndY = UtMath.smallest(chunkStartY + chunkDataMaxHeight+1, fieldHeight);
				int chunkWidth = chunkEndX - chunkStartX;
				int chunkHeight = chunkEndY - chunkStartY;
				final float[] chunkData = new float[chunkWidth*chunkHeight];
				int chunkX =0;
				int chunkY=0;
				for (int x = chunkStartX; x < chunkEndX; x++){
					chunkY=0;
					for (int y = chunkStartY; y < chunkEndY; y++){
						chunkData[chunkY * chunkWidth + chunkX] = masterChunk.data[y * fieldWidth + x];
						chunkY++;
					}
					chunkX++;
				}


				System.out.println("creating chunk: "+chunk);
				TerrainChunk terrainChunk = new TerrainChunk(isStatic,chunkWidth,chunkHeight,smooth,vertexAttributes);
				terrainChunk.terrain = this;
				terrainChunk.meshCoordX = meshCoordX;
				terrainChunk.meshCoordY = meshCoordY;
				terrainChunk.chunkStartX = chunkStartX;
				terrainChunk.chunkStartY = chunkStartY;
				terrainChunk.chunkEndX = chunkEndX;
				terrainChunk.chunkEndY = chunkEndY;
				terrainChunk.set(chunkData);

				chunk++;
				chunks.add(terrainChunk);
				meshCoordY++;

			}

			meshCoordX++;
		}
		System.out.println("copying chunks");
		this.chunks = new TerrainChunk[chunks.size];
		for (int i = 0; i < chunks.size; i++) {
			this.chunks[i] = chunks.items[i];

			int addX = this.chunks[i].meshCoordX;
			int addY = this.chunks[i].meshCoordY;

			float chunkWorldStartX = UtMath.scalarLimitsExtrapolation(this.chunks[i].chunkStartX + addX, 0, fieldWidth, corner00.x, corner10.x);
			float chunkWorldStartY = UtMath.scalarLimitsExtrapolation(this.chunks[i].chunkStartY + addY, 0, fieldHeight, corner00.z, corner01.z);
			float chunkWorldEndX = UtMath.scalarLimitsExtrapolation(this.chunks[i].chunkEndX + addX, 0, fieldWidth, corner00.x, corner10.x);
			float chunkWorldEndY = UtMath.scalarLimitsExtrapolation(this.chunks[i].chunkEndY + addY, 0, fieldHeight, corner00.z, corner01.z);

			this.chunks[i].configureField(chunkWorldStartX, chunkWorldStartY, chunkWorldEndX, chunkWorldEndY, color, magnitude);
		}
	}

	protected void createRenderables(TerrainLoader terrainLoader, TerrainLoader.TerrainParameter parameter)
	{
		for (TerrainChunk chunk : chunks) {
			chunk.createRenderable(terrainLoader.getDiffuseMap(chunk,parameter));
		}
	}

	public Vector3 getWeightedNormalAt(Vector3 worldCoordinate,Vector3 store) {
		TerrainChunk chunk = masterChunk;
		float fieldX = UtMath.scalarLimitsInterpolation(worldCoordinate.x, chunk.corner00.x, chunk.corner10.x, 0, chunk.width - 1);
		float fieldY = UtMath.scalarLimitsInterpolation(worldCoordinate.z, chunk.corner00.z, chunk.corner01.z, 0, chunk.height - 1);
		int x0 = (int) fieldX;
		int y0 = (int) fieldY;
		return chunk.getWeightedNormalAt(x0, y0 + 1, store);

	}

	public float getElevation(Vector3 worldCoordinate)
	{
		return getElevation(worldCoordinate.x, worldCoordinate.z);
	}

	private final Vector3 temp = new Vector3();

	public float getElevation(float worldX, float worldZ)
	{
		TerrainChunk chunk = masterChunk;
		// convert world coordinates to field coordinates
		float chunkX = UtMath.scalarLimitsInterpolation(worldX, chunk.corner00.x, chunk.corner10.x, 0, chunk.width - 1);
		float chunkY = UtMath.scalarLimitsInterpolation(worldZ, chunk.corner00.z, chunk.corner01.z, 0, chunk.height - 1);
		return chunk.getElevation(chunkX, chunkY);

	}

	public boolean intersect(Ray ray, Vector3 store) {
//		if(true){
//			return Intersector.intersectRayTriangles(ray, masterChunk.vertices, masterChunk.indices, masterChunk.stride, store);
//
//		}
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

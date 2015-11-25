package asf.medieval.terrain;

import asf.medieval.utility.UtLog;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
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

	private Array<Array<TerrainChunk>> chunkGrid;
	//public int maxVertsPerChunks = 32767;
	//public int maxVertsPerChunks = 1000;

	public final Vector3 corner00 = new Vector3(-250, 0, -250);
	public final Vector3 corner11 = new Vector3(250, 0, 250);
	public final Color color = new Color(0.75f,0.75f,0.75f,1f);
	public float magnitude = 30;

	public float[] fieldData;
	public int fieldWidth=128;
	public int fieldHeight=128;
	public int chunkDataMaxWidth = 128;
	public int chunkDataMaxHeight =128;

	/**
	 * camera to be used for frustum culling of terrain chunks- optional
	 */
	public Camera camera;

	protected void createHeightField(){
		float heightRatio = fieldHeight / (float)fieldWidth;
		corner00.z *= heightRatio;
		corner11.z *= heightRatio;
		final int vertexAttributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates;
		final boolean isStatic = true;
		final boolean smooth = true;
		chunkGrid = new Array<Array<TerrainChunk>>(true, 1, Array.class);
		TerrainChunk chunk = new TerrainChunk(isStatic,fieldWidth, fieldHeight,smooth, vertexAttributes);
		chunk.terrain = this;
		chunk.gridX = 0;
		chunk.gridY = 0;
		chunk.fieldStartX = 0;
		chunk.fieldStartY = 0;
		chunk.fieldEndX = fieldWidth;
		chunk.fieldEndY = fieldHeight;
		chunk.set(fieldData);
		putTerrainChunk(0, 0, chunk);
		chunk.configureField(corner00.x, corner00.z, corner11.x, corner11.z, color, magnitude);

	}


	public TerrainChunk getTerrainChunk(int x, int y){
		return chunkGrid.get(x).get(y);
	}

	private void putTerrainChunk(int x, int y, TerrainChunk chunk){
		Array<TerrainChunk> terrainChunks;
		if(chunkGrid.size > x){
			terrainChunks = chunkGrid.get(x);
		}else{
			terrainChunks = new Array<TerrainChunk>(true, 4, TerrainChunk.class);
			chunkGrid.add(terrainChunks);
			if(chunkGrid.get(x) != terrainChunks)
				throw new IllegalStateException("Chunks not added in correct order (x)");
		}

		terrainChunks.add(chunk);
		if(terrainChunks.get(y) != chunk)
			throw new IllegalStateException("Chunks not added in correct order (y)");
	}

	protected void createRenderables(TerrainLoader terrainLoader, TerrainLoader.TerrainParameter parameter)
	{
		for (Array<TerrainChunk> terrainChunks : chunkGrid) {
			for (TerrainChunk terrainChunk : terrainChunks) {
				terrainChunk.createRenderable(terrainLoader.getMaterial(terrainChunk, parameter));
			}
		}
	}

	public Vector3 getWeightedNormalAt(Vector3 worldCoordinate,Vector3 store) {
		// TODO: has same issue as getElevationFast(), the calcualted fieldX and fieldY values are wrong
		float fieldX = UtMath.scalarLimitsInterpolation(worldCoordinate.x, corner00.x, corner11.x, 0, fieldWidth - 1);
		float fieldY = UtMath.scalarLimitsInterpolation(worldCoordinate.z, corner00.z, corner11.z, 0, fieldHeight - 1);

		int gridX= (int)(fieldX / (float)chunkDataMaxWidth);
		int gridY= (int)(fieldY / (float)chunkDataMaxHeight);

		TerrainChunk chunk = getTerrainChunk(gridX, gridY);
		float chunkX = fieldX-(gridX * chunkDataMaxWidth);
		float chunkY = fieldY-(gridY * chunkDataMaxHeight);

		//int x0 = Math.round(fieldX);
		//int y0 = Math.round(fieldY) ;
		int x0 = (int)chunkX;
		int y0 = (int)chunkY;
		return chunk.getWeightedNormalAt(x0, y0, store);

	}

	public Vector3 getWorldCoordinate(int fieldX, int fieldY, Vector3 store){
		if(fieldX >= fieldWidth) fieldX = fieldWidth-1;
		if(fieldY >= fieldHeight) fieldY = fieldHeight-1;
		int gridX= (int)(fieldX / (float)chunkDataMaxWidth);
		int gridY= (int)(fieldY / (float)chunkDataMaxHeight);
		TerrainChunk chunk = getTerrainChunk(gridX, gridY);
		int chunkX = fieldX-(gridX * chunkDataMaxWidth);
		int chunkY = fieldY-(gridY * chunkDataMaxHeight);
		return chunk.getWorldCoordinate(chunkX, chunkY, store);

	}

	public Vector3 getWorldCoordinate(float fieldX, float fieldY, Vector3 store){
		if(fieldX >= fieldWidth) fieldX = fieldWidth-1;
		if(fieldY >= fieldHeight) fieldY = fieldHeight-1;
		int gridX= (int)(fieldX / (float)chunkDataMaxWidth);
		int gridY= (int)(fieldY / (float)chunkDataMaxHeight);
		TerrainChunk chunk = getTerrainChunk(gridX, gridY);
		float chunkX = fieldX-(gridX * chunkDataMaxWidth);
		float chunkY = fieldY-(gridY * chunkDataMaxHeight);
		return chunk.getWorldCoordinate(chunkX, chunkY, store);

	}

	public float getElevation(Vector3 worldCoordinate)
	{
		return getElevation(worldCoordinate.x, worldCoordinate.z);
	}

	public float getElevation(float worldX, float worldZ)
	{
		//TODO: this only works properly if thres only 1 terrain chunk
		// the calcualted fieldX and fieldY values are wrong if there is more than 1 chunk
		// I need to figure out how to back in to these values.
		// convert world coordinates to field coordinates
		float fieldX = UtMath.scalarLimitsInterpolation(worldX, corner00.x, corner11.x, 0, fieldWidth -1);
		float fieldY = UtMath.scalarLimitsInterpolation(worldZ, corner00.z, corner11.z, 0, fieldHeight-1);

		int gridX= (int)(fieldX / (float)chunkDataMaxWidth);
		int gridY= (int)(fieldY / (float)chunkDataMaxHeight);
		TerrainChunk chunk = getTerrainChunk(gridX, gridY);

		float chunkX = fieldX-(gridX * chunkDataMaxWidth);
		float chunkY = fieldY-(gridY * chunkDataMaxHeight);

		return chunk.getElevation(chunkX, chunkY);



	}



	public boolean intersect(Ray ray, Vector3 store) {
		for (Array<TerrainChunk> terrainChunks : chunkGrid) {
			for (TerrainChunk terrainChunk : terrainChunks) {
				if(Intersector.intersectRayTriangles(ray, terrainChunk.vertices, terrainChunk.indices, terrainChunk.stride, store))
					return true;
			}
		}
		return false;
	}


	@Override
	public void dispose() {
		for (Array<TerrainChunk> terrainChunks : chunkGrid) {
			for (TerrainChunk terrainChunk : terrainChunks) {
				terrainChunk.dispose();
			}
		}
	}

	@Override
	public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
		for (Array<TerrainChunk> terrainChunks : chunkGrid) {
			for (TerrainChunk terrainChunk : terrainChunks) {

				if(camera== null || camera.frustum.boundsInFrustum(
					terrainChunk.renderable.meshPart.center.x,
					terrainChunk.renderable.meshPart.center.y,
					terrainChunk.renderable.meshPart.center.z,
					terrainChunk.renderable.meshPart.halfExtents.x,
					terrainChunk.renderable.meshPart.halfExtents.y,
					terrainChunk.renderable.meshPart.halfExtents.z

				)){
					renderables.add(terrainChunk.renderable);
				}

			}
		}
	}



	protected void createHeightFieldMultipleChunks(){

		float heightRatio = fieldHeight / (float)fieldWidth;
		corner00.z *= heightRatio;
		corner11.z *= heightRatio;

		final int vertexAttributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates;
		final boolean isStatic = true;
		final boolean smooth = true;
		//final int maxDataPerChunk = smooth ? maxVertsPerChunks : maxVertsPerChunks /2;

		chunkGrid = new Array<Array<TerrainChunk>>(true, 4, Array.class);
		int chunk =0;
		int gridX=0;
		int gridY=0;
		for(int chunkStartX = 0; chunkStartX < fieldWidth; chunkStartX+=chunkDataMaxWidth){
			gridY = 0;
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
						chunkData[chunkY * chunkWidth + chunkX] = fieldData[y * fieldWidth + x];
						chunkY++;
					}
					chunkX++;
				}


				//System.out.println("creating chunk: "+chunk);
				TerrainChunk terrainChunk = new TerrainChunk(isStatic,chunkWidth,chunkHeight,smooth,vertexAttributes);
				terrainChunk.terrain = this;
				terrainChunk.gridX = gridX;
				terrainChunk.gridY = gridY;
				terrainChunk.fieldStartX = chunkStartX;
				terrainChunk.fieldStartY = chunkStartY;
				terrainChunk.fieldEndX = chunkEndX;
				terrainChunk.fieldEndY = chunkEndY;
				terrainChunk.set(chunkData);

				chunk++;
				putTerrainChunk(gridX, gridY, terrainChunk);
				gridY++;

			}

			gridX++;
		}

		for (Array<TerrainChunk> terrainChunks : chunkGrid) {
			for (TerrainChunk terrainChunk : terrainChunks) {
				// TODO: i think this is what is causing slight "stretching" of the diffuse texture
				// and is probably also why the elevation finding code seems to be slightly off
				// also if you use scalarLimitsExtrapolate instead of Interpolate, then
				// get the weighted normal of the highest (in Y) edge- youll get an index
				// out of bounds error.
				// I think this is because chunks need one extra row/col of verticies to connect
				// chunks, i dont think this properly accounts for that... (ie somekind of +1 or -1 thing)
				float chunkWorldStartX = UtMath.scalarLimitsExtrapolation(terrainChunk.fieldStartX + terrainChunk.gridX, 0, fieldWidth, corner00.x, corner11.x);
				float chunkWorldStartY = UtMath.scalarLimitsExtrapolation(terrainChunk.fieldStartY + terrainChunk.gridY, 0, fieldHeight, corner00.z, corner11.z);
				float chunkWorldEndX = UtMath.scalarLimitsExtrapolation(terrainChunk.fieldEndX + terrainChunk.gridX, 0, fieldWidth, corner00.x, corner11.x);
				float chunkWorldEndY = UtMath.scalarLimitsExtrapolation(terrainChunk.fieldEndY + terrainChunk.gridY, 0, fieldHeight, corner00.z, corner11.z);

				terrainChunk.configureField(chunkWorldStartX, chunkWorldStartY, chunkWorldEndX, chunkWorldEndY, color, magnitude);
			}
		}


	}

	public Vector3 verifyWorldCoordinateSlow(Vector3 worldCoordinate, Vector3 store){
		// convert world coordinates to field coordinates
		float worldX = worldCoordinate.x;
		float worldZ = worldCoordinate.z;
		store.set(worldX,0,worldZ);
		for (Array<TerrainChunk> terrainChunks : chunkGrid) {
			for (TerrainChunk terrainChunk : terrainChunks) {
				if(	UtMath.isBetween(worldX, terrainChunk.corner00.x, terrainChunk.corner11.x) && UtMath.isBetween(worldZ, terrainChunk.corner00.z, terrainChunk.corner11.z) ){

					float fieldX = UtMath.scalarLimitsInterpolation(worldX, terrainChunk.corner00.x, terrainChunk.corner11.x, terrainChunk.fieldStartX, terrainChunk.fieldEndX -1);
					float fieldY = UtMath.scalarLimitsInterpolation(worldZ, terrainChunk.corner00.z, terrainChunk.corner11.z, terrainChunk.fieldStartY, terrainChunk.fieldEndY -1);
					return getWorldCoordinate(fieldX, fieldY, store);
				}
			}
		}

		throw new IllegalStateException("no coord found for: "+UtMath.round(worldCoordinate,2));
	}

	public float getElevationSlow(float worldX, float worldZ)
	{
		Vector3 store = new Vector3();
		store.set(worldX, 0, worldZ);
		for (Array<TerrainChunk> terrainChunks : chunkGrid) {
			for (TerrainChunk terrainChunk : terrainChunks) {
				if(	UtMath.isBetween(worldX, terrainChunk.corner00.x, terrainChunk.corner11.x) && UtMath.isBetween(worldZ, terrainChunk.corner00.z, terrainChunk.corner11.z) ){

					float fieldX = UtMath.scalarLimitsInterpolation(worldX, terrainChunk.corner00.x, terrainChunk.corner11.x, terrainChunk.fieldStartX, terrainChunk.fieldEndX -1);
					float fieldY = UtMath.scalarLimitsInterpolation(worldZ, terrainChunk.corner00.z, terrainChunk.corner11.z, terrainChunk.fieldStartY, terrainChunk.fieldEndY -1);
					return getWorldCoordinate(fieldX, fieldY, store).y;
				}
			}
		}
		UtLog.error("could not find elevation, returning zero");
		return 0;

	}

}

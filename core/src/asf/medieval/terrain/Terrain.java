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

	public Terrain()
	{

	}

	private TerrainChunk getTerrainChunk(int x, int y){
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

	private Array<Array<TerrainChunk>> chunkGrid;
	//public int maxVertsPerChunks = 32767 ;
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

	protected void createHeightField(){

		float heightRatio = fieldHeight / (float)fieldWidth;
		corner00.z *= heightRatio;
		corner11.z *= heightRatio;

		final int vertexAttributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates;
		final boolean isStatic = true;
		final boolean smooth = true;
		//final int maxDataPerChunk = smooth ? maxVertsPerChunks : maxVertsPerChunks /2;

		chunkGrid = new Array<Array<TerrainChunk>>(true, 4, Array.class);
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
						chunkData[chunkY * chunkWidth + chunkX] = fieldData[y * fieldWidth + x];
						chunkY++;
					}
					chunkX++;
				}


				//System.out.println("creating chunk: "+chunk);
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
				putTerrainChunk(meshCoordX, meshCoordY, terrainChunk);
				meshCoordY++;

			}

			meshCoordX++;
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
				float chunkWorldStartX = UtMath.scalarLimitsExtrapolation(terrainChunk.chunkStartX + terrainChunk.meshCoordX, 0, fieldWidth, corner00.x, corner11.x);
				float chunkWorldStartY = UtMath.scalarLimitsExtrapolation(terrainChunk.chunkStartY + terrainChunk.meshCoordY, 0, fieldHeight, corner00.z, corner11.z);
				float chunkWorldEndX = UtMath.scalarLimitsExtrapolation(terrainChunk.chunkEndX + terrainChunk.meshCoordX, 0, fieldWidth, corner00.x, corner11.x);
				float chunkWorldEndY = UtMath.scalarLimitsExtrapolation(terrainChunk.chunkEndY + terrainChunk.meshCoordY, 0, fieldHeight, corner00.z, corner11.z);

				terrainChunk.configureField(chunkWorldStartX, chunkWorldStartY, chunkWorldEndX, chunkWorldEndY, color, magnitude);
			}
		}


	}

	protected void createRenderables(TerrainLoader terrainLoader, TerrainLoader.TerrainParameter parameter)
	{
		for (Array<TerrainChunk> terrainChunks : chunkGrid) {
			for (TerrainChunk terrainChunk : terrainChunks) {
				terrainChunk.createRenderable(terrainLoader.getDiffuseMap(terrainChunk, parameter));
			}
		}
	}

	public Vector3 getWeightedNormalAt(Vector3 worldCoordinate,Vector3 store) {

		float fieldX = UtMath.scalarLimitsInterpolation(worldCoordinate.x, corner00.x, corner11.x, 0, fieldWidth - 1);
		float fieldY = UtMath.scalarLimitsInterpolation(worldCoordinate.z, corner00.z, corner11.z, 0, fieldHeight - 1);

		int gridX= (int)(fieldX / (float)chunkDataMaxWidth);
		int gridY= (int)(fieldY / (float)chunkDataMaxHeight);

		TerrainChunk chunk = getTerrainChunk(gridX, gridY);

		fieldX -= gridX *(float)chunkDataMaxWidth;
		fieldY -= gridY *(float)chunkDataMaxHeight;

		//int x0 = Math.round(fieldX);
		//int y0 = Math.round(fieldY) ;
		int x0 = (int)fieldX;
		int y0 = (int)fieldY;
		return chunk.getWeightedNormalAt(x0, y0, store);

	}

	public float getElevation(Vector3 worldCoordinate)
	{
		return getElevation(worldCoordinate.x, worldCoordinate.z);
	}

	public float getElevation(float worldX, float worldZ)
	{
		// convert world coordinates to field coordinates
		float fieldX = UtMath.scalarLimitsInterpolation(worldX, corner00.x, corner11.x, 0, fieldWidth -1);
		float fieldY = UtMath.scalarLimitsInterpolation(worldZ, corner00.z, corner11.z, 0, fieldHeight-1);
		int gridX= (int)(fieldX / (float)chunkDataMaxWidth);
		int gridY= (int)(fieldY / (float)chunkDataMaxHeight);
		TerrainChunk chunk = getTerrainChunk(gridX, gridY);

		fieldX -= gridX *(float)chunkDataMaxWidth;
		fieldY -= gridY *(float)chunkDataMaxHeight;

		return chunk.getElevation(fieldX, fieldY);

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
				renderables.add(terrainChunk.renderable);
			}
		}
	}
}

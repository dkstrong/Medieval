package asf.medieval.view;

import asf.medieval.terrain.TerrainChunk;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class TerrainDebugView implements View {


	private MedievalWorld world;

	public TerrainDebugView(MedievalWorld world) {
		this.world = world;

		// mountains512.png
		//drawMesh(10,10, Color.GOLDENROD);
		////drawMesh(11,10, Color.MAGENTA);
		//drawMesh(10,11, Color.BLUE);
		////drawMesh(11,11, Color.RED);


		// seed

		drawMesh(0,0, Color.BLUE);
//		drawElevations(0,0,Color.PURPLE);
		drawFloatElevations(0,0,Color.PURPLE);
//
//		drawMesh(1,0, Color.RED);
//		drawElevations(1,0,Color.ORANGE);
//		drawFloatElevations(1,0,Color.ORANGE);
//
//		drawMesh(2,0, Color.OLIVE);
//		drawElevations(2,0,Color.GREEN);
//		drawFloatElevations(2,0,Color.GREEN);

/////////////////////////////////////
//		drawMesh(0,1, Color.FIREBRICK);
//		drawElevations(0,1,Color.CHARTREUSE);
//		drawFloatElevations(0,1,Color.CHARTREUSE);

		//drawMesh(1,1, Color.GOLD);
		//drawElevations(1,1,Color.YELLOW);
		//drawFloatElevations(1,1,Color.YELLOW);

//		drawMesh(2,1, Color.BROWN);
//		drawElevations(2,1,Color.SALMON);
//		drawFloatElevations(2,1,Color.SALMON);
/////////////////////////////////////////////

//		drawMesh(0,2, Color.BLUE);
//		drawElevations(0,2,Color.PURPLE);
//		drawFloatElevations(0,2,Color.PURPLE);
//
//		drawMesh(1,2, Color.RED);
//		drawElevations(1,2,Color.ORANGE);
//		drawFloatElevations(1,2,Color.ORANGE);
//
//		drawMesh(2,2, Color.OLIVE);
//		drawElevations(2,2,Color.GREEN);
//		drawFloatElevations(2,2,Color.GREEN);

		//////////////////////////

//		drawMesh(1,5, Color.BLUE);
//		drawElevations(1,5,Color.RED);
//		drawFloatElevations(1,5,Color.FOREST);
	}

	private void drawElevations(int chunkX, int chunkY, Color color){
		TerrainChunk terrainChunk = world.terrainView.terrain.getTerrainChunk(chunkX, chunkY);
		for(int x=0; x< terrainChunk.width; x++){
			for(int y=0; y< terrainChunk.height; y++){
				Vector3 worldPoint = world.terrainView.terrain.getWorldCoordinate(x+terrainChunk.chunkStartX,y+terrainChunk.chunkStartY, new Vector3());
				worldPoint.y = world.terrainView.terrain.getElevation(worldPoint);
				world.addGameObject(new DebugShapeView(world).point(worldPoint,30,color));
			}
		}
	}

	private void drawFloatElevations(int chunkX, int chunkY, Color color){
		TerrainChunk terrainChunk = world.terrainView.terrain.getTerrainChunk(chunkX, chunkY);
		for(int x=0; x< terrainChunk.width; x++){
			for(int y=0; y< terrainChunk.height; y++){
				float xfloat = x+0.25f;
				float yFloat = y+0.25f;
				Vector3 worldPoint = world.terrainView.terrain.getWorldCoordinate(xfloat+terrainChunk.chunkStartX,yFloat+terrainChunk.chunkStartY, new Vector3());
				//worldPoint.y = world.terrainView.terrain.getElevation(worldPoint);
				world.addGameObject(new DebugShapeView(world).point(worldPoint,30,color));
				world.terrainView.terrain.verifyWorldCoordinate(worldPoint, worldPoint);
				world.addGameObject(new DebugShapeView(world).point(worldPoint,40,color.cpy().mul(0.5f)));

			}
		}
	}

	private void drawMesh(int chunkX, int chunkY, Color color){
		TerrainChunk terrainChunk = world.terrainView.terrain.getTerrainChunk(chunkX, chunkY);
		System.out.println(String.format("chunk: %s,%s , width: %s, height:%s", chunkX, chunkY, terrainChunk.width,terrainChunk.height));

		//final int i = (chunkY * width +chunkX)*stride;
		//store.set(vertices[i], vertices[i + 1], vertices[i + 2]);

		for(int i=0; i <terrainChunk.indices.length; i+=3)
		{
			Vector3 worldPoint = getWorldPoint(terrainChunk,terrainChunk.indices[i+0]);
			Vector3 worldPoint1 = getWorldPoint(terrainChunk,terrainChunk.indices[i+1]);
			world.addGameObject(new DebugShapeView(world).arrow(worldPoint, worldPoint1, color));

			worldPoint = getWorldPoint(terrainChunk,terrainChunk.indices[i+1]);
			worldPoint1 = getWorldPoint(terrainChunk,terrainChunk.indices[i+2]);
			world.addGameObject(new DebugShapeView(world).arrow(worldPoint, worldPoint1, color));

			worldPoint = getWorldPoint(terrainChunk,terrainChunk.indices[i+2]);
			worldPoint1 = getWorldPoint(terrainChunk,terrainChunk.indices[i+0]);
			world.addGameObject(new DebugShapeView(world).arrow(worldPoint, worldPoint1, color));
		}
	}

	private Vector3 getWorldPoint(TerrainChunk terrainChunk, int index){
		int i = index*terrainChunk.stride;

		float v0 = terrainChunk.vertices[i+terrainChunk.posPos];
		float v1 = terrainChunk.vertices[i+terrainChunk.posPos+1];
		float v2 = terrainChunk.vertices[i+terrainChunk.posPos+2];

		return new Vector3(v0,v1,v2);
	}


	@Override
	public void update(float delta) {


	}
	@Override
	public void render(float delta) {

	}
}

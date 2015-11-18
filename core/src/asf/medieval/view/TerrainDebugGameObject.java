package asf.medieval.view;

import asf.medieval.utility.HeightField;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

/**
 * Created by Daniel Strong on 11/11/2015.
 */
public class TerrainDebugGameObject implements GameObject{


	private MedievalWorld world;

	public TerrainDebugGameObject(MedievalWorld world) {
		this.world = world;
		//debugElevation();

	}
	@Override
	public void update(float delta) {
	}



	private void debugElevation(){
		final HeightField field = world.scenario.heightField;

		Vector3 tempV1 = new Vector3();
		Vector3 tempV2 = new Vector3();

		field.getWorldCoordinate(0, 0,tempV1);
		field.getWorldCoordinate(1, 0,tempV2);
		float segmentWidth = UtMath.abs(tempV1.x - tempV2.x);

		int chunkWidth = field.width/9;
		int chunkHeight = field.width/9;

		int xStart = 16;
		int yStart = 16;

		for(int x=xStart; x <xStart+chunkWidth; x++){
			for(int y=yStart; y<yStart+chunkHeight; y++){
				tempV1.set(x, 0, y);
				field.getWorldCoordinate(x, y,tempV1);
				world.addGameObject(new DebugPosGameObject(world, tempV1,2,Color.YELLOW));
				for(int j=1; j < 10; j++){
					float floatJ = j / 10f * segmentWidth;
					for(int k=1; k <10; k++)
					{
						float floatK = k / 10f * segmentWidth;
						tempV2.set(tempV1.x + floatJ, tempV1.y, tempV1.z+floatK);
						tempV2.y = field.getElevation(tempV2);
						world.addGameObject(new DebugPosGameObject(world, tempV2,2,Color.BLUE));
					}
				}
			}
		}
	}




	@Override
	public void render(float delta) {

	}
}

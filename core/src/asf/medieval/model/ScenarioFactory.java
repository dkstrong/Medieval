package asf.medieval.model;

import asf.medieval.utility.HeightField;
import asf.medieval.utility.OpenSimplexNoise;
import asf.medieval.utility.UtMath;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

/**
 * Created by Daniel Strong on 11/12/2015.
 */
public class ScenarioFactory {



	public static Scenario scenarioTest(Random random)
	{
		ScenarioRand scenarioRand = new ScenarioRand(random);

		HeightField field = heightFieldFromOpenSimplexNoise(scenarioRand);
		//HeightField field = heightFieldFromImage();

		Scenario scenario = new Scenario(scenarioRand, field);

		//steeringTest2(scenario);
		return scenario;
	}
	private static HeightField heightFieldFromOpenSimplexNoise(ScenarioRand scenarioRand){
		final int fieldWidth = 64;
		final int fieldHeight = 64;
		final float data[] = new float[fieldWidth*fieldHeight];

		final double featureSize = 20d;

		OpenSimplexNoise noise = new OpenSimplexNoise(scenarioRand.random.nextLong());
		for (int x = 0; x < fieldWidth; x++){
			for (int y = 0; y < fieldHeight; y++){
				//data[y * fieldWidth + x] = (float) noise.eval(x / featureSize, y / featureSize);
				data[y * fieldWidth + x] = UtMath.smallest((float)noise.eval(x / featureSize, y / featureSize), 0.999f);
			}
		}




		HeightField field = new HeightField(true, data, fieldWidth,fieldHeight,true, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates);

		int w = 50, h = 50;
		float magnitude = 10f;
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

		return field;
	}

	private static HeightField heightFieldFromImage(){
		Pixmap data = new Pixmap(Gdx.files.internal("heightmap.png"));

		HeightField field = new HeightField(true, data, true, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates);
		data.dispose();

		int w = 250, h = 250;
		float magnitude = .15f;
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

		return field;
	}

	private static HeightField heightFieldFlat(){
		final float data[] = new float[64*64];

		HeightField field = new HeightField(true, data, 64,64,false, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates);

		int w = 50, h = 50;
		float magnitude = 1f;
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

		return field;
	}

	public static void seekPosition(SoldierToken character, Vector3 position)
	{




		//ScenarioLocation seekTarget = new ScenarioLocation();
		//seekTarget.vec.set(vec);

	}


	public static Scenario steeringTest(Scenario scenario)
	{
		SoldierToken character = scenario.newSoldier();

		seekPosition(character, new Vector3(10,0,0));

		return scenario;
	}

	public static Scenario steeringTest2(Scenario scenario)
	{

		Array<SoldierToken> characters;

		characters = new Array<SoldierToken>();

		for (int i = 0; i < 25; i++) {
			final SoldierToken character = scenario.newSoldier();

			scenario.setRandomNonOverlappingPosition(character, characters, 5);
			//setRandomOrientation(character);
			// TODO: https://github.com/libgdx/gdx-ai/blob/master/tests/src/com/badlogic/gdx/ai/tests/steer/scene2d/tests/Scene2dCollisionAvoidanceTest.java

			characters.add(character);
		}

		return scenario;
	}


	private ScenarioFactory()
	{

	}

}

package asf.medieval.model;

import asf.medieval.utility.HeightField;
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



	public static Scenario scenarioFlat()
	{
		ScenarioRand scenarioRand = new ScenarioRand(new Random(1));

		int w = 50, h = 50;
		float magnitude = 1f;
		HeightField field = heightFieldFlat();
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


		Scenario scenario = new Scenario(scenarioRand, field);

		//steeringTest2(scenario);
		return scenario;
	}

	private static HeightField heightFieldFromImage(){
		Pixmap data = new Pixmap(Gdx.files.internal("heightmap.png"));

		HeightField field = new HeightField(true, data, true, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates);
		data.dispose();

		return field;
	}

	private static HeightField heightFieldFlat(){
		final float data[] = new float[64*64];

		HeightField field = new HeightField(true, data, 64,64,false, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates);


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

package asf.medieval.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Daniel Strong on 11/12/2015.
 */
public class ScenarioFactory {



	public static void seekPosition(SoldierToken character, Vector3 position)
	{


		character.setTarget(position);

		//ScenarioLocation seekTarget = new ScenarioLocation();
		//seekTarget.vec.set(vec);

	}


	public static Scenario steeringTest(Scenario scenario)
	{
		if(scenario == null)
			scenario = new Scenario(null);
		SoldierToken character = scenario.newSoldier();

		seekPosition(character, new Vector3(10,0,0));

		return scenario;
	}

	public static Scenario steeringTest2(Scenario scenario)
	{
		if(scenario == null)
			scenario = new Scenario(null);

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
